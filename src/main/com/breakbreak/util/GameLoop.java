package main.com.breakbreak.util;

import main.com.breakbreak.components.*;
import main.com.breakbreak.enums.CollisionType;
import main.com.breakbreak.enums.State;
import main.com.breakbreak.events.CollisionEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameLoop implements ActionListener {

    private static final int FPS = 60;
    private static final int DELAY_MS = 1000 / FPS;
    private static final double BASE_SPEED = 4.5;

    // Colecciones dinámicas concurrentes para el soporte de los proyectiles y reclutas de Attack on Titan
    private final List<ReclutaComponent> escuadronAliado = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final List<BolaFuego> bolasFuegoActivas = new java.util.concurrent.CopyOnWriteArrayList<>();

    private final JComponent host;
    private final PaddleComponent paddle;
    private final BallComponent ball;
    private final BorderComponent border;
    private final BlockComponent[][] blocks;
    private final List<HeartComponent> hearts;
    private final List<GameEventListener> listeners = new ArrayList<>();
    private final PowerUpManager powerUpManager;
    private final List<CollisionObserver> observers = new ArrayList<>();

    private final Timer timer;
    private State state = State.READY;
    private double ballX, ballY;
    private double vx = 3.0;
    private double vy = -3.0;
    private int lives = 3;
    private int totalBlocks = 42; // SINCRONIZADO EXCLUSIVAMENTE CON LA NUEVA MATRIZ 7x6 (42 BLOQUES)
    private int destroyedBlocks = 0;
    private int score = 0;

    // --- VARIABLES DE ESTADO LOGICO PARA LOS 19 PODERES DE ATTACK ON TITAN ---
    private int damageMultiplier = 1;
    private int scoreMultiplier = 1;
    private boolean stickyPaddle = false;
    private boolean ballAttached = false;
    private boolean armoredPaddle = false;
    private boolean shootingMode = false;
    private long lastShotTime = 0;

    // Referencia para enviar las actualizaciones al feed estático del Sidebar derecho
    private BonoSidebarPanel bonoSidebarPanel;

    public JComponent getHost() {
        return this.host;
    }

    public GameLoop(JComponent host, BallComponent ball, PaddleComponent paddle,
                    BorderComponent border, BlockComponent[][] blocks, List<HeartComponent> hearts) {
        this.host = host;
        this.ball = ball;
        this.paddle = paddle;
        this.border = border;
        this.blocks = blocks;
        this.hearts = hearts;

        this.timer = new Timer(DELAY_MS, this);
        this.timer.setCoalesce(true);

        this.powerUpManager = new PowerUpManager(host, paddle, border);
        this.powerUpManager.setGameLoop(this);

        this.observers.add(this.powerUpManager);

        syncCoordinates();
        this.timer.start();
    }

    public void syncCoordinates() {
        if (ball != null) {
            this.ballX = ball.getX();
            this.ballY = ball.getY();
        }
    }

    public void addListener(GameEventListener listener) {
        if (listener != null) listeners.add(listener);
    }

    public State getState() { return state; }

    public void start() {
        if (state == State.READY || state == State.PAUSED) {
            if (state == State.PAUSED) {
                state = State.RUNNING;
            }
            if (timer != null && !timer.isRunning()) {
                timer.start();
            }
        }
    }

    public void pause() {
        if (state == State.RUNNING) {
            state = State.PAUSED;
            if (timer != null) timer.stop();
        }
    }

    public void resetGame() {
        if (timer != null) timer.stop();
        state = State.READY;
        lives = 3;
        destroyedBlocks = 0;
        score = 0;
        vx = 3.0;
        vy = -3.0;

        // Limpieza absoluta de modificadores tácticos entre rondas
        this.damageMultiplier = 1;
        this.scoreMultiplier = 1;
        this.stickyPaddle = false;
        this.ballAttached = false;
        this.armoredPaddle = false;
        this.shootingMode = false;

        if (ball != null) {
            ball.setSize(26, 26);
        }

        // CORRECCIÓN: Restaura el ancho nativo (90px) de la raqueta eliminando deformaciones residuales
        if (paddle != null) {
            paddle.setBounds(195, 540, 90, 20);
        }

        // Purgado absoluto de elementos gráficos móviles residuales de la ronda anterior
        for (BolaFuego f : bolasFuegoActivas) {
            if (f != null && host != null) host.remove(f);
        }
        bolasFuegoActivas.clear();

        for (ReclutaComponent r : escuadronAliado) {
            if (r != null && host != null) host.remove(r);
        }
        escuadronAliado.clear();

        if (powerUpManager != null) powerUpManager.clear();
        for (GameEventListener l : listeners) {
            if (l != null) l.onReset();
        }

        if (bonoSidebarPanel != null) {
            bonoSidebarPanel.reset();
        }

        for (HeartComponent h : hearts) {
            if (h != null) h.setHeartState(true);
        }

        if (blocks != null) {
            for (int r = 0; r < blocks.length; r++) {
                if (blocks[r] != null) {
                    for (int c = 0; c < blocks[r].length; c++) {
                        if (blocks[r][c] != null) {
                            blocks[r][c].setVisible(true);
                            blocks[r][c].setDestroyed(false);
                        }
                    }
                }
            }
        }

        if (ball != null) ball.setLocation(227, 450);
        if (paddle != null) paddle.setLocation(195, 540);
        syncCoordinates();
        if (host != null) host.repaint();

        if (timer != null) timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) { update(); }

    private void update() {
        if (host == null || ball == null || paddle == null || border == null || blocks == null) return;

        if (state == State.READY || state == State.RUNNING) {
            for (Component child : host.getComponents()) {
                if (child instanceof BackgroundComponent) {
                    ((BackgroundComponent) child).actualizarAnimacion();
                    break;
                }
                if (child instanceof JLayeredPane) {
                    for (Component subChild : ((JLayeredPane) child).getComponents()) {
                        if (subChild instanceof BackgroundComponent) {
                            ((BackgroundComponent) subChild).actualizarAnimacion();
                            break;
                        }
                    }
                }
            }
        }

        // COMPUERTA DE SEGURIDAD CONTRACTUAL EN PAUSA, VICTORIA O DERROTA
        if (state != State.RUNNING && state != State.READY) {
            host.repaint();
            Toolkit.getDefaultToolkit().sync();
            return;
        }

        int size = ball.getWidth();
        Rectangle area = border.getInnerBounds();
        if (area == null) return;

        // COMPUERTA DE CONTROL DE SAQUE E IMÁN: Controla la fijación en READY o Cristalización activa
        if ((state == State.READY) || (stickyPaddle && ballAttached)) {
            ballX = paddle.getX() + (paddle.getWidth() / 2) - (size / 2);
            ballY = paddle.getY() - size;
            ball.setPosition(ballX, ballY);
            this.ballAttached = true;

            // Si el motor está en READY, congelamos el procesamiento físico para esperar el saque por clic
            if (state == State.READY) {
                host.repaint();
                Toolkit.getDefaultToolkit().sync();
                return;
            }
        } else {
            ballX += vx;
            ballY += vy;
        }

        // Límites y rebotes contra los mosaicos del BorderComponent
        if (ballX <= area.x) { ballX = area.x; vx = Math.abs(vx); }
        else if (ballX + size >= area.x + area.width) { ballX = area.x + area.width - size; vx = -Math.abs(vx); }

        if (ballY <= area.y) { ballY = area.y; vy = Math.abs(vy); }

        ball.setPosition(ballX, ballY);

        // Rebote elástico contra la raqueta (PaddleComponent)
        if (vy > 0 && ball.getBounds() != null && paddle.getBounds() != null && ball.getBounds().intersects(paddle.getBounds())) {
            if (this.stickyPaddle) {
                this.ballAttached = true;
                this.vy = 0;
                this.vx = 0;
            } else {
                int ballCenterX = ball.getX() + size / 2;
                int paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
                double offset = (ballCenterX - paddleCenterX) / (double)(paddle.getWidth() / 2);

                vx = offset * 4.0;
                vy = -Math.abs(vy);

                double currentMagnitude = Math.sqrt(vx * vx + vy * vy);
                if (currentMagnitude > 0) {
                    vx = (vx / currentMagnitude) * BASE_SPEED;
                    vy = (vy / currentMagnitude) * BASE_SPEED;
                }
                if (Math.abs(vy) < 1.5) vy = -2.5;

                ball.setVx((int)Math.round(vx));
                ball.setVy((int)Math.round(vy));
            }
            ballY = paddle.getY() - size;
            ball.setPosition(ballX, ballY);
        }

        // Bucle estructural de impactos contra bloques con damageMultiplier (D) y scoreMultiplier (F)
        outerLoop:
        for (int r = 0; r < blocks.length; r++) {
            if (blocks[r] == null) continue;
            for (int c = 0; c < blocks[r].length; c++) {
                BlockComponent block = blocks[r][c];
                if (block != null && block.isVisible() && !block.isDestroyed() && ball.getBounds() != null && ball.getBounds().intersects(block.getBounds())) {
                    vy = -vy;
                    ball.setVy((int)Math.round(vy));

                    boolean blockBroken = false;
                    for (int d = 0; d < damageMultiplier; d++) {
                        if (block.takeDamage()) {
                            blockBroken = true;
                            break;
                        }
                    }

                    if (blockBroken) {
                        destroyedBlocks++;
                        score += (block.getPointsValue() * scoreMultiplier);

                        if (powerUpManager != null) powerUpManager.checkAndSpawn(block);
                        CollisionEvent ev = new CollisionEvent(ball, block, CollisionType.BALL_BLOCK);
                        for (CollisionObserver obs : observers) {
                            if (obs != null) obs.onCollision(ev);
                        }
                    } else {
                        score += (25 * scoreMultiplier);
                    }

                    for (GameEventListener l : listeners) {
                        if (l != null) l.onScoreChanged(score);
                    }

                    if (destroyedBlocks >= totalBlocks) {
                        state = State.WIN;
                        if (timer != null) timer.stop();
                        for (GameEventListener l : listeners) {
                            if (l != null) l.onVictory(score);
                        }
                    }
                    break outerLoop;
                }
            }
        }

        if (powerUpManager != null) powerUpManager.update();

        // =========================================================================
        // 🔴 MOTOR SÍNCRONO: LANZAMIENTO TRIPLE EN ABANICO DE CAÑONAZOS GIF (CÁPSULA N)
        // =========================================================================
        if (this.shootingMode) {
            long now = System.currentTimeMillis();
            if (now - lastShotTime > 600) {
                lastShotTime = now;
                int centroRaquetaX = paddle.getX() + (paddle.getWidth() / 2) - 16;
                int spawnY = paddle.getY() - 32;

                BolaFuego fuegoIzquierda = new BolaFuego(centroRaquetaX, spawnY, -2.5, -5.0);
                BolaFuego fuegoCentro    = new BolaFuego(centroRaquetaX, spawnY,  0.0, -6.0);
                BolaFuego fuegoDerecha   = new BolaFuego(centroRaquetaX, spawnY,  2.5, -5.0);

                bolasFuegoActivas.add(fuegoIzquierda);
                bolasFuegoActivas.add(fuegoCentro);
                bolasFuegoActivas.add(fuegoDerecha);

                if (host != null) {
                    host.add(fuegoIzquierda, 0);
                    host.add(fuegoCentro, 0);
                    host.add(fuegoDerecha, 0);
                }
            }
        }

        for (BolaFuego fuego : bolasFuegoActivas) {
            if (fuego == null) continue;
            fuego.actualizarPosicion();

            if (fuego.getY() < area.y || fuego.getX() < area.x || fuego.getX() > area.x + area.width) {
                if (host != null) host.remove(fuego);
                bolasFuegoActivas.remove(fuego);
                continue;
            }

            for (int r = 0; r < blocks.length; r++) {
                if (blocks[r] == null) continue;
                for (int c = 0; c < blocks[r].length; c++) {
                    BlockComponent b = blocks[r][c];
                    if (b != null && b.isVisible() && !b.isDestroyed() && fuego.getBounds() != null && fuego.getBounds().intersects(b.getBounds())) {
                        if (host != null) host.remove(fuego);
                        bolasFuegoActivas.remove(fuego);

                        boolean broken = false;
                        for (int d = 0; d < damageMultiplier; d++) {
                            if (b.takeDamage()) {
                                broken = true;
                                break;
                            }
                        }
                        if (broken) {
                            destroyedBlocks++;
                            score += (b.getPointsValue() * scoreMultiplier);
                            if (powerUpManager != null) powerUpManager.checkAndSpawn(b);
                        } else {
                            score += (25 * scoreMultiplier);
                        }
                        for (GameEventListener l : listeners) {
                            if (l != null) l.onScoreChanged(score);
                        }
                        if (destroyedBlocks >= totalBlocks) {
                            state = State.WIN;
                            if (timer != null) timer.stop();
                            for (GameEventListener l : listeners) {
                                if (l != null) l.onVictory(score);
                            }
                        }
                        break;
                    }
                }
            }
        }

        // =========================================================================
        // 🟣 MOTOR SÍNCRONO: REBOTE AUTÓNOMO DE MIKASA Y ARMIN EN EL CANVAS (CÁPSULA V)
        // =========================================================================
        for (ReclutaComponent recluta : escuadronAliado) {
            if (recluta == null) continue;
            recluta.actualizarPosicion();

            int rX = recluta.getX();
            int rY = recluta.getY();
            double rVx = recluta.getVx();
            double rVy = recluta.getVy();
            int rW = recluta.getWidth();
            int rH = recluta.getHeight();

            if (rX <= area.x) { rX = area.x; rVx = Math.abs(rVx); }
            else if (rX + rW >= area.x + area.width) { rX = area.x + area.width - rW; rVx = -Math.abs(rVx); }
            if (rY <= area.y) { rY = area.y; rVy = Math.abs(rVy); }

            if (rVy > 0 && recluta.getBounds() != null && paddle.getBounds() != null && recluta.getBounds().intersects(paddle.getBounds())) {
                rVy = -Math.abs(rVy);
                rY = paddle.getY() - rH;
            }

            recluta.setVx(rVx);
            recluta.setVy(rVy);
            recluta.setLocation(rX, rY);

            if (rY + rH >= area.y + area.height) {
                if (host != null) host.remove(recluta);
                escuadronAliado.remove(recluta);
                continue;
            }

            for (int r = 0; r < blocks.length; r++) {
                if (blocks[r] == null) continue;
                for (int c = 0; c < blocks[r].length; c++) {
                    BlockComponent b = blocks[r][c];
                    if (b != null && b.isVisible() && !b.isDestroyed() && recluta.getBounds() != null && recluta.getBounds().intersects(b.getBounds())) {
                        recluta.setVy(-rVy);

                        if (b.takeDamage()) {
                            destroyedBlocks++;
                            score += (b.getPointsValue() * scoreMultiplier);
                            if (powerUpManager != null) powerUpManager.checkAndSpawn(b);
                        } else {
                            score += (25 * scoreMultiplier);
                        }
                        for (GameEventListener l : listeners) {
                            if (l != null) l.onScoreChanged(score);
                        }
                        if (destroyedBlocks >= totalBlocks) {
                            state = State.WIN;
                            if (timer != null) timer.stop();
                            for (GameEventListener l : listeners) {
                                if (l != null) l.onVictory(score);
                            }
                        }
                        break;
                    }
                }
            }
        }

        // =========================================================================
        // COMPUERTA DE SEGURIDAD FINAL: VALIDACIÓN DE CAÍDA AL VACÍO
        // =========================================================================
        if (ball != null && area != null) {
            if (ball.getY() + size >= area.y + area.height) {
                if (timer != null) timer.stop();
                lives--;

                for (BolaFuego f : bolasFuegoActivas) {
                    if (f != null && host != null) host.remove(f);
                }
                bolasFuegoActivas.clear();

                for (ReclutaComponent r : escuadronAliado) {
                    if (r != null && host != null) host.remove(r);
                }
                escuadronAliado.clear();

                if (lives > 0) {
                    if (hearts != null && lives < hearts.size() && hearts.get(lives) != null) {
                        hearts.get(lives).setHeartState(false);
                    }
                    if (ball != null) ball.setLocation(227, 450);
                    if (paddle != null) paddle.setLocation(195, 540);
                    syncCoordinates();
                    vx = 3.0;
                    vy = -3.0;
                    state = State.READY;

                    for (GameEventListener l : listeners) {
                        if (l != null) l.onLifeLost(lives);
                    }
                    if (timer != null) timer.start();
                } else {
                    if (hearts != null && !hearts.isEmpty() && hearts.get(0) != null) {
                        hearts.get(0).setHeartState(false);
                    }
                    state = State.GAME_OVER;
                    for (GameEventListener l : listeners) {
                        if (l != null) l.onGameOver(score);
                    }
                }
            }
        }

        if (host != null) host.repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    private void checkVictoryConditionInternal() {
        if (destroyedBlocks >= totalBlocks) {
            state = State.WIN;
            if (timer != null) timer.stop();
            for (GameEventListener l : listeners) {
                if (l != null) l.onVictory(score);
            }
        }
    }

    // =========================================================================
    // ENLACE HUD: MÉTODOS DE ACCESO PARA EL PANEL DE ANUNCIOS DEL SIDEBAR
    // =========================================================================

    public void setBonoSidebarPanel(main.com.breakbreak.components.BonoSidebarPanel panel) {
        this.bonoSidebarPanel = panel;
    }

    public main.com.breakbreak.components.BonoSidebarPanel getBonoSidebarPanel() {
        return this.bonoSidebarPanel;
    }

    // =========================================================================
    // PARÁMETROS ACCESORES MUTABLES REQUERIDOS POR EL REGISTRO CENTRAL STRATEGY
    // =========================================================================

    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }
    public double getVx() { return this.vx; }
    public double getVy() { return this.vy; }

    public BallComponent getBall() { return this.ball; }
    public PaddleComponent getPaddle() { return this.paddle; }

    public void addScore(int points) {
        this.score += points;
        for (GameEventListener l : listeners) {
            if (l != null) l.onScoreChanged(this.score);
        }
    }

    public void recoverLife() {
        if (this.lives < 3 && hearts != null) {
            if (this.lives >= 0 && this.lives < hearts.size() && hearts.get(this.lives) != null) {
                hearts.get(this.lives).setHeartState(true);
            }
            this.lives++;
            for (GameEventListener l : listeners) {
                if (l != null) l.onLifeLost(this.lives);
            }
        }
    }

    public void setGlobalDamageMultiplier(int mult) { this.damageMultiplier = mult; }
    public void setStickyPaddle(boolean active) { this.stickyPaddle = active; if (!active) this.ballAttached = false; }
    public void setArmoredPaddle(boolean active) { this.armoredPaddle = active; }
    public void setScoreMultiplier(int mult) { this.scoreMultiplier = mult; }

    /**
     * GATILLO DE HARDWARE: Al hacer clic izquierdo en el área de juego, se libera la pelota cristalizada o en saque inicial.
     */
    public void lanzarPelotaMagnetica() {
        if (state == State.READY || (this.stickyPaddle && this.ballAttached)) {
            this.ballAttached = false;
            if (state == State.READY) {
                state = State.RUNNING;
            }
            this.vx = (Math.random() > 0.5 ? 3.0 : -3.0);
            this.vy = -BASE_SPEED;
        }
    }

    public void duplicateBall() {
        if (host != null && paddle != null) {
            for (ReclutaComponent r : escuadronAliado) {
                if (r != null) host.remove(r);
            }
            escuadronAliado.clear();

            ReclutaComponent mikasa = new ReclutaComponent(paddle.getX(), paddle.getY() - 50, "mikasa", -3.5, -4.5);
            ReclutaComponent armin  = new ReclutaComponent(paddle.getX() + paddle.getWidth() - 38, paddle.getY() - 50, "armin", 3.5, -4.5);

            escuadronAliado.add(mikasa);
            escuadronAliado.add(armin);

            host.add(mikasa, 0);
            host.add(armin, 0);
            this.addScore(100);
            host.repaint();
        }
    }

    public void enableShootingMode(boolean active) { this.shootingMode = active; }

    /**
     * ⚪ EL RETUMBAR (R): Destruye síncronamente la fila superior completa de la grilla (Fila 0).
     */
    public void triggerRumbleEffect() {
        if (blocks == null || blocks.length == 0 || blocks[0] == null) return;

        // Recorremos todas las columnas (c) de la fila superior (Fila 0)
        for (int c = 0; c < blocks[0].length; c++) {
            BlockComponent block = blocks[0][c];
            if (block != null && block.isVisible() && !block.isDestroyed()) {
                block.setVisible(false);
                block.setDestroyed(true);
                destroyedBlocks++;
                score += (block.getPointsValue() * scoreMultiplier);
            }
        }
        for (GameEventListener l : listeners) {
            if (l != null) l.onScoreChanged(this.score);
        }
        checkVictoryConditionInternal();
    }

    /**
     * ⚪ LA COORDENADA (D): Purga instantáneamente todos los bloques de daño simple activos.
     */
    public void destroyAllBasicBlocks() {
        if (blocks == null) return;
        for (int r = 0; r < blocks.length; r++) {
            if (blocks[r] == null) continue;
            for (int c = 0; c < blocks[r].length; c++) {
                BlockComponent block = blocks[r][c];
                if (block != null && block.isVisible() && !block.isDestroyed()) {
                    String color = block.getColorKey().toLowerCase();
                    if (color.contains("green") || color.contains("aqua") || color.contains("cyan")) {
                        block.setVisible(false);
                        block.setDestroyed(true);
                        destroyedBlocks++;
                        score += (block.getPointsValue() * scoreMultiplier);
                    }
                }
            }
        }
        for (GameEventListener l : listeners) {
            if (l != null) l.onScoreChanged(this.score);
        }
        checkVictoryConditionInternal();
    }
}

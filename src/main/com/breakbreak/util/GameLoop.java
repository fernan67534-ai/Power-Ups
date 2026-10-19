package main.com.breakbreak.util;

import main.com.breakbreak.components.*;
import main.com.breakbreak.enums.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameLoop implements ActionListener {

    private static final int FPS = 60;
    private static final int DELAY_MS = 1000 / FPS;

    // Velocidad base constante
    private static final double BASE_SPEED = 4.5;

    private final JComponent host;
    private final PaddleComponent paddle;
    private final BallComponent ball;
    private final BorderComponent border;
    private final BlockComponent[][] blocks;
    private final List<HeartComponent> hearts;
    private final List<GameEventListener> listeners = new ArrayList<>();

    private final Timer timer;
    private State state = State.READY;
    private double vx = 3.0;
    private double vy = -3.0;
    private int lives = 3;
    private int totalBlocks = 25;
    private int destroyedBlocks = 0;
    private int score = 0;

    public GameLoop(JComponent host, BallComponent ball, PaddleComponent paddle,
                    BorderComponent border, BlockComponent[][] blocks, List<HeartComponent> hearts) {
        this.host = host;
        this.ball = ball;
        this.border = border;
        this.paddle = paddle;
        this.blocks = blocks;
        this.hearts = hearts;
        this.timer = new Timer(DELAY_MS, this);
        this.timer.setCoalesce(true);
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public State getState() {
        return state;
    }

    public void start() {
        if (state == State.READY || state == State.PAUSED) {
            vx = 3.0;
            vy = -3.0;
            state = State.RUNNING;
            timer.start();
        }
    }

    public void pause() {
        if (state == State.RUNNING) {
            state = State.PAUSED;
            timer.stop();
        }
    }

    public void resetGame() {
        timer.stop();
        state = State.READY;
        lives = 3;
        destroyedBlocks = 0;
        score = 0;
        vx = 3.0;
        vy = -3.0;

        for (GameEventListener l : listeners) {
            l.onReset();
        }

        for (HeartComponent h : hearts) {
            h.setAlive(true);
        }

        for (int r = 0; r < blocks.length; r++) {
            for (int c = 0; c < blocks[r].length; c++) {
                if (blocks[r][c] != null) {
                    blocks[r][c].setVisible(true);
                }
            }
        }

        ball.setLocation(219, 380);
        paddle.setLocation(162, 540);
        host.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    private void update() {
        if (state != State.RUNNING) return;

        int size = ball.getWidth();
        Rectangle area = border.getInnerBounds();

        // Actualizamos posición usando precisión double y casteando a int para los componentes
        double nextX = ball.getX() + vx;
        double nextY = ball.getY() + vy;

        // Rebotes en paredes laterales
        if (nextX <= area.x) {
            nextX = area.x;
            vx = Math.abs(vx);
        } else if (nextX + size >= area.x + area.width) {
            nextX = area.x + area.width - size;
            vx = -Math.abs(vx);
        }

        // Rebote en el techo
        if (nextY <= area.y) {
            nextY = area.y;
            vy = Math.abs(vy);
        }

        ball.setLocation((int) nextX, (int) nextY);

        // Colisión con la paleta con velocidad normalizada (estable)
        if (vy > 0 && ball.getBounds().intersects(paddle.getBounds())) {
            int ballCenterX = ball.getX() + size / 2;
            int paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
            double offset = (ballCenterX - paddleCenterX) / (double)(paddle.getWidth() / 2);

            // Modificamos vx basado en el punto de impacto (-1.0 a 1.0)
            vx = offset * 4.0;
            vy = -Math.abs(vy); // Forzar dirección hacia arriba

            // Normalizar el vector de velocidad para mantener una rapidez perfectamente constante
            double currentMagnitude = Math.sqrt(vx * vx + vy * vy);
            if (currentMagnitude > 0) {
                vx = (vx / currentMagnitude) * BASE_SPEED;
                vy = (vy / currentMagnitude) * BASE_SPEED;
            }

            // Evitar que la pelota quede atrapada horizontalmente
            if (Math.abs(vy) < 1.5) {
                vy = -2.5;
            }

            ball.setY(paddle.getY() - size);
        }

        // Colisión con bloques
        outerLoop:
        for (int r = 0; r < blocks.length; r++) {
            for (int c = 0; c < blocks[r].length; c++) {
                BlockComponent block = blocks[r][c];
                if (block != null && block.isVisible() && ball.getBounds().intersects(block.getBounds())) {
                    block.setVisible(false);
                    vy = -vy; // Invertir verticalmente
                    destroyedBlocks++;
                    score += 100;

                    for (GameEventListener l : listeners) {
                        l.onScoreChanged(score);
                    }

                    if (destroyedBlocks >= totalBlocks) {
                        state = State.WIN;
                        timer.stop();
                        for (GameEventListener l : listeners) {
                            l.onVictory(score);
                        }
                    }
                    break outerLoop;
                }
            }
        }

        // Pérdida de vida si cae al fondo
        if (ball.getY() + size >= area.y + area.height) {
            timer.stop();
            lives--;

            if (lives > 0) {
                hearts.get(lives).setAlive(false);
                ball.setLocation(219, 380);
                paddle.setLocation(162, 540);
                vx = 3.0;
                vy = -3.0;
                state = State.READY;
                for (GameEventListener l : listeners) {
                    l.onLifeLost(lives);
                }
            } else {
                hearts.get(0).setAlive(false);
                state = State.GAME_OVER;
                for (GameEventListener l : listeners) {
                    l.onGameOver(score);
                }
            }
        }

        host.repaint();
        Toolkit.getDefaultToolkit().sync();
    }
}
package main.com.breakbreak.app;

import main.com.breakbreak.components.*;
import main.com.breakbreak.enums.State;
import main.com.breakbreak.util.GameLoop;

// IMPORTACIONES CORECTAS DE TUS INTERFACES DE EVENTOS Y OLEADAS
import main.com.breakbreak.util.GameEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class BrickBreakApp extends JFrame implements GameEventListener {

    private final JLayeredPane gamePanel;
    private final JPanel sidebarPanel;
    private final JLabel scoreValueLabel;
    private final List<HeartComponent> hearts = new ArrayList<>();

    private final JButton playButton;
    private final JButton resetButton;
    private final JButton exitButton;

    private PaddleComponent paddle;
    private BallComponent ball;
    private BorderComponent border;
    private BlockComponent[][] blocks;
    private List<BlockComponent> blockList = new ArrayList<>();
    private GameLoop gameLoop;
    private GameOverPanel gameOverPanel;
    private BonoSidebarPanel bonoSidebarPanel;

    public BrickBreakApp() {
        // CONFIGURACIÓN DE LA VENTANA GENERAL (ANCHO DE JUEGO + SIDEBAR)
        setTitle("BREAKBREAK - PARADIS EDITION");
        setSize(860, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Contenedor principal con diseño absoluto nulo para manejar capas manuales
        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(10, 10, 10));

        // 1. LIENZO IZQUIERDO DE JUEGO (480x640)
        gamePanel = new JLayeredPane();
        gamePanel.setBounds(0, 0, 480, 640);
        gamePanel.setLayout(null);
        getContentPane().add(gamePanel);

        // 2. PANEL DERECHO DE SIDEBAR CONTROLADORA (380x640)
        sidebarPanel = new JPanel();
        sidebarPanel.setBounds(480, 0, 380, 640);
        sidebarPanel.setBackground(new Color(20, 21, 23));
        sidebarPanel.setLayout(null);
        getContentPane().add(sidebarPanel);

        // INICIALIZACIÓN DE COMPONENTES BASE DE FISICAS
        // REEMPLAZO LÍNEA 63: Encapsula las dimensiones en un objeto Rectangle nativo
        border = new BorderComponent(new Rectangle(0, 0, 480, 640));

        Rectangle area = border.getInnerBounds();

        ball = new BallComponent(227, 450, 26, 26);
        paddle = new PaddleComponent(195, 540, 90, 20);

        // Fondo animado cinemático de Shingeki no Kyojin
        // REEMPLAZO LÍNEA 69: Encapsula las dimensiones en un objeto Rectangle nativo
        // Pasa únicamente los dos enteros de dimensiones que solicita tu constructor
        BackgroundComponent background = new BackgroundComponent(480, 640);





        gamePanel.add(background, Integer.valueOf(-10)); // Capa Z más profunda
        // =========================================================================
        // ⚔️ AJUSTE GEOMÉTRICO SIMÉTRICO: MATRIZ TOTALMENTE CENTRADA (7 X 6)
        // =========================================================================
        // CORRECCIÓN: Agregamos [] para que Java entienda que es un arreglo de Strings válido
        String[] matrixColors = {"blue", "orange", "yellow", "aqua", "cyan", "green"};
        this.blocks = new BlockComponent[7][6];

        // Recalculamos el ancho exacto para cubrir el lienzo interno:
        // 416px / 6 columnas = 69 píxeles de ancho por bloque de forma exacta.
        int blockW = 69, blockH = 20;

        // Desplazamiento horizontal inicial: sumamos un offset de 34px para saltar la columna izquierda
        int gridStartX = area.x + 3;
        int gridStartY = area.y + 10; // Separación sutil y limpia del techo

        java.util.Random randomCromatico = new java.util.Random();

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                int bx = gridStartX + (c * blockW);
                int by = gridStartY + (r * blockH);

                String colorAleatorio = matrixColors[randomCromatico.nextInt(matrixColors.length)];
                BlockComponent block = new BlockComponent(bx, by, blockW, blockH, colorAleatorio);

                this.blocks[r][c] = block;
                blockList.add(block);
                gamePanel.add(block);
            }
        }



        // AGREGADO DE COMPONENTES MÓVILES AL LIENZO
        gamePanel.add(ball, Integer.valueOf(0));
        gamePanel.add(paddle, Integer.valueOf(0));
        gamePanel.add(border, Integer.valueOf(5)); // Capa del marco de piedra superior

        // =========================================================================
        // GATILLO DE CAPTURA DE HARDWARE (SOLUCIÓN AL CONGELAMIENTO DEL CLIC)
        // =========================================================================
        this.setFocusable(true);
        this.requestFocusInWindow();
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        // CONTROLADORES DE RASTREO DEL RATÓN SOBRE EL CANVAS IZQUIERDO
        gamePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (gameLoop != null && (gameLoop.getState() == State.RUNNING || gameLoop.getState() == State.READY)) {
                    int newX = e.getX() - (paddle.getWidth() / 2);
                    // Límites geométricos estrictos basados en tus marcos de piedra reales
                    if (newX < area.x) newX = area.x;
                    if (newX + paddle.getWidth() > area.x + area.width) newX = area.x + area.width - paddle.getWidth();
                    paddle.setLocation(newX, paddle.getY());
                }
            }
        });

        // GATILLO DE HARDWARE: Al hacer clic en el área de juego, se libera la pelota cristalizada
        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (gameLoop != null) {
                    gameLoop.lanzarPelotaMagnetica();
                }
            }
        });

        // PANELES DE DERROTA Y MARCO IMPERIAL DENTRO DE LAS CAPAS FRONTALES
        gameOverPanel = new GameOverPanel(0, 0, 480, 640);
        gamePanel.add(gameOverPanel, Integer.valueOf(10)); // Frente absoluto

        // =========================================================================
        // ❤️ REAJUSTE DE SIMETRÍA: ALINEACIÓN PERFECTA DE CORAZONES EN EL HUD
        // =========================================================================
        // Centramos los 3 corazones dentro de los 380px del Sidebar derecho.
        // Cada corazón mide 32px de ancho con 15px de separación entre ellos.
        int anchoTotalCorazones = (3 * 32) + (2 * 15); // 126px de ancho total del bloque de vidas
        int heartStartX = (380 - anchoTotalCorazones) / 2; // Da un margen exacto de 127px a los lados

        for (int i = 0; i < 3; i++) {
            int posicionX = heartStartX + (i * (32 + 15)); // 32px de tamaño + 15px de separación espacial
            int posicionY = 210; // Alineado verticalmente debajo de la etiqueta de texto

            HeartComponent heart = new HeartComponent(posicionX, posicionY, true);
            hearts.add(heart);
            sidebarPanel.add(heart);
        }






        // INICIALIZACIÓN OFICIAL DEL MOTOR PRINCIPAL DE JUEGO (GAMELOOP)
        gameLoop = new GameLoop(gamePanel, ball, paddle, border, blocks, hearts);
        gameLoop.addListener(this);
        // =========================================================================
        // 📊 SECCIÓN DE TEXTOS Y RÓTULOS DEL SIDEBAR DERECHO
        // =========================================================================
        JLabel titleLabel = new JLabel("BREAKBREAK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 40, 380, 40);
        sidebarPanel.add(titleLabel);

        JLabel scoreTitleLabel = new JLabel("PUNTUACIÓN", SwingConstants.CENTER);
        scoreTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        scoreTitleLabel.setForeground(Color.GRAY);
        scoreTitleLabel.setBounds(0, 100, 380, 20);
        sidebarPanel.add(scoreTitleLabel);

        scoreValueLabel = new JLabel("0000", SwingConstants.CENTER);
        scoreValueLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        scoreValueLabel.setForeground(Color.WHITE);
        scoreValueLabel.setBounds(0, 120, 380, 50);
        sidebarPanel.add(scoreValueLabel);

        JLabel livesTitleLabel = new JLabel("VIDAS", SwingConstants.CENTER);
        livesTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        livesTitleLabel.setForeground(Color.GRAY);
        livesTitleLabel.setBounds(0, 180, 380, 20);
        sidebarPanel.add(livesTitleLabel);

        // PANEL ESTÁTICO DE ANUNCIOS: Monitorea el feed de cápsulas de Attack on Titan
        bonoSidebarPanel = new BonoSidebarPanel();
        bonoSidebarPanel.setBounds(30, 260, 320, 70);
        sidebarPanel.add(bonoSidebarPanel);
        gameLoop.setBonoSidebarPanel(bonoSidebarPanel);

        // =========================================================================
        // 🎛️ BOTONERA MILITAR PREMIUM CON ENFOQUE FLUIDO DE HARDWARE
        // =========================================================================
        playButton = new MaterialButton("JUGAR");
        playButton.setBounds(50, 360, 280, 45);
        playButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        playButton.setBackground(new Color(40, 45, 50)); // Gris acorazado
        playButton.setForeground(Color.WHITE);
        playButton.setFocusable(false); // EVITA EL TRABADO: No roba el foco del juego
        sidebarPanel.add(playButton);

        resetButton = new MaterialButton("REINICIAR");
        resetButton.setBounds(50, 420, 280, 45);
        resetButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        resetButton.setBackground(new Color(50, 40, 40)); // Rojo óxido militar sutil
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusable(false); // EVITA EL TRABADO: No roba el foco del juego
        resetButton.setEnabled(false);
        sidebarPanel.add(resetButton);

        exitButton = new MaterialButton("SALIR");
        exitButton.setBounds(50, 480, 280, 45);
        exitButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        exitButton.setBackground(new Color(25, 25, 25)); // Carbón oscuro
        exitButton.setForeground(Color.LIGHT_GRAY);
        exitButton.setFocusable(false); // EVITA EL TRABADO: No roba el foco del juego
        sidebarPanel.add(exitButton);

        // =========================================================================
        // 🏁 LISTENERS OPERATIVOS BLINDADOS ANTI-CONGELAMIENTO
        // =========================================================================
        playButton.addActionListener(e -> {
            if (gameLoop != null) {
                if (gameLoop.getState() == main.com.breakbreak.enums.State.RUNNING) {
                    gameLoop.pause();
                    playButton.setText("REANUDAR");
                } else {
                    gameLoop.start();
                    playButton.setText("PAUSA");
                    resetButton.setEnabled(true);
                }
            }
            // Retorna síncronamente el control táctico al lienzo de juego
            gamePanel.requestFocusInWindow();
        });

        resetButton.addActionListener(e -> {
            if (gameLoop != null) {
                gameLoop.resetGame();
            }
            gamePanel.requestFocusInWindow();
        });

        exitButton.addActionListener(e -> System.exit(0));


        resetButton.addActionListener(e -> gameLoop.resetGame());
        exitButton.addActionListener(e -> System.exit(0));

        // Evento de foco de seguridad al abrir la ventana por primera vez
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE LA INTERFAZ DE EVENTOS (LISTENERS)
    // =========================================================================

    @Override
    public void onScoreChanged(int currentScore) {
        scoreValueLabel.setText(String.format("%04d", currentScore));
    }

    @Override
    public void onLifeLost(int currentLives) {
        playButton.setText("REANUDAR");
        gamePanel.requestFocusInWindow();
    }

    @Override
    public void onVictory(int finalScore) {
        playButton.setEnabled(false);
        WinPanel winPanel = new WinPanel(finalScore);
        gamePanel.add(winPanel, Integer.valueOf(15)); // Frente absoluto por encima de todo
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    @Override
    public void onGameOver(int finalScore) {
        gamePanel.setComponentZOrder(gameOverPanel, 0);
        gameOverPanel.setVisible(true);
        playButton.setEnabled(false);
        gamePanel.repaint();
    }

    @Override
    public void onReset() {
        scoreValueLabel.setText("0000");
        gameOverPanel.setVisible(false);

        playButton.setText("JUGAR"); // Restaura el letrero original nativo
        playButton.setEnabled(true);
        resetButton.setEnabled(false);

        if (bonoSidebarPanel != null) {
            bonoSidebarPanel.reset();
        }

        // Removemos pantallas de victoria de la ronda anterior si existieran
        for (Component c : gamePanel.getComponents()) {
            if (c instanceof WinPanel) {
                gamePanel.remove(c);
            }
        }

        ball.setVisible(true);
        paddle.setVisible(true);
        gamePanel.revalidate();
        gamePanel.repaint();

        // Solicita el foco para que el ratón y los clics respondan tras reiniciar
        gamePanel.requestFocusInWindow();
    }
}
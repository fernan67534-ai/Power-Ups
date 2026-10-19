package main.com.breakbreak.app;

import main.com.breakbreak.components.*;
import main.com.breakbreak.enums.State;
import main.com.breakbreak.util.GameEventListener;
import main.com.breakbreak.util.GameLoop;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickBreakApp extends JFrame implements GameEventListener {

    private JPanel rootPanel;
    private JLayeredPane gameArea;
    private JPanel gameElementsPanel;
    private GameOverPanel gameOverPanel;

    private BallComponent ball;
    private PaddleComponent paddle;
    private BorderComponent artDecoBorder;
    private BackgroundComponent background;
    private List<BlockComponent> blocks;
    private List<HeartComponent> hearts;
    private GameLoop gameLoop;
    private MaterialButton btnPlay;
    private MaterialButton btnRestart;
    private JLabel lblScoreVal;

    public BrickBreakApp() {
        setTitle("BreakBreak - Vertical Layout con Bordes Art Deco");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(23, 23, 28));
        rootPanel.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 55), 6));

        gameArea = new JLayeredPane();
        gameArea.setPreferredSize(new Dimension(460, 700));

        background = new BackgroundComponent(0, 0, 460, 700);
        gameArea.add(background, JLayeredPane.DEFAULT_LAYER);

        gameElementsPanel = new JPanel(null) {
            @Override
            public boolean isDoubleBuffered() {
                return true;
            }
        };
        gameElementsPanel.setBounds(0, 0, 460, 700);
        gameElementsPanel.setOpaque(false);

        blocks = new ArrayList<>();
        int blockWidth = 76;
        int blockHeight = 22;
        int startX = 31;
        int startY = 55;
        int gapX = 6;
        int gapY = 8;

        BlockComponent.BlockType[] types = BlockComponent.BlockType.values();
        Random random = new Random();
        BlockComponent[][] blockArray = new BlockComponent[5][5];

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int bx = startX + col * (blockWidth + gapX);
                int by = startY + row * (blockHeight + gapY);
                BlockComponent.BlockType randomType = types[random.nextInt(types.length)];
                BlockComponent block = new BlockComponent(bx, by, blockWidth, blockHeight, randomType);
                blocks.add(block);
                blockArray[row][col] = block;
                gameElementsPanel.add(block);
            }
        }

        ball = new BallComponent(219, 380, 35, 35);
        paddle = new PaddleComponent(162, 540, 160, 24);

        gameElementsPanel.add(ball);
        gameElementsPanel.add(paddle);

        artDecoBorder = new BorderComponent(new Rectangle(0, 0, 460, 700));
        gameElementsPanel.add(artDecoBorder);

        gameArea.add(gameElementsPanel, JLayeredPane.PALETTE_LAYER);

        gameOverPanel = new GameOverPanel();
        gameArea.add(gameOverPanel, JLayeredPane.MODAL_LAYER);

        rootPanel.add(gameArea, BorderLayout.CENTER);

        // Panel lateral derecho
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(300, 700));
        sidePanel.setBackground(new Color(23, 23, 28));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JLabel lblGameTitle = new JLabel("BREAKBREAK");
        lblGameTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGameTitle.setForeground(Color.WHITE);
        lblGameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(lblGameTitle);

        sidePanel.add(Box.createVerticalStrut(40));

        JLabel lblPuntuacion = new JLabel("PUNTUACIÓN");
        lblPuntuacion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPuntuacion.setForeground(Color.GRAY);
        lblPuntuacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(lblPuntuacion);

        sidePanel.add(Box.createVerticalStrut(5));

        lblScoreVal = new JLabel("0000");
        lblScoreVal.setFont(new Font("Segoe UI", Font.BOLD, 46));
        lblScoreVal.setForeground(Color.WHITE);
        lblScoreVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(lblScoreVal);

        sidePanel.add(Box.createVerticalStrut(30));

        JLabel lblVidas = new JLabel("VIDAS");
        lblVidas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVidas.setForeground(Color.GRAY);
        lblVidas.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(lblVidas);

        sidePanel.add(Box.createVerticalStrut(10));

        hearts = new ArrayList<>();
        JPanel livesContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        livesContainer.setOpaque(false);
        livesContainer.setMaximumSize(new Dimension(220, 40));

        HeartComponent h1 = new HeartComponent(0, 0, 32, 32, true);
        HeartComponent h2 = new HeartComponent(0, 0, 32, 32, true);
        HeartComponent h3 = new HeartComponent(0, 0, 32, 32, true);
        hearts.add(h1);
        hearts.add(h2);
        hearts.add(h3);

        livesContainer.add(h1);
        livesContainer.add(h2);
        livesContainer.add(h3);
        sidePanel.add(livesContainer);

        sidePanel.add(Box.createVerticalGlue());

        btnPlay = new MaterialButton("JUGAR");
        btnPlay.setMaximumSize(new Dimension(220, 42));
        btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnRestart = new MaterialButton("REINICIAR");
        btnRestart.setMaximumSize(new Dimension(220, 42));
        btnRestart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRestart.setEnabled(false);

        MaterialButton btnExit = new MaterialButton("SALIR");
        btnExit.setMaximumSize(new Dimension(220, 42));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));

        sidePanel.add(btnPlay);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(btnRestart);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(btnExit);

        rootPanel.add(sidePanel, BorderLayout.EAST);
        setContentPane(rootPanel);

        gameLoop = new GameLoop(gameElementsPanel, ball, paddle, artDecoBorder, blockArray, hearts);
        gameLoop.addListener(this);

        btnPlay.addActionListener(e -> {
            if (gameLoop.getState() == State.READY) {
                gameLoop.start();
                btnPlay.setText("PAUSAR");
            } else if (gameLoop.getState() == State.RUNNING) {
                gameLoop.pause();
                btnPlay.setText("CONTINUAR");
            } else if (gameLoop.getState() == State.PAUSED) {
                gameLoop.start();
                btnPlay.setText("PAUSAR");
            }
            gameElementsPanel.requestFocusInWindow();
        });

        btnRestart.addActionListener(e -> {
            gameLoop.resetGame();
            btnPlay.setText("JUGAR");
            btnPlay.setEnabled(true);
            btnRestart.setEnabled(false);
            gameElementsPanel.requestFocusInWindow();
        });

        gameElementsPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int newX = e.getX() - paddle.getWidth() / 2;
                Rectangle area = artDecoBorder.getInnerBounds();
                if (newX < area.x) newX = area.x;
                if (newX + paddle.getWidth() > area.x + area.width) {
                    newX = area.x + area.width - paddle.getWidth();
                }
                paddle.setLocation(newX, paddle.getY());
            }
        });
    }

    @Override
    public void onScoreChanged(int newScore) {
        lblScoreVal.setText(String.format("%04d", newScore));
    }

    @Override
    public void onLifeLost(int remainingLives) {
        btnPlay.setText("JUGAR");
    }

    @Override
    public void onGameOver(int finalScore) {
        btnPlay.setEnabled(false);
        btnRestart.setEnabled(true);
        gameOverPanel.showGameOver();
    }

    @Override
    public void onVictory(int finalScore) {
        btnPlay.setEnabled(false);
        btnRestart.setEnabled(true);
        gameOverPanel.showVictory(finalScore);
    }

    @Override
    public void onReset() {
        lblScoreVal.setText("0000");
        gameOverPanel.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BrickBreakApp().setVisible(true);
        });
    }
}
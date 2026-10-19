package main.com.breakbreak.app;

import main.com.breakbreak.components.*;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameComponentDemoFrame {

    private JFrame frame;

    public GameComponentDemoFrame() {
        frame = new JFrame("BreakBreak - Vertical Layout con Bordes Art Deco");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(780, 750);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(23, 23, 28));
        rootPanel.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 55), 6));

        JPanel gameArea = new JPanel(null);
        gameArea.setPreferredSize(new Dimension(460, 700));
        gameArea.setBackground(new Color(30, 30, 35));

        gameArea.add(new BallComponent(219, 380, 45, 45));
        gameArea.add(new PaddleComponent(162, 540, 160, 24));

        int blockWidth = 76;
        int blockHeight = 22;
        int startX = 31;
        int startY = 55;
        int gapX = 6;
        int gapY = 8;

        BlockComponent.BlockType[] types = BlockComponent.BlockType.values();
        Random random = new Random();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int bx = startX + col * (blockWidth + gapX);
                int by = startY + row * (blockHeight + gapY);

                BlockComponent.BlockType randomType = types[random.nextInt(types.length)];
                gameArea.add(new BlockComponent(bx, by, blockWidth, blockHeight, randomType));
            }
        }

        BorderComponent artDecoBorder = new BorderComponent(new Rectangle(0, 0, 460, 700));
        gameArea.add(artDecoBorder);

        BackgroundComponent bg = new BackgroundComponent(0, 0, 460, 700);
        gameArea.add(bg);

        rootPanel.add(gameArea, BorderLayout.CENTER);

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

        JLabel lblScoreVal = new JLabel("0000");
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

        JPanel livesContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        livesContainer.setOpaque(false);
        livesContainer.setMaximumSize(new Dimension(220, 40));

        livesContainer.add(new HeartComponent(0, 0, 32, 32, true));
        livesContainer.add(new HeartComponent(0, 0, 32, 32, true));
        livesContainer.add(new HeartComponent(0, 0, 32, 32, false));
        sidePanel.add(livesContainer);

        sidePanel.add(Box.createVerticalGlue());

        MaterialButton btnPlay = new MaterialButton("JUGAR");
        btnPlay.setMaximumSize(new Dimension(220, 42));
        btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);

        MaterialButton btnExit = new MaterialButton("SALIR");
        btnExit.setMaximumSize(new Dimension(220, 42));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> System.exit(0));

        sidePanel.add(btnPlay);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(btnExit);

        rootPanel.add(sidePanel, BorderLayout.EAST);
        frame.add(rootPanel);
    }

    public void showWindow() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameComponentDemoFrame demo = new GameComponentDemoFrame();
            demo.showWindow();
        });
    }
}
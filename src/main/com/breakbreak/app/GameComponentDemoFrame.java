package main.com.breakbreak.app;

import main.com.breakbreak.components.*;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameComponentDemoFrame extends JFrame {

    public GameComponentDemoFrame() {
        setTitle("BreakBreak - Laboratorio de Componentes Visuales");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Principal Absoluto (null layout)
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(20, 22, 24));
        setContentPane(mainPanel);

        // Panel contenedor interno del área de juego
        JPanel gameArea = new JPanel(null);
        gameArea.setBounds(0, 0, 800, 600);
        mainPanel.add(gameArea);

        // Instanciar el Borde Perimetral
        BorderComponent artDecoBorder = new BorderComponent(gameArea.getBounds());
        Rectangle area = artDecoBorder.getInnerBounds();

        // 1. INICIALIZACIÓN DE LA MATRIZ DE BLOQUES DE PRUEBA
        int blockWidth = 76;
        int blockHeight = 22;
        int startX = area.x + 31;
        int startY = area.y + 55;
        int gapX = 6;
        int gapY = 8;

        // Arreglo de colores String estándar que acepta tu BlockComponent (.jpg)
        String[] blockColors = {"red", "blue", "green", "orange", "cyan"};
        Random random = new Random();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int bx = startX + col * (blockWidth + gapX);
                int by = startY + row * (blockHeight + gapY);

                // Selección aleatoria limpia del color string sin usar un Enum inexistente
                String randomColor = blockColors[random.nextInt(blockColors.length)];
                BlockComponent block = new BlockComponent(bx, by, blockWidth, blockHeight, randomColor);
                gameArea.add(block);
            }
        }

        // 2. INICIALIZACIÓN DE ELEMENTOS DE JUEGO MÓVILES
        BallComponent ball = new BallComponent(area.x + area.width / 2 - 13, area.height - 120, 26, 26);
        PaddleComponent paddle = new PaddleComponent(area.x + area.width / 2 - 45, area.height - 40, 90, 20);
        gameArea.add(ball);
        gameArea.add(paddle);

        // 3. HUD DE CORAZONES (Usando el constructor real que acepta la posición y estado)
        for (int i = 0; i < 3; i++) {
            HeartComponent heart = new HeartComponent(area.x + 15 + (i * 28), area.y + 15, true);
            gameArea.add(heart, 0); // Insertar al frente
        }

        // 4. ACOPLAR BORDES Y FONDO DE LA INTERFAZ
        gameArea.add(artDecoBorder);

        // Constructor corregido de tu BackgroundComponent (recibe ancho y alto)
        BackgroundComponent background = new BackgroundComponent(800, 600);
        gameArea.add(background);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameComponentDemoFrame().setVisible(true);
        });
    }
}

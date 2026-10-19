package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {
    private JLabel lblTitle;
    private JLabel lblSubtitle;

    public GameOverPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(23, 23, 28, 230)); // Fondo semitransparente elegante
        setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2)); // Borde dorado Art Déco
        setBounds(80, 240, 300, 200);

        add(Box.createVerticalStrut(30));
        lblTitle = new JLabel("GAME OVER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(212, 175, 55));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblTitle);

        add(Box.createVerticalStrut(15));
        lblSubtitle = new JLabel("Te has quedado sin vidas");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.LIGHT_GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblSubtitle);

        setVisible(false); // Oculto por defecto
    }

    public void showVictory(int score) {
        lblTitle.setText("¡VICTORIA!");
        lblSubtitle.setText("Puntuación final: " + String.format("%04d", score));
        setVisible(true);
    }

    public void showGameOver() {
        lblTitle.setText("GAME OVER");
        lblSubtitle.setText("Te has quedado sin vidas");
        setVisible(true);
    }
}
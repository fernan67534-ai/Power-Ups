package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class WinPanel extends JComponent {
    private final int scoreFinal;

    public WinPanel(int scoreFinal) {
        this.scoreFinal = scoreFinal;
        setSize(480, 640);
        setBounds(0, 0, 480, 640);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Manto oscuro absoluto de fondo
        g2d.setColor(new Color(15, 15, 15, 245));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Borde imperial dorado cepillado
        g2d.setColor(new Color(180, 140, 45));
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawRect(20, 20, getWidth() - 40, getHeight() - 40);

        // Letrero Carmesí Militar
        g2d.setFont(new Font("Monospaced", Font.BOLD, 38));
        g2d.setColor(new Color(190, 30, 45));
        String txtVictoria = "VICTORIA";
        int widthTxt = g2d.getFontMetrics().stringWidth(txtVictoria);
        g2d.drawString(txtVictoria, (getWidth() - widthTxt) / 2, 200);

        g2d.setFont(new Font("Serif", Font.ITALIC, 16));
        g2d.setColor(Color.LIGHT_GRAY);
        String txtSub = "Has cruzado más allá de las murallas";
        int widthSub = g2d.getFontMetrics().stringWidth(txtSub);
        g2d.drawString(txtSub, (getWidth() - widthSub) / 2, 240);

        // Despliegue de puntuación de honor
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g2d.setColor(Color.WHITE);
        g2d.drawString("PUNTUACIÓN DE HONOR:", 120, 360);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 44));
        g2d.setColor(new Color(212, 175, 55));
        String puntosStr = String.format("%04d", scoreFinal);
        int widthPts = g2d.getFontMetrics().stringWidth(puntosStr);
        g2d.drawString(puntosStr, (getWidth() - widthPts) / 2, 420);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.setColor(Color.GRAY);
        String txtReiniciar = "[ Presiona REINICIAR en el panel de control ]";
        int widthRe = g2d.getFontMetrics().stringWidth(txtReiniciar);
        g2d.drawString(txtReiniciar, (getWidth() - widthRe) / 2, 560);

        g2d.dispose();
    }
}

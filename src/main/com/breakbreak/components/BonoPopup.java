package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class BonoPopup extends JComponent {
    private final String titulo;
    private final String beneficio;
    private float alpha = 1.0f;
    private final Timer fadeTimer;

    public BonoPopup(String titulo, String beneficio, int x, int y) {
        this.titulo = titulo;
        this.beneficio = beneficio;

        // Posición y dimensiones en el lienzo izquierdo
        setBounds(x, y, 220, 50);
        setOpaque(false);

        // Desvanecimiento suave en 2 segundos (60 pasos de opacidad)
        this.fadeTimer = new Timer(33, e -> {
            alpha -= 0.02f;
            if (alpha <= 0) {
                alpha = 0;
                ((Timer)e.getSource()).stop();
                if (getParent() != null) {
                    Container parent = getParent();
                    parent.remove(this);
                    parent.repaint();
                }
            } else {
                repaint();
            }
        });
        this.fadeTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Aplicamos el canal alfa dinámico para el efecto humo/desvanecimiento
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo oscuro translúcido estilo Paradis
        g2d.setColor(new Color(25, 25, 25, 220));
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        // Borde dorado metálico sutil
        g2d.setColor(new Color(212, 175, 55));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        // Renderizado de Textos Informativos
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString(titulo, 12, 20);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString(beneficio, 12, 38);

        g2d.dispose();
    }
}

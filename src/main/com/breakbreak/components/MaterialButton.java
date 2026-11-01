package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaterialButton extends JButton {
    private boolean mouseEncima = false;

    public MaterialButton(String texto) {
        super(tituloLimpiar(texto));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listener dinámico de interacción cromática en caliente
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEncima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseEncima = false;
                repaint();
            }
        });
    }

    private static String tituloLimpiar(String t) {
        return t != null ? t.toUpperCase() : "";
    }

    @Override
    public void setText(String text) {
        super.setText(tituloLimpiar(text));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cambia el brillo de Paradis dinámicamente si el cursor está encima del botón
        if (isEnabled()) {
            if (mouseEncima) {
                g2d.setColor(getBackground().brighter());
            } else {
                g2d.setColor(getBackground());
            }
        } else {
            g2d.setColor(new Color(40, 40, 40)); // Color de desactivado uniforme
        }

        // Renderizado del cuerpo del botón redondeado militar
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        // Marco o contorno elegante de la interfaz de usuario de Titan
        if (isEnabled()) {
            g2d.setColor(mouseEncima ? new Color(212, 175, 55) : new Color(120, 125, 130));
        } else {
            g2d.setColor(new Color(60, 60, 60));
        }
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        // Dibujado del texto centralizado
        g2d.setFont(getFont());
        if (isEnabled()) {
            g2d.setColor(mouseEncima ? Color.WHITE : getForeground());
        } else {
            g2d.setColor(Color.DARK_GRAY);
        }

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(getText(), x, y);

        g2d.dispose();
    }
}

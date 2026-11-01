package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class BonoSidebarPanel extends JPanel {
    private String tituloBono = "NINGUNO ACTIVO";
    private String descripcionBono = "Captura una cápsula en batalla.";
    private Color colorBorde = new Color(80, 80, 80);

    public BonoSidebarPanel() {
        // Dimensiones para encajar de manera exacta en tu Sidebar de 380x640
        setPreferredSize(new Dimension(320, 70));
        setMinimumSize(new Dimension(320, 70));
        setMaximumSize(new Dimension(320, 70));
        setOpaque(false);
    }

    public void setBonoActivo(String titulo, String descripcion, Color colorTipo) {
        this.tituloBono = titulo.toUpperCase();
        this.descripcionBono = descripcion;
        this.colorBorde = colorTipo;
        revalidate();
        repaint();
    }

    public void reset() {
        this.tituloBono = "NINGUNO ACTIVO";
        this.descripcionBono = "Captura una cápsula en batalla.";
        this.colorBorde = new Color(80, 80, 80);
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo oscuro del recubrimiento de la interfaz
        g2d.setColor(new Color(25, 25, 25));
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        // Marco militar dinámico (Cambia a Rojo, Azul, Verde, Morado o Gris)
        g2d.setColor(colorBorde);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        // Renderizado de Textos HUD
        g2d.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2d.setColor(Color.WHITE);
        g2d.drawString("⚡ ESTADO: " + tituloBono, 15, 26);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString(descripcionBono, 15, 46);

        g2d.dispose();
    }
}

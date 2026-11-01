package main.com.breakbreak.components;

import javax.swing.ImageIcon;
import java.awt.*;

public class BolaFuego extends GameComponent {
    private final double vx;
    private final double vy;
    private double currentX;
    private double currentY;
    private final ImageIcon gifIcon;

    public BolaFuego(int startX, int startY, double vx, double vy) {
        super(startX, startY, 32, 32);
        this.currentX = startX;
        this.currentY = startY;
        this.vx = vx;
        this.vy = vy;

        // 1. Intento Ordinario por Classpath virtual
        java.net.URL url = getClass().getResource("/fireball.gif");
        if (url == null) {
            url = getClass().getResource("/resources/fireball.gif");
        }

        if (url != null) {
            this.gifIcon = new ImageIcon(url);
        } else {
            // 2. SOLUCIÓN DEFINTIVA INMUNE: Carga por Descriptor de Archivo Físico en Disco
            java.io.File archivoFisico = new java.io.File("src/main/resources/fireball.gif");
            if (!archivoFisico.exists()) {
                archivoFisico = new java.io.File("src/main/resources/images/fireball.gif");
            }

            if (archivoFisico.exists()) {
                this.gifIcon = new ImageIcon(archivoFisico.getAbsolutePath());
            } else {
                System.err.println("⚠️ ALERTA: No se encontró el recurso gráfico: fireball.gif");
                this.gifIcon = null;
            }
        }
    }



    /**
     * Desplaza el proyectil en su trayectoria balística en cada tick a 60 FPS.
     */
    public void actualizarPosicion() {
        this.currentX += vx;
        this.currentY += vy;
        setLocation((int) Math.round(currentX), (int) Math.round(currentY));
    }

    @Override
    public ImageIcon getImage() {
        return gifIcon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gifIcon != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            // Mantiene el pixel-art completamente nítido y libre de desenfoque al moverse
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(gifIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}

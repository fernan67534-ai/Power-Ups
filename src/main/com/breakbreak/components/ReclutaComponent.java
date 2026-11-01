package main.com.breakbreak.components;

import javax.swing.ImageIcon;
import java.awt.*;

public class ReclutaComponent extends GameComponent {
    private double currentX;
    private double currentY;
    private double vx;
    private double vy;
    private final ImageIcon personajeIcon;

    /**
     * @param tipoPersonaje "mikasa" o "armin" para buscar el archivo correcto
     */
    public ReclutaComponent(int startX, int startY, String tipoPersonaje, double vx, double vy) {
        super(startX, startY, 38, 44);
        this.currentX = startX;
        this.currentY = startY;
        this.vx = vx;
        this.vy = vy;

        String nombreArchivo = tipoPersonaje.toLowerCase() + ".gif";

        // 1. Intento por Classpath virtual ordinario
        java.net.URL url = getClass().getResource("/" + nombreArchivo);
        if (url == null) {
            url = getClass().getResource("/resources/" + nombreArchivo);
        }

        if (url != null) {
            this.personajeIcon = new ImageIcon(url);
        } else {
            // 2. SOLUCIÓN DEFINTIVA INMUNE: Carga por Descriptor de Archivo Físico en Disco
            java.io.File archivoFisico = new java.io.File("src/main/resources/" + nombreArchivo);
            if (!archivoFisico.exists()) {
                archivoFisico = new java.io.File("src/main/resources/images/" + nombreArchivo);
            }

            if (archivoFisico.exists()) {
                this.personajeIcon = new ImageIcon(archivoFisico.getAbsolutePath());
            } else {
                System.err.println("⚠️ ALERTA: No se encontró el recurso gráfico para: " + tipoPersonaje);
                this.personajeIcon = null;
            }
        }
    }

    public void actualizarPosicion() {
        this.currentX += vx;
        this.currentY += vy;
        setLocation((int) Math.round(currentX), (int) Math.round(currentY));
    }

    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }
    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }

    @Override
    public ImageIcon getImage() {
        return personajeIcon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (personajeIcon != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            // Filtro NEAREST_NEIGHBOR para que el pixel-art de Mikasa y Armin se vea súper nítido al moverse
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(personajeIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}

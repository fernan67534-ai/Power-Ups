package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;

public class HeartComponent extends GameComponent {
    private String imageKey;

    public HeartComponent(int x, int y, boolean active) {
        super(x, y, 24, 24); // Tamaño del icono en la Sidebar
        setHeartState(active);
    }

    /**
     * Alterna la textura del corazón de forma dinámica en tiempo real
     *
     * @param active true para corazón lleno (rojo), false para corazón vacío (negro)
     */
    public void setHeartState(boolean active) {
        this.imageKey = active ? "heart-full" : "heart-empty";

        // Sincronizar las claves binarias con tus recursos físicos reales del disco
        if (active) {
            setImage("heart-full", "heart.png");
        } else {
            setImage("heart-empty", "heart-empty.png");
        }

        // Forzar a Swing a redibujar este JComponent específico en el EDT
        repaint();
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(imageKey);
    }
}

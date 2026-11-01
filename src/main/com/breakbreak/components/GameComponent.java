package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.*;
import java.awt.*;

public abstract class GameComponent extends JComponent {

    public GameComponent(int x, int y, int width, int height) {
        setBounds(x, y, width, height);
    }

    public void setImage(String name, String path) {
        // Extraer el nombre base del archivo para sincronizarlo con el pre-loader de la caché
        String filename = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        ImageIcon icon = ImageCache.getInstance().getImage(filename);
        if (icon != null) {
            ImageCache.getInstance().addImage(name, icon);
        } else {
            System.err.println("Error cargando recurso en componente: " + path);
        }
    }

    public abstract ImageIcon getImage();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon icon = getImage();
        if (icon != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }
}

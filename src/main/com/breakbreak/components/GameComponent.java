package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public abstract class GameComponent extends JComponent {

    public GameComponent() {}

    public GameComponent(int x, int y, int width, int height) {
        setBounds(x, y, width, height);
    }

    public void setImage(String name, String filename) {
        try {
            // Carga robusta compatible con IDEs y compilaciones de Java
            URL imgUrl = getClass().getResource("/" + filename);
            if (imgUrl == null) {
                imgUrl = getClass().getResource("/resources/" + filename);
            }
            if (imgUrl == null) {
                imgUrl = getClass().getClassLoader().getResource(filename);
            }

            if (imgUrl != null) {
                ImageIcon image = new ImageIcon(imgUrl);
                ImageCache.getInstance().addImage(name, image);
            } else {
                // Respaldo por archivo local si estuviera en desarrollo directo
                java.io.File imgFile = new java.io.File("src/main/resources/" + filename);
                if (imgFile.exists()) {
                    ImageIcon image = new ImageIcon(imgFile.getAbsolutePath());
                    ImageCache.getInstance().addImage(name, image);
                }
            }
        } catch (Exception e) {
            // Ignorado
        }
    }

    public abstract ImageIcon getImage();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        ImageIcon icon = getImage();
        if (icon != null && icon.getImage() != null) {
            g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
        g2.dispose();
    }
}
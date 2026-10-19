package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;

public class BackgroundComponent extends GameComponent {
    private static final String IMAGE_NAME = "background";

    public BackgroundComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImage(IMAGE_NAME, "background.gif");
        setOpaque(false);
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(IMAGE_NAME);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        ImageIcon icon = getImage();
        if (icon != null && icon.getImage() != null) {
            // Dibuja el fondo GIF
            g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);

            // Capa de contraste sutil para que los bloques resalten perfectamente sobre el cielo
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
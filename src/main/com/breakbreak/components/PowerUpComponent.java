package main.com.breakbreak.components;

import main.com.breakbreak.enums.PowerUpType;
import main.com.breakbreak.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PowerUpComponent extends GameComponent {

    // CORRECCIÓN CRÍTICA: Dimensiones reales calculadas a partir de tu archivo de 2048x640
    public static final int FRAME_WIDTH = 128;
    public static final int FRAME_HEIGHT = 64;

    // Dimensiones en pantalla (Ajustadas a 44x22 para mantener la escala ideal en el lienzo de juego)
    public static final int RENDER_WIDTH = 44;
    public static final int RENDER_HEIGHT = 22;

    public static final int FALL_SPEED = 2;

    private final PowerUpType type;
    private final ImageIcon[] frames = new ImageIcon[8];
    private final Timer animationTimer;
    private int frameIndex = 0;

    public PowerUpComponent(PowerUpType type, int x, int y) {
        // Inicializa el JComponent con el tamaño real de renderizado para evitar colisiones desfasadas
        super(x, y, RENDER_WIDTH, RENDER_HEIGHT);
        this.type = type;

        if (ImageCache.getInstance().getImage("powerups") == null) {
            setImage("powerups", "powerups.png");
        }

        extractFrames();

        this.animationTimer = new Timer(100, e -> {
            frameIndex = (frameIndex + 1) % 8;
            repaint();
        });
    }

    private void extractFrames() {
        ImageIcon sheet = ImageCache.getInstance().getImage("powerups");
        if (sheet == null) return;

        int row = type.getSpriteIndex() / 2;
        int firstCol = (type.getSpriteIndex() % 2) * 8;

        for (int f = 0; f < 8; f++) {
            // Multiplicación exacta basada en tus 128x64 píxeles reales de disco
            int srcX = (firstCol + f) * FRAME_WIDTH;
            int srcY = row * FRAME_HEIGHT;

            BufferedImage img = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();

            // FILTRO DE ALTA DEFINICIÓN: Mantiene las píldoras Pixel-Art completamente definidas
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(sheet.getImage(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT, srcX, srcY, srcX + FRAME_WIDTH, srcY + FRAME_HEIGHT, null);
            g2.dispose();

            frames[f] = new ImageIcon(img);
        }
    }

    public PowerUpType getType() { return type; }

    public void updatePosition() {
        setLocation(getX(), getY() + FALL_SPEED);
    }

    @Override
    public ImageIcon getImage() {
        return frames[frameIndex];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon icon = getImage();
        if (icon != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Escala los 128x64 píxeles del frame al tamaño 44x22 de la pantalla de forma súper nítida
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }

    @Override public void addNotify() { super.addNotify(); if (animationTimer != null) animationTimer.start(); }
    @Override public void removeNotify() { if (animationTimer != null) animationTimer.stop(); super.removeNotify(); }
}
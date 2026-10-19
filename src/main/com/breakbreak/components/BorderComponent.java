package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;
import java.awt.*;

public class BorderComponent extends GameComponent {

    private final ImageIcon topLeft;
    private final ImageIcon top;
    private final ImageIcon topRight;
    private final ImageIcon right;
    private final ImageIcon bottomRight;
    private final ImageIcon bottom;
    private final ImageIcon bottomLeft;
    private final ImageIcon left;
    private final int thickness;

    public BorderComponent(Rectangle bounds) {
        super(bounds.x, bounds.y, bounds.width, bounds.height);
        initImages();
        this.topLeft = requireIcon("top-left");
        this.top = requireIcon("top");
        this.topRight = requireIcon("top-right");
        this.right = requireIcon("right");
        this.bottomRight = requireIcon("bottom-right");
        this.bottom = requireIcon("bottom");
        this.bottomLeft = requireIcon("bottom-left");
        this.left = requireIcon("left");
        this.thickness = 25;
        setOpaque(false);
        setFocusable(false);
    }

    private void initImages() {
        if (ImageCache.getInstance().getImage("top-left") == null) {
            setImage("top-left", "corner-tl.png");
        }
        if (ImageCache.getInstance().getImage("top") == null) {
            setImage("top", "edge-t.png");
        }
        if (ImageCache.getInstance().getImage("top-right") == null) {
            setImage("top-right", "corner-tr.png");
        }
        if (ImageCache.getInstance().getImage("right") == null) {
            setImage("right", "edge-r.png");
        }
        if (ImageCache.getInstance().getImage("bottom-right") == null) {
            setImage("bottom-right", "corner-br.png");
        }
        if (ImageCache.getInstance().getImage("bottom") == null) {
            setImage("bottom", "edge-b.png");
        }
        if (ImageCache.getInstance().getImage("bottom-left") == null) {
            setImage("bottom-left", "corner-bl.png");
        }
        if (ImageCache.getInstance().getImage("left") == null) {
            setImage("left", "edge-l.png");
        }
    }

    private static ImageIcon requireIcon(String name) {
        return ImageCache.getInstance().getImage(name);
    }

    public Rectangle getInnerBounds() {
        return new Rectangle(
                getX() + thickness,
                getY() + thickness,
                getWidth() - thickness * 2,
                getHeight() - thickness * 2
        );
    }

    @Override
    public ImageIcon getImage() {
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );
            int width = getWidth();
            int height = getHeight();
            if (width < thickness * 2 || height < thickness * 2) {
                return;
            }
            drawIcon(g2, topLeft, 0, 0);
            drawIcon(g2, topRight, width - 80, 0);
            drawIcon(g2, bottomLeft, 0, height - 80);
            drawIcon(g2, bottomRight, width - 80, height - 80);

            drawTiles(g2, top, 80, 0, width - 160, thickness);
            drawTiles(g2, bottom, 80, height - thickness, width - 160, thickness);
            drawTiles(g2, left, 0, 80, thickness, height - 160);
            drawTiles(g2, right, width - thickness, 80, thickness, height - 160);
        } catch (Exception e) {
            g2.dispose();
        }
        g2.dispose();
    }

    private void drawIcon(Graphics2D g2, ImageIcon icon, int x, int y) {
        if (icon != null && icon.getImage() != null) {
            g2.drawImage(icon.getImage(), x, y, 80, 80, this);
        }
    }

    private void drawTiles(Graphics2D g2, ImageIcon icon, int x, int y, int areaWidth, int areaHeight) {
        if (areaWidth <= 0 || areaHeight <= 0 || icon == null) {
            return;
        }
        int tileWidth = icon.getIconWidth();
        int tileHeight = icon.getIconHeight();
        if (tileWidth <= 0 || tileHeight <= 0) return;

        Graphics2D edge = (Graphics2D) g2.create();
        edge.clipRect(x, y, areaWidth, areaHeight);
        try {
            if (tileWidth < tileHeight) {
                drawVerticalTiles(edge, icon, x, y, areaWidth, areaHeight, tileWidth, tileHeight);
            } else {
                drawHorizontalTiles(edge, icon, x, y, areaWidth, areaHeight, tileWidth, tileHeight);
            }
        } catch (Exception e) {
            edge.dispose();
        }
        edge.dispose();
    }

    private void drawHorizontalTiles(Graphics2D edge, ImageIcon icon, int x, int y, int areaWidth, int areaHeight,
                                     int tileWidth, int tileHeight) {
        for (int px = x; px < x + areaWidth; px += tileWidth) {
            edge.drawImage(
                    icon.getImage(),
                    px, y,
                    px + tileWidth, y + areaHeight,
                    0, 0, tileWidth, tileHeight,
                    this
            );
        }
    }

    private void drawVerticalTiles(Graphics2D edge, ImageIcon icon, int x, int y, int areaWidth, int areaHeight,
                                   int tileWidth, int tileHeight) {
        for (int py = y; py < y + areaHeight; py += tileHeight) {
            edge.drawImage(
                    icon.getImage(),
                    x, py,
                    x + areaWidth, py + tileHeight,
                    0, 0, tileWidth, tileHeight,
                    this
            );
        }
    }
}
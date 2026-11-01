package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;
import java.awt.*;

public class BorderComponent extends GameComponent {

    private final ImageIcon topLeft, top, topRight, right, bottomRight, bottom, bottomLeft, left;
    private final int thickness = 25;

    public BorderComponent(Rectangle bounds) {
        super(bounds.x, bounds.y, bounds.width, bounds.height);
        this.topLeft = ImageCache.getInstance().getImage("corner-tl.png");
        this.top = ImageCache.getInstance().getImage("edge-t.png");
        this.topRight = ImageCache.getInstance().getImage("corner-tr.png");
        this.right = ImageCache.getInstance().getImage("edge-r.png");
        this.bottomRight = ImageCache.getInstance().getImage("corner-br.png");
        this.bottom = ImageCache.getInstance().getImage("edge-b.png");
        this.bottomLeft = ImageCache.getInstance().getImage("corner-bl.png");
        this.left = ImageCache.getInstance().getImage("edge-l.png");
        setOpaque(false);
        setFocusable(false);
    }

    public Rectangle getInnerBounds() {
        return new Rectangle(getX() + thickness, getY() + thickness, getWidth() - thickness * 2, getHeight() - thickness * 2);
    }

    @Override
    public ImageIcon getImage() { return null; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int w = getWidth();
            int h = getHeight();

            if (w < thickness * 2 || h < thickness * 2) return;

            if (topLeft != null) g2.drawImage(topLeft.getImage(), 0, 0, 80, 80, this);
            if (topRight != null) g2.drawImage(topRight.getImage(), w - 80, 0, 80, 80, this);
            if (bottomLeft != null) g2.drawImage(bottomLeft.getImage(), 0, h - 80, 80, 80, this);
            if (bottomRight != null) g2.drawImage(bottomRight.getImage(), w - 80, h - 80, 80, 80, this);

            if (top != null && bottom != null) {
                int tileW = top.getIconWidth() > 0 ? top.getIconWidth() : 32;
                for (int x = 80; x < w - 80; x += tileW) {
                    g2.drawImage(top.getImage(), x, 0, tileW, thickness, this);
                    g2.drawImage(bottom.getImage(), x, h - thickness, tileW, thickness, this);
                }
            }
            if (left != null && right != null) {
                int tileH = left.getIconHeight() > 0 ? left.getIconHeight() : 32;
                for (int y = 80; y < h - 80; y += tileH) {
                    g2.drawImage(left.getImage(), 0, y, thickness, tileH, this);
                    g2.drawImage(right.getImage(), w - thickness, y, thickness, tileH, this);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}

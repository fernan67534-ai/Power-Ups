package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;

public class BallComponent extends GameComponent {
    private static final String IMAGE_NAME = "ball";
    private int vx;
    private int vy;

    public BallComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImage(IMAGE_NAME, "ball.gif");
    }

    public int getVx() { return vx; }
    public void setVx(int vx) { this.vx = vx; }
    public int getVy() { return vy; }
    public void setVy(int vy) { this.vy = vy; }

    public void setY(int y) {
        setLocation(getX(), y);
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(IMAGE_NAME);
    }
}
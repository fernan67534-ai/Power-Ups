package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;

public class BallComponent extends GameComponent {
    private static final String IMAGE_NAME = "ball";
    public static final int BALL_SPEED_X = 4;
    public static final int BALL_SPEED_Y = 4;

    private double xPos;
    private double yPos;
    private int vx = BALL_SPEED_X;
    private int vy = -BALL_SPEED_Y;

    public BallComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.xPos = x;
        this.yPos = y;
        // Ajustado al formato .gif que tienes en tu carpeta de recursos
        setImage(IMAGE_NAME, "/ball.gif");
    }

    public int getVx() { return vx; }
    public void setVx(int vx) { this.vx = vx; }
    public int getVy() { return vy; }
    public void setVy(int vy) { this.vy = vy; }
    public double getXPos() { return xPos; }
    public double getYPos() { return yPos; }

    public void setX(int x) {
        this.xPos = x;
        setLocation((int)Math.round(xPos), (int)Math.round(yPos));
    }

    public void setY(int y) {
        this.yPos = y;
        setLocation((int)Math.round(xPos), (int)Math.round(yPos));
    }

    public void setPosition(double x, double y) {
        this.xPos = x;
        this.yPos = y;
        setLocation((int)Math.round(xPos), (int)Math.round(yPos));
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(IMAGE_NAME);
    }
}

package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class PaddleComponent extends GameComponent {
    private static final String IMAGE_NAME = "paddle";

    public PaddleComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
        // Ajustado al formato .jpg que tienes en tu carpeta de recursos
        setImage(IMAGE_NAME, "/paddle.jpg");
    }

    public boolean resolveCollision(BallComponent ball) {
        Rectangle ballBounds = ball.getBounds();
        if (!ballBounds.intersects(getBounds())) return false;

        int paddleX = getX();
        int paddleWidth = getWidth();
        int ballCenterX = ballBounds.x + ballBounds.width / 2;

        int relativeHit = ballCenterX - paddleX;
        int section = Math.clamp(relativeHit * 4 / paddleWidth, 0, 3);

        switch (section) {
            case 0 -> { ball.setVx(-5); ball.setVy(-3); }
            case 1 -> { ball.setVx(-3); ball.setVy(-5); }
            case 2 -> { ball.setVx(3); ball.setVy(-5); }
            case 3 -> { ball.setVx(5); ball.setVy(-3); }
        }
        ball.setY(getY() - ball.getHeight());
        return true;
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(IMAGE_NAME);
    }
}

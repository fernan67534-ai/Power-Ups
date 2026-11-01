package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;

public class BlockComponent extends GameComponent {
    private String imageKey;
    private String currentColor;
    private int health;
    private final int pointsValue;
    private boolean destroyed = false;

    public BlockComponent(int x, int y, int width, int height, String colorType) {
        super(x, y, width, height);
        this.currentColor = colorType.toLowerCase();
        this.imageKey = "block-" + this.currentColor;
        setImage(imageKey, "block-" + this.currentColor + ".jpg");

        // NUEVA SECUENCIA DE RESISTENCIA EXCLUSIVA CON TUS RECURSOS REALES
        switch (this.currentColor) {
            case "blue" -> {
                this.health = 3;
                this.pointsValue = 300;
            }
            case "orange" -> {
                this.health = 2;
                this.pointsValue = 200;
            }
            case "yellow" -> {
                this.health = 1;
                this.pointsValue = 100;
            }
            default -> {
                // aqua, cyan, green actúan directamente como nivel 1 (se rompen de un golpe)
                this.health = 1;
                this.pointsValue = 100;
            }
        }
    }

    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }
    public int getPointsValue() { return pointsValue; }
    public String getColorKey() { return currentColor; }

    /**
     * Ciclo de daño consistente: Azul (3) -> Naranja (2) -> Amarillo (1) -> Destrucción.
     */
    public boolean takeDamage() {
        if (destroyed) return false;

        this.health--;

        if (this.health <= 0) {
            this.destroyed = true;
            setVisible(false);
            return true;
        } else {
            // Evolución cromática controlada a partir de tus archivos físicos
            if (this.currentColor.equals("blue")) {
                this.currentColor = "orange";
                this.imageKey = "block-orange";
                setImage(imageKey, "block-orange.jpg"); // Sincronización explícita con el componente base
            } else if (this.currentColor.equals("orange")) {
                this.currentColor = "yellow";
                this.imageKey = "block-yellow";
                setImage(imageKey, "block-yellow.jpg"); // Sincronización explícita con el componente base
            }

            repaint(); // Forzar actualización visual en el EDT de Swing
            return false;
        }
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(imageKey);
    }
}

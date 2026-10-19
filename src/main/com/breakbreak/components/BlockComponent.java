package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;

public class BlockComponent extends GameComponent {

    public enum BlockType {
        AQUA("block-aqua", "block-aqua.jpg"),
        BLUE("block-blue", "block-blue.jpg"),
        CYAN("block-cyan", "block-cyan.jpg"),
        GREEN("block-green", "block-green.jpg"),
        ORANGE("block-orange", "block-orange.jpg"),
        YELLOW("block-yellow", "block-yellow.jpg");

        final String key;
        final String path;

        BlockType(String key, String path) {
            this.key = key;
            this.path = path;
        }
    }

    private final BlockType type;

    public BlockComponent(int x, int y, int width, int height, BlockType type) {
        super(x, y, width, height);
        this.type = type;
        setImage(type.key, type.path);
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(type.key);
    }
}
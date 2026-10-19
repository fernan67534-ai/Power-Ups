package main.com.breakbreak.util;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static ImageCache instance;
    private final Map<String, ImageIcon> cache;

    private ImageCache() {
        cache = new HashMap<>();
    }

    public static synchronized ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    public void addImage(String name, ImageIcon image) {
        cache.put(name, image);
    }

    public ImageIcon getImage(String name) {
        return cache.get(name);
    }
}
package main.com.breakbreak.util;

import javax.swing.ImageIcon;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static ImageCache instance;
    private final Map<String, ImageIcon> cache = new HashMap<>();

    private ImageCache() {
        preloadAllResources();
    }

    public static synchronized ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    public void addImage(String name, ImageIcon image) {
        if (name != null && image != null) {
            cache.put(name, image);
        }
    }

    public ImageIcon getImage(String name) {
        if (name == null) return null;
        ImageIcon icon = cache.get(name);
        if (icon == null) icon = cache.get(name.toLowerCase());
        if (icon == null) icon = cache.get(name.toUpperCase());
        if (icon == null) icon = cache.get(name.replace("_", "-"));
        if (icon == null) icon = cache.get(name.replace("-", "_"));
        return icon;
    }

    private void preloadAllResources() {
        String[] files = {
                "ball.gif",
                "block-aqua.jpg", "block-blue.jpg", "block-cyan.jpg",
                "block-green.jpg", "block-orange.jpg", "block-yellow.jpg",
                "corner-bl.png", "corner-br.png", "corner-tl.png", "corner-tr.png",
                "edge-b.png", "edge-l.png", "edge-r.png", "edge-t.png",
                "heart.png", "heart-empty.png", "paddle.jpg", "powerups.png"
        };

        // Lista de rutas físicas en disco donde IntelliJ aloja los recursos del proyecto
        String[] pathsToTest = {
                "src/main/resources/",
                "resources/",
                "src/resources/",
                ""
        };

        for (String filename : files) {
            ImageIcon loadedIcon = null;

            // Intento 1: Carga nativa portable mediante Classpath
            try {
                URL resourceUrl = getClass().getResource("/" + filename);
                if (resourceUrl == null) resourceUrl = getClass().getResource("/resources/" + filename);
                if (resourceUrl != null) {
                    loadedIcon = new ImageIcon(resourceUrl);
                }
            } catch (Exception ignored) {}

            // Intento 2 (Respaldo definitivo): Búsqueda secuencial física en el sistema de archivos
            if (loadedIcon == null || loadedIcon.getIconWidth() == -1) {
                for (String basePath : pathsToTest) {
                    File file = new File(basePath + filename);
                    if (file.exists() && file.isFile()) {
                        loadedIcon = new ImageIcon(file.getAbsolutePath());
                        if (loadedIcon.getIconWidth() != -1) {
                            break; // Encontrado y cargado con éxito
                        }
                    }
                }
            }

            // Registrar en el mapa indexado con múltiples variantes de llaves para evitar fallas
            if (loadedIcon != null && loadedIcon.getIconWidth() != -1) {
                String nameWithoutExt = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;
                cache.put(filename, loadedIcon);
                cache.put(nameWithoutExt, loadedIcon);
                cache.put(nameWithoutExt.toLowerCase(), loadedIcon);
                cache.put(nameWithoutExt.toUpperCase(), loadedIcon);
                cache.put(nameWithoutExt.replace("-", "_"), loadedIcon);
            } else {
                System.err.println("ALERTA CRÍTICA: No se pudo localizar el archivo gráfico: " + filename);
            }
        }
    }

    public void setImage(String name, String path) {
        // Redirige directamente al motor centralizado para evitar errores individuales por diagonal
        String filename = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        ImageIcon icon = getImage(filename);
        if (icon != null) {
            cache.put(name, icon);
        }
    }
}

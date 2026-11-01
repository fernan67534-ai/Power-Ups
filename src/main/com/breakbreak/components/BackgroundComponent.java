package main.com.breakbreak.components;

import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackgroundComponent extends GameComponent {
    private int frameActual;
    private long ultimoTiempoFrame;
    private final int delayPorFrameMs; // Controla la velocidad de la animación (100ms = fluido)

    // Lista dinámica para almacenar los fotogramas cargados con éxito desde el disco
    private final List<ImageIcon> frames;

    public BackgroundComponent(int width, int height) {
        super(0, 0, width, height);
        this.frameActual = 0;
        this.ultimoTiempoFrame = System.currentTimeMillis();

        // Ajuste de velocidad: 100 milisegundos por cuadro para que la animación fluya
        this.delayPorFrameMs = 100;
        this.frames = new ArrayList<>();

        // Caminos de búsqueda físicos idénticos a los validados en tu ImageCache
        String[] pathsToTest = {
                "src/main/resources/background_frames.png/",
                "resources/background_frames.png/",
                "src/resources/background_frames.png/"
        };

        String hashBase = "96ea36be2206410ffc5204f668253b7a4Ht8oNM3F7Fkbm7f-";

        // Escaneo e indexación segura de los fotogramas del 0 al 60
        for (int i = 0; i <= 60; i++) {
            String filename = hashBase + i + ".png";

            for (String basePath : pathsToTest) {
                File file = new File(basePath + filename);
                if (file.exists() && file.isFile()) {
                    ImageIcon loadedIcon = new ImageIcon(file.getAbsolutePath());
                    if (loadedIcon.getIconWidth() != -1) {
                        frames.add(loadedIcon);
                        break; // Fotograma cargado, saltamos al siguiente índice
                    }
                }
            }
        }

        // Reporte de control en la consola de IntelliJ
        System.out.println("Éxito en el Laboratorio: Se indexaron " + frames.size() + " fotogramas reales de fondo.");

        // Inicializamos la llave "background" con el cuadro 0 en caliente
        actualizarImagenEnCache();
    }

    /**
     * Hace avanzar cíclicamente el fotograma del fondo basándose en el tiempo transcurrido.
     * Este método debe ser llamado de forma constante en el ciclo central de tu GameLoop.
     */
    public void actualizarAnimacion() {
        if (frames.isEmpty()) return;

        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoTiempoFrame >= delayPorFrameMs) {
            frameActual = (frameActual + 1) % frames.size();
            ultimoTiempoFrame = tiempoActual;

            // Sobrescribimos el puntero en la caché central
            actualizarImagenEnCache();

            // Forzamos el repintado jerárquico inmediato en Swing
            this.repaint();
            if (getParent() != null) {
                getParent().repaint();
            }
        }
    }

    /**
     * Sincroniza el fotograma activo con el mapa de la caché sin alterar recursos ajenos.
     */
    private void actualizarImagenEnCache() {
        if (!frames.isEmpty() && frameActual < frames.size()) {
            ImageIcon frameIcon = frames.get(frameActual);
            if (frameIcon != null) {
                // Inyección limpia bajo la llave fija que consume tu jerarquía visual
                ImageCache.getInstance().addImage("background", frameIcon);
            }
        }
    }

    @Override
    public ImageIcon getImage() {
        // Mantiene la consistencia devolviendo el recurso activo desde el mapa indexado
        return ImageCache.getInstance().getImage("background");
    }
}

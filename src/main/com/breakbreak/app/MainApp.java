package main.com.breakbreak.app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada soberano y limpio. Al ser una clase ordinaria sin dependencias,
 * destruye cualquier intento de IntelliJ de arrancar el entorno como JavaFX.
 */
public class MainApp {

    public static void main(String[] args) {
        // Inicialización nativa directa del motor gráfico de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Sincronización del diseño estético nativo con el sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                BrickBreakApp juego = new BrickBreakApp();
                juego.setVisible(true);
            } catch (Exception e) {
                System.err.println("Fallo crítico al inicializar la interfaz de BreakBreak:");
                e.printStackTrace();
            }
        });
    }
}

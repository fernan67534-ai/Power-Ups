package main.com.breakbreak.events;

import main.com.breakbreak.enums.PowerUpType;
import main.com.breakbreak.util.GameLoop;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

public final class PowerUpRegistry {
    private static final Map<PowerUpType, PowerUpAction> registry = new EnumMap<>(PowerUpType.class);

    // Paleta cromática oficial de Attack on Titan para los marcos del Sidebar
    private static final Color ROJO = new Color(190, 30, 45);
    private static final Color AZUL = new Color(30, 100, 190);
    private static final Color VERDE = new Color(40, 160, 80);
    private static final Color MORADO = new Color(130, 50, 180);
    private static final Color GRIS = new Color(120, 120, 120);

    static {
        // --- 🔴 CÁPSULAS ROJAS (Ataque) ---
        registry.put(PowerUpType.L, game -> {
            game.setVy((int)(game.getVy() * 1.5));
            actualizarHUD(game, "LANZA RELÁMPAGO (L)", "Velocidad de ataque vertical x1.5", ROJO);
        });

        registry.put(PowerUpType.S, game -> {
            game.setVx(game.getVx() * 1.4);
            actualizarHUD(game, "ESCUADRÓN DE CAMPO (S)", "Impulso de gas horizontal x1.4", ROJO);
        });

        registry.put(PowerUpType.R, game -> {
            game.setVx(game.getVx() * 0.7);
            actualizarHUD(game, "RUGIDO DEL TITÁN (R)", "Pelota ralentizada para control", ROJO);
        });

        registry.put(PowerUpType.D, game -> {
            game.setGlobalDamageMultiplier(2);
            actualizarHUD(game, "DOBLE CUCHILLA (D)", "Doble daño destructivo a bloques", ROJO);
        });

        registry.put(PowerUpType.B, game -> {
            game.addScore(150);
            actualizarHUD(game, "BOMBARDEO MARLEYS (B)", "Ataque colateral: +150 puntos", ROJO);
        });

        registry.put(PowerUpType.T_ARMOR, game -> {
            game.setArmoredPaddle(true);
            actualizarHUD(game, "TITÁN ACORAZADO (T)", "Armadura de Reiner activa", ROJO);
        });

        // --- 🔵 CÁPSULAS AZULES (Físicas) ---
        registry.put(PowerUpType.E, game -> {
            var p = game.getPaddle();
            if (p != null) {
                p.setBounds(p.getX() - 15, p.getY(), p.getWidth() + 30, p.getHeight());
            }
            actualizarHUD(game, "ESTIRAMIENTO CABLES (E)", "Ancho de raqueta aumentado +30px", AZUL);
        });

        registry.put(PowerUpType.C, game -> {
            game.setStickyPaddle(true);
            actualizarHUD(game, "CRISTALIZACIÓN (C)", "Endurecimiento: Pelota magnética", AZUL);
        });

        registry.put(PowerUpType.M, game -> {
            if (game.getBall() != null) {
                game.getBall().setSize(game.getBall().getWidth() * 2, game.getBall().getHeight() * 2);
            }
            actualizarHUD(game, "MEGA TITÁN (M)", "Gigantismo: Pelota gigante", AZUL);
        });

        // --- 🟢 CÁPSULAS VERDES (Tiempo) ---
        registry.put(PowerUpType.T_TIME, game -> {
            game.setVx(game.getVx() * 0.6);
            game.setVy(game.getVy() * 0.6);
            actualizarHUD(game, "TIEMPO ACKERMAN (T)", "Reflejos: Cámara lenta global", VERDE);
        });

        registry.put(PowerUpType.B_SMOKE, game -> {
            game.addScore(75);
            actualizarHUD(game, "BENGALA DE HUMO (B)", "Efecto táctico: +75 puntos", VERDE);
        });

        // --- 🟣 CÁPSULAS MORADAS (Multiplicadores) ---
        registry.put(PowerUpType.V, game -> {
            game.duplicateBall();
            actualizarHUD(game, "VÍNCULO DE SANGRE (V)", "Clonación: Multipelota activa", MORADO);
        });

        registry.put(PowerUpType.T_TROOPS, game -> {
            game.recoverLife();
            actualizarHUD(game, "TROPAS GUARNICIÓN (T)", "Emblema de rosas: Vida añadida", MORADO);
        });

        registry.put(PowerUpType.N, game -> {
            game.enableShootingMode(true);
            actualizarHUD(game, "SUERO DE TITÁN (N)", "Modo artillería láser activado", MORADO);
        });

        registry.put(PowerUpType.G, game -> {
            game.setVy(game.getVy() * 1.2);
            actualizarHUD(game, "GAS DE MANIOBRA (G)", "Propulsión vertical incrementada", MORADO);
        });

        registry.put(PowerUpType.F, game -> {
            game.setScoreMultiplier(3);
            actualizarHUD(game, "FRENESÍ DE EREN (F)", "Furia de ataque: Puntos x3", MORADO);
        });

        registry.put(PowerUpType.P, game -> {
            game.addScore(500);
            actualizarHUD(game, "PUNTOS DE PARADIS (P)", "Valor de honor: +500 puntos directos", MORADO);
        });

        // --- ⚪ CÁPSULAS GRISES / ESPECIALES ---
        registry.put(PowerUpType.R_RUMBLE, game -> {
            game.triggerRumbleEffect();
            actualizarHUD(game, "EL RETUMBAR (R)", "Línea completa de bloques destruida", GRIS);
        });

        registry.put(PowerUpType.D_COORD, game -> {
            game.destroyAllBasicBlocks();
            actualizarHUD(game, "LA COORDENADA (D)", "Purga de bloques de daño simple", GRIS);
        });
    }

    /**
     * Envía de forma síncrona los datos al componente permanente en el Sidebar derecho.
     * Si no está enlazado directamente, rastrea el árbol de componentes activos de la ventana.
     */
    private static void actualizarHUD(GameLoop game, String titulo, String beneficio, Color colorBorde) {
        if (game.getBonoSidebarPanel() != null) {
            game.getBonoSidebarPanel().setBonoActivo(titulo, beneficio, colorBorde);
            return;
        }

        try {
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                if (window instanceof javax.swing.JFrame && window.isShowing()) {
                    javax.swing.JFrame frame = (javax.swing.JFrame) window;
                    main.com.breakbreak.components.BonoSidebarPanel panel = buscarBonoPanel(frame.getContentPane());
                    if (panel != null) {
                        game.setBonoSidebarPanel(panel);
                        panel.setBonoActivo(titulo, beneficio, colorBorde);
                        return;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private static main.com.breakbreak.components.BonoSidebarPanel buscarBonoPanel(java.awt.Container container) {
        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof main.com.breakbreak.components.BonoSidebarPanel) {
                return (main.com.breakbreak.components.BonoSidebarPanel) comp;
            }
            if (comp instanceof java.awt.Container) {
                main.com.breakbreak.components.BonoSidebarPanel encontrado = buscarBonoPanel((java.awt.Container) comp);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    @Deprecated
    private static void mostrarPopup(GameLoop game, String titulo, String beneficio) {
        // Obsoleto: Limpia el lienzo izquierdo eliminando ventanas emergentes
    }

    public static PowerUpAction getAction(PowerUpType type) {
        return registry.getOrDefault(type, game -> {});
    }
}

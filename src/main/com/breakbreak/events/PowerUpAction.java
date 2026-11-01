package main.com.breakbreak.events;

import main.com.breakbreak.util.GameLoop;

@FunctionalInterface
public interface PowerUpAction {
    /**
     * Aplica la modificación de estado correspondiente al juego.
     * @param game Instancia del GameLoop activo que contiene las físicas y parámetros.
     */
    void apply(GameLoop game);
}

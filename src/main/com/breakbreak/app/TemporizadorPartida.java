package main.com.breakbreak.app;

import javax.swing.Timer;
import java.awt.event.ActionListener;

public class TemporizadorPartida {
    private final Timer timer;
    private int segundosTranscurridos;

    public TemporizadorPartida(int delayMs, ActionListener listener) {
        this.segundosTranscurridos = 0;
        this.timer = new Timer(delayMs, listener);
        this.timer.setCoalesce(true);
    }

    public void iniciar() { timer.start(); }
    public void pausar() { timer.stop(); }
    public void reiniciar() { timer.stop(); segundosTranscurridos = 0; }
}

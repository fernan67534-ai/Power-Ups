package main.com.breakbreak.app;

import main.com.breakbreak.components.MaterialButton;

import javax.swing.*;
import java.awt.*;

public class TemporizadorPartida extends JFrame {

    private int segundosRestantes = 30;

    private final JLabel etiquetaTiempo;
    private final Timer temporizador;

    public TemporizadorPartida() {
        // Configuración de la ventana
        setTitle("BreakBreak - Temporizador de Partida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(new Color(23, 23, 28));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        // Título
        JLabel titulo = new JLabel(
                "TEMPORIZADOR DE PARTIDA",
                SwingConstants.CENTER
        );
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));

        // Etiqueta del tiempo
        etiquetaTiempo = new JLabel("00:30", SwingConstants.CENTER);
        etiquetaTiempo.setForeground(new Color(80, 220, 140));
        etiquetaTiempo.setFont(new Font("Segoe UI", Font.BOLD, 52));

        // Panel central
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setOpaque(false);
        panelCentro.add(titulo, BorderLayout.NORTH);
        panelCentro.add(etiquetaTiempo, BorderLayout.CENTER);

        // Botones personalizados
        MaterialButton botonIniciar = new MaterialButton("Iniciar");
        MaterialButton botonPausar = new MaterialButton("Pausar");
        MaterialButton botonReiniciar = new MaterialButton("Reiniciar");

        // Tamaño preferido de los botones
        botonIniciar.setPreferredSize(new Dimension(110, 38));
        botonPausar.setPreferredSize(new Dimension(110, 38));
        botonReiniciar.setPreferredSize(new Dimension(110, 38));

        // Panel de botones
        JPanel panelBotones = new JPanel(
                new GridLayout(1, 3, 10, 0)
        );
        panelBotones.setOpaque(false);
        panelBotones.add(botonIniciar);
        panelBotones.add(botonPausar);
        panelBotones.add(botonReiniciar);

        // Agregar componentes a la ventana
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);

        // Temporizador Swing: un evento cada segundo
        temporizador = new Timer(1000, e -> actualizarTiempo());

        // Botón Iniciar
        botonIniciar.addActionListener(e -> {
            if (segundosRestantes > 0) {
                temporizador.start();
            }
        });

        // Botón Pausar
        botonPausar.addActionListener(e -> {
            temporizador.stop();
        });

        // Botón Reiniciar
        botonReiniciar.addActionListener(e -> {
            temporizador.stop();
            segundosRestantes = 30;
            actualizarEtiqueta();
            etiquetaTiempo.setForeground(new Color(80, 220, 140));
        });
    }

    // Actualiza la cuenta regresiva
    private void actualizarTiempo() {
        if (segundosRestantes > 0) {
            segundosRestantes--;
            actualizarEtiqueta();
        }

        if (segundosRestantes == 0) {
            temporizador.stop();

            etiquetaTiempo.setForeground(new Color(231, 76, 60));

            JOptionPane.showMessageDialog(
                    this,
                    "¡El tiempo se ha agotado!",
                    "Fin de la partida",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // Actualiza el texto de la etiqueta por separado
    private void actualizarEtiqueta() {
        int minutos = segundosRestantes / 60;
        int segundos = segundosRestantes % 60;

        etiquetaTiempo.setText(
                String.format("%02d:%02d", minutos, segundos)
        );
    }

    // Punto de entrada del programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TemporizadorPartida ventana =
                    new TemporizadorPartida();

            ventana.setVisible(true);
        });
    }
}
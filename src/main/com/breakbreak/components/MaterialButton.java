package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class MaterialButton extends JButton {
    public MaterialButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(new Color(33, 37, 41));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
        g2.dispose();
        super.paintComponent(g);
    }
}
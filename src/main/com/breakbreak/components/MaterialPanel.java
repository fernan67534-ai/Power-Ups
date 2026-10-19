package main.com.breakbreak.components;

import javax.swing.*;
import java.awt.*;

public class MaterialPanel extends JPanel {
    public MaterialPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(45, 45, 45));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        g2.setColor(new Color(65, 65, 65));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        g2.dispose();
    }
}
package main.com.breakbreak.components;

import javax.swing.ImageIcon;
import java.awt.*;

public class GameOverPanel extends GameComponent {

    public GameOverPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        setVisible(false);
    }

    @Override
    public ImageIcon getImage() { return null; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Manto oscuro traslúcido idéntico a la pantalla de victoria
        g2.setColor(new Color(15, 15, 15, 245));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Borde imperial desgastado
        g2.setColor(new Color(140, 110, 35, 180));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(20, 20, getWidth() - 40, getHeight() - 40);

        // Letrero GAME OVER
        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
        g2.setColor(new Color(160, 20, 30));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GAME OVER";
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        g2.drawString(text, textX, 240);

        g2.setFont(new Font("Serif", Font.ITALIC, 15));
        g2.setColor(new Color(170, 170, 170));
        String subTheme = "Las murallas han caído ante el enemigo...";
        FontMetrics ftm = g2.getFontMetrics();
        int subThemeX = (getWidth() - ftm.stringWidth(subTheme)) / 2;
        g2.drawString(subTheme, subThemeX, 280);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.GRAY);
        FontMetrics sfm = g2.getFontMetrics();
        String sub = "[ Presiona REINICIAR en el panel de control ]";
        int subX = (getWidth() - sfm.stringWidth(sub)) / 2;
        g2.drawString(sub, subX, 560);

        g2.dispose();
    }
}

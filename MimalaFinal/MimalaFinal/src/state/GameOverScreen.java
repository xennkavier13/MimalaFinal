package state;

import javax.swing.*;
import java.awt.*;

public class GameOverScreen extends JPanel {

    private final JFrame frame;
    private final ImageIcon background;
    private final String victoryPath = "assets/FightingUI/AI/VictoryScreen.png";
    private final String defeatPath = "assets/FightingUI/AI/DefeatScreen.png";

    public GameOverScreen(JFrame frame, boolean isVictory) {
        this.frame = frame;

        // Choose the appropriate background based on the result
        this.background = new ImageIcon(isVictory ? victoryPath : defeatPath);

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null && background.getImageLoadStatus() == MediaTracker.COMPLETE) {
            g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Game Over - BG Error", 100, 100);
        }
    }
}

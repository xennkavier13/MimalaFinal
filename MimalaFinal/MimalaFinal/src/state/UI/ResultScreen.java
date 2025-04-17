package state.UI;

import state.GameOverScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResultScreen extends JPanel {

    private final JFrame frame;
    private ImageIcon resultOverlayImage = null;
    private boolean showResultOverlay = false;
    private boolean player1Won = false; // Needed to pass to GameOverScreen
    private String p1Name, p2Name, mapPath, gameMode;

    public static boolean isVsAI = false;

    public ResultScreen(JFrame frame, boolean player1Won, String p1Name, String p2Name, String mapPath, String gameMode) {
        this.frame = frame;
        this.player1Won = player1Won;
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.mapPath = mapPath;
        this.gameMode = gameMode;

        isVsAI = gameMode.equals("PVC");

        setOpaque(false); // Transparent
        setLayout(null);
        setBounds(0, 0, 1920, 1080); // Same size as game panel

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (showResultOverlay) {
                    transitionToGameOverScreen();
                }
            }
        });

        showGameResult(player1Won);
    }

    /**
     * Shows the correct result overlay based on game mode and outcome.
     */
    public void showGameResult(boolean player1Won) {
        this.player1Won = player1Won;

        String resultPath;

        if (isVsAI) {
            resultPath = player1Won
                    ? "/assets/FightingUI/AI/VictoryScreen.png"
                    : "/assets/FightingUI/AI/DefeatScreen.png";
        } else {
            resultPath = player1Won
                    ? "/assets/FightingUI/PVP/player1WinsTest.png"
                    : "/assets/FightingUI/PVP/player2WinsTest.png";
        }

        java.net.URL imageURL = getClass().getResource(resultPath);
        if (imageURL != null) {
            resultOverlayImage = new ImageIcon(imageURL);
            showResultOverlay = true;
            repaint();
        } else {
            System.err.println("Result overlay image not found at: " + resultPath);
        }
    }

    /**
     * After click, transitions to GameOverScreen.
     */
    private void transitionToGameOverScreen() {
        System.out.println("Transitioning to GameOverScreen...");

        JLayeredPane layeredPane = frame.getLayeredPane();
        Component[] components = layeredPane.getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component comp : components) {
            if (comp instanceof ResultScreen) {
                layeredPane.remove(comp);
                break;
            }
        }

        frame.revalidate();
        frame.repaint();

        GameOverScreen gameOver = new GameOverScreen(frame, player1Won, p1Name, p2Name, mapPath, gameMode);
        frame.setContentPane(gameOver);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (showResultOverlay && resultOverlayImage != null) {
            Graphics2D g2d = (Graphics2D) g;

            // Dim the background
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw the result overlay full screen
            g2d.drawImage(resultOverlayImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}

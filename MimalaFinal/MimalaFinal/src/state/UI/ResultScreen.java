package state.UI;

import state.GameOverScreen;
import state.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; // Import MouseAdapter
import java.awt.event.MouseEvent;  // Import MouseEvent

// Renamed from GameOverScreen
public class ResultScreen extends JPanel {

    private final JFrame frame;
    private final ImageIcon background;
    // Keep paths for victory/defeat images
    private final String AIvictoryPath = "assets/FightingUI/AI/VictoryScreen.png";
    private final String AIdefeatPath = "assets/FightingUI/AI/DefeatScreen.png";
    // Store game details needed for a potential rematch
    private final String p1Name, p2Name, mapPath, gameMode;
    private final boolean player1Won; // Store result


    // Modify constructor to potentially receive game details later if needed for rematch
    public ResultScreen(JFrame frame, boolean isVictory, String p1, String p2, String map, String mode) {
        this.frame = frame;
        this.player1Won = isVictory; // Store result

        this.p1Name = p1;
        this.p2Name = p2;
        this.mapPath = map;
        this.gameMode = mode;


        String playerWinPath = "";
        String imagePathString = "";        // Choose the appropriate background based on the result
        if (GameScreen.isVsAI) {
            imagePathString = isVictory ? AIvictoryPath : AIdefeatPath;
        } else {
            if (player1Won) {
                playerWinPath = "assets/FightingUI/PVP/player1Wins.png";
            } else {
                playerWinPath = "assets/FightingUI/PVP/player2Wins.png";
            }
            imagePathString = playerWinPath;
        }

        // Ensure path starts with "/" if relative to classpath root
        java.net.URL imageURL = getClass().getResource("/" + imagePathString);

        if (imageURL != null) {
            this.background = new ImageIcon(imageURL);
            System.out.println("Loaded result screen background: /" + imagePathString);
        } else {
            System.err.println("ERROR: Result screen background image not found at: /" + imagePathString);
            this.background = null;
        }

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        // --- Add Mouse Listener to transition on click ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                transitionToOptionsScreen();
            }
        });
        // Add cursor hint
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Transitions the frame to the GameOverOptionsScreen.
     */
    private void transitionToOptionsScreen() {
        System.out.println("Executing transitionToOptionsScreen...");
        // Pass necessary info for rematch if implemented later
        GameOverScreen optionsScreen = new GameOverScreen(frame, player1Won, p1Name, p2Name, mapPath, gameMode);
        frame.setContentPane(optionsScreen);
        frame.revalidate();
        frame.repaint();
        System.out.println("Switched to GameOverOptionsScreen.");
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Debugging print removed for cleaner output, uncomment if needed
        // System.out.println("ResultScreen paintComponent - Width: " + getWidth() + ", Height: " + getHeight());

        if (background != null && background.getImage() != null) {
            g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            // Optionally draw "Click anywhere to continue" if not part of the image

        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String status = player1Won ? "VICTORY" : "DEFEAT";
            g.drawString("Game Over: " + status + " (BG Image Error)", 100, 100);
            System.err.println("Result screen background image failed to load or is null.");
        }
    }
}
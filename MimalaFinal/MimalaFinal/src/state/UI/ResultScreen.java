package state.UI;

import state.GameOverScreen;
// Import MainMenu if needed for fallback navigation
import state.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL; // Keep URL import

public class ResultScreen extends JPanel {

    private final JFrame frame;
    private ImageIcon resultOverlayImage = null;
    private boolean showResultOverlay = false;
    // Make player1Won final if only set in constructor
    private final boolean player1Won;
    private final String p1Name, p2Name, mapPath, gameMode; // Store game details

    // Remove static isVsAI - rely on gameMode passed to constructor
    // public static boolean isVsAI = false;

    public ResultScreen(JFrame frame, boolean player1Won, String p1Name, String p2Name, String mapPath, String gameMode) {
        this.frame = frame;
        this.player1Won = player1Won;
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.mapPath = mapPath;
        this.gameMode = gameMode; // Store the specific game mode

        // Remove static isVsAI assignment
        // isVsAI = gameMode.equals("PVC");

        setOpaque(false); // Transparent panel
        setLayout(null);
        // Set bounds AFTER adding to layered pane might be better, but okay here for now
        setBounds(0, 0, 1920, 1080);

        // Add listener to proceed to next screen on click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Only transition if the overlay is successfully shown
                if (showResultOverlay) {
                    transitionToGameOverScreen();
                } else {
                    // Fallback if overlay failed to load
                    System.out.println("Result overlay not shown, but click detected. Transitioning...");
                    transitionToGameOverScreen();
                }
            }
        });

        // Load the appropriate image based on game mode and result
        showGameResult(); // Call the updated method
    }

    /**
     * Determines and loads the correct result overlay image based
     * on the game mode and whether player 1 won.
     */
    // Remove parameter as player1Won is now a field
    private void showGameResult() {
        String resultPath = null; // Path to the image to load

        // Check for null gameMode for safety
        if (this.gameMode == null) {
            System.err.println("ResultScreen Error: Game mode is null!");
            // Handle error state - maybe show nothing or an error message?
            showResultOverlay = false; // Prevent click transition if error state
            repaint();
            return;
        }

        // --- Determine image path based on game mode and win/loss ---
        switch (this.gameMode) {
            case "Arcade":
                if (this.player1Won) {
                    // Note: Arcade wins usually bypass ResultScreen now.
                    // Keep this path defined in case logic changes or for testing.
                    resultPath = "/assets/FightingUI/AI/VictoryScreen.png";
                } else {
                    // <<< USE NEW ARCADE DEFEAT PATH >>>
                    resultPath = "/assets/FightingUI/AI/DefeatScreen.png"; // CHANGE TO YOUR ACTUAL PATH
                }
                break;

            case "PVC": // Player vs Computer (Non-Arcade)
                resultPath = this.player1Won
                        ? "/assets/FightingUI/AI/VictoryScreen.png"    // PVC Win
                        : "/assets/FightingUI/AI/DefeatScreen.png";     // PVC Loss (Original AI Defeat)
                break;

            case "PVP": // Player vs Player
                resultPath = this.player1Won
                        ? "/assets/FightingUI/PVP/player1WinsTest.png"  // PVP P1 Win (Use your actual image)
                        : "/assets/FightingUI/PVP/player2WinsTest.png"; // PVP P2 Win (Use your actual image)
                break;

            default:
                System.err.println("ResultScreen Error: Unknown game mode '" + this.gameMode + "'!");
                showResultOverlay = false; // Prevent display/click
                repaint();
                return;
        }

        // --- Load the determined image ---
        System.out.println("ResultScreen attempting to load: " + resultPath);
        // Use getResource for reliable path finding from classpath
        URL imageURL = getClass().getResource(resultPath);
        if (imageURL != null) {
            resultOverlayImage = new ImageIcon(imageURL);
            showResultOverlay = true; // Flag that image is ready
            System.out.println("Result overlay loaded successfully.");
        } else {
            System.err.println("Result overlay image not found at: " + resultPath);
            showResultOverlay = false; // Flag that image failed to load
            // Consider displaying fallback text in paintComponent
        }
        repaint(); // Request repaint to draw the loaded image or fallback state
    }

    /**
     * After click, transitions to GameOverScreen.
     */
    private void transitionToGameOverScreen() {
        System.out.println("Transitioning from ResultScreen to GameOverScreen...");

        // Remove this ResultScreen overlay from the layered pane
        // Using frame.getLayeredPane() is correct here if ResultScreen was added to it
        JLayeredPane layeredPane = frame.getLayeredPane();
        layeredPane.remove(this); // Remove itself

        // Repaint the area underneath before setting new content pane
        // (This might help avoid visual glitches depending on how things are layered)
        // frame.revalidate();
        // frame.repaint();

        // Create and set the GameOverScreen as the main content pane
        // Pass all necessary details forward
        GameOverScreen gameOver = new GameOverScreen(frame, player1Won, p1Name, p2Name, mapPath, gameMode);
        frame.setContentPane(gameOver);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Important for transparent panels

        if (showResultOverlay && resultOverlayImage != null) {
            Graphics2D g2d = (Graphics2D) g.create(); // Use create for safety

            // Optional: Dim the background (you had this before)
            // If your result images are opaque, you might not need this.
            // If they have transparency, this helps them stand out.
            g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw the result overlay image, scaled to fill the panel
            g2d.drawImage(resultOverlayImage.getImage(), 0, 0, getWidth(), getHeight(), this);

            g2d.dispose(); // Release the graphics copy
        } else if (!showResultOverlay) {
            // Optional: Draw fallback text if image failed to load and you want feedback
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0,0,0,180)); // Dim background
            g2d.fillRect(0,0, getWidth(), getHeight());
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2d.drawString("Result Image Error", getWidth()/2 - 150, getHeight()/2);
            g2d.dispose();
        }
    }
}
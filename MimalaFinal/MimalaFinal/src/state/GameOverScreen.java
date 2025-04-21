package state;

// Import necessary classes for Arcade reset logic
import state.CharacterScreen.Select.CharacterSelection; // To go back if needed
import state.UI.PVPLeaderboard;
import state.character.CharacterDataLoader; // Needed to get character list
import util.PlayerName;                   // Needed to get player name for Arcade

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.*; // Need for Random, List, Set
import java.util.List;
import java.util.stream.Collectors; // Need for Stream filtering

import static util.AudioManager.stopMusic; // Assuming this exists and works

public class GameOverScreen extends JPanel {

    private final JFrame frame;
    private JLabel backgroundLabel = null;
    // Remove unused player1Won field if not needed for display logic
    // private final boolean player1Won;

    // --- Button Paths (Keep as is) ---
    private final String rematchOffPath = "assets/GameOver/Rematch/Rematch_Off.png";
    private final String rematchHoverPath = "assets/GameOver/Rematch/Rematch_Hover.png";
    // Rename "Rematch" to "Play Again" for Arcade? Optional but clearer.
    private final String playAgainArcadeOffPath = "assets/GameOver/Rematch/PlayAgain_Off.png"; // <<< CREATE THIS IMAGE
    private final String playAgainArcadeHoverPath = "assets/GameOver/Rematch/PlayAgain_Hover.png"; // <<< CREATE THIS IMAGE
    private final String menuOffPath = "assets/GameOver/ReturnToMainMenu/MainMenu_off.png";
    private final String menuHoverPath = "assets/GameOver/ReturnToMainMenu/MainMenu_hover.png";
    private final String leaderboardOffPath = "assets/GameOver/Leaderboard/Leaderboard_off.png";
    private final String leaderboardHoverPath = "assets/GameOver/Leaderboard/Leaderboard_hover.png";

    // --- Game Details (Keep as is) ---
    private final String p1Name, p2Name, mapPath, gameMode;
    private final String bgPath = "assets/GameOver/GameOver.gif";

    // --- Add Random and Map List for Arcade Reset ---
    private final Random random = new Random();
    private static final String[] mapPaths = { // Should match list used elsewhere
            "assets/StageMap/Waters.gif",
            "assets/StageMap/Dojo.gif",
            "assets/StageMap/DesertedLand.gif",
            "assets/StageMap/DragonLair.gif",
            "assets/StageMap/King'sThrone.gif",
            "assets/StageMap/RoyalPalace.gif"
    };


    public GameOverScreen(JFrame frame, boolean player1Won, String p1, String p2, String map, String mode) {
        this.frame = frame;
        // this.player1Won = player1Won; // Store if needed for display
        this.p1Name = p1;     // This is the character name for P1
        this.p2Name = p2;     // Character name for P2/Last AI opponent
        this.mapPath = map;   // Last map played
        this.gameMode = mode; // Crucial for logic

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        setupBackgroundGif(this);
        setupButtons();
    }

    private void setupBackgroundGif(Component componentForTracker) {
        // Use getResource for background path
        ImageIcon gifIcon = loadImageIconResource("/" + bgPath, -1, -1, componentForTracker);
        if (gifIcon != null) {
            backgroundLabel = new JLabel(gifIcon);
            backgroundLabel.setBounds(0, 0, 1920, 1080);
            add(backgroundLabel);
            // Ensure background is behind buttons if added later
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        } else {
            System.err.println("Failed to load background GIF: " + bgPath);
            // Add fallback color?
            setBackground(Color.DARK_GRAY);
        }
    }

    private void setupButtons() {
        Component componentForTracker = this;
        JLabel primaryActionButton; // Will be Rematch or Play Again

        // --- Define button positions ---
        int primaryBtnX = 500, primaryBtnY = 500; // Position for Rematch/Play Again
        int leaderboardBtnX = 500, leaderboardBtnY = 585;
        int menuBtnX = 500, menuBtnY = 675;

        // --- Create Rematch / Play Again Button ---
        if (gameMode != null && gameMode.equals("Arcade")) {
            // --- Arcade Mode: Create "Play Again" Button ---
            System.out.println("Setting up 'Play Again' button for Arcade Mode.");
            primaryActionButton = createButton(
                    rematchOffPath, // Use specific "Play Again" image paths
                    rematchHoverPath,
                    primaryBtnX, primaryBtnY, // Use same position for consistency
                    primaryBtnX, primaryBtnY, // Hover position (can be same)
                    () -> { // Action for Arcade "Play Again"
                        System.out.println("Arcade Play Again clicked. Resetting streak and starting new run...");
                        GameScreen.arcadeWins = 0; // <<< RESET ARCADE STREAK

                        // Select a *new* random opponent and map for the fresh run
                        // We need the player's CHARACTER name (p1Name)
                        String nextOpponent = selectRandomOpponent(p1Name); // Use helper
                        String nextMap = selectRandomMap();          // Use helper

                        if (nextOpponent == null || nextMap == null) {
                            System.err.println("Failed to setup next arcade match. Returning to menu.");
                            JOptionPane.showMessageDialog(frame, "Error starting next match. Returning to menu.", "Arcade Error", JOptionPane.ERROR_MESSAGE);
                            frame.setContentPane(new MainMenu(frame)); // Fallback to menu
                        } else {
                            System.out.println("Starting new Arcade run. Opponent: " + nextOpponent + ", Map: " + nextMap);
                            // Start GameScreen with player's char, new opponent, new map, Arcade mode
                            frame.setContentPane(new GameScreen(frame, p1Name, nextOpponent, nextMap, "Arcade"));
                        }
                        frame.revalidate();
                        frame.repaint();
                    },
                    componentForTracker
            );

        } else {
            // --- Non-Arcade Mode: Create "Rematch" Button ---
            System.out.println("Setting up 'Rematch' button for mode: " + gameMode);
            primaryActionButton = createButton(
                    rematchOffPath,
                    rematchHoverPath,
                    primaryBtnX, primaryBtnY,
                    primaryBtnX, primaryBtnY, // Hover position (can be same)
                    () -> { // Action for non-Arcade "Rematch"
                        System.out.println("Rematch button clicked for mode: " + gameMode);
                        // Basic check for necessary info
                        if (p1Name != null && p2Name != null && mapPath != null && gameMode != null) {
                            // Rematch with the exact same parameters
                            frame.setContentPane(new GameScreen(frame, p1Name, p2Name, mapPath, gameMode));
                        } else {
                            System.err.println("Cannot rematch, missing game parameters. Returning to menu.");
                            JOptionPane.showMessageDialog(frame, "Cannot start rematch (missing info). Returning to menu.", "Rematch Error", JOptionPane.WARNING_MESSAGE);
                            frame.setContentPane(new MainMenu(frame)); // Fallback
                        }
                        frame.revalidate();
                        frame.repaint();
                    },
                    componentForTracker
            );
        }


        // --- Create Leaderboard Button ---
        JLabel leaderboardButton = createButton(
                leaderboardOffPath,
                leaderboardHoverPath,
                leaderboardBtnX, leaderboardBtnY,
                leaderboardBtnX, leaderboardBtnY, // Hover position
                () -> {
                    System.out.println("Leaderboard button clicked");
                    // Go to the *first* leaderboard screen (e.g., PVP)
                    // It should handle navigation to others from there
                    JPanel leaderboardScreen = new PVPLeaderboard(frame, this); // Pass 'this' as previousScreen
                    frame.setContentPane(leaderboardScreen);
                    frame.revalidate();
                    frame.repaint();
                },
                componentForTracker
        );

        // --- Create Main Menu Button ---
        JLabel menuButton = createButton(
                menuOffPath,
                menuHoverPath,
                menuBtnX, menuBtnY,
                menuBtnX, menuBtnY, // Hover position
                () -> {
                    System.out.println("Menu button clicked");
                    frame.setContentPane(new MainMenu(frame));
                    frame.revalidate();
                    frame.repaint();
                },
                componentForTracker
        );

        // --- Add buttons to panel ---
        // Make sure background is added first or sent to back later
        if (primaryActionButton != null) add(primaryActionButton);
        if (leaderboardButton != null) add(leaderboardButton);
        if (menuButton != null) add(menuButton);

        // Ensure background is behind buttons if it was added *after* them
        if (backgroundLabel != null && backgroundLabel.getParent() == this) {
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        }

        // Optional: Stop any lingering music when reaching game over
        stopMusic(); // Make sure AudioManager.stopMusic() exists and works
    }

    // --- createButton Method (Keep as is, ensure paths use getResource) ---
    private JLabel createButton(String offPath, String hoverPath,
                                int offX, int offY, int hoverX, int hoverY,
                                Runnable action, Component componentForTracker) {

        ImageIcon offIcon = loadImageIconResource("/" + offPath, -1, -1, componentForTracker);
        ImageIcon hoverIcon = loadImageIconResource("/" + hoverPath, -1, -1, componentForTracker);

        // Handle case where hover icon might be missing
        if (hoverIcon == null) hoverIcon = offIcon;

        // Handle case where off icon is missing (critical)
        if (offIcon == null) {
            System.err.println("Error loading mandatory button icon: " + offPath);
            JLabel errorLabel = new JLabel("ERR");
            errorLabel.setBounds(offX, offY, 100, 30); // Placeholder size
            errorLabel.setForeground(Color.RED);
            errorLabel.setOpaque(true);
            errorLabel.setBackground(Color.BLACK);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            return errorLabel; // Return placeholder label
        }

        int offWidth = offIcon.getIconWidth();
        int offHeight = offIcon.getIconHeight();
        // Use hover dimensions if hover icon loaded successfully
        int hoverWidth = (hoverIcon != null) ? hoverIcon.getIconWidth() : offWidth;
        int hoverHeight = (hoverIcon != null) ? hoverIcon.getIconHeight() : offHeight;


        JLabel button = new JLabel(offIcon);
        button.setBounds(offX, offY, offWidth, offHeight);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon finalOffIcon = offIcon; // Need final vars for lambda
        ImageIcon finalHoverIcon = hoverIcon;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (finalHoverIcon != null) {
                    button.setIcon(finalHoverIcon);
                    // Adjust bounds for hover effect if hover icon is different size/position
                    button.setBounds(hoverX, hoverY, hoverWidth, hoverHeight);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(finalOffIcon);
                // Reset bounds to normal state
                button.setBounds(offX, offY, offWidth, offHeight);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }
        });

        return button;
    }

    // --- loadImageIconResource Method (Keep as is) ---
    private ImageIcon loadImageIconResource(String resourcePath, int width, int height, Component component) {
        URL imgURL = getClass().getResource(resourcePath); // getResource is better for classpath
        if (imgURL == null) {
            System.err.println("Image not found: " + resourcePath);
            return null;
        }
        ImageIcon icon = new ImageIcon(imgURL);
        // Optional scaling (currently not used as width/height are -1)
        if (width > 0 && height > 0) {
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return icon;
    }

    // --- Helper methods copied from GameScreen for Arcade restart ---
    private String selectRandomOpponent(String playerCharacter) {
        Set<String> allNames = CharacterDataLoader.getAllCharacterNames();
        if (allNames == null || allNames.isEmpty()) {
            System.err.println("Cannot select opponent: No characters loaded.");
            return null;
        }
        List<String> possibleOpponents = allNames.stream()
                .filter(name -> !name.equals(playerCharacter))
                .collect(Collectors.toList());
        if (possibleOpponents.isEmpty()) {
            // Fallback: allow fighting self if only one character exists? Or return error?
            System.out.println("Warning: No opponents other than player's character found. Allowing self-match or error.");
            // Let's allow self-match as a fallback for now. Could return null instead.
            possibleOpponents = new ArrayList<>(allNames);
            if (possibleOpponents.isEmpty()) return null; // Should not happen if allNames wasn't empty
        }
        return possibleOpponents.get(random.nextInt(possibleOpponents.size()));
    }

    private String selectRandomMap() {
        if (mapPaths == null || mapPaths.length == 0) {
            System.err.println("Cannot select map: mapPaths array is empty or null.");
            return null;
        }
        return mapPaths[random.nextInt(mapPaths.length)];
    }

    // --- addNotify / paintComponent (Keep as is) ---
    @Override
    public void addNotify() {
        super.addNotify();
        // Re-ensure background is present and at the back
        if (backgroundLabel != null && backgroundLabel.getParent() != this) {
            add(backgroundLabel);
        }
        if (backgroundLabel != null) {
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        }
        // Re-add buttons if they were somehow removed (less likely needed)
        // if (getComponentCount() <= 1) { setupButtons(); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fallback paint if background label isn't ready/added
        if (backgroundLabel == null || backgroundLabel.getParent() != this) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String message = "Background Image Error";
            g.drawString(message, 50, 50);
        }
        // Background label (if added) paints itself. Buttons paint themselves.
    }
}
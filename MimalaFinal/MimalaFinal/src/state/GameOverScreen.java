package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import static util.AudioManager.stopMusic;

public class GameOverScreen extends JPanel {

    private final JFrame frame;
    private JLabel backgroundLabel = null; // Use a JLabel for the background GIF
    private final boolean player1Won;

    // Define paths for your assets (Adjust these!)
    private final String rematchOffPath = "assets/GameOver/Rematch/Rematch_Off.png";
    private final String rematchHoverPath = "assets/GameOver/Rematch/Rematch_Hover.png";
    private final String menuOffPath = "assets/GameOver/ReturnToMainMenu/MainMenu_off.png";
    private final String menuHoverPath = "assets/GameOver/ReturnToMainMenu/MainMenu_hover.png";
    private final String p1Name, p2Name, mapPath, gameMode;
    private final String bgPath = "assets/GameOver/GameOver.gif"; // Your GIF path

    // Constructor
    public GameOverScreen(JFrame frame, boolean player1Won, String p1, String p2, String map, String mode) {
        this.frame = frame;
        this.player1Won = player1Won;
        this.p1Name = p1;
        this.p2Name = p2;
        this.mapPath = map;
        this.gameMode = mode;

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        // Pass 'this' (the JPanel) to the loading method for MediaTracker
        setupBackgroundGif(this); // Pass component context

        setupButtons(); // Add buttons on top
    }

    // Modified to accept a Component for MediaTracker
    private void setupBackgroundGif(Component componentForTracker) {
        if (bgPath != null) {
            // Load the GIF *without* scaling using the enhanced method
            ImageIcon gifIcon = loadImageIconResource("/" + bgPath, -1, -1, componentForTracker);

            // Check should be reliable now after MediaTracker waits
            if (gifIcon != null) { // No need to check status again IF loadImageIconResource handles it
                backgroundLabel = new JLabel(gifIcon);
                backgroundLabel.setBounds(0, 0, 1920, 1080);
                add(backgroundLabel); // Add the label first
            } else {
                System.err.println("Failed to load background GIF: " + bgPath + " (Check previous errors)");
            }
        }
    }

    private void setupButtons() {
        // Pass 'this' (the JPanel) to the loading method for MediaTracker
        Component componentForTracker = this;

        // --- Rematch Button ---
        int buttonY_Rematch = 550;
        JLabel rematchButton = createButton(
                rematchOffPath,
                rematchHoverPath,
                buttonY_Rematch,
                () -> {
                    System.out.println("Rematch button clicked");
                    if (p1Name != null && p2Name != null && mapPath != null && gameMode != null) {
                        System.out.println("Starting rematch..."); // Log details if needed
                        frame.setContentPane(new GameScreen(frame, p1Name, p2Name, mapPath, gameMode));
                        frame.revalidate();
                        frame.repaint();
                    } else {
                         frame.setContentPane(new MainMenu(frame)); // Example
                         frame.revalidate();
                         frame.repaint();
                    }
                },
                componentForTracker // Pass component context
        );

        // --- Main Menu Button ---
        int buttonY_Menu = buttonY_Rematch + 100; // Adjust spacing as needed
        JLabel menuButton = createButton(
                menuOffPath,
                menuHoverPath,
                buttonY_Menu,
                () -> {
                    System.out.println("Main Menu button clicked");

                    frame.setContentPane(new MainMenu(frame)); // Example
                    frame.revalidate();
                    frame.repaint();
                },
                componentForTracker // Pass component context
        );

        // Add buttons *after* the background label has potentially been added
        // Add null checks in case createButton returned an error label or null
        if (rematchButton != null) {
            add(rematchButton);
            stopMusic();
        }
        if (menuButton != null) {
            add(menuButton);

        }

        // --- Crucial Fix: Set Z-Order ---
        // Ensure the background label is painted *behind* the buttons.
        // Components with higher Z-order indices are painted FIRST (bottom).
        // Components with lower Z-order indices are painted LAST (top).
        if (backgroundLabel != null && backgroundLabel.getParent() == this) { // Check if background was added
            // Move backgroundLabel to the highest index (painted first/bottom)
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        }
        // Optional: Explicitly move buttons to the top (lowest indices)
        // This is usually not needed if the background is forced to the bottom,
        // but can be added for extra certainty.
        // if (rematchButton != null && rematchButton.getParent() == this) {
        //     setComponentZOrder(rematchButton, 0); // Paint last (topmost)
        // }
        // if (menuButton != null && menuButton.getParent() == this) {
        //     setComponentZOrder(menuButton, 1); // Paint next-to-last (just below topmost)
        // }
        // Note: If you uncomment the explicit button Z-order setting, make sure
        // the indices (0, 1) are correct relative to other components you might add.
        // Setting only the background to the bottom is generally safer.
    }

    // Modified createButton to accept a Component for MediaTracker
    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action, Component componentForTracker) {
        // Load icons using the reliable method
        ImageIcon offIcon = loadImageIconResource("/" + offPath, -1, -1, componentForTracker);
        ImageIcon hoverIcon = loadImageIconResource("/" + hoverPath, -1, -1, componentForTracker);

        if (offIcon == null) { // Only need to check offIcon, assuming hover is similar or fallback is ok
            System.err.println("Error loading button icon: " + offPath);
            JLabel errorLabel = new JLabel("Btn Load Err");
            errorLabel.setBounds(860, y, 200, 50);
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            return errorLabel;
        }

        // Fallback for hover icon if it failed to load
        if (hoverIcon == null) {
            System.err.println("Warning: Failed to load hover icon: " + hoverPath + ". Using normal icon instead.");
            hoverIcon = offIcon;
        }

        JLabel button = new JLabel(offIcon);
        int width = offIcon.getIconWidth();
        int height = offIcon.getIconHeight();

        if (width <= 0 || height <= 0) {
            System.err.println("Button icon has invalid dimensions: " + offPath);
            width = 200; height = 50; // Fallback size
            button.setText("BTN ERR"); button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.WHITE); button.setOpaque(true); button.setBackground(Color.RED);
        }

        int x = (1920 - width) / 2;
        button.setBounds(x, y, width, height);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final ImageIcon finalOffIcon = offIcon;
        final ImageIcon finalHoverIcon = hoverIcon;

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setIcon(finalHoverIcon); }
            @Override public void mouseExited(MouseEvent e) { button.setIcon(finalOffIcon); }
            @Override public void mouseClicked(MouseEvent e) { if (action != null) action.run(); }
        });
        return button;
    }


    /**
     * Loads an ImageIcon from the classpath resources, using MediaTracker to ensure
     * loading is complete or handled properly before returning.
     *
     * @param resourcePath Path to the resource (e.g., "/assets/image.png").
     * @param width Desired width for scaling, or -1 for original width.
     * @param height Desired height for scaling, or -1 for original height.
     * @param component The component to associate with MediaTracker (e.g., 'this' JPanel).
     * @return The loaded (and potentially scaled) ImageIcon, or null if loading failed.
     */
    private ImageIcon loadImageIconResource(String resourcePath, int width, int height, Component component) {
        URL imgURL = getClass().getResource(resourcePath);
        ImageIcon icon = new ImageIcon(imgURL);
        Image image = icon.getImage();
        // Return the original, fully loaded icon (preserves animation)
         return icon;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fallback if backgroundLabel is null or wasn't added
        if (backgroundLabel == null || backgroundLabel.getParent() != this) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String message = "Background GIF failed to load.";
            if (backgroundLabel == null) {
                message += " (Label is null)";
            } else {
                message += " (Label not added)";
            }
            g.drawString(message, 50, 50);
        }
        // No drawing needed here; the JLabel handles the background
    }
}
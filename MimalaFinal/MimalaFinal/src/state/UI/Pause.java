package state.UI;

import state.GameScreen;
import state.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

// Removed: import static util.AudioManager.stopMusic; // Pause screen shouldn't stop music

public class Pause extends JPanel {

    private final JFrame frame;
    private final Image pauseBackground;
    private final GameScreen gameScreen; // Reference to the game screen being paused

    // --- Button Paths --- (Use getResource consistently)
    private final String resumeOffPath = "/assets/PauseScreen/PAUSE/Resume/Resume_Off.png";
    private final String resumeHoverPath = "/assets/PauseScreen/PAUSE/Resume/Resume_Hover.png";
    private final String exitOffPath = "/assets/PauseScreen/PAUSE/Exit/Exit_off.png";
    private final String exitHoverPath = "/assets/PauseScreen/PAUSE/Exit/Exit_hover.png";

    public Pause(JFrame frame, GameScreen gameScreen) {
        this.frame = frame;
        this.gameScreen = gameScreen; // Store the reference
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true); // Allow pause screen itself to get focus if needed

        // Load background
        pauseBackground = new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/PauseScreen.gif")).getImage();

        // === Resume Button ===
        JLabel resumeButton = createButton(
                resumeOffPath, resumeHoverPath,
                500, 548, // off position
                500, 548, // hover position (Adjust if hover image is different size/pos)
                () -> { // Action on click
                    // <<< CALL resumeGame() on the stored gameScreen instance >>>
                    gameScreen.resumeGame();
                    // Now switch back the content pane
                    frame.setContentPane(gameScreen);
                    frame.revalidate();
                    frame.repaint();
                    // GameScreen's resumeGame should handle requesting focus back
                },
                this // ImageObserver
        );

        // === Exit Button ===
        JLabel exitButton = createButton(
                exitOffPath, exitHoverPath,
                500, 636, // off position
                500, 636, // hover position (Adjust if needed)
                () -> {
                    // Ensure game state is cleaned up if exiting mid-game?
                    // gameScreen.cleanup(); // Optional: if GameScreen needs specific cleanup on exit
                    JPanel mainMenu = new MainMenu(frame);
                    // No need for opaque/background settings here, MainMenu handles itself
                    frame.setContentPane(mainMenu);
                    frame.revalidate();
                    frame.repaint();
                    // Consider stopping any game-related audio managed outside GameScreen here
                },
                this // ImageObserver
        );

        add(resumeButton);
        add(exitButton);

        // Optional: Add Escape key binding to Resume from Pause screen
        setupEscapeKeyBinding(frame);

        // Request focus for the pause screen itself
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // <<< Optional: Add Escape key binding to resume >>>
    private void setupEscapeKeyBinding(JFrame frame) {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        KeyStroke escapeKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        String actionKey = "resumeGameAction";

        im.put(escapeKey, actionKey);
        am.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("Escape pressed on Pause screen. Resuming game.");
                // Same action as the resume button
                gameScreen.resumeGame();
                frame.setContentPane(gameScreen);
                frame.revalidate();
                frame.repaint();
            }
        });
    }


    // --- createButton method --- (Ensure it handles null icons)
    private JLabel createButton(String offPath, String hoverPath,
                                int offX, int offY, int hoverX, int hoverY,
                                Runnable action, Component componentForTracker) {

        final ImageIcon offIcon = loadImageIconResource(offPath, -1, -1, componentForTracker);
        final ImageIcon hoverIcon = loadImageIconResource(hoverPath, -1, -1, componentForTracker);

        // Handle critical failure for offIcon
        if (offIcon == null) {
            System.err.println("FATAL: Failed to load OFF icon: " + offPath);
            JLabel errorLabel = new JLabel("BTN ERR");
            errorLabel.setBounds(offX, offY, 100, 30); // Placeholder size
            errorLabel.setForeground(Color.RED);
            errorLabel.setOpaque(true);
            errorLabel.setBackground(Color.DARK_GRAY);
            return errorLabel;
        }
        // Use offIcon if hoverIcon failed to load
        final ImageIcon finalHoverIcon = (hoverIcon != null) ? hoverIcon : offIcon;


        final int offWidth = offIcon.getIconWidth();
        final int offHeight = offIcon.getIconHeight();
        // Use hover dimensions if available, otherwise off dimensions
        final int hoverWidth = (hoverIcon != null) ? hoverIcon.getIconWidth() : offWidth;
        final int hoverHeight = (hoverIcon != null) ? hoverIcon.getIconHeight() : offHeight;

        final JLabel button = new JLabel(offIcon);
        button.setBounds(offX, offY, offWidth, offHeight);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(finalHoverIcon); // Use finalHoverIcon (might be same as offIcon)
                button.setBounds(hoverX, hoverY, hoverWidth, hoverHeight); // Adjust bounds on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(offIcon);
                button.setBounds(offX, offY, offWidth, offHeight); // Reset bounds
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }
        });

        return button;
    }

    // --- loadImageIconResource method --- (Use getResource consistently)
    private ImageIcon loadImageIconResource(String resourcePath, int width, int height, Component component) {
        // Ensure path starts with "/" for absolute classpath resource lookup
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        URL imgURL = getClass().getResource(resourcePath);
        if (imgURL == null) {
            System.err.println("Image not found: " + resourcePath);
            return null; // Return null if not found
        }
        ImageIcon icon = new ImageIcon(imgURL);
        // Optional scaling (not used here as width/height are -1)
        if (width > 0 && height > 0 && icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return icon; // Return original icon
    }

    // --- paintComponent method --- (Keep as is)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (pauseBackground != null) {
            g.drawImage(pauseBackground, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK); // Fallback
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Pause BG Missing", 50, 50);
        }
    }
}
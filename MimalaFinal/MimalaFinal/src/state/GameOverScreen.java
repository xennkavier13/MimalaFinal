package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameOverScreen extends JPanel {

    private final JFrame frame;
    private final ImageIcon background;
    // Define paths for your assets
    private final String bgPath = "MimalaFinal/MimalaFinal/src/assets/GameOverScreen/GameOverBG.png"; // CHANGE TO YOUR BG PATH
    private final String restartOffPath = "MimalaFinal/MimalaFinal/src/assets/GameOverScreen/Buttons/Restart_Off.png"; // CHANGE PATH
    private final String restartHoverPath = "MimalaFinal/MimalaFinal/src/assets/GameOverScreen/Buttons/Restart_Hover.png"; // CHANGE PATH
    private final String menuOffPath = "MimalaFinal/MimalaFinal/src/assets/GameOverScreen/Buttons/Menu_Off.png"; // CHANGE PATH
    private final String menuHoverPath = "MimalaFinal/MimalaFinal/src/assets/GameOverScreen/Buttons/Menu_Hover.png"; // CHANGE PATH


    public GameOverScreen(JFrame frame) {
        this.frame = frame;
        this.background = new ImageIcon(bgPath); // Load background image

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080)); // Match game resolution
        setupButtons();
    }

    private void setupButtons() {
        // --- Restart Button ---
        JLabel restartButton = createButton(
                restartOffPath,
                restartHoverPath,
                600, // Adjust Y position as needed
                () -> {
                    System.out.println("Restart button clicked");
                    // Transition back to ModeSelection (or CharacterSelection)
                    frame.setContentPane(new ModeSelection(frame)); // Or your starting screen
                    frame.revalidate();
                    frame.repaint();
                }
        );

        // --- Main Menu Button ---
        JLabel menuButton = createButton(
                menuOffPath,
                menuHoverPath,
                700, // Adjust Y position as needed
                () -> {
                    System.out.println("Main Menu button clicked");
                    // Transition back to Main Menu or exit
                    // Example: Go back to ModeSelection (if that's your main menu)
                    frame.setContentPane(new ModeSelection(frame)); // Or new MainMenu(frame)
                    // Or Exit: System.exit(0);
                    frame.revalidate();
                    frame.repaint();
                }
        );

        add(restartButton);
        add(menuButton);
    }

    // Re-use or adapt the button creation logic from ModeSelection
    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action) {
        ImageIcon offIcon = loadImageIcon(offPath);
        ImageIcon hoverIcon = loadImageIcon(hoverPath);

        if (offIcon == null || hoverIcon == null) {
            System.err.println("Error loading button icons for Game Over screen.");
            // Return an empty label or handle error appropriately
            JLabel errorLabel = new JLabel("Error");
            errorLabel.setBounds(860, y, 200, 50);
            return errorLabel;
        }

        JLabel button = new JLabel(offIcon);
        int x = (1920 - offIcon.getIconWidth()) / 2; // Center button horizontally
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(offIcon);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Optional: Add sound effect here
                action.run();
            }
        });
        return button;
    }

    // Helper method to handle image loading with error checking
    private ImageIcon loadImageIcon(String path) {
        try {
            // Using ClassLoader might be more reliable for resources
            java.net.URL imgURL = getClass().getClassLoader().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                // Fallback for direct path (less reliable)
                System.err.println("Warning: Could not load image as resource: " + path + ". Trying direct path.");
                ImageIcon icon = new ImageIcon(path);
                if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                    System.err.println("ERROR: Failed to load image icon from path: " + path);
                    return null;
                }
                return icon;
            }

        } catch (Exception e) {
            System.err.println("ERROR: Exception loading image icon: " + path);
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        if (background != null && background.getImageLoadStatus() == MediaTracker.COMPLETE) {
            g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK); // Fallback
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Game Over - BG Error", 100, 100);
            System.err.println("Game Over background image error!");
        }
    }
}
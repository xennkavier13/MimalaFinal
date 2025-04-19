package state.CharacterScreen.Select;

import state.MapSelection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Random;

public abstract class CharacterSelectionScreen extends JPanel {
    protected final JFrame frame; // Make protected if needed by subclasses directly
    protected final String mode;  // Make protected if needed by subclasses directly
    private JPanel confirmationPanel;
    protected ImageIcon characterGif; // Keep protected

    private JPanel infoPanel;
    private boolean infoVisible = false;
    private JLabel closeInfoButton;
    private boolean closeVisible = false;

    // *** NEW: Store Player 1's selection if this screen is for Player 2 ***
    private final String firstPlayerSelection;

    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon", // Check if Aurelix or Auricannon
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };

    protected abstract String getGifPath();
    protected abstract String getCharacterName(); // Ensure this returns the character for THIS screen

    // *** NEW: Constructor called by SecondPlayerSelection (for P2) ***
    public CharacterSelectionScreen(JFrame frame, String mode, String firstPlayerSelection) {
        this.frame = frame;
        this.mode = mode;
        // Store the choice made by Player 1
        this.firstPlayerSelection = firstPlayerSelection;

        setPreferredSize(new Dimension(1920, 1080));
        setLayout(null);
        this.characterGif = loadImage(getGifPath());
        if (this.characterGif == null) {
            System.err.println("CRITICAL: Failed to load background GIF: " + getGifPath());
        }
        setupButtons();

    }

    public CharacterSelectionScreen(JFrame frame, String mode) {
        this(frame, mode, null); // Delegate to the new constructor, P1's choice is null initially
    }


    private ImageIcon loadImage(String resourcePath) {
        // loadImage implementation remains the same...
        if (resourcePath == null || resourcePath.isEmpty()) return null;
        try {
            URL imgURL = getClass().getResource(resourcePath);
            return (imgURL != null) ? new ImageIcon(imgURL) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JLabel backButton;
    private JLabel chooseButton;
    private JLabel infoButton;

    private void setupButtons() {
        String backOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png";
        String backHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png";

        String chooseOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_off.png";
        String chooseHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_hover.png";

        String infoBtnOff = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Info/info_off.png";
        String infoBtnHover = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Info/info_hover.png";

        infoButton = createButton(infoBtnOff, infoBtnHover, 1100, 825, () -> {
            if (infoVisible && closeVisible) {
                removeInfoPanel();
            } else {
                showInfoPanel();
            }
        });
        add(infoButton);

        backButton = createButton(backOffPath, backHoverPath, 50, 57, () -> {
            if (confirmationPanel == null) {
                // Go back to the correct previous screen
                if (this.firstPlayerSelection != null) {
                    // If P1 selection exists, we came from P2 selection
                    frame.setContentPane(new SecondPlayerSelection(frame, this.firstPlayerSelection, this.mode));
                } else {
                    // Otherwise, we came from P1 selection
                    frame.setContentPane(new CharacterSelection(frame, this.mode));
                }
                frame.revalidate();
                frame.repaint();
            }
        });
        add(backButton);

        chooseButton = createButton(chooseOffPath, chooseHoverPath, 1280, 825, () -> {
            if (confirmationPanel == null) {
                showConfirmationScreen();
            }
        });
        add(chooseButton);
    }

    private void showInfoPanel() {
        if (infoPanel != null) return;

        infoPanel = new JPanel(null);
        infoPanel.setBounds(0, 0, 1920, 1080);
        infoPanel.setOpaque(false);

        ImageIcon infoImage = loadImage(getInfoImagePath());
        if (infoImage != null) {
            JLabel infoLabel = new JLabel(infoImage);
            infoLabel.setBounds(0, 0, 1920, 1080);
            infoPanel.add(infoLabel);
        }

        closeInfoButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Close_button.png",
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Close_button.png",
                1470, 280, () -> {
                    removeInfoPanel();
                    closeInfoButton.setVisible(false);  // Hide the close button after clicking
                }
        );

        add(infoPanel);
        infoPanel.add(closeInfoButton);
        setComponentZOrder(closeInfoButton, 0);

        backButton.setEnabled(false);
        chooseButton.setEnabled(false);
        infoButton.setEnabled(false);

        closeVisible = true;
        infoVisible = true;
        revalidate();
        repaint();
    }

    private void removeInfoPanel() {
        if (infoPanel != null) {
            remove(infoPanel);
            infoPanel = null;
            closeVisible = false;
            infoVisible = false;

            revalidate();
            repaint();
        }
        backButton.setEnabled(true);
        chooseButton.setEnabled(true);
        infoButton.setEnabled(true);

        // Make sure to hide the close button after it's clicked
        if (closeVisible) {
            closeInfoButton.setVisible(false); // Hide the close button when clicked
        }
    }

    private void showConfirmationScreen() {
        // showConfirmationScreen implementation remains the same...
        if (confirmationPanel != null) {
            remove(confirmationPanel);
        }

        confirmationPanel = new JPanel(null);
        confirmationPanel.setBounds(0, 0, 1920, 1080);
        confirmationPanel.setOpaque(false);

        JLabel confirmImage = new JLabel(loadImage("/assets/CharacterSelectionScreen/CharacterConfirm/CharacterConfirm_off.png"));
        confirmImage.setBounds(0, 0, 1920, 1080);
        confirmationPanel.add(confirmImage);

        JLabel yesButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterConfirm/Yes_off.png",
                "/assets/CharacterSelectionScreen/CharacterConfirm/Yes_hover.png",
                700, 575, this::continueToNextScreen // Call the updated logic
        );

        JLabel noButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterConfirm/No_off.png",
                "/assets/CharacterSelectionScreen/CharacterConfirm/No_hover.png",
                923, 573, this::removeConfirmation
        );

        confirmationPanel.add(yesButton);
        confirmationPanel.add(noButton);
        add(confirmationPanel);
        setComponentZOrder(confirmationPanel, 0); // Bring confirmation panel to front

        // Ensure buttons are added correctly relative to confirmImage if needed
        confirmationPanel.setComponentZOrder(yesButton, 0);
        confirmationPanel.setComponentZOrder(noButton, 0);


        // Disable Back and Continue buttons
        backButton.setEnabled(false);
        chooseButton.setEnabled(false);
        infoButton.setEnabled(false);

        revalidate();
        repaint();
    }


    private void removeConfirmation() {
        // removeConfirmation implementation remains the same...
        if (confirmationPanel != null) {
            remove(confirmationPanel);
            confirmationPanel = null;

            backButton.setEnabled(true);
            chooseButton.setEnabled(true);
            infoButton.setEnabled(true);

            revalidate(); // Revalidate after removing component
            repaint();
        }
    }

    // *** UPDATED LOGIC FOR CONTINUING ***
    private void continueToNextScreen() {
        String currentCharacterSelection = getCharacterName(); // The character confirmed on THIS screen

        // Check if this screen instance holds Player 1's selection
        if (this.firstPlayerSelection != null && "PVP".equalsIgnoreCase(this.mode)) {
            // --- This means Player 2 is confirming ---
            // P1's choice is stored in this.firstPlayerSelection
            // P2's choice is currentCharacterSelection
            System.out.println("Player 2 confirmed: " + currentCharacterSelection);
            System.out.println("Proceeding to Map Selection with P1: " + this.firstPlayerSelection + " and P2: " + currentCharacterSelection);

            // Proceed to Map Selection with both choices
            frame.setContentPane(new MapSelection(frame, this.firstPlayerSelection, currentCharacterSelection, "PVP"));

        } else {
            // --- This means Player 1 is confirming (or it's PVC mode or Arcade mode) ---
            String player1Choice = currentCharacterSelection; // P1 has confirmed this character
            System.out.println("Player 1 confirmed: " + player1Choice);

            if ("PVC".equalsIgnoreCase(this.mode)) {
                System.out.println("Mode is PVC. Selecting AI opponent and proceeding to Map Selection.");
                String player2Selection = selectRandomCharacterForPVE(player1Choice);
                System.out.println("AI selected: " + player2Selection);
                frame.setContentPane(new MapSelection(frame, player1Choice, player2Selection, "PVC"));

            } else if ("PVP".equalsIgnoreCase(this.mode)) {
                System.out.println("Mode is PVP. Proceeding to Second Player Selection.");
                // Proceed to Player 2's selection screen, passing P1's confirmed choice
                frame.setContentPane(new SecondPlayerSelection(frame, player1Choice, "PVP"));
            } else if ("ARCADE".equalsIgnoreCase(this.mode)) {
                // --- Arcade Mode Logic ---
                System.out.println("Mode is Arcade. Selecting AI opponent and proceeding to Map Selection.");

                // Randomly select a character for Player 2 (AI opponent)
                String player2Selection = selectRandomCharacterForArcade(player1Choice);
                System.out.println("Arcade Mode AI selected: " + player2Selection);

                // Proceed to the Map Selection screen with Player 1 and AI opponent
                frame.setContentPane(new MapSelection(frame, player1Choice, player2Selection, "ARCADE"));
            }
        }

        frame.revalidate();
        frame.repaint();
    }

    private String selectRandomCharacterForArcade(String player1Choice) {
        // Arcade mode random selection - AI chooses from the available pool, ensuring not to pick Player 1's character
        Random random = new Random();
        String aiChoice;
        do {
            aiChoice = ALL_CHARACTER_NAMES[random.nextInt(ALL_CHARACTER_NAMES.length)];
        } while (aiChoice.equals(player1Choice)); // Ensure AI doesn't pick the same character
        return aiChoice;
    }


    private JLabel createButton(String offResourcePath, String hoverResourcePath, int x, int y, Runnable action) {
        // createButton implementation remains the same...
        ImageIcon offIcon = loadImage(offResourcePath);
        ImageIcon hoverIcon = loadImage(hoverResourcePath);

        // Add basic error handling for missing icons
        if (offIcon == null) {
            System.err.println("Error loading button icon: " + offResourcePath);
            return new JLabel("Error"); // Return a placeholder
        }

        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Add hand cursor
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled() && hoverIcon != null) button.setIcon(hoverIcon);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setIcon(offIcon);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (button.isEnabled()) action.run();
            }
        });
        return button;
    }

    private String selectRandomCharacterForPVE(String player1Choice) {
        // selectRandomCharacterForPVE implementation remains the same...
        Random random = new Random();
        String aiChoice;
        do {
            aiChoice = ALL_CHARACTER_NAMES[random.nextInt(ALL_CHARACTER_NAMES.length)];
        } while (aiChoice.equals(player1Choice)); // Ensure AI doesn't pick the same character
        return aiChoice;
    }


    @Override
    protected void paintComponent(Graphics g) {
        // paintComponent implementation remains the same...
        super.paintComponent(g);
        if (characterGif != null && characterGif.getImage() != null) {
            g.drawImage(characterGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Error: Background GIF not loaded! Path: " + getGifPath() , 50, 50);
        }
        // If confirmationPanel exists, it should be painted on top automatically by Swing's Z-order
    }

    protected abstract String getInfoImagePath();
}
package state.CharacterScreen.Select;

import state.CharacterScreen.*;
import state.MapSelection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Random;

public class SecondCharacterSelectionScreen extends JPanel {

    private final JFrame frame;
    private final String characterName; // The character being displayed
    private final String mode;
    private final String firstPlayerSelection; // Null if P1 is viewing, non-null if P2 is viewing

    private final ImageIcon characterGif;
    private JPanel confirmationPanel;

    private JLabel backButton;
    private JLabel chooseButton;
    private JLabel infoButton;

    private JPanel infoPanel;
    private boolean infoVisible = false;
    private JLabel closeInfoButton;
    private boolean closeVisible = false;

    private JLabel infoOverlay;


    // Base paths - assuming a structure like /assets/CharacterScreenStuff/...
    private final String GIF_PATH_BASE = "/assets/CharacterSelectionScreen/CharacterScreen/"; // Adjust if needed
    private final String BUTTON_PATH_BASE = "/assets/CharacterSelectionScreen/CharacterScreenButtons/";

    // All character names (for random AI selection)
    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon", // Ensure consistency
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };

    public SecondCharacterSelectionScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        this.frame = frame;
        this.characterName = characterName;
        this.mode = mode;
        this.firstPlayerSelection = firstPlayerSelection; // Will be null for P1

        setPreferredSize(new Dimension(1920, 1080));
        setLayout(null);

        // Load the specific character's GIF dynamically
        this.characterGif = loadImage(getGifPath());
        if (this.characterGif == null) {
            System.err.println("CRITICAL: Failed to load background GIF for " + characterName);
        }

        setupButtons();

    }

    // Dynamically determines the GIF path based on character name
    private String getGifPath() {
        // Add any necessary name mappings here if filenames differ
        String gifName = characterName;
        // Example mapping: if (characterName.equals("Astridra")) gifName = "Astrida";
        return GIF_PATH_BASE + gifName + "Selection.gif"; // Assuming this naming convention
    }

    private ImageIcon loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            System.err.println("Error: Null or empty resource path provided.");
            return null;
        }
        try {
            URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("CharacterInfoScreen: Resource not found: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("CharacterInfoScreen: Error loading image resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    private void setupButtons() {
        // --- Back Button ---
        String backOffPath = BUTTON_PATH_BASE + "Back/Back_off.png";
        String backHoverPath = BUTTON_PATH_BASE + "Back/Back_hover.png";

        backButton = createButton(backOffPath, backHoverPath, 50, 57, () -> {
            if (confirmationPanel == null) { // Only allow back if confirmation isn't shown
                // Go back to the correct previous screen
                if (this.firstPlayerSelection != null) {
                    // If firstPlayerSelection exists, P2 was viewing, go back to P2 selection grid
                    System.out.println("Back pressed from P2 view. Returning to SecondPlayerSelection.");
                    frame.setContentPane(new SecondPlayerSelection(frame, this.firstPlayerSelection, this.mode));
                } else {
                    // Otherwise, P1 was viewing, go back to P1 selection grid
                    System.out.println("Back pressed from P1 view. Returning to CharacterSelection.");
                    frame.setContentPane(new CharacterSelection(frame, this.mode));
                }
                frame.revalidate();
                frame.repaint();
            }
        });
        add(backButton);

        String contOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_off.png";
        String contHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_hover.png";

        chooseButton = createButton(contOffPath, contHoverPath, 1280, 825, () -> {
            if (confirmationPanel == null) {
                showConfirmationScreen();
            }
        });
        // Check if continue buttons loaded, provide fallback text if not
        if (chooseButton.getIcon() == null) {
            System.err.println("Failed to load Continue button icons at path prefix: " + BUTTON_PATH_BASE + "Select/");
            chooseButton.setText("SELECT " + characterName);
            chooseButton.setForeground(Color.GREEN);
            chooseButton.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            // Adjust bounds if using text
            chooseButton.setBounds(1200, 890, 200, 50);
        }
        add(chooseButton);

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
    }

    private CharacterSelectionScreen getCharacterScreenInstance() {
        switch (characterName) {
            case "Astridra":
                return new AstridraScreen(frame, mode);
            case "Ignisveil":
                return new IgnisveilScreen(frame, mode);
            case "Zenfang":
                return new ZenfangScreen(frame, mode);
            case "Varkos":
                return new VarkosScreen(frame, mode);
            case "Vexmorth":
                return new VexmorthScreen(frame, mode);
            case "Auricannon":
                return new AuricannonScreen(frame, mode);
            case "Azurox":
                return new AzuroxScreen(frame, mode);
            case "Pyrothar":
                return new PyrotharScreen(frame, mode);
            default:
                System.err.println("Unknown character: " + characterName);
                return null;
        }
    }

    private String getInfoImagePath() {
        CharacterSelectionScreen screen = getCharacterScreenInstance();
        if (screen != null) {
            return screen.getInfoImagePath();
        }
        System.err.println("Could not retrieve info image path for character: " + characterName);
        return null;
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

        if (closeVisible) {
            closeInfoButton.setVisible(false); // Hide the close button when clicked
        }
    }

    private void showConfirmationScreen() {
        if (confirmationPanel != null) {
            remove(confirmationPanel); // Remove if already exists
        }

        confirmationPanel = new JPanel(null); // Use null layout for absolute positioning inside
        confirmationPanel.setBounds(0, 0, 1920, 1080); // Centered position
        confirmationPanel.setOpaque(false); // Make panel transparent

        // Background for confirmation
        JLabel confirmImage = new JLabel(loadImage(BUTTON_PATH_BASE + "../CharacterConfirm/CharacterConfirm_off.png")); // Path to confirm background
        if (confirmImage.getIcon() != null) {
            confirmImage.setBounds(0, 0, 1920, 1080); // Position relative to confirmationPanel
            confirmationPanel.add(confirmImage);
        } else {
            System.err.println("Confirmation background image failed to load.");
            confirmationPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2)); // Visual fallback
            confirmationPanel.setOpaque(true);
            confirmationPanel.setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black fallback
        }

        // Yes Button
        JLabel yesButton = createButton(
                BUTTON_PATH_BASE + "../CharacterConfirm/Yes_off.png",
                BUTTON_PATH_BASE + "../CharacterConfirm/Yes_hover.png",
                700, 575, // Position relative to confirmationPanel
                this::confirmSelection // Action: Call the confirmation logic
        );
        confirmationPanel.add(yesButton);

        // No Button
        JLabel noButton = createButton(
                BUTTON_PATH_BASE + "../CharacterConfirm/No_off.png",
                BUTTON_PATH_BASE + "../CharacterConfirm/No_hover.png",
                923, 573, // Position relative to confirmationPanel
                this::removeConfirmation // Action: Just remove the panel
        );
        confirmationPanel.add(noButton);


        // Add confirmation panel to the main panel and bring to front
        add(confirmationPanel);
        setComponentZOrder(confirmationPanel, 0);

        // Bring buttons to front *within* confirmation panel if confirmImage was added
        if (confirmImage.getIcon() != null) {
            confirmationPanel.setComponentZOrder(yesButton, 0);
            confirmationPanel.setComponentZOrder(noButton, 0);
        }


        // Disable Back and Continue buttons while confirmation is visible
        backButton.setEnabled(false);
        chooseButton.setEnabled(false);
        infoButton.setEnabled(false);

        revalidate();
        repaint();
    }

    private void removeConfirmation() {
        if (confirmationPanel != null) {
            remove(confirmationPanel);
            confirmationPanel = null;

            backButton.setEnabled(true);
            chooseButton.setEnabled(true);
            infoButton.setEnabled(true);

            revalidate();
            repaint();
        }
    }

    // *** CORE LOGIC: Called when 'Yes' is clicked on confirmation ***
    private void confirmSelection() {
        // The character being confirmed is 'this.characterName'

        // Case 1: Player 1 is confirming (firstPlayerSelection is null)
        if (this.firstPlayerSelection == null) {
            String player1Choice = this.characterName;
            System.out.println("Player 1 CONFIRMED: " + player1Choice);

            if ("PVP".equalsIgnoreCase(this.mode)) {
                // P1 confirmed, now go to P2's selection grid
                System.out.println("Mode is PVP. Proceeding to Second Player Selection.");
                frame.setContentPane(new SecondPlayerSelection(frame, player1Choice, this.mode));
            } else { // PVE mode ("PVC" or other assumed PVE modes)
                // P1 confirmed, AI selects, then go to MapSelection
                System.out.println("Mode is PVE/PVC. Selecting AI opponent.");
                String player2Choice = selectRandomCharacterForPVE(player1Choice);
                System.out.println("AI selected: " + player2Choice);
                System.out.println("Proceeding to Map Selection.");
                frame.setContentPane(new MapSelection(frame, player1Choice, player2Choice,"PVC")); // Assuming MapSelection constructor
            }
        }
        // Case 2: Player 2 is confirming (firstPlayerSelection is NOT null)
        else {
            String player2Choice = this.characterName;
            System.out.println("Player 2 CONFIRMED: " + player2Choice);
            System.out.println("Player 1 was: " + this.firstPlayerSelection);
            // Both players selected (P1 in firstPlayerSelection, P2 in player2Choice)
            // Proceed to Map Selection
            System.out.println("Proceeding to Map Selection.");
            frame.setContentPane(new MapSelection(frame, this.firstPlayerSelection, player2Choice,"PVP")); // Pass P1's and P2's choices
        }

        // Update the frame
        frame.revalidate();
        frame.repaint();
    }


    private JLabel createButton(String offResourcePath, String hoverResourcePath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadImage(offResourcePath);
        ImageIcon hoverIcon = loadImage(hoverResourcePath);

        // Handle missing icons gracefully
        if (offIcon == null) {
            System.err.println("Error loading button icon: " + offResourcePath);
            // Create a fallback text label if icon fails
            JLabel errorButton = new JLabel("Btn?");
            errorButton.setBounds(x, y, 80, 30); // Placeholder size
            errorButton.setForeground(Color.RED);
            errorButton.setBorder(BorderFactory.createLineBorder(Color.RED));
            errorButton.setOpaque(true);
            errorButton.setBackground(Color.DARK_GRAY);
            errorButton.addMouseListener(new MouseAdapter() { // Still make it clickable
                @Override public void mouseClicked(MouseEvent e) { if (errorButton.isEnabled()) action.run(); }
            });
            return errorButton;
        }

        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled() && hoverIcon != null) button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setIcon(offIcon); // Revert to offIcon
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (button.isEnabled()) action.run(); // Execute the provided action
            }
        });
        return button;
    }

    // Helper for PVE AI selection
    private String selectRandomCharacterForPVE(String player1Choice) {
        Random random = new Random();
        String aiChoice;
        do {
            aiChoice = ALL_CHARACTER_NAMES[random.nextInt(ALL_CHARACTER_NAMES.length)];
        } while (aiChoice.equals(player1Choice)); // Ensure AI doesn't pick the same character
        return aiChoice;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the character GIF background
        if (characterGif != null && characterGif.getImage() != null) {
            g.drawImage(characterGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback if GIF failed
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Error: Background GIF not loaded for " + characterName, 50, 100);
            g.drawString("Expected Path: " + getGifPath() , 50, 130);
        }
        // Confirmation panel is added as a component, Swing handles painting it on top.
    }
}
package state.CharacterScreen.Select;

// Add necessary imports
import state.*; // For GameScreen, MapSelection, ModeSelection etc.
import util.PlayerName; // Assuming you might use this? Not strictly needed here.
import state.character.CharacterDataLoader; // For potentially better opponent selection later

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CharacterSelectionScreen extends JPanel {
    protected final JFrame frame;
    protected final String mode;
    private JPanel confirmationPanel;
    protected ImageIcon characterGif;

    private JPanel infoPanel;
    // Removed unused flags: infoVisible, closeVisible
    private JLabel closeInfoButton;

    // Stores Player 1's selection if this screen is confirming for Player 2 (in PVP)
    private final String firstPlayerSelection;

    // For random selection helpers if CharacterDataLoader isn't used
    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };
    // Needed for Arcade random map selection
    private final String[] mapPaths = {
            "assets/StageMap/Waters.gif",
            "assets/StageMap/Dojo.gif",
            "assets/StageMap/DesertedLand.gif",
            "assets/StageMap/DragonLair.gif",
            "assets/StageMap/King'sThrone.gif",
            "assets/StageMap/RoyalPalace.gif"
    };
    // Random instance for selections
    private final Random random = new Random();

    // --- Abstract methods for subclasses ---
    protected abstract String getGifPath();
    protected abstract String getCharacterName(); // Character for THIS screen
    protected abstract String getInfoImagePath(); // Path for the info panel image

    // --- Main Constructor ---
    public CharacterSelectionScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        // Store passed arguments
        this.frame = frame;
        this.mode = mode;
        this.firstPlayerSelection = firstPlayerSelection; // Will be null if P1 is choosing

        // Basic panel setup
        setPreferredSize(new Dimension(1920, 1080));
        setLayout(null);

        // Load main background GIF using subclass method
        this.characterGif = loadImage(getGifPath());
        if (this.characterGif == null) {
            System.err.println("CRITICAL: Failed to load background GIF: " + getGifPath() + " for " + getCharacterName());
            // Consider adding a fallback background color
        }

        // Setup common UI elements (buttons)
        setupButtons();

        // Add Escape Key Binding
        setupEscapeKeyBinding(); // Pass frame no longer needed
        setFocusable(true); // Make sure panel can receive key events
        // Request focus after component is added to frame
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // --- Delegating Constructor (Used by CharacterSelection for P1) ---
    // This constructor seems unnecessary if CharacterSelection directly calls the main one with null.
    // If CharacterSelection *only* passes (frame, mode), this needs to be removed or the main
    // constructor needs to handle the characterName differently (e.g., by calling getCharacterName() itself).
    // Let's comment this out for now, assuming CharacterSelection passes all needed args.
    /*
    public CharacterSelectionScreen(JFrame frame, String mode) {
        this(frame, null, mode, null); // How does it know the characterName without it being passed?
                                      // This constructor likely needs removal or redesign.
                                      // Assumes CharacterSelection calls the 4-arg constructor directly.
    }
    */


    // --- Helper Methods ---

    private ImageIcon loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            System.err.println("loadImage: null or empty resource path provided.");
            return null;
        }
        try {
            URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("loadImage: Resource not found: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("loadImage: Error loading image '" + resourcePath + "': " + e.getMessage());
            // e.printStackTrace(); // Optional: print stack trace for debugging
            return null;
        }
    }

    // Declare buttons as fields
    private JLabel backButton;
    private JLabel chooseButton;
    private JLabel infoButton;

    private void setupButtons() {
        // Define paths using constants if preferred
        String backOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png";
        String backHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png";
        String chooseOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_off.png";
        String chooseHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_hover.png";
        String infoBtnOff = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Info/info_off.png";
        String infoBtnHover = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Info/info_hover.png";

        // Create and add buttons
        backButton = createButton(backOffPath, backHoverPath, 50, 57, () -> {
            // Prevent action if overlay is visible
            if (confirmationPanel != null || infoPanel != null) return;
            // Go back to the correct previous screen
            if (this.firstPlayerSelection != null) { // Came from P2 selection grid
                frame.setContentPane(new SecondPlayerSelection(frame, this.firstPlayerSelection, this.mode));
            } else { // Came from P1 selection grid
                frame.setContentPane(new CharacterSelection(frame, this.mode));
            }
            frame.revalidate(); frame.repaint();
        });
        add(backButton);

        chooseButton = createButton(chooseOffPath, chooseHoverPath, 1280, 825, () -> {
            // Prevent action if overlay is visible
            if (confirmationPanel != null || infoPanel != null) return;
            showConfirmationScreen();
        });
        add(chooseButton);

        infoButton = createButton(infoBtnOff, infoBtnHover, 1110, 825, () -> {
            if (infoPanel != null) { // If info is visible, hide it
                removeInfoPanel();
            } else if (confirmationPanel == null) { // Only show if confirmation isn't up
                showInfoPanel();
            }
        });
        add(infoButton);
    }


    private void showInfoPanel() {
        if (infoPanel != null || confirmationPanel != null) return; // Don't show if other panel is up

        infoPanel = new JPanel(null); infoPanel.setBounds(0, 0, 1920, 1080); infoPanel.setOpaque(false);

        ImageIcon infoImage = loadImage(getInfoImagePath()); // Use abstract method
        if (infoImage != null) {
            JLabel infoLabel = new JLabel(infoImage); infoLabel.setBounds(0, 0, 1920, 1080); infoPanel.add(infoLabel);
        } else { infoPanel.setBackground(new Color(0,0,0,150)); /* Fallback */ }

        // --- Close Button for Info Panel ---
        String closeOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Close_button.png"; // Use correct path
        String closeHoverPath = closeOffPath; // No hover effect needed?
        closeInfoButton = createButton(closeOffPath, closeHoverPath, 1470, 280, this::removeInfoPanel); // Action removes panel
        infoPanel.add(closeInfoButton);
        infoPanel.setComponentZOrder(closeInfoButton, 0); // Ensure close button is clickable

        // Add panel and bring to front
        add(infoPanel); setComponentZOrder(infoPanel, 0);

        // Disable main buttons while info is shown
        backButton.setEnabled(false); chooseButton.setEnabled(false); infoButton.setEnabled(false);
        revalidate(); repaint();
    }

    private void removeInfoPanel() {
        if (infoPanel != null) {
            remove(infoPanel); infoPanel = null; // Remove panel and clear reference
            // Re-enable main buttons
            backButton.setEnabled(true); chooseButton.setEnabled(true); infoButton.setEnabled(true);
            revalidate(); repaint();
        }
    }

    private void showConfirmationScreen() {
        if (confirmationPanel != null || infoPanel != null) return; // Don't show if info panel is up

        confirmationPanel = new JPanel(null); confirmationPanel.setBounds(0, 0, 1920, 1080); confirmationPanel.setOpaque(false);

        // Load confirmation background overlay
        ImageIcon confirmBgIcon = loadImage("/assets/CharacterSelectionScreen/CharacterConfirm/CharacterConfirm_off.png");
        if (confirmBgIcon != null) {
            JLabel confirmImage = new JLabel(confirmBgIcon);
            confirmImage.setBounds(0, 0, 1920, 1080);
            confirmationPanel.add(confirmImage);
        } else {
            // Fallback if image missing - semi-transparent overlay
            confirmationPanel.setOpaque(true);
            confirmationPanel.setBackground(new Color(0,0,0,200));
        }

        // --- Yes/No Buttons ---
        String yesOffPath = "/assets/CharacterSelectionScreen/CharacterConfirm/Yes_off.png";
        String yesHoverPath = "/assets/CharacterSelectionScreen/CharacterConfirm/Yes_hover.png";
        JLabel yesButton = createButton(yesOffPath, yesHoverPath, 700, 575, this::confirmSelection); // Calls updated logic

        String noOffPath = "/assets/CharacterSelectionScreen/CharacterConfirm/No_off.png";
        String noHoverPath = "/assets/CharacterSelectionScreen/CharacterConfirm/No_hover.png";
        JLabel noButton = createButton(noOffPath, noHoverPath, 923, 573, this::removeConfirmation);

        // Add buttons to the confirmation panel
        confirmationPanel.add(yesButton);
        confirmationPanel.add(noButton);

        // Add confirmation panel to the main screen panel and bring to front
        add(confirmationPanel);
        setComponentZOrder(confirmationPanel, 0);

        // If using a background image for confirmation, ensure buttons are layered on top of it
        if (confirmBgIcon != null) {
            confirmationPanel.setComponentZOrder(yesButton, 0);
            confirmationPanel.setComponentZOrder(noButton, 0);
        }

        // Disable main buttons while confirmation is shown
        backButton.setEnabled(false); chooseButton.setEnabled(false); infoButton.setEnabled(false);

        revalidate(); repaint();
    }


    private void removeConfirmation() {
        if (confirmationPanel != null) {
            remove(confirmationPanel); confirmationPanel = null; // Remove and clear reference
            // Re-enable main buttons
            backButton.setEnabled(true); chooseButton.setEnabled(true); infoButton.setEnabled(true);
            revalidate(); repaint();
        }
    }

    // *** THIS IS THE CORE LOGIC AFTER CONFIRMATION ***
    private void confirmSelection() {
        String confirmedCharacter = getCharacterName(); // Get character name from the specific subclass (e.g., "Astridra")

        // Case 1: Player 1 is confirming (firstPlayerSelection was null when screen created)
        if (this.firstPlayerSelection == null) {
            System.out.println("Player 1 CONFIRMED: " + confirmedCharacter);

            // --- Handle different modes AFTER P1 confirmation ---
            switch (this.mode.toUpperCase()) { // Use toUpperCase for case-insensitive compare
                case "PVP":
                    System.out.println("Mode is PVP. Proceeding to Second Player Selection grid.");
                    frame.setContentPane(new SecondPlayerSelection(frame, confirmedCharacter, this.mode)); // Pass P1's choice
                    break;

                case "PVC":
                    System.out.println("Mode is PVC. Selecting AI opponent and proceeding to Map Selection.");
                    String pvcOpponent = selectRandomOpponent(confirmedCharacter); // Select actual AI character
                    if (pvcOpponent == null) { // Error handling
                        JOptionPane.showMessageDialog(frame, "Error selecting AI opponent.", "Error", JOptionPane.ERROR_MESSAGE);
                        frame.setContentPane(new ModeSelection(frame)); return;
                    }
                    System.out.println("AI Opponent selected: " + pvcOpponent);
                    frame.setContentPane(new MapSelection(frame, confirmedCharacter, pvcOpponent, this.mode));
                    break;

                case "ARCADE":
                    System.out.println("Mode is Arcade. Resetting streak, selecting opponent/map, starting game...");
                    GameScreen.arcadeWins = 0; // Reset streak
                    String firstOpponent = selectRandomOpponent(confirmedCharacter);
                    String firstMap = selectRandomMap();

                    if (firstOpponent == null || firstMap == null) { // Error handling
                        System.err.println("Error setting up Arcade match (Opponent or Map missing).");
                        JOptionPane.showMessageDialog(frame, "Error starting Arcade mode.", "Arcade Error", JOptionPane.ERROR_MESSAGE);
                        frame.setContentPane(new ModeSelection(frame)); return;
                    }
                    System.out.println("Starting Arcade Game. P1: " + confirmedCharacter + ", Opponent: " + firstOpponent + ", Map: " + firstMap);
                    frame.setContentPane(new GameScreen(frame, confirmedCharacter, firstOpponent, firstMap, this.mode)); // Start GameScreen
                    break;

                default:
                    System.err.println("Unknown mode [" + this.mode + "] during P1 confirmation. Returning to Mode Selection.");
                    frame.setContentPane(new ModeSelection(frame));
                    break;
            }
        }
        // Case 2: Player 2 is confirming (firstPlayerSelection is NOT null) - Only happens in PVP
        else {
            String player2Choice = confirmedCharacter;
            System.out.println("Player 2 CONFIRMED: " + player2Choice);
            System.out.println("Player 1 was: " + this.firstPlayerSelection);

            // Optional: Prevent P2 choosing same character as P1
            if (player2Choice.equals(this.firstPlayerSelection)) {
                JOptionPane.showMessageDialog(frame, "Player 2 cannot choose the same character as Player 1.", "Character Already Selected", JOptionPane.WARNING_MESSAGE);
                removeConfirmation(); // Close confirmation panel
                return; // Stop progression
            }

            System.out.println("Both players confirmed (PVP). Proceeding to Map Selection.");
            frame.setContentPane(new MapSelection(frame, this.firstPlayerSelection, player2Choice, this.mode));
        }

        // Update the frame content
        frame.revalidate();
        frame.repaint();
    }


    // --- Generic Button Creation Helper ---
    private JLabel createButton(String offResourcePath, String hoverResourcePath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadImage(offResourcePath);
        ImageIcon hoverIcon = loadImage(hoverResourcePath); // Can be null if no hover effect needed

        if (offIcon == null) { // Handle missing icon gracefully
            System.err.println("Error: Button image missing - " + offResourcePath);
            JLabel errorLabel = new JLabel("BTN?"); errorLabel.setBounds(x, y, 50, 30); errorLabel.setForeground(Color.RED);
            return errorLabel;
        }

        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Use hoverIcon if available, otherwise just use offIcon for hover state
        ImageIcon finalHoverIcon = (hoverIcon != null) ? hoverIcon : offIcon;

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) { button.setIcon(finalHoverIcon); }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) { button.setIcon(offIcon); }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Execute action only if button is enabled and action is provided
                if (button.isEnabled() && action != null) {
                    action.run();
                }
            }
        });
        return button;
    }

    // --- Random Selection Helpers (Using static list as fallback) ---

    // Selects opponent, excluding playerCharacter
    private String selectRandomOpponent(String playerCharacter) {
        Set<String> allNames = null;
        try { allNames = CharacterDataLoader.getAllCharacterNames(); } catch (Exception e) { /* Ignore */ }

        List<String> characterPool = (allNames != null && !allNames.isEmpty())
                ? new ArrayList<>(allNames)
                : List.of(ALL_CHARACTER_NAMES);

        if (characterPool.isEmpty()) return null;

        List<String> possibleOpponents = characterPool.stream()
                .filter(name -> !name.equals(playerCharacter))
                .collect(Collectors.toList());

        if (possibleOpponents.isEmpty()) { // Fallback if filtering removed all
            possibleOpponents = new ArrayList<>(characterPool);
            if (possibleOpponents.isEmpty()) return null;
        }
        return possibleOpponents.get(random.nextInt(possibleOpponents.size()));
    }

    // Selects random map
    private String selectRandomMap() {
        if (mapPaths == null || mapPaths.length == 0) return null;
        return mapPaths[random.nextInt(mapPaths.length)];
    }


    // --- paintComponent ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (characterGif != null && characterGif.getImage() != null) {
            g.drawImage(characterGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Error: Background GIF Missing (" + getCharacterName() + ")", 50, 100);
        }
        // Overlays (infoPanel, confirmationPanel) are handled by Swing's Z-order when added.
    }

    // --- Escape Key Binding ---
    private void setupEscapeKeyBinding() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionKey = "goBackFromCharacterScreen";

        im.put(escapeKey, actionKey);
        am.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If confirmation or info panel is showing, close it first
                if (confirmationPanel != null) {
                    removeConfirmation();
                    return;
                }
                if (infoPanel != null) {
                    removeInfoPanel();
                    return;
                }
                // Otherwise, perform the back action
                System.out.println("Escape pressed. Going back.");
                goBack(); // Use a separate method for clarity
            }
        });
    }

    // Helper method for back action
    private void goBack() {
        if (this.firstPlayerSelection != null) { // Came from P2 selection grid
            frame.setContentPane(new SecondPlayerSelection(frame, this.firstPlayerSelection, this.mode));
        } else { // Came from P1 selection grid
            frame.setContentPane(new CharacterSelection(frame, this.mode));
        }
        frame.revalidate(); frame.repaint();
    }

} // End of CharacterSelectionScreen class
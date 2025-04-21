package state.CharacterScreen.Select;

// Add necessary imports for Arcade logic
import state.*; // GameScreen, MapSelection, etc.
import state.character.CharacterDataLoader;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

// ... other imports (Swing, AWT, etc.) ...
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;


public class SecondCharacterSelectionScreen extends JPanel { // Renaming suggestion: CharacterConfirmScreen?

    // --- Existing Fields ---
    private final JFrame frame;
    private final String characterName; // The character being displayed/confirmed
    private final String mode;
    private final String firstPlayerSelection;

    private final ImageIcon characterGif;
    private JPanel confirmationPanel; // Added for confirmation logic
    private JLabel backButton;
    private JLabel chooseButton; // Might rename this to 'Confirm' conceptually
    private JLabel infoButton; // Added info button logic
    private JPanel infoPanel;
    private boolean infoVisible = false;
    private JLabel closeInfoButton;
    // private boolean closeVisible = false; // Not strictly needed if managed by infoPanel != null

    // --- Paths ---
    private final String GIF_PATH_BASE = "/assets/CharacterSelectionScreen/CharacterScreen/";
    private final String BUTTON_PATH_BASE = "/assets/CharacterSelectionScreen/CharacterScreenButtons/";
    private final String NAME_PATH_BASE = "/assets/CharacterSelectionScreen/CharacterNames/"; // <<< ADDED for name display

    // --- Fields needed for Arcade logic ---
    private final Random random = new Random();
    private final String[] mapPaths = { // Make sure this list is accurate
            "assets/StageMap/Waters.gif",
            "assets/StageMap/Dojo.gif",
            "assets/StageMap/DesertedLand.gif",
            "assets/StageMap/DragonLair.gif",
            "assets/StageMap/King'sThrone.gif",
            "assets/StageMap/RoyalPalace.gif"
    };
    // Needed for random opponent selection helper
    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };


    // --- Constructor (Keep as is) ---
    public SecondCharacterSelectionScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        this.frame = frame;
        this.characterName = characterName;
        this.mode = mode;
        this.firstPlayerSelection = firstPlayerSelection;

        setPreferredSize(new Dimension(1920, 1080));
        setLayout(null);

        this.characterGif = loadImage(getGifPath());
        if (this.characterGif == null) {
            System.err.println("CRITICAL: Failed to load background GIF for " + characterName);
            // Consider adding a fallback background color
            setBackground(Color.DARK_GRAY);
        }

        setupButtons();
        // Add Escape Key Binding (Recommended)
        setupEscapeKeyBinding(frame);
        setFocusable(true);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // --- getGifPath, loadImage, setupButtons, showInfoPanel, removeInfoPanel (Keep as is) ---
    private String getGifPath() { /* ... no change ... */
        String gifName = characterName;
        return GIF_PATH_BASE + gifName + "Selection.gif";
    }
    private ImageIcon loadImage(String resourcePath) { /* ... no change ... */
        if (resourcePath == null || resourcePath.isEmpty()) return null;
        try {
            URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) return new ImageIcon(imgURL);
            System.err.println("CharacterInfoScreen: Resource not found: " + resourcePath);
            return null;
        } catch (Exception e) { System.err.println("Error loading image: " + resourcePath); return null; }
    }
    private void setupButtons() { /* ... no change ... */
        // --- Back Button ---
        String backOffPath = BUTTON_PATH_BASE + "Back/Back_off.png";
        String backHoverPath = BUTTON_PATH_BASE + "Back/Back_hover.png";
        backButton = createButton(backOffPath, backHoverPath, 50, 57, () -> { /* ... back logic ... */
            if (confirmationPanel != null || infoPanel != null) return; // Prevent back if overlay shown
            if (this.firstPlayerSelection != null) {
                frame.setContentPane(new SecondPlayerSelection(frame, this.firstPlayerSelection, this.mode));
            } else {
                frame.setContentPane(new CharacterSelection(frame, this.mode));
            }
            frame.revalidate(); frame.repaint();
        });
        add(backButton);

        // --- Choose/Confirm Button ---
        String contOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_off.png";
        String contHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Choose/Choose_hover.png";
        chooseButton = createButton(contOffPath, contHoverPath, 1280, 825, this::showConfirmationScreen);
        add(chooseButton);

        // --- Info Button ---
        String infoBtnOff = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Info/info_off.png";
        String infoBtnHover = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Info/info_hover.png";
        infoButton = createButton(infoBtnOff, infoBtnHover, 1100, 825, () -> {
            if (infoPanel != null) { // If info is visible, hide it
                removeInfoPanel();
            } else if (confirmationPanel == null) { // Only show if confirmation isn't up
                showInfoPanel();
            }
        });
        add(infoButton);
    }
    private void showInfoPanel() { /* ... no change ... */
        if (infoPanel != null || confirmationPanel != null) return; // Don't show if other panel is up

        infoPanel = new JPanel(null); infoPanel.setBounds(0, 0, 1920, 1080); infoPanel.setOpaque(false);
        // Load specific info image based on characterName
        ImageIcon infoImage = loadImage(getInfoImagePath()); // Use helper
        if (infoImage != null) {
            JLabel infoLabel = new JLabel(infoImage); infoLabel.setBounds(0, 0, 1920, 1080); infoPanel.add(infoLabel);
        } else { infoPanel.setBackground(new Color(0,0,0,150)); /* Fallback */ }

        closeInfoButton = createButton( /* ... close button setup ... */
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Close_button.png",
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Close_button.png",
                1470, 280, this::removeInfoPanel // Action is just removeInfoPanel
        );
        infoPanel.add(closeInfoButton); infoPanel.setComponentZOrder(closeInfoButton, 0);
        add(infoPanel); setComponentZOrder(infoPanel, 0); // Add panel and bring to front

        backButton.setEnabled(false); chooseButton.setEnabled(false); infoButton.setEnabled(false); // Disable main buttons
        revalidate(); repaint();
    }
    private String getInfoImagePath() { /* ... needs implementation based on your assets ... */
        // Example: return "/assets/CharacterInfoImages/" + this.characterName + "_info.png";
        System.err.println("getInfoImagePath() needs implementation for character: " + characterName);
        return null;
    }
    private void removeInfoPanel() { /* ... no change ... */
        if (infoPanel != null) {
            remove(infoPanel); infoPanel = null;
            backButton.setEnabled(true); chooseButton.setEnabled(true); infoButton.setEnabled(true); // Re-enable main buttons
            revalidate(); repaint();
        }
    }

    // --- showConfirmationScreen, removeConfirmation (Keep as is) ---
    private void showConfirmationScreen() { /* ... no change ... */
        if (confirmationPanel != null || infoPanel != null) return; // Don't show if info panel is up

        confirmationPanel = new JPanel(null); confirmationPanel.setBounds(0, 0, 1920, 1080); confirmationPanel.setOpaque(false);
        JLabel confirmImage = new JLabel(loadImage(BUTTON_PATH_BASE + "../CharacterConfirm/CharacterConfirm_off.png"));
        if (confirmImage.getIcon() != null) { confirmImage.setBounds(0, 0, 1920, 1080); confirmationPanel.add(confirmImage); }
        else { confirmationPanel.setOpaque(true); confirmationPanel.setBackground(new Color(0,0,0,200)); }

        JLabel yesButton = createButton(BUTTON_PATH_BASE + "../CharacterConfirm/Yes_off.png", BUTTON_PATH_BASE + "../CharacterConfirm/Yes_hover.png", 700, 575, this::confirmSelection);
        confirmationPanel.add(yesButton);
        JLabel noButton = createButton(BUTTON_PATH_BASE + "../CharacterConfirm/No_off.png", BUTTON_PATH_BASE + "../CharacterConfirm/No_hover.png", 923, 573, this::removeConfirmation);
        confirmationPanel.add(noButton);

        add(confirmationPanel); setComponentZOrder(confirmationPanel, 0);
        if (confirmImage.getIcon() != null) { confirmationPanel.setComponentZOrder(yesButton, 0); confirmationPanel.setComponentZOrder(noButton, 0); }
        backButton.setEnabled(false); chooseButton.setEnabled(false); infoButton.setEnabled(false); // Disable main buttons
        revalidate(); repaint();
    }
    private void removeConfirmation() { /* ... no change ... */
        if (confirmationPanel != null) {
            remove(confirmationPanel); confirmationPanel = null;
            backButton.setEnabled(true); chooseButton.setEnabled(true); infoButton.setEnabled(true); // Re-enable main buttons
            revalidate(); repaint();
        }
    }

    // *** MODIFIED confirmSelection method ***
    private void confirmSelection() {
        String confirmedCharacter = this.characterName;

        // Case 1: Player 1 is confirming (firstPlayerSelection was null when screen loaded)
        if (this.firstPlayerSelection == null) {
            System.out.println("Player 1 CONFIRMED: " + confirmedCharacter);

            // --- Check Game Mode ---
            if ("PVP".equalsIgnoreCase(this.mode)) {
                System.out.println("Mode is PVP. Proceeding to Second Player Selection.");
                frame.setContentPane(new SecondPlayerSelection(frame, confirmedCharacter, this.mode)); // Pass P1's choice

            } else if ("Arcade".equalsIgnoreCase(this.mode)) {
                // <<< ARCADE MODE LOGIC >>>
                System.out.println("Mode is Arcade. Resetting streak and starting game...");
                GameScreen.arcadeWins = 0; // Reset streak
                String firstOpponent = selectRandomOpponent(confirmedCharacter); // Select opponent
                String firstMap = selectRandomMap(); // Select map

                if (firstOpponent == null || firstMap == null) {
                    System.err.println("Error setting up Arcade match from confirmation.");
                    JOptionPane.showMessageDialog(frame, "Error starting Arcade mode.", "Arcade Error", JOptionPane.ERROR_MESSAGE);
                    frame.setContentPane(new ModeSelection(frame)); // Fallback to ModeSelection
                } else {
                    System.out.println("Starting Arcade Game. Opponent: " + firstOpponent + ", Map: " + firstMap);
                    frame.setContentPane(new GameScreen(frame, confirmedCharacter, firstOpponent, firstMap, this.mode)); // Start GameScreen
                }

            } else if ("PVC".equalsIgnoreCase(this.mode)) {
                // <<< PVC MODE LOGIC >>>
                System.out.println("Mode is PVC. Selecting AI opponent and proceeding to Map Selection.");
                // String player2Choice = selectRandomCharacterForPVE(confirmedCharacter); // Can reuse random or just use "Computer"
                String player2Choice = "Computer"; // Use "Computer" placeholder for stats loading in GameScreen
                System.out.println("AI Opponent Placeholder: " + player2Choice);
                frame.setContentPane(new MapSelection(frame, confirmedCharacter, player2Choice, this.mode));

            } else {
                System.err.println("Unknown mode [" + this.mode + "] during P1 confirmation. Returning to Mode Selection.");
                frame.setContentPane(new ModeSelection(frame)); // Fallback
            }
        }
        // Case 2: Player 2 is confirming (firstPlayerSelection is NOT null)
        else {
            // This block only runs in PVP mode after P1 has chosen.
            String player2Choice = confirmedCharacter;
            System.out.println("Player 2 CONFIRMED: " + player2Choice);
            System.out.println("Player 1 was: " + this.firstPlayerSelection);

            // Optional: Prevent P2 choosing same character as P1 one last time
            if (player2Choice.equals(this.firstPlayerSelection)) {
                JOptionPane.showMessageDialog(frame, "Player 2 cannot choose the same character as Player 1.", "Character Already Selected", JOptionPane.WARNING_MESSAGE);
                removeConfirmation(); // Remove confirmation panel without proceeding
                return;
            }


            System.out.println("Both players confirmed. Proceeding to Map Selection.");
            frame.setContentPane(new MapSelection(frame, this.firstPlayerSelection, player2Choice, this.mode));
        }

        // Update the frame
        frame.revalidate();
        frame.repaint();
    }


    // --- createButton (Keep as is or use your version) ---
    private JLabel createButton(String offResourcePath, String hoverResourcePath, int x, int y, Runnable action) { /* ... no change ... */
        ImageIcon offIcon = loadImage(offResourcePath);
        ImageIcon hoverIcon = loadImage(hoverResourcePath);
        if (offIcon == null) { JLabel err = new JLabel("ERR"); err.setBounds(x,y,50,30); return err;}
        JLabel button = new JLabel(offIcon); button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight()); button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ImageIcon finalHover = (hoverIcon != null) ? hoverIcon : offIcon;
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (button.isEnabled()) button.setIcon(finalHover); }
            @Override public void mouseExited(MouseEvent e) { if (button.isEnabled()) button.setIcon(offIcon); }
            @Override public void mouseClicked(MouseEvent e) { if (button.isEnabled()) action.run(); }
        });
        return button;
    }


    // --- Helper for PVE AI selection (Renamed for clarity) ---
    // This is now only used for Arcade opponent selection
    private String selectRandomOpponent(String playerCharacter) {
        // Use CharacterDataLoader if available, otherwise fallback to local list
        Set<String> allNames = null;
        try { // Defensively get names
            allNames = CharacterDataLoader.getAllCharacterNames();
        } catch (Exception e) {
            System.err.println("Error getting names from CharacterDataLoader: " + e.getMessage());
        }

        // Use local list as fallback or if loader failed
        List<String> characterPool = (allNames != null && !allNames.isEmpty())
                ? new ArrayList<>(allNames)
                : List.of(ALL_CHARACTER_NAMES); // Use static list as fallback

        if (characterPool.isEmpty()) {
            System.err.println("Cannot select opponent: No character names available.");
            return null;
        }

        List<String> possibleOpponents = characterPool.stream()
                .filter(name -> !name.equals(playerCharacter))
                .collect(Collectors.toList());
        if (possibleOpponents.isEmpty()) {
            // Fallback: If only one character exists or P1 chose the only other one
            System.out.println("Warning: No opponents other than player's character found. Allowing self-match or picking first.");
            possibleOpponents = new ArrayList<>(characterPool); // Use the full pool
            if (possibleOpponents.isEmpty()) return null;
        }
        return possibleOpponents.get(random.nextInt(possibleOpponents.size()));
    }

    // --- Helper for Random Map Selection ---
    private String selectRandomMap() {
        if (mapPaths == null || mapPaths.length == 0) {
            System.err.println("Error: No map paths available!");
            return null;
        }
        return mapPaths[random.nextInt(mapPaths.length)];
    }


    // --- paintComponent (Keep as is) ---
    @Override
    protected void paintComponent(Graphics g) { /* ... no change ... */
        super.paintComponent(g);
        if (characterGif != null && characterGif.getImage() != null) {
            g.drawImage(characterGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Error: Background GIF Missing", 50, 100);
        }
    }

    // --- Escape Key Binding (Added - goes back to previous selection grid) ---
    private void setupEscapeKeyBinding(JFrame frame) {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionKey = "goBackFromConfirm";

        im.put(escapeKey, actionKey);
        am.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If confirmation panel or info panel is showing, just close it
                if (confirmationPanel != null) {
                    removeConfirmation();
                    return;
                }
                if (infoPanel != null) {
                    removeInfoPanel();
                    return;
                }

                // Otherwise, go back to the appropriate selection grid
                System.out.println("Escape pressed on Character Info Screen. Going back.");
                if (firstPlayerSelection != null) { // P2 was viewing
                    frame.setContentPane(new SecondPlayerSelection(frame, firstPlayerSelection, mode));
                } else { // P1 was viewing
                    frame.setContentPane(new CharacterSelection(frame, mode));
                }
                frame.revalidate();
                frame.repaint();
            }
        });
    }
}
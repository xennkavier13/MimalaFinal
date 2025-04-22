package state.CharacterScreen.Select;

import state.*;
import state.CharacterScreen.*;
import state.character.CharacterDataLoader; // <<< ADD IMPORT

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList; // <<< ADD IMPORT
import java.util.List;     // <<< ADD IMPORT
import java.util.Random;
import java.util.Set;      // <<< ADD IMPORT
import java.util.stream.Collectors;// <<< ADD IMPORT


public class CharacterSelection extends JPanel {
    // --- Fields mostly unchanged ---
    private final ImageIcon characterSelectionBg;
    private final JFrame frame;
    // Array below is now redundant for opponent selection if using CharacterDataLoader
    // private final String[] characterNames = { ... };
    private String firstPlayerSelection = null;
    private String secondPlayerSelection = null; // Not needed here for Arcade anymore
    // private static final String AI_PLAYER_NAME = "Computer"; // No longer needed here
    private final String mode;
    private Clip music;
    private final JLabel characterNameLabel = new JLabel();
    private final Random random = new Random(); // <<< ADD Random instance field
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };
    // We need the map paths here now for Arcade mode's first map
    private final String[] mapPaths = {
            "assets/StageMap/Waters.gif",
            "assets/StageMap/Dojo.gif",
            "assets/StageMap/DesertedLand.gif",
            "assets/StageMap/DragonLair.gif",
            "assets/StageMap/King'sThrone.gif",
            "assets/StageMap/RoyalPalace.gif"
    };

    public CharacterSelection(JFrame frame, String mode) {
        this.frame = frame;
        this.mode = mode;
        // Use getResource for better path handling
        characterSelectionBg = new ImageIcon(getClass().getResource("/assets/CharacterSelectionScreen/CharacterSelect_BGcombine.gif"));

        // PVC logic can remain, but AI name isn't needed here anymore
        // if ("PVC".equals(this.mode)) { ... }

        setLayout(null);  // Absolute layout
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();

        characterNameLabel.setBounds(760, 945, 400, 100);
        characterNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        characterNameLabel.setVisible(false);
        add(characterNameLabel);
    }

    private void setupButtons() {
        // Back button (unchanged)
        JLabel backButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png",
                "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png",
                50, 57,
                () -> {
                    frame.setContentPane(new ModeSelection(frame)); // Assuming ModeSelection exists
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(backButton);

        // Create hover buttons using paths relative to resources if possible
        createCharacterButton("Pyrothar", "/assets/CharacterSelectionScreen/Character_hover/Pyrothar_hover.png", 630, 182);
        createCharacterButton("Azurox", "/assets/CharacterSelectionScreen/Character_hover/Azurox_hover.png", 330, 203);
        createCharacterButton("Zenfang", "/assets/CharacterSelectionScreen/Character_hover/Zenfang_hover.png", 1000, 203);
        createCharacterButton("Auricannon", "/assets/CharacterSelectionScreen/Character_hover/Auricannon_hover.png", 1150, 203);
        createCharacterButton("Vexmorth", "/assets/CharacterSelectionScreen/Character_hover/Vexmorth_hover.png", 352, 570);
        createCharacterButton("Astridra", "/assets/CharacterSelectionScreen/Character_hover/Astridra_hover.png", 673, 575);
        createCharacterButton("Varkos", "/assets/CharacterSelectionScreen/Character_hover/Varkos_hover.png", 866, 575);
        createCharacterButton("Ignisveil", "/assets/CharacterSelectionScreen/Character_hover/Ignisveil_hover.png", 1260, 497);
    }

    // createButton needs to use getResource
    private JLabel createButton(String normalImagePath, String hoverImagePath, int x, int y, Runnable onClickAction) {
        ImageIcon normalIcon = new ImageIcon(getClass().getResource(normalImagePath));
        ImageIcon hoverIcon = new ImageIcon(getClass().getResource(hoverImagePath));
        // ... rest of method is okay ...
        JLabel button = new JLabel(normalIcon);
        button.setBounds(x, y, normalIcon.getIconWidth(), normalIcon.getIconHeight());

        // Mouse listener for hover effects and click action
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(normalIcon);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                onClickAction.run();
            }
        });

        return button;
    }

    private ImageIcon loadImageIcon(String path) { /* ... no change ... */
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Missing image resource: " + path);
            return null;
        }
    }

    private void createCharacterButton(String characterName, String hoverImagePath, int x, int y) {
        ImageIcon hoverIcon = loadImageIcon(hoverImagePath);
        if (hoverIcon == null) return;

        JLabel button = new JLabel();
        button.setBounds(x, y, hoverIcon.getIconWidth(), hoverIcon.getIconHeight());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
                ImageIcon nameIcon = loadImageIcon("/assets/CharacterSelectionScreen/CharacterNames/" + characterName + ".png");
                if (nameIcon != null) characterNameLabel.setIcon(nameIcon);
                characterNameLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(null);
                characterNameLabel.setVisible(false);
            }

            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            // MODIFIED MOUSE CLICKED LOGIC
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Character clicked: " + characterName + " in Mode: " + mode);
                // stopMusic(); // Optional

                // --- MODIFIED: All modes now go to the character confirmation screen first ---
                // The firstPlayerSelection is null at this point, indicating P1 is choosing.
                System.out.println("Transitioning to " + characterName + " screen for P1 confirmation...");

                // Use your specific character screen classes or a generic one
                // Assuming you use specific screens like PyrotharScreen, AzuroxScreen etc.
                // If you use a generic screen (like SecondCharacterSelectionScreen) for all,
                // adjust the 'new XScreen()' calls accordingly.
                switch (characterName) {
                    case "Pyrothar":
                        // Pass null for firstPlayerSelection as P1 hasn't confirmed yet
                        frame.setContentPane(new PyrotharScreen(frame, "Pyrothar", mode, null));
                        break;
                    case "Azurox":
                        frame.setContentPane(new AzuroxScreen(frame, "Azurox", mode, null));
                        break;
                    case "Zenfang":
                        frame.setContentPane(new ZenfangScreen(frame, "Zenfang", mode, null));
                        break;
                    case "Auricannon":
                        frame.setContentPane(new AuricannonScreen(frame, "Auricannon", mode, null));
                        break;
                    case "Vexmorth":
                        frame.setContentPane(new VexmorthScreen(frame, "Vexmorth", mode, null));
                        break;
                    case "Astridra":
                        frame.setContentPane(new AstridraScreen(frame, "Astridra", mode, null));
                        break;
                    case "Varkos":
                        frame.setContentPane(new VarkosScreen(frame, "Varkos", mode, null));
                        break;
                    case "Ignisveil":
                        frame.setContentPane(new IgnisveilScreen(frame, "Ignisveil", mode, null));
                        break;
                    default:
                        System.err.println("No specific screen class defined for: " + characterName);
                        // Fallback to ModeSelection or show an error
                        JOptionPane.showMessageDialog(frame, "Screen not found for " + characterName, "Error", JOptionPane.ERROR_MESSAGE);
                        frame.setContentPane(new ModeSelection(frame));
                        break;
                }

                // The logic for what happens *after* confirmation is now handled
                // within the specific character screen's confirmSelection method.

                frame.revalidate();
                frame.repaint();
            }
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            // END OF MODIFIED METHOD
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        });
        add(button);
    }


    private String selectRandomCharacter() {
        Random random = new Random();
        return characterNames[random.nextInt(characterNames.length)];
    }
    // Updated to select from CharacterDataLoader, excluding player's choice
    private String selectRandomOpponent(String playerCharacter) {
        Set<String> allNames = CharacterDataLoader.getAllCharacterNames();
        if (allNames.isEmpty()) {
            System.err.println("Error: No character names loaded from CharacterDataLoader!");
            return null; // Or handle error appropriately
        }
        // Ensure player's character is excluded if possible
        List<String> possibleOpponents = allNames.stream()
                .filter(name -> !name.equals(playerCharacter))
                .collect(Collectors.toList());

        // If filtering resulted in an empty list (e.g., only one character loaded),
        // fallback to choosing from all characters.
        if (possibleOpponents.isEmpty()) {
            possibleOpponents = new ArrayList<>(allNames);
            if (possibleOpponents.isEmpty()) return null; // Still empty? Major issue.
        }


        return possibleOpponents.get(random.nextInt(possibleOpponents.size()));
    }

    // Selects a random map path
    private String selectRandomMap() {
        if (mapPaths == null || mapPaths.length == 0) {
            System.err.println("Error: No map paths available!");
            return null; // Or handle error appropriately
        }
        return mapPaths[random.nextInt(mapPaths.length)];
    }

    // --- Music methods (unchanged, ensure paths are correct) ---
    private void playMusic(String filePath) { /* ... no change ... */ }
    private void stopMusic() { /* ... no change ... */ }

    // --- paintComponent (unchanged, ensure path is correct) ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Use getResource for background path
        if (characterSelectionBg != null) {
            g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY); // Fallback
            g.fillRect(0,0,getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("BG Missing", 100, 100);
        }
    }
}
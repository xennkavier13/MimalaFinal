package state.CharacterScreen.Select;

import state.*; // Import GameScreen
import state.CharacterScreen.*;
// Remove: import state.MapSelection; // Don't need to go here for Arcade
import state.character.CharacterDataLoader; // <<< ADD IMPORT

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    // private String player2Selection = null; // Not needed here for Arcade anymore
    // private static final String AI_PLAYER_NAME = "Computer"; // No longer needed here
    private final String mode;
    private Clip music;
    private final JLabel characterNameLabel = new JLabel();
    private final Random random = new Random(); // <<< ADD Random instance field

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
            // CORRECTED MOUSE CLICKED LOGIC
            //vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Character clicked: " + characterName);
                // stopMusic(); // Optional

                // --- Logic depends on mode ---
                switch (mode) {
                    case "PVP":
                        // In PVP, go to the specific character screen for confirmation
                        System.out.println("Transitioning to " + characterName + " screen for P1 confirmation...");
                        switch (characterName) {
                            case "Pyrothar": frame.setContentPane(new PyrotharScreen(frame, mode)); break;
                            case "Azurox": frame.setContentPane(new AzuroxScreen(frame, mode)); break;
                            case "Zenfang": frame.setContentPane(new ZenfangScreen(frame, mode)); break;
                            case "Auricannon": frame.setContentPane(new AuricannonScreen(frame, mode)); break;
                            case "Vexmorth": frame.setContentPane(new VexmorthScreen(frame, mode)); break;
                            case "Astridra": frame.setContentPane(new AstridraScreen(frame, mode)); break;
                            case "Varkos": frame.setContentPane(new VarkosScreen(frame, mode)); break;
                            case "Ignisveil": frame.setContentPane(new IgnisveilScreen(frame, mode)); break;
                            default:
                                System.err.println("No specific screen for: " + characterName);
                                frame.setContentPane(new ModeSelection(frame)); // Fallback
                                break;
                        }
                        // The character screen's "Confirm" button will handle going to SecondPlayerSelection
                        break;

                    case "PVC":
                        // In PVC, select P1 and go directly to Map Selection
                        firstPlayerSelection = characterName;
                        System.out.println("Player 1 selected: " + firstPlayerSelection);
                        System.out.println("Transitioning to Map Selection for PVC...");
                        frame.setContentPane(new MapSelection(frame, firstPlayerSelection, "Computer", mode));
                        break;

                    case "Arcade":
                        // In Arcade, select P1, reset wins, pick opponent/map, go to GameScreen
                        firstPlayerSelection = characterName;
                        System.out.println("Player 1 selected: " + firstPlayerSelection);
                        System.out.println("Starting Arcade Mode...");
                        GameScreen.arcadeWins = 0;
                        String firstOpponent = selectRandomOpponent(firstPlayerSelection);
                        String firstMap = selectRandomMap();

                        if (firstOpponent == null || firstMap == null) {
                            JOptionPane.showMessageDialog(frame, "Error starting Arcade mode.", "Arcade Error", JOptionPane.ERROR_MESSAGE);
                            frame.setContentPane(new ModeSelection(frame)); // Fallback
                        } else {
                            System.out.println("First Arcade Opponent: " + firstOpponent);
                            System.out.println("First Arcade Map: " + firstMap);
                            frame.setContentPane(new GameScreen(frame, firstPlayerSelection, firstOpponent, firstMap, mode));
                        }
                        break;

                    default:
                        System.err.println("Unknown game mode in CharacterSelection: " + mode);
                        frame.setContentPane(new ModeSelection(frame)); // Fallback
                        break;
                }

                frame.revalidate();
                frame.repaint();
            }
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            // END OF CORRECTED METHOD
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        });
        add(button);
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
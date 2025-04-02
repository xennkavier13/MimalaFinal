package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
// No need for Random here anymore
// No need for SecondPlayerSelection or MapSelection imports here anymore

public class CharacterSelection extends JPanel {
    // Use resource paths (relative to classpath root, use forward slashes)
    private final String BG_PATH = "/assets/CharacterSelectionScreen/Character_off/Characters_off.png";
    private final String HOVER_PATH_BASE = "/assets/CharacterSelectionScreen/Character_hover/";

    private ImageIcon characterSelectionBg;
    private final JFrame frame;
    // Character names are still needed to create buttons
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Aurelix",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };
    private final String[] characterHoverPaths = {
            HOVER_PATH_BASE + "Pyrothar_hover.png", HOVER_PATH_BASE + "Azurox_hover.png",
            HOVER_PATH_BASE + "Zenfang_hover.png", HOVER_PATH_BASE + "Aurelix_hover.png",
            HOVER_PATH_BASE + "Vexmorth_hover.png", HOVER_PATH_BASE + "Astrida_hover.png", // Check spelling: Astrida vs Astridra
            HOVER_PATH_BASE + "Varkos_hover.png", HOVER_PATH_BASE + "Ignisveil_hover.png"
    };

    // Removed: private String firstPlayerSelection = null;
    private final String mode; // Keep mode as it's needed by CharacterInfoScreen

    public CharacterSelection(JFrame frame, String mode) {
        this.frame = frame;
        this.mode = mode;

        System.out.println("Attempting CharacterSelection resource loading. Check project structure.");

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("CharacterSelection: Resource not found: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("CharacterSelection: Error loading image resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }


    private void setupButtons() {
        int startX = 280; int startY = 205;
        int mapWidth = 300;
        int mapHeight = 250;
        int spacingX = 15;
        int spacingY = 50;
        int cols = 4;
        int totalGridWidth = (cols * mapWidth) + ((cols-1) * spacingX);
        startX = (getPreferredSize().width - totalGridWidth) / 2;
        if (startX < 0) startX = 10;

        // Row 1
        createCharacterButton(characterNames[0], characterHoverPaths[0], startX + 0 * (mapWidth + spacingX), startY, mapWidth, mapHeight);
        createCharacterButton(characterNames[1], characterHoverPaths[1], startX + 1 * (mapWidth + spacingX), startY, mapWidth, mapHeight);
        createCharacterButton(characterNames[2], characterHoverPaths[2], startX + 2 * (mapWidth + spacingX), startY, mapWidth, mapHeight);
        createCharacterButton(characterNames[3], characterHoverPaths[3], startX + 3 * (mapWidth + spacingX), startY, mapWidth, mapHeight);
        // Row 2
        int secondRowY = startY + mapHeight + spacingY;
        createCharacterButton(characterNames[4], characterHoverPaths[4], startX + 0 * (mapWidth + spacingX), secondRowY, mapWidth, mapHeight);
        createCharacterButton(characterNames[5], characterHoverPaths[5], startX + 1 * (mapWidth + spacingX), secondRowY, mapWidth, mapHeight);
        createCharacterButton(characterNames[6], characterHoverPaths[6], startX + 2 * (mapWidth + spacingX), secondRowY, mapWidth, mapHeight);
        createCharacterButton(characterNames[7], characterHoverPaths[7], startX + 3 * (mapWidth + spacingX), secondRowY, mapWidth, mapHeight);
    }

    private void createCharacterButton(String characterName, String hoverImagePath, int x, int y, int width, int height) {
        ImageIcon hoverIcon = loadImage(hoverImagePath);
        if (hoverIcon == null) {
            System.err.println("Failed to load hover icon for: " + characterName + " at path: " + hoverImagePath);
            JLabel errorLabel = new JLabel(characterName + " (X)");
            errorLabel.setBounds(x, y, width, height);
            errorLabel.setForeground(Color.RED);
            errorLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
            add(errorLabel);
            return;
        }

        JLabel button = new JLabel();
        button.setBounds(x, y, width, height);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Image scaledHover = hoverIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledHover));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // --- MODIFIED Logic ---
                // ALWAYS go to the CharacterInfoScreen when a character is clicked.
                // The selection confirmation happens *there*.
                System.out.println("Viewing info for: " + characterName); // Log action
                frame.setContentPane(new state.CharacterInfoScreen(frame, characterName, mode));
                frame.revalidate();
                frame.repaint();
            }
        });
        add(button);
    }

    // Removed: selectRandomCharacter() method - This logic moves to CharacterInfoScreen

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (characterSelectionBg == null) {
            characterSelectionBg = loadImage(BG_PATH);
        }
        if (characterSelectionBg != null) {
            g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Background image missing: " + BG_PATH, 20, 20);
        }
    }
}
package state.CharacterScreen.Select;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class SecondPlayerSelection extends JPanel {
    // Use resource paths
    private final String HOVER_PATH_BASE = "/assets/CharacterSelectionScreen/Character_hover/";

    private ImageIcon characterSelectionBg;
    private final JFrame frame;
    private final String firstPlayerSelection; // P1's choice is needed
    private final String mode; // Should always be PVP here

    private final JLabel characterNameLabel = new JLabel();

    // Use the same names as CharacterSelection for consistency
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon", // Or Auricannon?
            "Vexmorth", "Astridra", // Or Astrida?
            "Varkos", "Ignisveil"
    };
    private final String[] characterHoverPaths = generateHoverPaths(); // Use helper


    public SecondPlayerSelection(JFrame frame, String firstPlayerSelection, String mode) {
        this.frame = frame;
        this.firstPlayerSelection = firstPlayerSelection;
        this.mode = mode; // Should be "PVP"
        characterSelectionBg = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterSelect_BGcombine.gif");

        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();

        characterNameLabel.setBounds(760, 945, 400, 100); // default position, adjust as needed
        characterNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        characterNameLabel.setVisible(false);
        add(characterNameLabel);
    }

    // Helper to generate hover paths (same as CharacterSelection)
    private String[] generateHoverPaths() {
        String[] paths = new String[characterNames.length];
        for (int i = 0; i < characterNames.length; i++) {
            String imageName = characterNames[i];
            paths[i] = HOVER_PATH_BASE + imageName + "_hover.png";
        }
        return paths;
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("SecondPlayerSelection: Resource not found: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("SecondPlayerSelection: Error loading image resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    private void setupButtons() {
        // Create hover buttons for each character
        createCharacterButton("Pyrothar", "/assets/CharacterSelectionScreen/Character_hover/Pyrothar_hover.png", 630, 182);
        createCharacterButton("Azurox", "/assets/CharacterSelectionScreen/Character_hover/Azurox_hover.png", 330, 203);
        createCharacterButton("Zenfang", "/assets/CharacterSelectionScreen/Character_hover/Zenfang_hover.png", 1000, 203);
        createCharacterButton("Auricannon", "/assets/CharacterSelectionScreen/Character_hover/Auricannon_hover.png", 1150, 203);
        createCharacterButton("Vexmorth", "/assets/CharacterSelectionScreen/Character_hover/Vexmorth_hover.png", 352, 570);
        createCharacterButton("Astridra", "/assets/CharacterSelectionScreen/Character_hover/Astridra_hover.png", 673, 575);
        createCharacterButton("Varkos", "/assets/CharacterSelectionScreen/Character_hover/Varkos_hover.png", 866, 575);
        createCharacterButton("Ignisveil", "/assets/CharacterSelectionScreen/Character_hover/Ignisveil_hover.png", 1260, 497);
    }

    private void createCharacterButton(String characterName, String hoverImagePath, int x, int y) {
        ImageIcon hoverIcon = loadImage(hoverImagePath);
        // Error handling as before...


        JLabel button = new JLabel();
        assert hoverIcon != null;
        button.setBounds(x, y, hoverIcon.getIconWidth(), hoverIcon.getIconHeight());

            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setIcon(hoverIcon);

                    ImageIcon nameIcon = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterNames\\" + characterName + ".png");
                    characterNameLabel.setIcon(nameIcon);
                    characterNameLabel.setVisible(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setIcon(null);
                    characterNameLabel.setVisible(false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // --- MODIFIED LOGIC ---
                    // Go to CharacterInfoScreen, passing P1's choice along with P2's potential choice.
                    System.out.println("Player 2 clicked: " + characterName + ". Showing info screen...");
                    frame.setContentPane(new SecondCharacterSelectionScreen(frame, characterName, mode, firstPlayerSelection));
                    frame.revalidate();
                    frame.repaint();
                }
            });


        add(button);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);

    }
}
package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class CharacterSelection extends JPanel {
    private final ImageIcon characterSelectionBg;
    private final JFrame frame;
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Aurelix",
            "Vexmorth", "Astrida", "Varkos", "Ignisveil"
    };
    private String firstPlayerSelection = null;
    private final String mode;

    public CharacterSelection(JFrame frame, String mode) {
        this.frame = frame;
        this.mode = mode;
        characterSelectionBg = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_off\\Characters_off.png");

        setLayout(null);  // Absolute layout
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
    }

    private void setupButtons() {
        // Add the off (background) button first, as it is static and common for all characters
        JLabel offButton = createOffButton();
        add(offButton);

        // Create hover buttons for each character
        createCharacterButton("Pyrothar", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Pyrothar_hover.png", 280, 205);
        createCharacterButton("Azurox", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Azurox_hover.png", 590, 205);
        createCharacterButton("Zenfang", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Zenfang_hover.png", 905, 205);
        createCharacterButton("Aurelix", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Aurelix_hover.png", 1220, 205);
        createCharacterButton("Vexmorth", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Vexmorth_hover.png", 280, 555);
        createCharacterButton("Astrida", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Astrida_hover.png", 603, 555);
        createCharacterButton("Varkos", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Varkos_hover.png", 925, 548);
        createCharacterButton("Ignisveil", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Ignisveil_hover.png", 1240, 553);
    }

    private void createCharacterButton(String characterName, String hoverImagePath, int x, int y) {
        ImageIcon hoverIcon = new ImageIcon(hoverImagePath);

        JLabel button = new JLabel();
        button.setBounds(x, y, hoverIcon.getIconWidth(), hoverIcon.getIconHeight());

        // Mouse listener for hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (firstPlayerSelection == null) {
                    firstPlayerSelection = characterName;
                    System.out.println(firstPlayerSelection + " selected by Player 1!");
                    if (mode.equals("PVP")) {
                        // Transition to second player's selection
                        frame.setContentPane(new SecondPlayerSelection(frame, firstPlayerSelection));
                    } else {
                        // Randomly select a character for Player vs Computer
                        String secondPlayerSelection = selectRandomCharacter();
                        System.out.println(secondPlayerSelection + " selected for Player 2!");
                        // Proceed to the game with both selections
                        frame.setContentPane(new GameScreen(frame, firstPlayerSelection, secondPlayerSelection));
                    }
                    frame.revalidate();
                    frame.repaint();
                }
            }
        });

        add(button);
    }

    private String selectRandomCharacter() {
        Random random = new Random();
        return characterNames[random.nextInt(characterNames.length)];
    }

    private JLabel createOffButton() {
        // Create and position the off button (background image)
        ImageIcon offIcon = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_off\\Characters_off.png");
        JLabel offButton = new JLabel(offIcon);
        offButton.setBounds(0, 0, getWidth(), getHeight()); // Cover the entire screen with the off image
        return offButton;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

package state;

import javax.swing.*;
import java.awt.*;

public class CharacterSelection extends JPanel {
    private final ImageIcon characterSelectionBg;

    public CharacterSelection(Frame gameFrame) {
        JFrame frame = (JFrame) gameFrame;
        characterSelectionBg = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_off\\Characters_off.png");
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();
    }

    private void setupButtons() {
        // Add the off (background) button first, as it is static and common for all characters
        JLabel offButton = createOffButton();
        add(offButton);

        JLabel pyrotharSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Pyrothar_hover.png",
                280, // X position for Pyrothar button
                205   // Y position for Pyrothar button
        );

        JLabel azuroxSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Azurox_hover.png",
                590, // X position for Azurox button
                205  // Y position for Azurox button
        );

        JLabel zenfangSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Zenfang_hover.png",
                905, // X position for Azurox button
                205  // Y position for Azurox button
        );

        JLabel aurelixSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Aurelix_hover.png",
                1220, // X position for Azurox button
                205  // Y position for Azurox button
        );

        JLabel vexmorthSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Vexmorth_hover.png",
                280, // X position for Pyrothar button
                555   // Y position for Pyrothar button
        );

        JLabel astridaSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Astrida_hover.png",
                603, // X position for Azurox button
                555  // Y position for Azurox button
        );

        JLabel varkosSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Varkos_hover.png",
                925, // X position for Azurox button
                548  // Y position for Azurox button
        );

        JLabel ignisveilSelect = createHoverButton(
                "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Ignisveil_hover.png",
                1240, // X position for Azurox button
                553  // Y position for Azurox button
        );

        // Add hover buttons to the panel
        add(pyrotharSelect);
        add(azuroxSelect);
        add(zenfangSelect);
        add(aurelixSelect);
        add(vexmorthSelect);
        add(astridaSelect);
        add(varkosSelect);
        add(ignisveilSelect);
    }

    private JLabel createOffButton() {
        // Create and position the off button (background image)
        ImageIcon offIcon = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_off\\Characters_off.png");
        JLabel offButton = new JLabel(offIcon);
        offButton.setBounds(0, 0, getWidth(), getHeight()); // Cover the entire screen with the off image
        return offButton;
    }

    private JLabel createHoverButton(String hoverPath, int x, int y) {
        // Load hover image for character selection
        ImageIcon hoverIcon = new ImageIcon(hoverPath);

        // Create a label for the hover button
        JLabel button = new JLabel();
        button.setBounds(x, y, hoverIcon.getIconWidth(), hoverIcon.getIconHeight()); // Set initial position

        // Set the hover icon initially to null (it should be hidden)
        button.setIcon(null);

        // Add mouse listener for hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Show hover image when the mouse enters
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Hide hover image when the mouse exits
                button.setIcon(null);
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SecondPlayerSelection extends JPanel {
    private final ImageIcon characterSelectionBg;
    private final JFrame frame;
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon",
            "Vexmorth", "Astrida", "Varkos", "Ignisveil"
    };
    private final String firstPlayerSelection;

    public SecondPlayerSelection(JFrame frame, String firstPlayerSelection) {
        this.frame = frame;
        this.firstPlayerSelection = firstPlayerSelection;
        characterSelectionBg = loadIcon("assets/CharacterSelectionScreen/CharacterSelectOff.png");

        setLayout(null); // Use null layout for absolute positioning
        setPreferredSize(new Dimension(1920, 1080));

        // Manually position and add character buttons
        createCharacterButton("Pyrothar", "assets/CharacterSelectionScreen/Character_hover/Pyrothar_hover.png", 273, 199);
        createCharacterButton("Azurox", "assets/CharacterSelectionScreen/Character_hover/Azurox_hover.png", 583, 197);
        createCharacterButton("Zenfang", "assets/CharacterSelectionScreen/Character_hover/Zenfang_hover.png", 902, 197);
        createCharacterButton("Auricannon", "assets/CharacterSelectionScreen/Character_hover/Auricannon_hover.png", 1154, 199);
        createCharacterButton("Vexmorth", "assets/CharacterSelectionScreen/Character_hover/Vexmorth_hover.png", 273, 545);
        createCharacterButton("Astrida", "assets/CharacterSelectionScreen/Character_hover/Astrida_hover.png", 599, 545);
        createCharacterButton("Varkos", "assets/CharacterSelectionScreen/Character_hover/Varkos_hover.png", 924, 549);
        createCharacterButton("Ignisveil", "assets/CharacterSelectionScreen/Character_hover/Ignisveil_hover.png", 1234, 545);
    }

    private void createCharacterButton(String characterName, String hoverImagePath, int x, int y) {
        String basePath = "assets/CharacterSelectionScreen/";
        ImageIcon hoverIcon = loadIcon(hoverImagePath);

        JLabel button = new JLabel("", JLabel.CENTER);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(386, 456));

        // Set position based on manual x and y
        button.setBounds(x, y, 386, 456);

        // Set the initial icon to null so that it's not visible by default
        button.setIcon(null);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);  // Show hover icon when the mouse enters
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(null);  // Hide hover icon when the mouse exits
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(characterName + " selected by Player 2!");
                System.out.println("Transitioning to GameScreen...");
                frame.setContentPane(new MapSelection(frame, firstPlayerSelection, characterName));
                frame.revalidate();
                frame.repaint();
                System.out.println("Transition complete.");
            }
        });

        add(button);
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Warning: Missing image at " + path);
        return new ImageIcon();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

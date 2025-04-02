package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SecondPlayerSelection extends JPanel {
    private final ImageIcon characterSelectionBg;
    private final JFrame frame;
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Aurelix",
            "Vexmorth", "Astrida", "Varkos", "Ignisveil"
    };
    private final String firstPlayerSelection;

    public SecondPlayerSelection(JFrame frame, String firstPlayerSelection) {
        this.frame = frame;
        this.firstPlayerSelection = firstPlayerSelection;
        characterSelectionBg = loadIcon("assets/CharacterSelectionScreen/CharacterSelect_BG.png");

        setLayout(null); // Use null layout for absolute positioning
        setPreferredSize(new Dimension(1920, 1080));

        JLabel backgroundLabel = new JLabel(loadIcon("assets/CharacterSelectionScreen/Character_off/Characters_off.png"));
        backgroundLabel.setBounds(0, 0, 1920, 1080);
        add(backgroundLabel);

        // Create and add character buttons
        for (String characterName : characterNames) {
            if (!characterName.equals(firstPlayerSelection)) {
                JLabel characterButton = createCharacterButton(characterName);
                add(characterButton);
            }
        }
    }

    private JLabel createCharacterButton(String characterName) {
        String basePath = "assets/CharacterSelectionScreen/";
        ImageIcon hoverIcon = resizeIcon(loadIcon(basePath + "Character_hover/" + characterName + "_hover.png"), 386, 456);

        JLabel button = new JLabel("", JLabel.CENTER);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(386, 456));

        // Set the position of the button based on its index
        int index = java.util.Arrays.asList(characterNames).indexOf(characterName);
        int x = 280 + (index % 4) * 320; // Adjust x position based on index
        int y = 205 + (index / 4) * 300; // Adjust y position based on index
        button.setBounds(x, y, 386, 456); // Set bounds for the button

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

        return button;
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon.getImage() == null) return new ImageIcon();
        Image resizedImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
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

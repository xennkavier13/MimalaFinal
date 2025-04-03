package state;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameScreen extends JPanel {
    private final JFrame frame;
    private final String firstPlayerCharacter;
    private final String secondPlayerCharacter;
    private ImageIcon mapImage;

    public GameScreen(JFrame frame, String firstPlayerCharacter, String secondPlayerCharacter, String selectedMap) {
        this.frame = frame;
        this.firstPlayerCharacter = firstPlayerCharacter;
        this.secondPlayerCharacter = secondPlayerCharacter;
        try {
            URL imageURL = getClass().getResource("/" + selectedMap); // Ensure correct path
            if (imageURL != null) {
                mapImage = new ImageIcon(imageURL);
            } else {
                System.err.println("Error: Map image resource not found: " + selectedMap);
            }
        } catch (Exception e) {
            System.err.println("Error loading map image: " + selectedMap);
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(1920, 1080)); // Adjust as needed

        // Create a panel for the game actions
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());

        // Create buttons for actions
        JButton attackButton = new JButton("Attack");
        JButton skill1Button = new JButton("Skill 1");
        JButton skill2Button = new JButton("Skill 2");

        // Add action listeners to buttons
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction("Attack");
            }
        });

        skill1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction("Skill 1");
            }
        });

        skill2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction("Skill 2");
            }
        });

        // Add buttons to the action panel
        actionPanel.add(attackButton);
        actionPanel.add(skill1Button);
        actionPanel.add(skill2Button);

        // Add the action panel to the main panel
        add(actionPanel, BorderLayout.SOUTH);

        // Display selected characters
        JLabel characterLabel = new JLabel("Player 1: " + firstPlayerCharacter + " vs Player 2: " + secondPlayerCharacter);
        characterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(characterLabel, BorderLayout.CENTER);
    }

    private void performAction(String action) {
        // Here you can implement the logic for what happens when an action is performed
        System.out.println("Action performed: " + action);
        // You can add more game logic here, such as updating health, animations, etc.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            g.drawImage(mapImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Map Image Not Found!", getWidth() / 2 - 50, getHeight() / 2);
        }
    }
}
package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameScreen extends JPanel {
    private final JFrame frame;
    private final String firstPlayerCharacter;
    private final String secondPlayerCharacter;

    public GameScreen(JFrame frame, String firstPlayerCharacter, String secondPlayerCharacter) {
        this.frame = frame;
        this.firstPlayerCharacter = firstPlayerCharacter;
        this.secondPlayerCharacter = secondPlayerCharacter;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1920, 1080));

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
        // You can add background images or other graphics here
        // g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
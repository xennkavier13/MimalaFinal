package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Pause extends JPanel {
    private final ImageIcon pauseBackground;
    private final JFrame frame;
    private final JPanel gamePanel; // Reference to the game panel
    private boolean isPaused = false;

    public Pause(JFrame gameFrame, JPanel gamePanel) {
        this.frame = gameFrame;
        this.gamePanel = gamePanel; // Store the reference to the game panel
        pauseBackground = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/PauseScreen/PauseBGwithText.png");
        setLayout(null);
        setFocusable(true); // Make sure the panel is focusable
        requestFocusInWindow(); // Request focus for key events
        setupButtons();
        setupKeyListener();
    }

    private void setupButtons() {
        // Resume button
        JLabel resumeButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/PauseScreen/Buttons/Resume/Resume_off.png",
                "MimalaFinal/MimalaFinal/src/assets/PauseScreen/Buttons/Resume/Resume_on.png",
                450 // Adjust manually
        );

        // Exit button
        JLabel exitButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/PauseScreen/Buttons/Exit/Exit_off.png",
                "MimalaFinal/MimalaFinal/src/assets/PauseScreen/Buttons/Exit/Exit_on.png",
                550 // Adjust manually
        );

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose(); // Close game
                new MainMenu(frame); // Redirect to main menu
            }
        });

        resumeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePause(); // Resume the game
            }
        });

        add(resumeButton);
        add(exitButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(800, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(offIcon);
            }
        });
        return button;
    }

    private void setupKeyListener() {
        setFocusable(true); // Ensures the panel can receive key events
        requestFocusInWindow(); // Requests focus so key events are captured

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            frame.setContentPane(this); // Show pause screen
        } else {
            frame.setContentPane(gamePanel); // Return to the game panel
        }
        frame.revalidate();
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(pauseBackground.getImage(), 0, 0, getWidth(), getHeight(), this); // Draw the background correctly
    }
}
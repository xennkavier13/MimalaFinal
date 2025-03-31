package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Pause extends JPanel {
    private ImageIcon pauseBackground;
    private JLabel resumeButton, exitButton;
    private JFrame frame;
    private boolean isPaused = false;
    private int yOffset = -1080; // Start off-screen

    public Pause(JFrame gameFrame) {
        this.frame = gameFrame;
        pauseBackground = new ImageIcon("C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\PauseScreen\\PauseBGwithText.png");
        setLayout(null);
        setupButtons();
        setupKeyListener();
    }

    private void setupButtons() {
        resumeButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\PauseScreen\\Buttons\\Resume\\Resume_off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\PauseScreen\\Buttons\\Resume\\Resume_on.png",
                800, 450 // Adjust manually
        );

        exitButton = createButton(
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\PauseScreen\\Buttons\\Exit\\Exit_off.png",
                "C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\PauseScreen\\Buttons\\Exit\\Exit_on.png",
                800, 550 // Adjust manually
        );

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose(); // Close game
                new MainMenu(); // Redirect to main menu
            }
        });

        add(resumeButton);
        add(exitButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int x, int y) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());

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
        frame.addKeyListener(new KeyAdapter() {
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
            new Thread(() -> {
                for (int i = -1080; i <= 0; i += 20) { // Sliding animation
                    yOffset = i;
                    repaint();
                    try { Thread.sleep(5); } catch (InterruptedException ignored) {}
                }
            }).start();
            frame.setContentPane(this);
            frame.revalidate();
        } else {
            frame.setContentPane(new MainMenu()); // Resume = return to game
            frame.revalidate();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(pauseBackground.getImage(), 0, yOffset, getWidth(), getHeight(), this);
    }
}
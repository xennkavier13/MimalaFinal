package state.UI;

import state.GameScreen;
import state.MainMenu;

import javax.swing.*;
import java.awt.*;

public class Pause extends JPanel {

    private JFrame frame;
    private Image pauseBackground;
    private JButton resumeButton, exitButton;

    public Pause(JFrame frame, GameScreen gameScreen) {
        this.frame = frame;
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));

        // Load background image
        pauseBackground = new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/PauseScreen_nobutton.png")).getImage();

        // --- Resume Button ---
        resumeButton = new JButton(new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/Resume/Resume_off.png")));
        resumeButton.setRolloverIcon(new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/Resume/Resume_hover.png")));
        resumeButton.setBorderPainted(false);
        resumeButton.setContentAreaFilled(false);
        resumeButton.setFocusPainted(false);
        resumeButton.setBounds(700, 400, 500, 100); // Adjust position & size

        resumeButton.addActionListener(e -> {
            frame.setContentPane(gameScreen); // Resume by going back to existing GameScreen
            frame.revalidate();
            frame.repaint();
        });

        // --- Exit Button ---
        exitButton = new JButton(new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/Exit/Exit_off.png")));
        exitButton.setRolloverIcon(new ImageIcon(getClass().getResource("/assets/PauseScreen/PAUSE/Exit/Exit_hover.png")));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(700, 520, 500, 100); // Adjust position & size

        exitButton.addActionListener(e -> {
            // Transition smoothly with black background
            JPanel newScreen = new MainMenu(frame);
            newScreen.setOpaque(true);
            newScreen.setBackground(Color.BLACK);

            // Smooth transition to new screen
            SwingUtilities.invokeLater(() -> {
                frame.getContentPane().removeAll();  // Clear old components first
                frame.setBackground(Color.BLACK);   // Prevent white flash
                frame.setContentPane(newScreen);
                frame.revalidate();
                frame.repaint();
            });


        });

        add(resumeButton);
        add(exitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(pauseBackground, 0, 0, getWidth(), getHeight(), this);
    }
}

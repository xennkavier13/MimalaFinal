package state;

import state.UI.CreditsScreen;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainMenu extends JPanel {
    private final ImageIcon mainMenuGif;
    private final JFrame frame;
    private Clip music;

    public MainMenu(JFrame frame) {
        this.frame = frame;
        mainMenuGif = loadIcon("assets/MainMenuScreen/MainMenuBG.gif");
        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(1920, 1080));

        // Apply smooth transition background (avoids flash)
        frame.setBackground(Color.BLACK);

        playMusic("/assets/MainMenuScreen/Sounds/MimalaMainMenuMusic.wav");

        setupButtons();
    }

    private void setupButtons() {
        JLabel startButton = createButton(
                "assets/MainMenuScreen/Start/Start_off.png",
                "assets/MainMenuScreen/Start/Start_hover.png",
                450, () -> {

                    stopMusic();

                    // Transition smoothly with black background
                    JPanel newScreen = new ModeSelection(frame);
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

                    // Stop music after the Map Selection Screen is shown
                    SwingUtilities.invokeLater(() -> stopMusic());
                }
        );

        JLabel endButton = createButton(
                "assets/MainMenuScreen/End/End_off.png",
                "assets/MainMenuScreen/End/End_hover.png",
                525, () -> {
                    stopMusic(); // Stop music when exiting
                    System.exit(0);
                }
        );

        JLabel creditsButton = createButton(
                "assets/MainMenuScreen/Credits/Credits_off.png",
                "assets/MainMenuScreen/Credits/Credits_hover.png",
                600, () -> {
                    stopMusic();

                    // Show credits screen on click
                    JPanel newScreen = new CreditsScreen(frame);
                    newScreen.setBackground(Color.BLACK); // Prevent flash during transition

                    // Smooth transition to credits screen
                    SwingUtilities.invokeLater(() -> {
                        frame.getContentPane().removeAll();
                        frame.setBackground(Color.BLACK); // Avoid flash
                        frame.setContentPane(newScreen);
                        frame.revalidate();
                        frame.repaint();
                    });
                }
        );

        add(startButton);
        add(endButton);
        add(creditsButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action) {
        ImageIcon offIcon = loadIcon(offPath);
        ImageIcon hoverIcon = loadIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(680, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setIcon(offIcon);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mainMenuGif.getImage(), 0, 0, getWidth(), getHeight(), this);
    }

    private void playMusic(String filePath) {
        try {
            // Load from resources (classpath)
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) {
                System.err.println("Music file not found in resources: " + filePath);
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            music = AudioSystem.getClip();
            music.open(audioStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (music != null && music.isRunning()) {
            music.stop();
            music.close();
        }
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Warning: Missing image at " + path);
        return new ImageIcon();
    }
}

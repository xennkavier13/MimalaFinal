package state.UI;

import state.MainMenu;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class IntroScreen extends JPanel {
    private final JFrame frame;
    private final ImageIcon gif;
    private final int gifDuration = 23500; // Duration of the new single GIF in milliseconds
    private Image currentImage;
    private Timer gifTimer;
    private Clip music;

    public IntroScreen(JFrame frame) {
        this.frame = frame;
        setLayout(null); // No layout manager
        setBackground(Color.BLACK); // Ensure the background is black during the intro
        setFocusable(true);

        // Use only one GIF instead of three
        gif = loadIcon("assets/MainMenuScreen/MimalaIntro.gif");

        setBounds(0, 0, frame.getWidth(), frame.getHeight());

        playGIF();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                skipToMainMenu();
            }
        });

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentImage != null) {
            g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), this); // Stretch to fit
        }
    }

    private void playGIF() {
        currentImage = gif.getImage();
        repaint();

        // Timer to handle the duration of the GIF
        gifTimer = new Timer(gifDuration, e -> skipToMainMenu());
        gifTimer.setRepeats(false);
        gifTimer.start();

        playIntroMusic();


    }

    private void playIntroMusic() {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/assets/MainMenuScreen/Sounds/intro_music.wav");
            if (audioSrc == null) {
                System.err.println("Audio file not found!");
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            music = AudioSystem.getClip();
            music.open(audioStream);
            music.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void skipToMainMenu() {
        if (gifTimer != null) {
            gifTimer.stop(); // Stop the timer if it is running
        }

        if (music != null && music.isRunning()) {
            music.stop();
            music.close();
        }

        JPanel newScreen = new MainMenu(frame);
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.setBackground(Color.BLACK); // Make sure to keep the background black during the transition
            frame.setContentPane(newScreen);
            frame.revalidate();
            frame.repaint();
        });
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        System.err.println("Missing GIF: " + path);
        return new ImageIcon();
    }
}

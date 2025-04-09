package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class IntroScreen extends JPanel {
    private final JFrame frame;
    private final ImageIcon[] gifs;
    private final int[] realDurations;
    private int currentIndex = 0;
    private final Timer[] gifTimers;
    private Image currentImage;

    public IntroScreen(JFrame frame) {
        this.frame = frame;
        setLayout(null); // No layout manager
        setBackground(Color.BLACK); // Ensure the background is black during the intro
        setFocusable(true);

        gifs = new ImageIcon[] {
                loadIcon("assets/MainMenuScreen/MimalaIntroFirst.gif"),
                loadIcon("assets/MainMenuScreen/MimalaIntroSecond.gif"),
                loadIcon("assets/MainMenuScreen/MimalaIntroThird.gif")
        };

        realDurations = new int[]{7000, 8000, 6000}; // first gif, second gif, third gif
        gifTimers = new Timer[gifs.length];

        setBounds(0, 0, frame.getWidth(), frame.getHeight());

        playNextGIF();

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

    private void playNextGIF() {
        if (currentIndex < gifs.length) {
            ImageIcon icon = gifs[currentIndex];
            currentImage = icon.getImage();
            repaint();

            gifTimers[currentIndex] = new Timer(realDurations[currentIndex], e -> {
                currentIndex++;
                playNextGIF();
            });
            gifTimers[currentIndex].setRepeats(false);
            gifTimers[currentIndex].start();
        } else {
            skipToMainMenu();
        }
    }

    private void skipToMainMenu() {
        // Stop all timers to prevent any unnecessary actions
        for (Timer t : gifTimers) {
            if (t != null) t.stop();
        }
        JPanel newScreen = new MainMenu(frame);
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.setBackground(Color.BLACK);  // Make sure to keep the background black during the transition
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

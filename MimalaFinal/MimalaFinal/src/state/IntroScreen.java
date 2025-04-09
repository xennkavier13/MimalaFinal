package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IntroScreen extends JPanel {
    private final JFrame frame;
    private final ImageIcon[] gifs;
    private final int[] durations; // Milliseconds per GIF
    private int currentIndex = 0;
    private Timer gifTimer;
    private JLabel gifLabel;

    public IntroScreen(JFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        gifs = new ImageIcon[]{
                loadIcon("assets/MainMenuScreen/MimalaIntroFirst.gif"),
                loadIcon("assets/MainMenuScreen/MimalaIntroSecond.gif"),
                loadIcon("assets/MainMenuScreen/MimalaIntroThird.gif")
        };

        durations = new int[]{
                5500, // First GIF duration
                5500, // Second GIF duration
                5000  // Third GIF duration
        };

        gifLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image image = gifs[currentIndex].getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this); // scale to fit
            }
        };

        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gifLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(gifLabel, BorderLayout.CENTER);

        gifLabel.setIcon(gifs[0]); // set first gif
        startGifTimer();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                skipToMainMenu();
            }
        });
    }

    private void startGifTimer() {
        gifTimer = new Timer(durations[currentIndex], e -> {
            currentIndex++;
            if (currentIndex < gifs.length) {
                gifLabel.setIcon(gifs[currentIndex]); // update GIF
                gifLabel.repaint(); // force repaint with scaling
                gifTimer.setDelay(durations[currentIndex]);
                gifTimer.restart();
            } else {
                skipToMainMenu();
            }
        });
        gifTimer.setRepeats(false);
        gifTimer.start();
    }

    private void skipToMainMenu() {
        if (gifTimer != null) gifTimer.stop();
        JPanel newScreen = new MainMenu(frame);

        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
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

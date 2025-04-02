package state;


import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JPanel {
    private final ImageIcon mainMenuGif;
    private final JFrame frame;
    private Clip music;

    public MainMenu(JFrame frame) {
        this.frame = frame;
        mainMenuGif = loadIcon("assets/MainMenuScreen/MainMenuPixelated.gif");
        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(1920, 1080));

        // Play the music using AudioManager (ensuring it doesn't restart if already playing)

        setupButtons();
    }

    private void setupButtons() {
        // Transition to ModeSelection
        JLabel startButton = createButton(
                "assets/MainMenuScreen/Start/Start_Off.png",
                "assets/MainMenuScreen/Start/Start_On.png",
                450, () -> {
                    // Create the new panel off-screen
                    JPanel newScreen = new ModeSelection(frame);
                    newScreen.setOpaque(true);
                    newScreen.setBackground(Color.BLACK); // Force the background to black

                    // Swap the content pane smoothly
                    SwingUtilities.invokeLater(() -> {
                        frame.getContentPane().removeAll();  // Clear old components first
                        frame.setBackground(Color.BLACK);   // Prevent white flashing
                        frame.setContentPane(newScreen);
                        frame.revalidate();
                        frame.repaint();
                    });
                }
        );

        JLabel endButton = createButton(
                "assets/MainMenuScreen/End/End_Off.png",
                "assets/MainMenuScreen/End/End_On.png",
                525, () -> {
                    stopMusic(); // Stop music when exiting
                    System.exit(0);
                }
        );

        JLabel creditsButton = createButton(
                "assets/MainMenuScreen/Credits/Credits_Off.png",
                "assets/MainMenuScreen/Credits/Credits_On.png",
                600, () -> System.out.println("Credits Clicked")
        );

        add(startButton);
        add(endButton);
        add(creditsButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action) {
        ImageIcon offIcon = loadIcon(offPath);
        ImageIcon hoverIcon = loadIcon(hoverPath);
        JLabel button = new JLabel(offIcon);
        button.setBounds(970, y, offIcon.getIconWidth(), offIcon.getIconHeight());

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
            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                System.err.println("Music file not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
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
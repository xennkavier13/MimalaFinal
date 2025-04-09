package state;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ModeSelection extends JPanel {
    private final ImageIcon modeSelectionBg;
    private final JFrame frame;
    private Clip music;

    public ModeSelection(JFrame gameFrame) {
        this.frame = gameFrame;
        modeSelectionBg = new ImageIcon("MimalaFinal/MimalaFinal/src/assets/MainMenuScreen/MainMenuBG.gif");
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();

        playMusic("/assets/MainMenuScreen/Sounds/MimalaMainMenuMusic.wav");
    }

    private void setupButtons() {
        JLabel pvpButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PVP/PVP_off.png",
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PVP/PVP_hover.png",
                450, () -> {
                    frame.setContentPane(new CharacterSelection(frame, "PVP")); // Pass mode
                    frame.revalidate();
                    frame.repaint();
                }
        );

        JLabel pvcButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PvAI/PvAI_off.png",
                "MimalaFinal/MimalaFinal/src/assets/ModeSelectionScreen/Buttons/PvAI/PvAI_hover.png",
                530, () -> {
                    frame.setContentPane(new CharacterSelection(frame, "PVC")); // Pass mode
                    frame.revalidate();
                    frame.repaint();
                }
        );

        add(pvpButton);
        add(pvcButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action) {
        ImageIcon offIcon = new ImageIcon(offPath);
        ImageIcon hoverIcon = new ImageIcon(hoverPath);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(modeSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}
package state.CharacterScreen.Select;

import state.CharacterScreen.*;
import state.MapSelection;
import state.ModeSelection;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class CharacterSelection extends JPanel {
    private final ImageIcon characterSelectionBg;
    private final JFrame frame;
    private final String[] characterNames = {
            "Pyrothar", "Azurox", "Zenfang", "Auricannon",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };
    private String firstPlayerSelection = null;
    private String player2Selection = null;
    private static final String AI_PLAYER_NAME = "Computer";
    private final String mode;
    private Clip music;

    public CharacterSelection(JFrame frame, String mode) {
        this.frame = frame;
        this.mode = mode;
        characterSelectionBg = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterSelect_BGcombine.png");

        if ("PVC".equals(this.mode)) {
            this.player2Selection = AI_PLAYER_NAME;
            System.out.println("PVC mode: Player 2 set to " + AI_PLAYER_NAME);
            // Potentially disable P2 selection UI elements here
        }
        setLayout(null);  // Absolute layout
        setPreferredSize(new Dimension(1920, 1080));
        setupButtons();

//        playMusic("/assets/MainMenuScreen/Sounds/MimalaMainMenuMusic.wav");
    }

    // Inside the CharacterSelection class, update the setupButtons() method

    private void setupButtons() {
        // Back button
        JLabel backButton = createButton(
                "MimalaFinal/MimalaFinal/src/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png",
                "MimalaFinal/MimalaFinal/src/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png",
                50, 57,
                () -> {
                    frame.setContentPane(new ModeSelection(frame));
                    frame.revalidate();
                    frame.repaint();
                }
        );
        add(backButton);

        // Create hover buttons for each character
        createCharacterButton("Pyrothar", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Pyrothar_hover.png", 614, 223);
        createCharacterButton("Azurox", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Azurox_hover.png", 325, 243);
        createCharacterButton("Zenfang", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Zenfang_hover.png", 982, 245);
        createCharacterButton("Auricannon", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Auricannon_hover.png", 1140, 243);
        createCharacterButton("Vexmorth", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Vexmorth_hover.png", 349, 610);
        createCharacterButton("Astridra", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Astrida_hover.png", 657, 614);
        createCharacterButton("Varkos", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Varkos_hover.png", 847, 614);
        createCharacterButton("Ignisveil", "MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\Character_hover\\Ignisveil_hover.png", 1249, 535);
    }

    private JLabel createButton(String normalImagePath, String hoverImagePath, int x, int y, Runnable onClickAction) {
        ImageIcon normalIcon = new ImageIcon(normalImagePath);
        ImageIcon hoverIcon = new ImageIcon(hoverImagePath);

        JLabel button = new JLabel(normalIcon);
        button.setBounds(x, y, normalIcon.getIconWidth(), normalIcon.getIconHeight());

        // Mouse listener for hover effects and click action
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(normalIcon);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                onClickAction.run();
            }
        });

        return button;
    }



    private void createCharacterButton(String characterName, String hoverImagePath, int x, int y) {
        ImageIcon hoverIcon = new ImageIcon(hoverImagePath);

        JLabel button = new JLabel();
        button.setBounds(x, y, hoverIcon.getIconWidth(), hoverIcon.getIconHeight());
        System.out.println(hoverIcon.getIconWidth() +" + " + hoverIcon.getIconHeight());
        // Mouse listener for hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (characterName.equals("Pyrothar")) {
                    frame.setContentPane(new PyrotharScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Azurox")){
                    frame.setContentPane(new AzuroxScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Astridra")){
                    frame.setContentPane(new AstridraScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Vexmorth")){
                    frame.setContentPane(new VexmorthScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Zenfang")){
                    frame.setContentPane(new ZenfangScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Auricannon")){
                    frame.setContentPane(new AuricannonScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Varkos")){
                    frame.setContentPane(new VarkosScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                } else if(characterName.equals("Ignisveil")){
                    frame.setContentPane(new IgnisveilScreen(frame,mode));
                    frame.revalidate();
                    frame.repaint();
                }else if (firstPlayerSelection == null) {
                    firstPlayerSelection = characterName;
                    System.out.println(firstPlayerSelection + " selected by Player 1!");
                    if (mode.equals("PVP")) {
                        frame.setContentPane(new SecondPlayerSelection(frame, firstPlayerSelection, "PVP"));
                    } else {
                        String secondPlayerSelection = selectRandomCharacter();
                        System.out.println(secondPlayerSelection + " selected for Player 2!");
                        frame.setContentPane(new MapSelection(frame, firstPlayerSelection, secondPlayerSelection,"PVC"));
                    }
                    frame.revalidate();
                    frame.repaint();
                }
            }
        });

        add(button);
    }

    private String selectRandomCharacter() {
        Random random = new Random();
        return characterNames[random.nextInt(characterNames.length)];
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
        g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

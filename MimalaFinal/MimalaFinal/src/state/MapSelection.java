package state;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.awt.event.KeyEvent;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import state.CharacterScreen.*; // Imports PyrotharScreen, AzuroxScreen etc.
import state.CharacterScreen.Select.SecondPlayerSelection; // For going back in PvP
import state.CharacterScreen.Select.CharacterSelectionScreen; // Superclass might be needed
import state.ModeSelection;

public class MapSelection extends JPanel {
    private final ImageIcon mapSelectionBg;
    private final JFrame frame;
    private Clip music;

    private final String[] mapPaths = {
            "assets/StageMap/Waters.gif",
            "assets/StageMap/Dojo.gif",
            "assets/StageMap/DesertedLand.gif",
            "assets/StageMap/DragonLair.gif",
            "assets/StageMap/King'sThrone.gif",
            "assets/StageMap/RoyalPalace.gif"
    };

    private final String[] mapPreviewPaths = {
            "assets/StageMap/Waters.png",
            "assets/StageMap/Dojo.png",
            "assets/StageMap/DesertedLand.png",
            "assets/StageMap/DragonLair.png",
            "assets/StageMap/King'sThrone.png",
            "assets/StageMap/RoyalPalace.png"
    };

    private final String[] mapNames = {
            "Waters", "Dojo", "DesertedLand", "DragonLair", "King'sThrone", "RoyalPalace"
    };

    private String selectedMapPath = null;
    private JLabel currentlySelectedMapLabel = null;
    private final Border selectionBorder = BorderFactory.createLineBorder(Color.YELLOW, 5);
    private final Border hoverBorder = BorderFactory.createLineBorder(new Color(206, 144, 10), 10); // #ce900a
    private final Border defaultBorder = BorderFactory.createLineBorder(Color.BLACK, 10);

    private final String firstPlayerSelection;
    private final String secondPlayerSelection;
    private final Random random = new Random();
    private final String gameMode;

    public MapSelection(JFrame frame, String firstPlayerSelection, String secondPlayerSelection, String mode) {
        this.frame = frame;
        mapSelectionBg = new ImageIcon(getClass().getResource("/assets/StageMap/MapSelectBG.gif"));
        setLayout(null);
        setPreferredSize(new Dimension(1920, 1080));
        this.firstPlayerSelection = firstPlayerSelection;
        this.secondPlayerSelection = secondPlayerSelection;
        this.gameMode = mode;

        setBackground(Color.WHITE);

        setupComponents();
//        playMusic("/assets/MainMenuScreen/Sounds/MimalaMainMenuMusic.wav");

        setFocusable(true);

        setupEscapeKeyBinding(); // Add the escape key functionality

        // <<< ADD THIS LINE (at the very end of constructor preferably) >>>
        SwingUtilities.invokeLater(this::requestFocusInWindow); // Request focus
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapSelectionBg != null) {
            g.drawImage(mapSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void setupComponents() {
        Dimension panelSize = getPreferredSize();
        int panelWidth = panelSize.width;
        int panelHeight = panelSize.height;

        int titleY = 10;

        int mapColumns = 3;
        int mapWidth = 450;
        int mapHeight = 300;
        int spacingX = 80;
        int spacingY = 80;
        int totalGridWidth = (mapColumns * mapWidth) + ((mapColumns - 1) * spacingX);
        int startX = (panelWidth - totalGridWidth) / 2;
        int startY = titleY + 200;
        if (startX < 0) startX = 10;
        if (startY < 0) startY = 10;

        createMapPreviewButton(mapNames[0], mapPreviewPaths[0], startX, startY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[1], mapPreviewPaths[1], startX + mapWidth + spacingX, startY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[2], mapPreviewPaths[2], startX + 2 * (mapWidth + spacingX), startY, mapWidth, mapHeight);

        int secondRowY = startY + mapHeight + spacingY;
        createMapPreviewButton(mapNames[3], mapPreviewPaths[3], startX, secondRowY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[4], mapPreviewPaths[4], startX + mapWidth + spacingX, secondRowY, mapWidth, mapHeight);
        createMapPreviewButton(mapNames[5], mapPreviewPaths[5], startX + 2 * (mapWidth + spacingX), secondRowY, mapWidth, mapHeight);

        JLabel selectButton = createButton(
                "/assets/MapSelectionScreen/Select_off.png",
                "/assets/MapSelectionScreen/Select_hover.png",
                secondRowY + mapHeight + 50,
                () -> {
                    if (selectedMapPath != null) {
                        stopMusic();
                        transitionToGameScreen(selectedMapPath);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a map first.", "No Map Selected", JOptionPane.WARNING_MESSAGE);
                    }
                }
        );

        JLabel randomButton = createButton(
                "/assets/MapSelectionScreen/Random_off.png",
                "/assets/MapSelectionScreen/Random_hover.png",
                secondRowY + mapHeight + 50,
                () -> {
                    stopMusic();
                    int randomIndex = random.nextInt(mapPaths.length);
                    transitionToGameScreen(mapPaths[randomIndex]);
                }
        );

        int buttonSpacing = 50;
        int selectButtonWidth = selectButton.getIcon().getIconWidth();
        int randomButtonWidth = randomButton.getIcon().getIconWidth();
        int totalButtonsWidth = selectButtonWidth + randomButtonWidth + buttonSpacing;
        int buttonsStartX = (panelWidth - totalButtonsWidth) / 2;

        selectButton.setBounds(buttonsStartX, secondRowY + mapHeight + 50, selectButtonWidth, selectButton.getIcon().getIconHeight());
        randomButton.setBounds(buttonsStartX + selectButtonWidth + buttonSpacing, secondRowY + mapHeight + 50, randomButtonWidth, randomButton.getIcon().getIconHeight());

        add(selectButton);
        add(randomButton);
    }

    private JLabel createButton(String offPath, String hoverPath, int y, Runnable action) {
        ImageIcon offIcon = new ImageIcon(getClass().getResource(offPath));
        ImageIcon hoverIcon = new ImageIcon(getClass().getResource(hoverPath));
        JLabel button = new JLabel(offIcon);
        button.setBounds(0, y, offIcon.getIconWidth(), offIcon.getIconHeight());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(offIcon);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return button;
    }

    private void createMapPreviewButton(String mapName, String previewResourcePath, int x, int y, int width, int height) {
        JLabel button = new JLabel();
        try {
            URL imgURL = getClass().getResource("/" + previewResourcePath);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } else {
                button.setText("Missing: " + mapName);
                button.setOpaque(true);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.RED);
            }
        } catch (Exception e) {
            button.setText("Error: " + mapName);
            button.setOpaque(true);
            button.setBackground(Color.DARK_GRAY);
            button.setForeground(Color.ORANGE);
        }

        button.setBounds(x, y, width, height);
        button.setBorder(defaultBorder);

        final JLabel finalButton = button;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (currentlySelectedMapLabel != finalButton) {
                    finalButton.setBorder(hoverBorder);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (currentlySelectedMapLabel != finalButton) {
                    finalButton.setBorder(defaultBorder);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentlySelectedMapLabel != null) {
                    currentlySelectedMapLabel.setBorder(defaultBorder);
                }
                finalButton.setBorder(selectionBorder);
                currentlySelectedMapLabel = finalButton;
                selectedMapPath = mapPaths[mapNameToIndex(mapName)];
            }
        });
        add(button);
    }

    private int mapNameToIndex(String mapName) {
        for (int i = 0; i < mapNames.length; i++) {
            if (mapNames[i].equals(mapName)) return i;
        }
        return -1;
    }

    private void setupEscapeKeyBinding() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionKey = "goBackFromMapSelect";

        im.put(escapeKey, actionKey);
        am.put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Escape pressed on Map Selection. Going back...");
                stopMusic(); // Stop music if any was playing

                // Determine the previous screen based on game mode
                if ("PVP".equalsIgnoreCase(gameMode)) {
                    // Go back to Player 2's character selection grid
                    System.out.println("Going back to SecondPlayerSelection screen.");
                    frame.setContentPane(new SecondPlayerSelection(frame, firstPlayerSelection, gameMode));
                } else { // PVC or Arcade mode
                    // Go back to Player 1's character *confirmation* screen
                    String p1Character = firstPlayerSelection;
                    System.out.println("Going back to confirmation screen for P1: " + p1Character);
                    JPanel previousScreen = null;
                    // Use a switch to instantiate the correct character screen
                    // Assumes you have classes like PyrotharScreen, AzuroxScreen etc.
                    if (p1Character == null) { // Safety check if P1 name is somehow null
                        System.err.println("Cannot go back: Player 1 character unknown. Returning to Mode Selection.");
                        previousScreen = new ModeSelection(frame);
                    } else {
                        switch (p1Character) {
                            case "Pyrothar":
                                previousScreen = new PyrotharScreen(frame, p1Character, gameMode, null); // Pass null for P1 selection arg
                                break;
                            case "Azurox":
                                previousScreen = new AzuroxScreen(frame, p1Character, gameMode, null);
                                break;
                            case "Zenfang":
                                previousScreen = new ZenfangScreen(frame, p1Character, gameMode, null);
                                break;
                            case "Auricannon":
                                previousScreen = new AuricannonScreen(frame, p1Character, gameMode, null);
                                break;
                            case "Vexmorth":
                                previousScreen = new VexmorthScreen(frame, p1Character, gameMode, null);
                                break;
                            case "Astridra":
                                previousScreen = new AstridraScreen(frame, p1Character, gameMode, null);
                                break;
                            case "Varkos":
                                previousScreen = new VarkosScreen(frame, p1Character, gameMode, null);
                                break;
                            case "Ignisveil":
                                previousScreen = new IgnisveilScreen(frame, p1Character, gameMode, null);
                                break;
                            default:
                                System.err.println("Cannot go back: Unknown character '" + p1Character + "'. Returning to Mode Selection.");
                                previousScreen = new ModeSelection(frame); // Fallback
                                break;
                        }
                    }
                    frame.setContentPane(previousScreen);
                }
                frame.revalidate();
                frame.repaint();
            }
        });
        System.out.println("Escape key binding set up for MapSelection.");
    }

    private void transitionToGameScreen(String mapResourcePath) {
        if (mapResourcePath == null) return;
        SwingUtilities.invokeLater(() -> {
            stopMusic();
            frame.setContentPane(new GameScreen(frame, firstPlayerSelection, secondPlayerSelection, mapResourcePath, gameMode));
            frame.revalidate();
            frame.repaint();
        });
    }

    private void playMusic(String filePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) return;
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            music = AudioSystem.getClip();
            music.open(audioStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();
        } catch (Exception ignored) {}
    }

    private void stopMusic() {
        if (music != null && music.isRunning()) {
            music.stop();
            music.close();
        }
    }
}

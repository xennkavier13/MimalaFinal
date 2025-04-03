package state.CharacterScreen;

import state.CharacterSelection;
import state.MapSelection;
import state.SecondPlayerSelection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Random;

public abstract class CharacterSelectionScreen extends JPanel {
    private final JFrame frame;
    private final String mode;
    private JPanel confirmationPanel;

    private static final String[] ALL_CHARACTER_NAMES = {
            "Pyrothar", "Azurox", "Zenfang", "Aurelix",
            "Vexmorth", "Astridra", "Varkos", "Ignisveil"
    };

    protected ImageIcon characterGif;

    // Abstract method to be implemented by each character
    protected abstract String getGifPath();

    public CharacterSelectionScreen(JFrame frame, String mode) {
        this.frame = frame;
        this.mode = mode;
        setPreferredSize(new Dimension(1920, 1080));
        setLayout(null);

        this.characterGif = loadImage(getGifPath());
        if (this.characterGif == null) {
            System.err.println("CRITICAL: Failed to load background GIF: " + getGifPath());
        }
        setupButtons();
    }

    private ImageIcon loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) return null;
        try {
            URL imgURL = getClass().getResource(resourcePath);
            return (imgURL != null) ? new ImageIcon(imgURL) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JLabel backButton;
    private JLabel continueButton;

    private void setupButtons() {
        String backOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_off.png";
        String backHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/Back/Back_hover.png";
        String contOffPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/AstridraButtons/offButton.png";
        String contHoverPath = "/assets/CharacterSelectionScreen/CharacterScreenButtons/AstridraButtons/hoverButton.png";

        backButton = createButton(backOffPath, backHoverPath, 0, 30, () -> {
            if (confirmationPanel == null) { // Prevent interaction when confirmation is open
                frame.setContentPane(new CharacterSelection(frame, this.mode));
                frame.revalidate();
                frame.repaint();
            }
        });
        add(backButton);

        continueButton = createButton(contOffPath, contHoverPath, 1200, 890, () -> {
            if (confirmationPanel == null) { // Prevent interaction when confirmation is open
                showConfirmationScreen();
            }
        });
        add(continueButton);
    }

    private void showConfirmationScreen() {
        if (confirmationPanel != null) {
            remove(confirmationPanel);
        }

        confirmationPanel = new JPanel(null);
        confirmationPanel.setBounds(510, 190, 900, 700);
        confirmationPanel.setOpaque(false);

        // Load and add the confirmation background first
        JLabel confirmImage = new JLabel(loadImage("/assets/CharacterSelectionScreen/CharacterConfirm/CharacterConfirm_off.png"));
        confirmImage.setBounds(0, 0, 900, 700);
        confirmationPanel.add(confirmImage);

        // Create Yes and No buttons
        JLabel yesButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterConfirm/Yes_off.png",
                "/assets/CharacterSelectionScreen/CharacterConfirm/Yes_on.png",
                270, 390, this::continueToNextScreen
        );

        JLabel noButton = createButton(
                "/assets/CharacterSelectionScreen/CharacterConfirm/No_off.png",
                "/assets/CharacterSelectionScreen/CharacterConfirm/No_on.png",
                467, 390, this::removeConfirmation
        );

        confirmationPanel.add(yesButton);
        confirmationPanel.add(noButton);
        add(confirmationPanel);

        // Bring buttons to front
        confirmationPanel.setComponentZOrder(yesButton, 0);
        confirmationPanel.setComponentZOrder(noButton, 0);

        // Disable Back and Continue buttons
        backButton.setEnabled(false);
        continueButton.setEnabled(false);

        revalidate();
        repaint();
    }

    private void removeConfirmation() {
        if (confirmationPanel != null) {
            remove(confirmationPanel);
            confirmationPanel = null;

            // Re-enable Back and Continue buttons
            backButton.setEnabled(true);
            continueButton.setEnabled(true);

            repaint();
        }
    }

    private void continueToNextScreen() {
        String player1Selection = getCharacterName();
        if ("PVC".equalsIgnoreCase(this.mode)) {
            String player2Selection = selectRandomCharacterForPVE(player1Selection);
            frame.setContentPane(new MapSelection(frame, player1Selection, player2Selection));
        } else if ("PVP".equalsIgnoreCase(this.mode)) {
            frame.setContentPane(new SecondPlayerSelection(frame, player1Selection));
        }
        frame.revalidate();
        frame.repaint();
    }

    private JLabel createButton(String offResourcePath, String hoverResourcePath, int x, int y, Runnable action) {
        ImageIcon offIcon = loadImage(offResourcePath);
        ImageIcon hoverIcon = loadImage(hoverResourcePath);

        if (offIcon == null) return new JLabel("Error");

        JLabel button = new JLabel(offIcon);
        button.setBounds(x, y, offIcon.getIconWidth(), offIcon.getIconHeight());
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setIcon(hoverIcon); }
            @Override public void mouseExited(MouseEvent e) { button.setIcon(offIcon); }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return button;
    }

    private String selectRandomCharacterForPVE(String player1Choice) {
        Random random = new Random();
        String aiChoice;
        do {
            aiChoice = ALL_CHARACTER_NAMES[random.nextInt(ALL_CHARACTER_NAMES.length)];
        } while (aiChoice.equals(player1Choice));
        return aiChoice;
    }

    // Abstract method to be implemented by each character
    protected abstract String getCharacterName();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (characterGif != null) {
            g.drawImage(characterGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Error: Background GIF not loaded!", 50, 50);
        }
    }
}

package state.UI;

import state.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreditsScreen extends JPanel {
    private final JFrame frame;
    private final ImageIcon creditsGif;
    private final JLabel gifLabel;

    private final int gifDuration = 25000; // 27 seconds (adjust as necessary)

    public CreditsScreen(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK); // Ensuring the background is consistent

        creditsGif = loadIcon("assets/MainMenuScreen/Credits.gif");

        gifLabel = new JLabel(creditsGif);
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gifLabel.setVerticalAlignment(SwingConstants.CENTER);

        add(gifLabel, BorderLayout.CENTER);

        // Start the timer for automatic return to Main Menu after the duration
        new Timer(gifDuration, e -> returnToMainMenu()) {{
            setRepeats(false);
            start();
        }};

        // Allow skipping by mouse click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                returnToMainMenu();
            }
        });
    }

    private void returnToMainMenu() {
        JPanel mainMenu = new MainMenu(frame);
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.setBackground(Color.BLACK); // Avoid flash when returning
            frame.setContentPane(mainMenu);
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

package state;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JPanel {
    private ImageIcon mainMenuGif;

    public MainMenu() {
        mainMenuGif = new ImageIcon("C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\MainMenu.gif");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0); // Exit on click (replace later for "click to continue") FOR NOW
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mainMenuGif.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

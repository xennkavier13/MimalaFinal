package state;

import javax.swing.*;
import java.awt.*;

public class CharacterSelection extends JPanel {
    private final ImageIcon characterSelectionBg;

    public CharacterSelection() {
        characterSelectionBg = new ImageIcon("C:\\Users\\User\\IdeaProjects\\MimalaFinal\\MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\SampleCharacterSelection.png");
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(characterSelectionBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}

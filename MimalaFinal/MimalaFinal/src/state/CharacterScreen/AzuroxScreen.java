package state.CharacterScreen;

import javax.swing.*;
import java.awt.*;

public class AzuroxScreen extends JPanel{

    public AzuroxScreen(JFrame frame) {
        setLayout(new BorderLayout());

        // Load the looping GIF
        ImageIcon azuroxGif = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreen\\AzuroxSelection.gif");
        JLabel gifLabel = new JLabel(azuroxGif);
        gifLabel.setHorizontalAlignment(JLabel.CENTER);

        add(gifLabel, BorderLayout.CENTER);
    }
}
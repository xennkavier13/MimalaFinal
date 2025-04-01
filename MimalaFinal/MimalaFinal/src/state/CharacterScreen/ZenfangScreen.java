package state.CharacterScreen;

import javax.swing.*;
import java.awt.*;

public class ZenfangScreen extends JPanel{

    public ZenfangScreen(JFrame frame) {
        setLayout(new BorderLayout());

        // Load the looping GIF
        ImageIcon pyrotharGif = new ImageIcon("MimalaFinal\\MimalaFinal\\src\\assets\\CharacterSelectionScreen\\CharacterScreen\\ZenfangSelection.gif");
        JLabel gifLabel = new JLabel(pyrotharGif);
        gifLabel.setHorizontalAlignment(JLabel.CENTER);

        add(gifLabel, BorderLayout.CENTER);
    }
}

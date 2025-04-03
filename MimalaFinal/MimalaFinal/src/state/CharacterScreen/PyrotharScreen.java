package state.CharacterScreen;

import javax.swing.*;

public class PyrotharScreen extends CharacterSelectionScreen {

    // Constructor used by CharacterSelection (for P1)
    public PyrotharScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    // Constructor used by SecondPlayerSelection (for P2)
    public PyrotharScreen(JFrame frame, String mode, String firstPlayerSelection) {
        super(frame, mode, firstPlayerSelection);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/PyrotharSelection.gif"; // Example path - ADJUST AS NEEDED
    }

    @Override
    protected String getCharacterName() {
        return "Pyrothar";
    }
}
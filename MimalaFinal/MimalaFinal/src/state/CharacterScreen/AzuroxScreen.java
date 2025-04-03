package state.CharacterScreen;

import javax.swing.*;

public class AzuroxScreen extends CharacterSelectionScreen {

    public AzuroxScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/AzuroxSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Azurox";
    }
}

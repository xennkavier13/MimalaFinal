package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

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

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/AzuroxInfo.png";
    }
}

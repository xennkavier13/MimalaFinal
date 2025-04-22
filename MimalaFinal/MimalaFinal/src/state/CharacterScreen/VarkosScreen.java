package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class VarkosScreen extends CharacterSelectionScreen {

    public VarkosScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        super(frame, characterName, mode, firstPlayerSelection);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/VarkosSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Varkos";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/VarkosInfo.png";
    }
}

package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class PyrotharScreen extends CharacterSelectionScreen {

    public PyrotharScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        super(frame, characterName, mode, firstPlayerSelection);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/PyrotharSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Pyrothar";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/PyrotharInfo.png";
    }
}
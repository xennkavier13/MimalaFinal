package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.JFrame;

public class VexmorthScreen extends CharacterSelectionScreen {

    public VexmorthScreen(JFrame frame, String characterName, String mode, String firstPlayerSelection) {
        super(frame, characterName, mode, firstPlayerSelection);
    }
    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/vexmorthSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Vexmorth";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/VexmorthInfo.png";
    }
}
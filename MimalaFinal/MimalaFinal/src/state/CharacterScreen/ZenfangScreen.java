package state.CharacterScreen;

import state.CharacterScreen.Select.CharacterSelectionScreen;

import javax.swing.*;

public class ZenfangScreen extends CharacterSelectionScreen {

    public ZenfangScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    @Override
    protected String getGifPath() {
        return "/assets/CharacterSelectionScreen/CharacterScreen/ZenfangSelection.gif";
    }

    @Override
    protected String getCharacterName() {
        return "Zenfang";
    }

    @Override
    protected String getInfoImagePath() {
        return "/assets/CharacterSelectionScreen/CharacterInfos/ZenfangInfo.png";
    }
}

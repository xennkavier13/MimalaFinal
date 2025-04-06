package state.CharacterScreen;

import javax.swing.JFrame;

public class VexmorthScreen extends CharacterSelectionScreen {

    public VexmorthScreen(JFrame frame, String mode) {
        super(frame, mode);
    }

    public VexmorthScreen(JFrame frame, String mode, String firstPlayerSelection) {
        super(frame, mode, firstPlayerSelection);
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
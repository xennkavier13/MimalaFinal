package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {
    private static Clip music;

    public static void playMusic(String filePath) {
        if (music != null && music.isRunning()) {
            return; // Music is already playing, don't restart
        }

        try {
            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                System.err.println("Music file not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            music = AudioSystem.getClip();
            music.open(audioStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (music != null && music.isRunning()) {
            music.stop();
            music.close();
        }
    }
}

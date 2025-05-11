package spacegame;

import javax.sound.sampled.*;

public class SoundPlayer {

    public static void play(String resourcePath) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(
                    SoundPlayer.class.getResource(resourcePath));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + resourcePath);
            e.printStackTrace();
        }
    }
}

package VueControleur;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;

public class Music {
    Clip clip;
    AudioInputStream sound;

    public Music(String soundFileName) {
        setFile(soundFileName);
    }

    public void setFile(String soundFileName) {
        try {
            File file = new File(soundFileName);
            sound = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(sound);
        } catch (Exception e) {
            System.out.println("Error initialisation music");
        }
    }
    public void play() {
        try {
            clip.start();
        } catch (Exception e) {
            System.out.println("Error lecture music");
        }
    }
    public void stop() throws IOException {
        try {
            sound.close();
            clip.close();
            clip.stop();
        } catch (Exception e) {
            System.out.println("Error lecture music");
        }
    }
}

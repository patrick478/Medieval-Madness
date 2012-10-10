package soundengine;

import java.net.URL;
import javax.sound.sampled.*;

public class LoopSound {

  public static void main(String[] args) throws Exception {
    URL url = new URL(
      "Daybreak.wav");
    Clip clip = AudioSystem.getClip();
    AudioInputStream ais = AudioSystem.
      getAudioInputStream( url );
    clip.open(ais);
    clip.loop(5);
    javax.swing.JOptionPane.showMessageDialog(null, "Close to exit!");
  }
} 

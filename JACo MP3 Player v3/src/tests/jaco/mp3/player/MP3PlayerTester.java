package tests.jaco.mp3.player;

import jaco.mp3.player.MP3Player;

import java.io.File;

public class MP3PlayerTester {

  public static void main(String[] args) {
    // new MP3Player(new
    // File("E:/Mp3/12. Raaban & Evana - Burn It Up-ES.mp3")).play();
    // new MP3Player().add(new File("E:/Mp3"), false).play();

    final MP3Player player = new MP3Player();
    player.add(new File("resources"), false).play();

    new Thread() {
      public void run() {
        try {
          Thread.sleep(2000);
        } catch (Exception e) {}

        System.out.println("skipForward");
        player.skipForward();
      }
    }.start();
  }

}

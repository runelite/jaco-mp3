package tests.jaco.mp3;

import jaco.mp3.MP3Player;

import java.io.File;

public class MP3PlayerTester {

  public static void main(String[] args) {
    new MP3Player(new File("E:/Mp3/12. Raaban & Evana - Burn It Up-ES.mp3")).play();
  }

}

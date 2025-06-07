package tests.jaco.mp3.player;

import jaco.mp3.player.MP3Player;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class MP3PlayerTest {

  @Test
  public void playSingleMp3File() throws Exception {

    MP3Player player = new MP3Player();
    player.add(new File("resources"), false);

    player.play();
    Assert.assertTrue(player.isPlaying());

    player.pause();
    Assert.assertTrue(player.isPaused());

    player.play();
    Thread.sleep(2000);
    player.stop();
    Assert.assertTrue(player.isStopped());

  }

}

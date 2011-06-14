package tests.jaco.mp3.player;

import jaco.mp3.player.MP3Player;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MP3PlayerTester {

  public static void main(String[] args) throws Exception {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    MP3Player player = new MP3Player();
    player.add(new File("resources"), false);

    // player.play();

    JFrame frame = new JFrame("MP3 Player");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(player);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

  }

}

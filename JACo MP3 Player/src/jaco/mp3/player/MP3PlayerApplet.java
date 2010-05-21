package jaco.mp3.player;

import jaco.mp3.player.plaf.MP3PlayerUICompact;

import java.awt.Color;
import java.net.URL;

import javax.swing.JApplet;

public class MP3PlayerApplet extends JApplet {

  /** serialVersionUID */
  private static final long serialVersionUID = 46146126474303823L;

  @Override
  public void init() {
    try {

      try {
        getContentPane().setBackground(Color.decode(getParameter("background")));
      } catch (Exception e) {}

      if ("true".equals(getParameter("compact"))) {
        MP3Player.setDefaultUI(MP3PlayerUICompact.class);
      }

      MP3Player player = new MP3Player();
      player.setRepeat(true);

      String[] playlist = getParameter("playlist").split(",");
      for (String mp3 : playlist) {
        player.addToPlayList(new URL(getCodeBase() + mp3.trim()));
      }

      getContentPane().add(player);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

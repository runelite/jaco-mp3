package jaco.mp3.player;

import jaco.mp3.player.plaf.MP3PlayerUICompact;

import java.awt.Color;
import java.net.URL;

import javax.swing.JApplet;

public class MP3PlayerApplet extends JApplet {

  /** serialVersionUID */
  private static final long serialVersionUID = 46146126474303823L;

  // private MP3Player player;

  @Override
  public void init() {
    try {

      getContentPane().setBackground(Color.decode(getParameter("boxbgcolor")));

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

  @Override
  public void stop() {
  // if (player != null) {
  // player.stop();
  // }
  }

  @Override
  public void destroy() {
  // player = null;
  }

}

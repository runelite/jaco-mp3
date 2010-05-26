package jaco.mp3.player;

/**
 * The listener interface for handling player events.
 * 
 * @version 1.02, May 26, 2010
 * @author Cristian Sulea (<a
 *         href=http://cristiansulea.entrust.ro/>http://cristiansulea
 *         .entrust.ro</a>)
 */
public interface MP3PlayerListener {

  /**
   * Invoked when a player start playing.
   */
  void onPlay(MP3Player player);

  /**
   * Invoked when a player pause.
   */
  void onPause(MP3Player player);

  /**
   * Invoked when a player stops.
   */
  void onStop(MP3Player player);

}

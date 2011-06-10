/*
 * Copyright (C) <2010> Cristian Sulea ( http://cristiansulea.entrust.ro )
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jaco.mp3.player;

import jaco.mp3.MP3;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Java MP3 Player
 * 
 * <pre>
 * new MP3Player(&quot;test.mp3&quot;).play();
 * ...
 * new MP3Player(new File(&quot;test.mp3&quot;)).play();
 * </pre>
 * 
 * @version 2.00, April 15, 2011
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
@SuppressWarnings("serial")
public class MP3Player extends JPanel {

  public static final String UI_CLASS_ID = MP3Player.class.getSimpleName() + "UI";

  public String getUIClassID() {
    return UI_CLASS_ID;
  }

  //

  /**
   * Sets the look and feel (L&F) class that renders the {@link MP3Player}
   * component.
   * 
   * @param uiClass
   *          the {@link MP3PlayerUI} L&F class
   */
  public static void setUIClass(Class<? extends MP3PlayerUI> uiClass) {
    UIManager.getDefaults().put(MP3Player.UI_CLASS_ID, uiClass.getName());
  }

  static {
    if (UIManager.getDefaults().get(MP3Player.UI_CLASS_ID) == null) {
      setUIClass(MP3PlayerUI.class);
    }
  }

  //

  private final Random random = new Random();

  private volatile List<MP3> playlist = new Vector<MP3>();

  private volatile MP3 mp3;

  private volatile boolean isPaused = false;
  private volatile boolean isStopped = true;

  private volatile int volume;

  private volatile boolean shuffle;
  private volatile boolean repeat;

  public MP3Player() {
    super();
    init();
  }

  public MP3Player(URL... urls) {
    super();
    for (URL mp3 : urls) {
      add(mp3);
    }
    init();
  }

  public MP3Player(File... files) {
    super();
    for (File mp3 : files) {
      add(mp3);
    }
    init();
  }

  private void init() {
    setVolume(55);
    setShuffle(false);
    setRepeat(true);
  }

  /**
   * Appends the specified MP3 to the end of the play list.
   */
  public void add(MP3 mp3) {
    this.playlist.add(mp3);
  }

  /**
   * Appends the specified URL to the end of the play list.
   */
  public void add(URL url) {
    add(new MP3(url));
  }

  /**
   * Appends the specified file (or all the files, recursively only if
   * specified, if represents a folder) to the end of the play list.
   */
  public void add(File file, boolean recursively) {

    if (file.isFile()) {
      if (file.getName().endsWith(".mp3")) {
        add(new MP3(file));
      }
    }

    else if (file.isDirectory()) {

      File[] files = file.listFiles();

      for (File file2 : files) {
        if (file2.isFile() || recursively) {
          add(file2, recursively);
        }
      }
    }

    else {
      throw new RuntimeException("WTF is this? ( " + file + " )");
    }
  }

  /**
   * Appends the specified file (or all the files, recursively, if represents a
   * folder) to the end of the play list.
   */
  public void add(File file) {
    add(file, true);
  }

  /**
   * Returns the current play list.
   * 
   * @return the current play list as {@link MP3} objects
   * 
   * @see #addToPlayList(URL)
   * @see #addToPlayList(File)
   */
  public List<MP3> getPlayList() {
    return playlist;
  }

  /**
   * Causes this player to start playing (or resume if the player is paused).
   * 
   * This is a non blocking method, with the result that two threads are running
   * concurrently: the current thread (which returns from the call to the
   * {@link #play()} method) and another thread (which executes the actual play:
   * stream read/decode, audio data write).
   */
  public void play() {

    if (isPaused) {

      isPaused = false;

      if (mp3 != null) {
        mp3.play(volume);
      }

      return;
    }

    if (mp3 != null) {
      mp3.stop();
    }

    isStopped = false;

    if (mp3 == null) {

      if (playlist.size() == 0) {
        return;
      }

      mp3 = playlist.get(0);
    }

    mp3.play(volume);

//    new Thread() {
//      public void run() {
//        mp3.joinPlayThread();
//        if (isPlaying()) {
//          skipForward();
//        }
//      }
//    }.start();
  }

  public boolean isPlaying() {
    return !isPaused && !isStopped;
  }

  /**
   * Forces the player to pause playing.
   * 
   * @see #play()
   */
  public void pause() {

    if (mp3 != null) {
      mp3.pause();
    }

    isPaused = true;
  }

  /**
   * Determines whether this player is paused.
   * 
   * @return true if the player is paused, false otherwise
   * 
   * @see #pause()
   */
  public boolean isPaused() {
    return isPaused;
  }

  /**
   * Forces the player to stop playing.
   * 
   * @see #play()
   */
  public void stop() {

    if (mp3 != null) {
      mp3.stop();
    }

    isStopped = true;
  }

  /**
   * Determines whether this player is stopped.
   * 
   * @return true if the player is stopped, false otherwise
   * 
   * @see #stop()
   */
  public boolean isStopped() {
    return isStopped;
  }

  /**
   * Forces the player to play next mp3 in the play list (or random if shuffle
   * is turned on).
   * 
   * @see #play()
   */
  public void skipForward() {
    _skip(1);
  }

  /**
   * Forces the player to play previous mp3 in the play list (or random if
   * shuffle is turned on).
   * 
   * @see #play()
   */
  public void skipBackward() {
    _skip(-1);
  }

  private void _skip(int items) {

    if (playlist.size() == 0) {
      return;
    }

    int index = playlist.indexOf(mp3);

    if (shuffle) {
      index = random.nextInt(playlist.size());
    }

    else {

      index += items;

      if (repeat) {
        if (index > playlist.size() - 1) {
          index = 0;
        } else if (index < 0) {
          index = playlist.size() - 1;
        }
      } else {
        if (index > playlist.size() - 1) {
          index = playlist.size() - 1;
        } else if (index < 0) {
          index = 0;
        }
      }
    }

    boolean isPlaying = isPlaying();

    stop();

    mp3 = playlist.get(index);

    if (isPlaying) {
      play();
    }
  }

  /**
   * Sets the volume of the player. The value is actually the percent value, so
   * the value must be in interval [0..100] or a runtime exception will be
   * throw.
   * 
   * @param volume
   * 
   * @throws IllegalArgumentException
   *           if the volume is not in interval [0..100]
   */
  public void setVolume(int volume) {

    if (volume < 0 || volume > 100) {
      throw new IllegalArgumentException("Wrong value for volume, must be in interval [0..100].");
    }

    this.volume = volume;

    if (mp3 != null) {
      mp3.setVolume(volume);
    }
  }

  /**
   * Returns the actual volume of the player.
   */
  public int getVolume() {
    return volume;
  }

  /**
   * When you turn on shuffle, the next mp3 to play will be randomly chosen from
   * the play list.
   * 
   * @param shuffle
   *          true if shuffle should be turned on, or false for turning off
   * 
   * @see #isShuffle()
   */
  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  /**
   * Returns the shuffle state of the player. True if the shuffle is on, false
   * if it's not.
   * 
   * @return true if the shuffle is on, false otherwise
   * 
   * @see #setShuffle(boolean)
   */
  public boolean isShuffle() {
    return shuffle;
  }

  /**
   * When you turn on repeat, the player will practically never stop. After the
   * last mp3 from the play list will finish, the first will be automatically
   * played, or a random one if shuffle is on.
   * 
   * @param repeat
   *          true if repeat should be turned on, or false for turning off
   * 
   * @see #isRepeat()
   */
  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

  /**
   * Returns the repeat state of the player. True if the repeat is on, false if
   * it's not.
   * 
   * @return true if the repeat is on, false otherwise
   * 
   * @see #setRepeat(boolean)
   */
  public boolean isRepeat() {
    return repeat;
  }

}

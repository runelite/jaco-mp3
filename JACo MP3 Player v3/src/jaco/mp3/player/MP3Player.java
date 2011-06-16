/*
 * Copyright (C) Cristian Sulea ( http://cristiansulea.entrust.ro )
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

import jaco.mp3.resources.Decoder;
import jaco.mp3.resources.Frame;
import jaco.mp3.resources.SampleBuffer;
import jaco.mp3.resources.SoundStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Java MP3 Player
 * 
 * @version 0.10.1, June 14, 2011
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
public class MP3Player extends JPanel {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = Logger.getLogger(MP3Player.class.getName());

  private final Random random = new Random();

  private List<Object> playlist = new ArrayList<Object>();

  private boolean isPaused = false;
  private boolean isStopped = true;

  private volatile int volume = 50;

  private volatile boolean shuffle = false;
  private volatile boolean repeat = true;

  private volatile Thread playingThread;
  private volatile int playingIndex = 0;
  private volatile SourceDataLine playingSource;
  private volatile int playingSourceVolume = 0;

  public MP3Player() {
    init();
  }

  public MP3Player(File... files) {

    for (File file : files) {
      add(file);
    }

    init();
  }

  public MP3Player(URL... urls) {

    for (URL url : urls) {
      add(url);
    }

    init();
  }

  private void init() {
    new MP3PlayerTheme() {
      public void apply(final MP3Player player) {

        final JButton playButton = new JButton();
        playButton.setText(">");
        playButton.setToolTipText("Play");
        playButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.play();
          }
        });

        final JButton pauseButton = new JButton();
        pauseButton.setText("||");
        pauseButton.setToolTipText("Pause");
        pauseButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.pause();
          }
        });

        final JButton stopButton = new JButton();
        stopButton.setText("#");
        stopButton.setToolTipText("Stop");
        stopButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.stop();
          }
        });

        final JButton skipBackwardButton = new JButton();
        skipBackwardButton.setText("|<");
        skipBackwardButton.setToolTipText("Skip Backward");
        skipBackwardButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.skipBackward();
          }
        });

        final JButton skipForwardButton = new JButton();
        skipForwardButton.setText(">|");
        skipForwardButton.setToolTipText("Skip Forward");
        skipForwardButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.skipForward();
          }
        });

        final JSlider volumeSlider = new JSlider();
        volumeSlider.setToolTipText("Volume");
        volumeSlider.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            player.setVolume(volumeSlider.getValue());
          }
        });
        volumeSlider.setMinimum(0);
        volumeSlider.setMaximum(100);
        volumeSlider.setMajorTickSpacing(50);
        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintTrack(true);

        final JCheckBox repeatCheckBox = new JCheckBox();
        repeatCheckBox.setText("Repeat");
        repeatCheckBox.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.setRepeat(repeatCheckBox.isSelected());
          }
        });

        final JCheckBox shuffleCheckBox = new JCheckBox();
        shuffleCheckBox.setText("Shuffle");
        shuffleCheckBox.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            player.setShuffle(shuffleCheckBox.isSelected());
          }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(player);
        player.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(playButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addComponent(pauseButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(stopButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(skipBackwardButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(skipForwardButton)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(shuffleCheckBox).addComponent(repeatCheckBox)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(volumeSlider, 0, 0, Short.MAX_VALUE))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(playButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(pauseButton).addComponent(stopButton).addComponent(skipBackwardButton).addComponent(skipForwardButton)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addComponent(shuffleCheckBox).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(repeatCheckBox)).addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

      }
    }.apply(this);
  }

  /**
   * Appends the specified file (or all the files, recursively only if
   * specified, if represents a folder) to the end of the play list.
   */
  public MP3Player add(File file, boolean recursively) {

    if (file.isFile()) {
      if (file.getName().endsWith(".mp3")) {
        synchronized (MP3Player.this) {
          playlist.add(file);
          System.out.println(file);
        }
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

    return this;
  }

  /**
   * Appends the specified file (or all the files, recursively, if represents a
   * folder) to the end of the play list.
   */
  public MP3Player add(File file) {
    add(file, true);
    return this;
  }

  /**
   * Appends the specified URL to the end of the play list.
   */
  public MP3Player add(URL url) {
    synchronized (MP3Player.this) {
      playlist.add(url);
    }
    return this;
  }

  public void setTheme(MP3PlayerTheme theme) {
    removeAll();
    theme.apply(this);
    revalidate();
    repaint();
  }

  /**
   * Starts the play (or resume if is paused).
   */
  public void play() {

    synchronized (MP3Player.this) {
      if (isPaused) {
        isPaused = false;
        MP3Player.this.notifyAll();
        return;
      }
    }

    stop();

    if (playingThread == null) {

      playingThread = new Thread() {
        public void run() {

          synchronized (MP3Player.this) {
            isStopped = false;
          }

          SoundStream stream;

          if (playlist.size() == 0) {
            LOGGER.log(Level.INFO, "play list is empty");
            stream = null;
          }

          else {

            try {

              Object playlistObject;

              synchronized (MP3Player.this) {
                playlistObject = playlist.get(playingIndex);
              }

              if (playlistObject instanceof File) {
                stream = new SoundStream(new FileInputStream((File) playlistObject));
              } else if (playlistObject instanceof URL) {
                stream = new SoundStream(((URL) playlistObject).openStream());
              } else {
                LOGGER.log(Level.WARNING, "this is impossible; how come the play list contains this kind of object? :: " + playlistObject.getClass());
                stream = null;
              }
            }

            catch (Exception e) {
              LOGGER.log(Level.SEVERE, "unable to open the sound stream", e);
              stream = null;
            }
          }

          if (stream != null) {

            Decoder decoder = new Decoder();

            while (true) {

              synchronized (MP3Player.this) {

                if (isStopped) {
                  break;
                }

                if (isPaused) {

                  if (playingSource != null) {
                    playingSource.flush();
                  }

                  playingSourceVolume = volume;

                  try {
                    MP3Player.this.wait();
                  } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "wait() failed", e);
                  }

                  continue;
                }
              }

              try {

                Frame frame = stream.readFrame();

                if (frame == null) {
                  break;
                }

                if (playingSource == null) {

                  int frequency = frame.frequency();
                  int channels = (frame.mode() == Frame.SINGLE_CHANNEL) ? 1 : 2;

                  AudioFormat format = new AudioFormat(frequency, 16, channels, true, false);
                  Line line = AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));

                  playingSource = (SourceDataLine) line;
                  playingSource.open(format);
                  playingSource.start();

                  setVolume(playingSource, playingSourceVolume = 0);
                }

                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, stream);

                short[] buffer = output.getBuffer();
                int offs = 0;
                int len = output.getBufferLength();

                if (playingSourceVolume != volume) {

                  if (playingSourceVolume > volume) {
                    playingSourceVolume -= 10;
                    if (playingSourceVolume < volume) {
                      playingSourceVolume = volume;
                    }
                  } else {
                    playingSourceVolume += 10;
                    if (playingSourceVolume > volume) {
                      playingSourceVolume = volume;
                    }
                  }

                  setVolume(playingSource, playingSourceVolume);
                }

                playingSource.write(toByteArray(buffer, offs, len), 0, len * 2);

                stream.closeFrame();
              }

              catch (Exception e) {
                LOGGER.log(Level.WARNING, "unexpected problems while playing " + toString(), e);
                break;
              }
            }

            //
            // source is null at this point only if first frame is null
            // this means that probably the file is not a mp3

            if (playingSource == null) {
              LOGGER.log(Level.INFO, "source is null because first frame is null, so probably the file is not a mp3");
            }

            else {

              synchronized (MP3Player.this) {
                if (!isStopped) {
                  playingSource.drain();
                } else {
                  playingSource.flush();
                }
              }

              playingSource.stop();
              playingSource.close();

              playingSource = null;
            }

            try {
              stream.close();
            } catch (Exception e) {
              LOGGER.log(Level.WARNING, "error closing the stream", e);
            }
          }

          boolean skipForwardAllowed;

          synchronized (MP3Player.this) {

            //
            // take the value before reset

            skipForwardAllowed = !isStopped;

            //
            // reset values

            isPaused = false;
            isStopped = true;
          }

          playingThread = null;

          if (skipForwardAllowed) {
            skipForward();
          }
        }
      };

      playingThread.start();
    }
  }

  /**
   * Starts the play (or resume if is paused) at the specified volume.
   * 
   * @see #setVolume(int)
   * @see #play()
   */
  public void play(int volume) {
    setVolume(volume);
    play();
  }

  public boolean isPlaying() {
    synchronized (MP3Player.this) {
      return !isPaused && !isStopped;
    }
  }

  public void pause() {
    synchronized (MP3Player.this) {
      isPaused = true;
      MP3Player.this.notifyAll();
    }
  }

  public boolean isPaused() {
    synchronized (MP3Player.this) {
      return isPaused;
    }
  }

  public void stop() {

    synchronized (MP3Player.this) {
      isStopped = true;
      MP3Player.this.notifyAll();
    }

    if (playingThread != null) {
      try {
        playingThread.join();
      } catch (InterruptedException e) {
        LOGGER.log(Level.SEVERE, "join() failed", e);
      }
    }
  }

  public boolean isStopped() {
    synchronized (MP3Player.this) {
      return isStopped;
    }
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

    boolean playAllowed = true;

    if (shuffle) {
      playingIndex = random.nextInt(playlist.size());
    }

    else {

      playingIndex += items;

      if (playingIndex > playlist.size() - 1) {
        if (repeat) {
          playingIndex = 0;
        } else {
          playingIndex = playlist.size() - 1;
          playAllowed = false;
        }
      } else if (playingIndex < 0) {
        if (repeat) {
          playingIndex = playlist.size() - 1;
        } else {
          playingIndex = 0;
          playAllowed = false;
        }
      }
    }

    if (playAllowed) {
      play();
    }
  }

  /**
   * Sets a new volume for this player. The value is actually the percent value,
   * so the value must be in interval [0..100].
   * 
   * @param volume
   *          the new volume
   * 
   * @throws IllegalArgumentException
   *           if the volume is not in interval [0..100]
   */
  public void setVolume(int volume) {

    if (volume < 0 || volume > 100) {
      throw new IllegalArgumentException("Wrong value for volume, must be in interval [0..100].");
    }

    this.volume = volume;
  }

  /**
   * Returns the actual volume.
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
   */
  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  /**
   * Returns the shuffle state of the player. True if the shuffle is on, false
   * if it's not.
   * 
   * @return true if the shuffle is on, false otherwise
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
   */
  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

  /**
   * Returns the repeat state of the player. True if the repeat is on, false if
   * it's not.
   * 
   * @return true if the repeat is on, false otherwise
   */
  public boolean isRepeat() {
    return repeat;
  }

  private void setVolume(SourceDataLine source, int volume) {

    try {

      FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
      BooleanControl muteControl = (BooleanControl) source.getControl(BooleanControl.Type.MUTE);

      if (volume == 0) {
        muteControl.setValue(true);
      } else {
        muteControl.setValue(false);
        gainControl.setValue((float) (Math.log(volume / 100d) / Math.log(10.0) * 20.0));
      }
    }

    catch (Exception e) {
      LOGGER.log(Level.WARNING, "unable to set the volume to the provided source", e);
    }
  }

  /**
   * Retrieves the position in milliseconds of the current audio sample being
   * played. This method delegates to the <code>
   * AudioDevice</code> that is used by this player to sound the decoded audio
   * samples.
   */
  public int getPosition() {
    int pos = 0;
    if (playingSource != null) {
      pos = (int) (playingSource.getMicrosecondPosition() / 1000);
    }
    return pos;
  }

  private byte[] toByteArray(short[] ss, int offs, int len) {
    byte[] bb = new byte[len * 2];
    int idx = 0;
    short s;
    while (len-- > 0) {
      s = ss[offs++];
      bb[idx++] = (byte) s;
      bb[idx++] = (byte) (s >>> 8);
    }
    return bb;
  }

}

package jaco.mp3.player;

import jaco.mp3.decoder.decoder.Bitstream;
import jaco.mp3.decoder.decoder.Decoder;
import jaco.mp3.decoder.decoder.Header;
import jaco.mp3.decoder.decoder.SampleBuffer;
import jaco.mp3.decoder.player.JavaSoundAudioDevice;
import jaco.mp3.player.plaf.MP3PlayerUI;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MP3Player extends JPanel {

  public static final String UI_CLASS_ID = MP3Player.class.getName() + "UI";

  public String getUIClassID() {
    return UI_CLASS_ID;
  }

  //

  public static void setDefaultUI(Class<? extends MP3PlayerUI> uiClass) {
    UIManager.getDefaults().put(MP3Player.UI_CLASS_ID, uiClass.getName());
  }

  static {
    if (UIManager.getDefaults().get(MP3Player.UI_CLASS_ID) == null) {
      setDefaultUI(MP3PlayerUI.class);
    }
  }

  //

  private final Random random = new Random();

  private List<MP3PlayerListener> listeners;

  private List<URL> playList = new Vector<URL>();
  private int playIndex;

  private boolean shuffle = false;
  private boolean repeat = false;

  private volatile boolean isPaused = false;
  private volatile boolean isStopped = true;
  private volatile boolean isStopping = false;

  public MP3Player() {}

  public MP3Player(URL... mp3s) {
    for (URL mp3 : mp3s) {
      addToPlayList(mp3);
    }
  }

  public MP3Player(File... mp3s) {
    for (File mp3 : mp3s) {
      addToPlayList(mp3);
    }
  }

  public synchronized void play() {

    if (listeners != null) {
      for (MP3PlayerListener listener : listeners) {
        listener.onPlay(MP3Player.this);
      }
    }

    if (isPaused) {
      isPaused = false;
      return;
    }

    _stop();

    isStopped = false;

    Thread thread = new Thread() {
      @Override
      public void run() {

        Decoder decoder = new Decoder();

        Bitstream stream;
        JavaSoundAudioDevice device;

        try {
          stream = new Bitstream(playList.get(playIndex).openStream());
        } catch (Exception e) {
          stream = null;
          // log.error("error opening the stream", e);
          e.printStackTrace();
        }

        if (stream != null) {

          try {
            device = new JavaSoundAudioDevice();
            device.open(decoder);
          } catch (Exception e) {
            device = null;
            try {
              stream.close();
            } catch (Exception e2) {}
            // log.error("error opening the audio device", e);
            e.printStackTrace();
          }

          if (device != null) {

            try {

              while (true) {

                if (isStopping) {
                  break;
                }

                if (isPaused) {
                  Thread.sleep(100);
                  continue;
                }

                Header h = stream.readFrame();

                if (h == null) {
                  break;
                }

                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, stream);

                device.write(output.getBuffer(), 0, output.getBufferLength());

                stream.closeFrame();
              }
            }

            catch (Exception e) {
              // log.error("error on play command", e);
              e.printStackTrace();
            }

            if (!isStopping) {
              device.flush();
            }

            device.close();
          }

          try {
            stream.close();
          } catch (Exception e) {
            // log.error("error closing the stream", e);
            e.printStackTrace();
          }
        }

        if (!isStopping) {
          new Thread() {
            public void run() {
              skipForward();
            }
          }.start();
        }

        isStopping = false;
        isStopped = true;
      }
    };
    thread.setDaemon(true);
    thread.start();
  }

  public synchronized void pause() {

    if (listeners != null) {
      for (MP3PlayerListener listener : listeners) {
        listener.onPause(MP3Player.this);
      }
    }

    isPaused = true;
  }

  public synchronized void stop() {

    if (listeners != null) {
      for (MP3PlayerListener listener : listeners) {
        listener.onStop(MP3Player.this);
      }
    }

    _stop();
  }

  private void _stop() {

    // if (isStopping) {
    // return;
    // }

    isPaused = false;

    isStopping = true;

    while (isStopping && !isStopped) {
      try {
        Thread.sleep(10);
      } catch (Exception e) {}
    }

    isStopping = false;
    isStopped = true;
  }

  public synchronized void skipForward() {

    if (shuffle) {
      playIndex = random.nextInt(playList.size());
      play();
    }

    else {
      if (playIndex >= playList.size() - 1) {
        if (repeat) {
          playIndex = 0;
          play();
        }
      } else {
        playIndex++;
        play();
      }
    }
  }

  public synchronized void skipBackward() {

    if (shuffle) {
      playIndex = random.nextInt(playList.size());
      play();
    }

    else {
      if (playIndex <= 0) {
        if (repeat) {
          playIndex = playList.size() - 1;
          play();
        }
      } else {
        playIndex--;
        play();
      }
    }
  }

  public boolean isPaused() {
    return isPaused;
  }

  public boolean isStopped() {
    return isStopped;
  }

  //

  public synchronized void addMP3PlayerListener(MP3PlayerListener listener) {
    if (listeners == null) {
      listeners = new ArrayList<MP3PlayerListener>();
    }
    listeners.add(listener);
  }

  public synchronized void removeMP3PlayerListener(MP3PlayerListener listener) {
    listeners.remove(listener);
  }

  public synchronized void removeAllMP3PlayerListeners() {
    listeners.clear();
  }

  //

  public void addToPlayList(URL mp3) {
    this.playList.add(mp3);
  }

  public void addToPlayList(File mp3) {
    try {
      this.playList.add(mp3.toURI().toURL());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<URL> getPlayList() {
    return playList;
  }

  //

  public boolean isShuffle() {
    return shuffle;
  }

  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  public boolean isRepeat() {
    return repeat;
  }

  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

}

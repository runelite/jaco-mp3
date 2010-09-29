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

import jaco.mp3.player.plaf.MP3PlayerUI;
import jaco.mp3.player.resources.Decoder;
import jaco.mp3.player.resources.Frame;
import jaco.mp3.player.resources.SampleBuffer;
import jaco.mp3.player.resources.SoundDevice;
import jaco.mp3.player.resources.SoundStream;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
 * @version 1.40, September 29, 2010
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
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

	private volatile List<MP3PlayerListener> listeners;

	private volatile List<URL> playList = new Vector<URL>();
	private volatile int playIndex;

	private volatile boolean isPaused = false;
	private volatile boolean isStopped = true;
	private volatile boolean isStopping = false;

	private volatile int volume;
	private volatile boolean shuffle;
	private volatile boolean repeat;

	public MP3Player() {
		super();
		init();
	}

	public MP3Player(URL... mp3s) {
		super();
		for (URL mp3 : mp3s) {
			addToPlayList(mp3);
		}
		init();
	}

	public MP3Player(File... mp3s) {
		super();
		for (File mp3 : mp3s) {
			addToPlayList(mp3);
		}
		init();
	}

	public MP3Player(String... mp3s) {
		super();
		for (String mp3 : mp3s) {
			addToPlayList(new File(mp3));
		}
		init();
	}

	protected void init() {
		setVolume(25);
		setShuffle(false);
		setRepeat(false);
	}

	/**
	 * Causes this player to start playing (or resume if the player is paused).
	 * 
	 * This is a non blocking method, with the result that two threads are running
	 * concurrently: the current thread (which returns from the call to the
	 * {@link #play()} method) and another thread (which executes the actual play:
	 * stream read/decode, audio data write).
	 * 
	 * @see #pause()
	 * @see #stop()
	 */
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

				SoundStream stream;
				SoundDevice device;

				try {
					stream = new SoundStream(playList.get(playIndex).openStream());
				} catch (Exception e) {
					stream = null;
					// log.error("error opening the stream", e);
					e.printStackTrace();
				}

				if (stream != null) {

					try {
						device = new SoundDevice();
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

								Frame frame = stream.readFrame();

								if (frame == null) {
									break;
								}

								SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, stream);

								device.setVolume(volume);
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

	/**
	 * Forces the player to pause playing.
	 * 
	 * @see #play()
	 */
	public synchronized void pause() {

		if (listeners != null) {
			for (MP3PlayerListener listener : listeners) {
				listener.onPause(MP3Player.this);
			}
		}

		isPaused = true;
	}

	/**
	 * Forces the player to stop playing.
	 * 
	 * @see #play()
	 */
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

	/**
	 * Forces the player to play next mp3 in the play list (or random if shuffle
	 * is turned on).
	 * 
	 * @see #play()
	 */
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

	/**
	 * Forces the player to play previous mp3 in the play list (or random if
	 * shuffle is turned on).
	 * 
	 * @see #play()
	 */
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
	 * Adds a {@link MP3PlayerListener} to the player.
	 * 
	 * @param listener
	 *          the listener to be added
	 */
	public synchronized void addMP3PlayerListener(MP3PlayerListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<MP3PlayerListener>();
		}
		listeners.add(listener);
	}

	/**
	 * Removes a {@link MP3PlayerListener} from the player.
	 * 
	 * @param listener
	 *          the listener to be removed
	 */
	public synchronized void removeMP3PlayerListener(MP3PlayerListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Removes all of the {@link MP3PlayerListener} listeners from this player.
	 */
	public synchronized void removeAllMP3PlayerListeners() {
		if (listeners != null) {
			listeners.clear();
			listeners = null;
		}
	}

	/**
	 * Appends the specified mp3 (as {@link URL} object) to the end of the play
	 * list.
	 * 
	 * @param mp3
	 *          the mp3 to be added
	 * 
	 * @see #addToPlayList(File)
	 * @see #getPlayList()
	 */
	public void addToPlayList(URL mp3) {
		this.playList.add(mp3);
	}

	/**
	 * Appends the specified mp3 (as {@link File} object) to the end of the play
	 * list.
	 * 
	 * @param mp3
	 *          the mp3 to be added
	 * 
	 * @see #addToPlayList(URL)
	 * @see #getPlayList()
	 */
	public void addToPlayList(File mp3) {
		try {
			this.playList.add(mp3.toURI().toURL());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the current play list.
	 * 
	 * @return the current play list as {@link URL} objects
	 * 
	 * @see #addToPlayList(URL)
	 * @see #addToPlayList(File)
	 */
	public List<URL> getPlayList() {
		return playList;
	}

	/**
	 * Returns the actual volume of the player.
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * Sets the volume of the player. The value is actually the percent value, so
	 * the value must be in interval [0..100].
	 */
	public void setVolume(int volume) {

		if (volume < 0 || volume > 100) {
			throw new RuntimeException("Wrong value for volume, must be in interval [0..100].");
		}

		this.volume = volume;
		getUI().onSetVolume(volume);
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
		getUI().onSetShuffle(shuffle);
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
		getUI().onSetRepeat(shuffle);
	}

	@Override
	public MP3PlayerUI getUI() {
		return (MP3PlayerUI) super.getUI();
	}

}

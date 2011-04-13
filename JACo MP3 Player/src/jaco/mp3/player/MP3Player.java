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

import jaco.mp3.player.resources.Decoder;
import jaco.mp3.player.resources.Frame;
import jaco.mp3.player.resources.SampleBuffer;
import jaco.mp3.player.resources.SoundDevice;
import jaco.mp3.player.resources.SoundStream;

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
 * @version 1.43, April 13, 2011
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
	 * @deprecated This method was replaced by a new one, with a better name:
	 *             {@link #setUIClass(Class)}.
	 */
	public static void setDefaultUI(Class<? extends MP3PlayerUI> uiClass) {
		UIManager.getDefaults().put(MP3Player.UI_CLASS_ID, uiClass.getName());
	}

	/**
	 * Sets the look and feel (L&F) class that renders the {@link MP3Player}
	 * component.
	 * 
	 * @param uiClass
	 *          the {@link MP3PlayerUI} L&F class
	 */
	public static void setUIClass(Class<? extends MP3PlayerUI> uiClass) {
		setDefaultUI(uiClass);
	}

	static {
		if (UIManager.getDefaults().get(MP3Player.UI_CLASS_ID) == null) {
			setUIClass(MP3PlayerUI.class);
		}
	}

	//

	private final Random random = new Random();

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

	public MP3Player(File... files) {
		super();
		for (File mp3 : files) {
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
	 */
	public void play() {
		play(false);
	}

	/**
	 * Causes this player to start playing (or resume if the player is paused).
	 * 
	 * This is a non blocking method, with the result that two threads are running
	 * concurrently: the current thread (which returns from the call to the
	 * {@link #play()} method) and another thread (which executes the actual play:
	 * stream read/decode, audio data write).
	 * 
	 * @param daemon
	 *          if true, marks the play thread as a daemon thread
	 */
	public void play(boolean daemon) {

		for (MP3PlayerListener listener : getMP3PlayerListeners()) {
			listener.onPlay(MP3Player.this);
		}

		if (isPaused) {
			isPaused = false;
			return;
		}

		_stop();

		isStopped = false;

		Thread thread = new Thread() {
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
		
		thread.setDaemon(daemon);
		thread.start();
	}

	/**
	 * Forces the player to pause playing.
	 * 
	 * @see #play()
	 */
	public void pause() {

		for (MP3PlayerListener listener : getMP3PlayerListeners()) {
			listener.onPause(MP3Player.this);
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

		for (MP3PlayerListener listener : getMP3PlayerListeners()) {
			listener.onStop(MP3Player.this);
		}

		_stop();
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
	public void skipForward() {

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
	public void skipBackward() {

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
	 * Adds a {@link MP3PlayerListener} to the player.
	 * 
	 * @param listener
	 *          the listener to be added
	 */
	public void addMP3PlayerListener(MP3PlayerListener listener) {
		listenerList.add(MP3PlayerListener.class, listener);
	}

	/**
	 * Removes a {@link MP3PlayerListener} from the player.
	 * 
	 * @param listener
	 *          the listener to be removed
	 */
	public void removeMP3PlayerListener(MP3PlayerListener listener) {
		listenerList.remove(MP3PlayerListener.class, listener);
	}

	/**
	 * Returns an array of all the {@link MP3PlayerListener}s added to the player
	 * with {@link #addMP3PlayerListener(MP3PlayerListener)}.
	 * 
	 * @return all of the {@link MP3PlayerListener}s added or an empty array if no
	 *         listeners have been added
	 */
	public MP3PlayerListener[] getMP3PlayerListeners() {
		return (MP3PlayerListener[]) listenerList.getListeners(MP3PlayerListener.class);
	}

	/**
	 * Appends the specified url to the end of the play list.
	 */
	public void addToPlayList(URL url) {
		try {
			this.playList.add(url.toURI().toURL());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Appends the specified file (or all the files, recursively, if represents a
	 * folder) to the end of the play list.
	 */
	public void addToPlayList(File file) {

		if (file.isFile()) {

			try {
				this.playList.add(file.toURI().toURL());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		else if (file.isDirectory()) {

			File[] files = file.listFiles();

			for (File file2 : files) {
				addToPlayList(file2);
			}
		}

		else {
			throw new RuntimeException("WTF is this? ( " + file + " )");
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
	 * Sets the volume of the player. The value is actually the percent value, so
	 * the value must be in interval [0..100] or a runtime exception will be
	 * throw.
	 * 
	 * @param volume
	 * 
	 * @throws RuntimeException
	 *           if the volume is not in interval [0..100]
	 */
	public MP3Player setVolume(int volume) {

		if (volume < 0 || volume > 100) {
			throw new RuntimeException("Wrong value for volume, must be in interval [0..100].");
		}

		for (MP3PlayerListener listener : getMP3PlayerListeners()) {
			listener.onSetVolume(MP3Player.this, volume);
		}

		this.volume = volume;

		return this;
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
	public MP3Player setShuffle(boolean shuffle) {

		for (MP3PlayerListener listener : getMP3PlayerListeners()) {
			listener.onSetShuffle(MP3Player.this, shuffle);
		}

		this.shuffle = shuffle;

		return this;
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
	public MP3Player setRepeat(boolean repeat) {

		for (MP3PlayerListener listener : getMP3PlayerListeners()) {
			listener.onSetRepeat(MP3Player.this, repeat);
		}

		this.repeat = repeat;

		return this;
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

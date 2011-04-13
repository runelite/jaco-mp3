package jaco.mp3.player;

import jaco.mp3.player.resources.Decoder;
import jaco.mp3.player.resources.Frame;
import jaco.mp3.player.resources.SampleBuffer;
import jaco.mp3.player.resources.SoundDevice;
import jaco.mp3.player.resources.SoundStream;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MP3 {

	private static final Logger LOGGER = Logger.getLogger(MP3.class.getName());

	private File file;
	private URL url;

	private boolean isPaused = false;
	private boolean isStopped = true;

	public MP3(File file) {
		this.file = file;
	}

	public MP3(URL url) {
		this.url = url;
	}

	public void play() {

		isStopped = false;

		Decoder decoder = new Decoder();

		SoundStream stream;
		SoundDevice device;

		try {
			stream = new SoundStream(new FileInputStream(file));
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
				LOGGER.log(Level.SEVERE, "error opening the audio device", e);
			}

			if (device != null) {

				try {

					while (true) {

						if (isStopped) {
							break;
						}

						if (isPaused) {
							Thread.sleep(10);
							continue;
						}

						Frame frame = stream.readFrame();

						if (frame == null) {
							break;
						}

						SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, stream);

						// device.setVolume(volume);
						device.write(output.getBuffer(), 0, output.getBufferLength());

						stream.closeFrame();
					}
				}

				catch (Exception e) {
					// log.error("error on play command", e);
					e.printStackTrace();
				}

				if (!isStopped) {
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

		isStopped = true;
	}

}

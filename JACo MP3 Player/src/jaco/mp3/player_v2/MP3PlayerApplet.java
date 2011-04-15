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

package jaco.mp3.player_v2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.JApplet;

/**
 * Java MP3 Player Applet
 * 
 * <pre>
 * &lt;script type=&quot;text/javascript&quot;
 *         src=&quot;http://www.java.com/js/deployJava.js&quot;&gt;
 * &lt;/script&gt;
 * 
 * &lt;script type=&quot;text/javascript&quot;&gt;
 * 
 *     var attributes = {
 * 
 *             code            : &quot;jaco.mp3.player.MP3PlayerApplet.class&quot;,
 *             archive         : &quot;jars/jaco-mp3-player.jar&quot;,
 *             width           : &quot;86&quot;,
 *             height          : &quot;16&quot;
 *         };
 * 
 *     var parameters = {
 * 
 *             separate_jvm    : &quot;true&quot;,
 *             codebase_lookup : &quot;true&quot;,
 * 
 *             background      : &quot;0xFFFFFF&quot;,
 *             compact         : &quot;false&quot;,
 *             playlist        : &quot;mp3s/01.mp3, mp3s/02.mp3, mp3s/03.mp3&quot;
 *         };
 * 
 *     var version = "1.6";
 * 
 *     deployJava.runApplet(attributes, parameters, version);
 * 
 * &lt;/script&gt;
 * </pre>
 * 
 * @version 1.15, May 27, 2010
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
public class MP3PlayerApplet extends JApplet {

	/** serialVersionUID */
	private static final long serialVersionUID = 46146126474303823L;

	@Override
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createGUI() {

		try {

			try {
				getContentPane().setBackground(Color.decode(getParameter("background")));
			} catch (Exception e) {}

			if ("true".equals(getParameter("compact"))) {
				//MP3Player.setDefaultUI(MP3PlayerUICompact.class);
			}

			MP3Player player = new MP3Player();
			player.setRepeat(true);

			String[] playlist = getParameter("playlist").split(",");
			for (String mp3 : playlist) {
				player.add(new URL(getCodeBase() + mp3.trim()));
			}

			getContentPane().add(player, BorderLayout.CENTER);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package jaco.mp3.player.tests;

import jaco.mp3.player.MP3Player;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Test1 {

	public static void main(String[] args) throws Exception {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// MP3Player.setDefaultUI(MP3PlayerUICompact.class);

		//

		final MP3Player player = new MP3Player();

		player.setRepeat(true);

		player.addToPlayList(new File("../JACo MP3 Player/web/demo/01.mp3"));
		player.addToPlayList(new File("../JACo MP3 Player/web/demo/02.mp3"));
		player.addToPlayList(new File("../JACo MP3 Player/web/demo/03.mp3"));

		//

		JFrame frame = new JFrame("MP3 Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(player);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}

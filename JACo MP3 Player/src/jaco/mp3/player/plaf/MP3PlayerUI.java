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

package jaco.mp3.player.plaf;

import jaco.mp3.player.MP3Player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * Java MP3 Player default UI
 * 
 * @version 2.00, September 29, 2010
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
public class MP3PlayerUI extends BasicPanelUI {

	public static ComponentUI createUI(JComponent c) {
		return new MP3PlayerUI();
	}

	//

	@Override
	public final void installUI(JComponent c) {
		super.installUI(c);
		installUI((MP3Player) c);
	}

	@Override
	public final void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		uninstallUI((MP3Player) c);
	}

	//

	private JButton playButton;
	private JButton pauseButton;
	private JButton stopButton;
	private JButton skipBackwardButton;
	private JButton skipForwardButton;
	private JSlider volumeSlider;
	private JCheckBox shuffleButton;
	private JCheckBox repeatButton;

	protected void installUI(final MP3Player player) {

		player.setOpaque(false);

		//

		pauseButton = new javax.swing.JButton();
		stopButton = new javax.swing.JButton();
		skipBackwardButton = new javax.swing.JButton();
		skipForwardButton = new javax.swing.JButton();
		volumeSlider = new javax.swing.JSlider();
		playButton = new javax.swing.JButton();
		repeatButton = new javax.swing.JCheckBox();
		shuffleButton = new javax.swing.JCheckBox();

		pauseButton.setText("||");
		stopButton.setText("#");
		skipBackwardButton.setText("|<");
		skipForwardButton.setText(">|");
		playButton.setText(">");
		repeatButton.setText("Repeat");
		shuffleButton.setText("Shuffle");

		volumeSlider.setMinimum(0);
		volumeSlider.setMaximum(100);
		volumeSlider.setMajorTickSpacing(50);
		volumeSlider.setMinorTickSpacing(10);
		// volumeSlider.setSnapToTicks(true);
		volumeSlider.setPaintTicks(true);
		volumeSlider.setPaintTrack(true);
		// volumeSlider.setPaintLabels(true);
		volumeSlider.setValue(player.getVolume());

		repeatButton.setSelected(player.isRepeat());
		shuffleButton.setSelected(player.isShuffle());

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(player);
		player.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(playButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(volumeSlider, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(pauseButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(stopButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(skipBackwardButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(skipForwardButton))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(repeatButton).addComponent(shuffleButton)).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(playButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(pauseButton).addComponent(stopButton).addComponent(skipBackwardButton).addComponent(skipForwardButton)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(layout.createSequentialGroup().addComponent(shuffleButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(repeatButton))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		//

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Object source = e.getSource();

				if (source == playButton) {
					player.play();
				} else if (source == pauseButton) {
					player.pause();
				} else if (source == stopButton) {
					player.stop();
				} else if (source == skipBackwardButton) {
					player.skipBackward();
				} else if (source == skipForwardButton) {
					player.skipForward();
				} else if (source == shuffleButton) {
					player.setShuffle(shuffleButton.isSelected());
				} else if (source == repeatButton) {
					player.setRepeat(repeatButton.isSelected());
				}
			}
		};

		playButton.addActionListener(actionListener);
		pauseButton.addActionListener(actionListener);
		stopButton.addActionListener(actionListener);
		skipBackwardButton.addActionListener(actionListener);
		skipForwardButton.addActionListener(actionListener);
		shuffleButton.addActionListener(actionListener);
		repeatButton.addActionListener(actionListener);

		//

		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				player.setVolume(volumeSlider.getValue());
			}
		});
	}

	protected void uninstallUI(final MP3Player player) {
		player.removeAll();
		player.removeAllMP3PlayerListeners();
	}

}

/*
 * Copyright (C) <2010> Cristian Sulea ( http://cristiansulea.entrust.ro )
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jaco.mp3.player;

/**
 * The listener interface for handling player events.
 * 
 * @version 1.03, May 27, 2010
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
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

package jaco.mp3.player.plaf;

import jaco.image.IconUtils;
import jaco.image.ImageUtils;
import jaco.mp3.player.MP3Player;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

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

  // private JButton shuffleButton;
  // private JButton repeatButton;

  protected void installUI(final MP3Player player) {

    player.setOpaque(false);

//    playButton = new PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerPlay.png")));
//    pauseButton = new PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerPause.png")));
//    stopButton = new PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerStop.png")));
//    skipBackwardButton = new PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerSkipBackward.png")));
//    skipForwardButton = new PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerSkipForward.png")));
    // shuffleButton = new
    // PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerShuffle.png")));
    // repeatButton = new
    // PlayerButton(ImageUtils.create(getClass().getResource("resources/mp3PlayerRepeat.png")));

    playButton = new PlayerButton("mp3PlayerPlay");
    pauseButton = new PlayerButton("mp3PlayerPause");
    stopButton = new PlayerButton("mp3PlayerStop");
    skipBackwardButton = new PlayerButton("mp3PlayerSkipBackward");
    skipForwardButton = new PlayerButton("mp3PlayerSkipForward");
    
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
        }
      }
    };

    playButton.addActionListener(actionListener);
    pauseButton.addActionListener(actionListener);
    stopButton.addActionListener(actionListener);
    skipBackwardButton.addActionListener(actionListener);
    skipForwardButton.addActionListener(actionListener);
    // shuffleButton.addActionListener(actionListener);
    // repeatButton.addActionListener(actionListener);

    //

    player.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
    player.add(playButton);
    player.add(pauseButton);
    player.add(stopButton);
    player.add(skipBackwardButton);
    player.add(skipForwardButton);
    // player.add(shuffleButton);
    // player.add(repeatButton);
  }

  @SuppressWarnings("serial")
  private class PlayerButton extends JButton {

    private PlayerButton(BufferedImage image) {

      BufferedImage image1 = ImageUtils.addBrightness(image, 0.05f);
      BufferedImage image2 = ImageUtils.addDarkness(image, 0.05f);

      setIcon(IconUtils.create(image));
      setRolloverIcon(IconUtils.create(image1));
      setPressedIcon(IconUtils.create(image2));

      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setBorder(BorderFactory.createEmptyBorder());
      setMargin(new Insets(0, 0, 0, 0));
      setContentAreaFilled(false);
      setFocusable(false);
      setFocusPainted(false);
    }
    
    private PlayerButton(String imageName) {

      BufferedImage image = ImageUtils.create(getClass().getResource("resources/" + imageName + ".png"));
      BufferedImage image1 = ImageUtils.addBrightness(image, 0.05f);
      BufferedImage image2 = ImageUtils.addDarkness(image, 0.05f);

      try {
        ImageIO.write(image, "png", new File(imageName + ".png"));
        ImageIO.write(image1, "png", new File(imageName + "Rollover.png"));
        ImageIO.write(image2, "png", new File(imageName + "Pressed.png"));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      setIcon(IconUtils.create(image));
      setRolloverIcon(IconUtils.create(image1));
      setPressedIcon(IconUtils.create(image2));

      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setBorder(BorderFactory.createEmptyBorder());
      setMargin(new Insets(0, 0, 0, 0));
      setContentAreaFilled(false);
      setFocusable(false);
      setFocusPainted(false);
    }

  }

  //
  // ---
  //

  protected void uninstallUI(final MP3Player player) {
    player.removeAll();
    player.removeAllMP3PlayerListeners();
  }

}

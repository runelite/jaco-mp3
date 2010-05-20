package jaco.mp3.player.plaf;

import jaco.image.IconUtils;
import jaco.image.ImageUtils;
import jaco.mp3.player.MP3Player;
import jaco.mp3.player.MP3PlayerListener;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class MP3PlayerUICompact extends MP3PlayerUI {

  public static ComponentUI createUI(JComponent c) {
    return new MP3PlayerUICompact();
  }

  //

  private JButton playerButton;

  protected void installUI(final MP3Player player) {

    player.setOpaque(false);

    final BufferedImage soundOnImage = ImageUtils.create(getClass().getResource("resources/mp3PlayerSoundOn.png"));
    final BufferedImage soundOnImageRollover = ImageUtils.addBrightness(soundOnImage, 0.05f);
    final BufferedImage soundOnImagePressed = ImageUtils.addDarkness(soundOnImage, 0.05f);

    final BufferedImage soundOffImage = ImageUtils.create(getClass().getResource("resources/mp3PlayerSoundOff.png"));
    final BufferedImage soundOffImageRollover = ImageUtils.addBrightness(soundOffImage, 0.05f);
    final BufferedImage soundOffImagePressed = ImageUtils.addDarkness(soundOffImage, 0.05f);

    playerButton = new JButton();
    playerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    playerButton.setBorder(BorderFactory.createEmptyBorder());
    playerButton.setMargin(new Insets(0, 0, 0, 0));
    playerButton.setContentAreaFilled(false);
    playerButton.setFocusable(false);
    playerButton.setFocusPainted(false);

    //

    playerButton.setIcon(IconUtils.create(soundOffImage));
    playerButton.setRolloverIcon(IconUtils.create(soundOffImageRollover));
    playerButton.setPressedIcon(IconUtils.create(soundOffImagePressed));

    playerButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (player.isPaused() || player.isStopped()) {
          player.play();
        } else {
          player.pause();
        }
      }
    });

    //

    player.addMP3PlayerListener(new MP3PlayerListener() {

      @Override
      public void onPlay(MP3Player player) {
        playerButton.setIcon(IconUtils.create(soundOnImage));
        playerButton.setRolloverIcon(IconUtils.create(soundOnImageRollover));
        playerButton.setPressedIcon(IconUtils.create(soundOnImagePressed));
      }

      @Override
      public void onPause(MP3Player player) {
        playerButton.setIcon(IconUtils.create(soundOffImage));
        playerButton.setRolloverIcon(IconUtils.create(soundOffImageRollover));
        playerButton.setPressedIcon(IconUtils.create(soundOffImagePressed));
      }

      @Override
      public void onStop(MP3Player player) {}
    });

    //

    player.add(playerButton);

    player.setLayout(new LayoutManager() {

      @Override
      public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {

          Dimension parentSize = parent.getSize();
          Dimension buttonSize = playerButton.getPreferredSize();

          Rectangle bounds = new Rectangle(buttonSize);

          bounds.x = (parentSize.width - buttonSize.width) / 2;
          bounds.y = (parentSize.height - buttonSize.height) / 2;

          playerButton.setBounds(bounds);
        }
      }

      @Override
      public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        Dimension size = new Dimension(playerButton.getPreferredSize());
        size.width = size.width + insets.left + insets.right;
        size.height = size.height + insets.top + insets.bottom;
        return size;
      }

      @Override
      public Dimension minimumLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        Dimension size = new Dimension(playerButton.getMinimumSize());
        size.width = size.width + insets.left + insets.right;
        size.height = size.height + insets.top + insets.bottom;
        return size;
      }

      @Override
      public void removeLayoutComponent(Component comp) {}

      @Override
      public void addLayoutComponent(String name, Component comp) {}

    });
  }

}

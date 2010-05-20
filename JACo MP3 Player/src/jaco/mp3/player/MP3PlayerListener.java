package jaco.mp3.player;

public interface MP3PlayerListener {

  void onPlay(MP3Player player);

  void onPause(MP3Player player);

  void onStop(MP3Player player);

}

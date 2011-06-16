

-injars      'xXx_dir_xXx/injars'
-libraryjars 'xXx_dir_xXx/libraryjars'
-outjars     'xXx_dir_xXx/outjars'

-libraryjars '<java.home>/lib/rt.jar'
-libraryjars '<java.home>/lib/jce.jar'


-ignorewarnings
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers


-repackageclasses  jaco.mp3.player

-keep public class jaco.mp3.player.MP3Player {
	public *;
}

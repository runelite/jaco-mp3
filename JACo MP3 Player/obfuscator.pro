


-injars      'xXx_dir_xXx/injars'
-libraryjars 'xXx_dir_xXx/libraryjars'
-outjars     'xXx_dir_xXx/outjars'

-libraryjars '<java.home>/lib/rt.jar'
-libraryjars '<java.home>/lib/jce.jar'



-ignorewarnings
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers



-keep public class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}
-keep public class * extends javax.swing.JApplet



-repackageclasses  jaco.mp3.player

-keep public class jaco.mp3.player.MP3Player {
	public *;
}

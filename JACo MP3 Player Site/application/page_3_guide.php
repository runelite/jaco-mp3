
<?php startSection() ?>
	
	<h3>API specification</h3>
	
	<p>
		<a href="javadocs/jaco/mp3/player/MP3Player.html">MP3Player API</a>
		<br />
		<a href="javadocs/jaco/mp3/player/MP3Player.html#play()">play()</a>
		, 
		<a href="javadocs/jaco/mp3/player/MP3Player.html#pause()">pause()</a>
		, 
		<a href="javadocs/jaco/mp3/player/MP3Player.html#stop()">stop()</a>
		, 
		<a href="javadocs/jaco/mp3/player/MP3Player.html#skipForward()">skipForward()</a>
		, 
		<a href="javadocs/jaco/mp3/player/MP3Player.html#skipBackward()">skipBackward()</a>
		<br />
		<br />
		<a href="javadocs/index.html">Project Java Docs Index</a>
	</p>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>Play a mp3 File</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;

import java.io.File;

public class Example1 {

  public static void main(String[] args) {
    new MP3Player(new File("test.mp3")).play();
  }

}
	</pre>
	
<?php endSection() ?>


<?php startSection() ?>
	
	<h3>... or URL</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;

import java.net.URL;

public class Example2 {

  public static void main(String[] args) throws Exception {
    new MP3Player(new URL("http://server.com/mp3s/test.mp3")).play();
  }

}
	</pre>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>Constructors accepts also multiple files</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;

import java.io.File;

public class Example3 {

  public static void main(String[] args) throws Exception {

    File file1 = new File("test1.mp3");
    File file2 = new File("test2.mp3");
    File file3 = new File("test3.mp3");

    new MP3Player(file1, file2, file3).play();
  }

}
	</pre>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>... or URLs</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;

import java.net.URL;

public class Example4 {

  public static void main(String[] args) throws Exception {

    URL url1 = new URL("http://server.com/mp3s/test1.mp3");
    URL url2 = new URL("http://server.com/mp3s/test2.mp3");
    URL url3 = new URL("http://server.com/mp3s/test3.mp3");

    new MP3Player(url1, url2, url3).play();
  }

}
	</pre>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>Add mp3s to play list</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;

import java.io.File;
import java.net.URL;

public class Example5 {

  public static void main(String[] args) throws Exception {

    MP3Player player = new MP3Player();

    player.addToPlayList(new File("test1.mp3"));
    player.addToPlayList(new File("test2.mp3"));
    player.addToPlayList(new URL("http://server.com/mp3s/test3.mp3"));

    player.play();
  }

}
	</pre>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>Repeat / Shuffle</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;

import java.io.File;

public class Example6 {

  public static void main(String[] args) throws Exception {

    MP3Player player = new MP3Player();

    player.addToPlayList(new File("test1.mp3"));
    player.addToPlayList(new File("test2.mp3"));
    player.addToPlayList(new File("test3.mp3"));

    player.setRepeat(true);
    player.setShuffle(true);

    player.play();
  }

}
	</pre>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>Added as component on a GUI</h3>
	
	<pre>

import jaco.mp3.player.MP3Player;
import jaco.mp3.player.plaf.MP3PlayerUICompact;

import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class Example7 {

  public static void main(String[] args) throws Exception {

    // MP3Player.setDefaultUI(MP3PlayerUICompact.class);

    //

    MP3Player player = new MP3Player();

    player.setRepeat(true);

    player.addToPlayList(new File("test.mp3"));
    player.addToPlayList(new File("test2.mp3"));
    player.addToPlayList(new File("test3.mp3"));
    player.addToPlayList(new URL("http://server.com/mp3s/test4.mp3"));

    //

    player.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

    JFrame frame = new JFrame("MP3 Player");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(player);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

}
	</pre>
	
<?php endSection() ?>

<?php startSection() ?>
	
	<h3>Added as applet on a web page</h3>
	
	<pre>

&lt;script type=&quot;text/javascript&quot;
        src=&quot;http://www.java.com/js/deployJava.js&quot;&gt;
&lt;/script&gt;

&lt;script type=&quot;text/javascript&quot;&gt;

    var attributes = {

            code            : &quot;jaco.mp3.player.MP3PlayerApplet.class&quot;,
            archive         : &quot;jars/jaco-mp3-player.jar&quot;,
            width           : &quot;86&quot;,
            height          : &quot;16&quot;
        };

    var parameters = {

            separate_jvm    : &quot;true&quot;,
            codebase_lookup : &quot;true&quot;,

            background      : &quot;0xFFFFFF&quot;,
            compact         : &quot;false&quot;,
            playlist        : &quot;mp3s/01.mp3, mp3s/02.mp3, mp3s/03.mp3&quot;
        };

    var version = "1.6";

    deployJava.runApplet(attributes, parameters, version);

&lt;/script&gt;
	</pre>
	
<?php endSection() ?>

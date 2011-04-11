<?php
	
	//
	// settings
	
	include 'settings.php';
	
	//
	// get the page

	if (!isset($_GET['page'])) {
		reset($pages);
		$_GET['page'] = key($pages);
	}
	
	$page = $_GET['page'];
	
	//
	// post (start & end) functions
	
	function startPost() {
		echo '<div class="post"><div class="post-bgtop"><div class="post-bgbtm">' . "\n";
	}
	
	function endPost() {
		echo '</div></div></div><!-- post -->';
	}
	
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	
	<title><?php echo $title ?></title>
	
	<?php if (isset($theme)) { ?>
		<link type="text/css" rel="stylesheet" href="themes/<?php echo $theme ?>/stylesheet.css" />
	<?php } ?>
	
	<script type="text/javascript" src="js/external.js"></script>
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	
	<meta http-equiv="cache-control" content="no-cache, no-store" />
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="expires" content="-1" />
	
	<meta name="author" content="Cristian Sulea ( http://cristiansulea.entrust.ro )" />
	<meta name="copyright" content="Cristian Sulea ( http://cristiansulea.entrust.ro )" />
	
	<meta name="keywords" content="<?php echo $keywords ?>" />
	<meta name="description" content="<?php echo $description ?>" />
	
	<meta name="google-site-verification" content="<?php echo $google_site_verification ?>" />
	
</head>

<body>
	
	<script>
		//document.body.style.fontSize = (screen.width / 80) + 'px';
	</script>
	
	<div id="header-container">
		
		<div id="header">
			
			<div id="header-logo">
				<h1><?php echo $title ?></h1>
				<a rel="external" href="<?php echo $project_link ?>">Java Application Core</a>
			</div>
			
			<div id="header-menu">
				<ul>
					<?php foreach ($pages as $key => $value) { ?>
						<li<?php if ($key == $page) echo ' class="active"' ?>><a href="?page=<?php echo $key ?>"><?php echo $value ?></a></li>
					<?php } ?>
				</ul>
			</div>
			
			<div style="clear: both;"></div>
			
		</div>
	</div>
	
	<div id="body-container">
		
		<div id="body">
			
			<div id="body-page">
				<?php include 'page_' . $page . '.php' ?>
			</div>
			
			<div id="body-sidebar">
				<div id="sidebar-bgtop">
					<div id="sidebar-bgbtm">
						
						<h2>Support</h2>
						<ul>
							<li><a rel="external" href="http://sourceforge.net/tracker/?group_id=<?php echo $project_group_id ?>&amp;atid=<?php echo $project_tracker_id_bugs ?>">Bugs</a></li>
							<li><a rel="external" href="http://sourceforge.net/tracker/?group_id=<?php echo $project_group_id ?>&amp;atid=<?php echo $project_tracker_id_support_requests ?>">Support Requests</a></li>
							<li><a rel="external" href="http://sourceforge.net/tracker/?group_id=<?php echo $project_group_id ?>&amp;atid=<?php echo $project_tracker_id_feature_requests ?>">Feature Requests</a></li>
						</ul>
						
						<h2>Hosted on</h2>
						<ul>
							<li>
								<a rel="external" href="<?php echo $project_link ?>">
									<img style="border:0; width:7.5em; height:1.875em"
										src="http://sflogo.sourceforge.net/sflogo.php?group_id=<?php echo $project_group_id ?>&amp;type=13"
										alt="SourceForge.net" />
								</a>									
							</li>
						</ul>
						
						<h2>W3C Validation</h2>
						<ul>
							<li>
								<a rel="external" href="http://validator.w3.org/check?uri=referer">
									<img style="border:0; width:5.5em; height:1.9375em" src="images/valid-XHTML.png" alt="Valid XHTML 1.0 Strict!" />
								</a>									
							</li>
							<li>
								<a rel="external" href="http://jigsaw.w3.org/css-validator/check/referer">
									<img style="border:0; width:5.5em; height:1.9375em" src="images/valid-CSS.gif" alt="Valid CSS!" />
								</a>
							</li>
						</ul>
						
					</div>
				</div>
			</div>
			
			<div style="clear: both;"></div>
			
		</div>
	</div>
	
	<div id="footer-container">
		<div id="footer">
			<p>Copyright &copy; 2010 Cristian Sulea ( <a rel="external" href="http://cristiansulea.entrust.ro/">http://cristiansulea.entrust.ro</a> )</p>
		</div>
	</div>
	
</body>
</html>
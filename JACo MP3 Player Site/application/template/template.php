<?php
	
	//
	// get the page
	
	if (!isset($_GET['page'])) {
		reset($pages);
		$_GET['page'] = key($pages);
	}
	
	$page = $_GET['page'];
	
	//
	// section (start & end) functions
	
	function startSection() {
		echo '<div class="section">' . "\n";
	}
	
	function endSection() {
		echo '</div><!-- section -->';
	}
	
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en-US" xmlns="http://www.w3.org/1999/xhtml" dir="ltr">
<head>
	
	<title><?php echo $title ?></title>
	
	<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
	
	<link rel="stylesheet" href="template/css/screen.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="template/css/print.css" type="text/css" media="print" />
	
	<!--[if IE 6]>
		<link rel="stylesheet" href="template/css/ie6.css" type="text/css" media="all" />
	<![endif]-->
	
	<link rel="shortcut icon" href="images/favicon.ico" />
	
	<script type="text/javascript" src="template/js/external.js"></script>
	
	<meta name="author" content="Cristian Sulea ( http://cristiansulea.entrust.ro )" />
	<meta name="copyright" content="Cristian Sulea ( http://cristiansulea.entrust.ro )" />
	
	<meta name="keywords" content="<?php echo $keywords ?>" />
	<meta name="description" content="<?php echo $description ?>" />
	
	<meta name="google-site-verification" content="<?php echo $google_site_verification ?>" />
	
</head>

<body>
	
	<div id="shell">
		<div id="border">
			
			<div id="header">
				
				<div id="title">
					<h1><?php echo $title ?></h1>
					<a id="jaco-link" rel="external" href="http://jaco.entrust.ro">Java Application COre</a>
				</div>
				
				<div id="socials">
					<h3>Socials</h3>
					<ul>
						<li><a href="#" class="rss">RSS</a></li>
						<li><a href="#" class="facebook">Facebook</a></li>
						<li class="last"><a href="#" class="twitter">Twitter</a></li>
					</ul>
				</div>
				
				<div style="clear: both;"></div>
			</div>

			<div id="navigation">
				<h3>Menu</h3>
				<ul>
					<?php foreach ($pages as $key => $value) { ?>
						<li><a<?php if ($key == $page) echo ' class="active"' ?> href="?page=<?php echo $key ?>"><?php echo $value ?></a></li>
					<?php } ?>
				</ul>
			</div>

			<div id="main">
				
				<div id="content" class="left">
					<?php include 'page_' . $page . '.php' ?>
				</div>

				<div id="sidebar" class="right">
					
					<div class="section">
						<h3>Support</h3>
						<ul>
							<li><a rel="external" class="external" href="http://sourceforge.net/tracker/?group_id=<?php echo $sf_project_group_id ?>&amp;atid=<?php echo $sf_project_tracker_bugs_id ?>" title="SourceForge.net: Tracker: Bugs">Bugs</a></li>
							<li><a rel="external" class="external" href="http://sourceforge.net/tracker/?group_id=<?php echo $sf_project_group_id ?>&amp;atid=<?php echo $sf_project_tracker_support_requests_id ?>" title="SourceForge.net: Tracker: Support Requests">Support Requests</a></li>
							<li><a rel="external" class="external" href="http://sourceforge.net/tracker/?group_id=<?php echo $sf_project_group_id ?>&amp;atid=<?php echo $sf_project_tracker_feature_requests_id ?>" title="SourceForge.net: Tracker: Feature Requests">Feature Requests</a></li>
						</ul>
					</div>
					
					<div class="section">
						<h3>Hosted on</h3>
						<ul>
							<li>
								<a rel="external" href="<?php echo $sf_project_link ?>" title="<?php echo $title ?>">
									<img style="border:0; width:120px; height:30px"
										src="http://sflogo.sourceforge.net/sflogo.php?group_id=<?php echo $sf_project_group_id ?>&amp;type=13"
										alt="SourceForge.net" />
								</a>
							</li>
						</ul>
					</div>
					
					<div class="section">
						<h3>W3C Validation</h3>
						<ul>
							<li>
								<a rel="external" href="http://validator.w3.org/check?uri=referer" title="Valid XHTML!">
									<img style="border:0; width:88px; height:31px" src="template/images/valid-xhtml.png" alt="Valid XHTML!" />
								</a>							
							</li>
							<li>
								<a rel="external" href="http://jigsaw.w3.org/css-validator/check/referer" title="Valid CSS!">
									<img style="border:0; width:88px; height:31px" src="template/images/valid-css.gif" alt="Valid CSS!" />
								</a>
							</li>
						</ul>
					</div>
					
				</div>
				
				<div style="clear: both;"></div>
			</div>

			<div id="shadow-l"></div>
			<div id="shadow-r"></div>
			<div id="shadow-b"></div>
		</div>

		<div id="footer">
			
			<div id="footer-navigation" class="left">
				<h3>Menu</h3>
				<ul>
					<?php $last_key = end(array_keys($pages)); ?>
					<?php foreach ($pages as $key => $value) { ?>
						<li<?php if ($key == $last_key) echo ' class="last"' ?>><a href="?page=<?php echo $key ?>"><?php echo $value ?></a></li>
					<?php } ?>
				</ul>
			</div>
			
			<div class="right">
				<p id="copyright">
					Copyright &copy; 2011 Cristian Sulea ( <a rel="external" href="http://cristiansulea.entrust.ro/" title="Cristian Sulea">http://cristiansulea.entrust.ro</a> )
				</p>
				<p id="copyright-design">
					Design by ChocoTemplates ( <a rel="external" href="http://chocotemplates.com" title="The Sweetest CSS Templates WorldWide">http://chocotemplates.com</a> )
				</p>
			</div>
			
			<div style="clear: both;"></div>
		</div>

	</div>
</body>
</html>
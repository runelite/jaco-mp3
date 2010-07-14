
function applet(separate_jvm, width, height, code, archive, parameters) {

	var java_arguments = '-Xmx256m';

	var s = '';

	s += '<object ' +
				'classid  = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"' +
				'codebase = "http://java.sun.com/update/1.6.0/jinstall-6u20-windows-i586.cab#Version=6,0,0,1"' +

				'width  = "' + width  + '"' +
				'height = "' + height + '">' +

				'<param name = "code"    value = "' + code    + '" />' +
				'<param name = "archive" value = "' + archive + '" />';

	for (var parameter in parameters) {
		s += '<param name = "' + parameter + '" value = "' + parameters[parameter] + '" />';
	}

	s+=         '<param name = "codebase_lookup" value = "true" />' +
				'<param name = "separate_jvm"    value = "' + separate_jvm + '" />' +
				'<param name = "java_arguments"  value = "' + java_arguments + '" />' +

				'<comment>' +

					'<embed ' +
						'pluginspage = "http://java.sun.com/products/plugin/index.html#download" ' +
						'type        = "application/x-java-applet;version=1.6" ' +

						'width  = "' + width  + '" ' +
						'height = "' + height + '" ' +

						'code    = "' + code    + '" ' +
						'archive = "' + archive + '" ';

	for (var parameter in parameters) {
		s += parameter + ' = "' + parameters[parameter] + '" ';
	}

	s +=                'codebase_lookup = "true" ' +
						'separate_jvm    = "' + separate_jvm + '" ' +
						'java_arguments  = "' + java_arguments + '" />' +

				'</comment>' +
			'</object>';
	document.write(s);
}

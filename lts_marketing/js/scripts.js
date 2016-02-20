$(function() {
	$('#demo-html').tooltipster({
		content: $(
			'<img src="doc/images/spiderman.png" width="50" height="50" />
			<h2>Wednesday, July 13</h2>'
			),
		// setting a same value to minWidth and maxWidth will result in a fixed width
		interactive: true,
		autoClose: false,
		minWidth: auto,
		maxWidth: auto,
		position: 'top'
	});

	prettyPrint();
	
	
});
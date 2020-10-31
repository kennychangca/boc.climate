// INCLUDE JQUERY & JQUERY UI 1.12.1
$( function() {
	$( "#datepicker_start" ).datepicker({
		dateFormat: "dd-mm-yy"
		,	duration: "fast"
	});
	$( "#datepicker_end" ).datepicker({
		dateFormat: "dd-mm-yy"
		,	duration: "fast"
	});
} );


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>XLSX Streaming Test</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<script>
    //Variable to store your files
    var files;

    $(document).ready(function() {
	// Add events
	$('input[type=file]').on('change', prepareUpload);

	$('form').on('submit', uploadFiles);

    });

    // Grab the files and set them to our variable
    function prepareUpload(event) {
	files = event.target.files;
    }

    // Catch the form submit and upload the files
    function uploadFiles(event) {
	event.stopPropagation(); // Stop stuff happening
	event.preventDefault(); // Totally stop stuff happening

	// START A LOADING SPINNER HERE
	$("input[type=submit]").attr("disabled", "disabled");

	// Create a formdata object and add the files
	var data = new FormData();
	$.each(files, function(key, value) {
	    data.append(key, value);
	});

	$.ajax({
	    url : 'jersey/upload/xlsx',
	    type : 'POST',
	    data : data,
	    cache : false,
	    dataType : 'text',
	    processData : false, // Don't process the files
	    contentType : false // Set content type to false as jQuery will tell the server its a query string request
	}).done(function(data, textStatus, jqXHR) {
	    $('#status').text(jqXHR.statusText + " (" + jqXHR.status + ")");
	    $('#results').text(data);
	}).fail(function(jqXHR, textStatus, errorThrown) {
	    $('#status').text(jqXHR.statusText + " (" + jqXHR.status + ")");
	    
	    var div = $('<div/>');
	    var msg = "ERROR: " + div.text(textStatus).html();
	    msg = msg + "<br/>" + div.text(errorThrown).html();
	    $('#results').html(msg);
	}).always(function() {
	    // STOP LOADING SPINNER
	    $("input[type=submit]").removeAttr("disabled");
	});
    }
</script>
</head>
<body>
	<h1>XLSX Streaming Test</h1>
	Select a file to upload:
	<br />
	<form action="jersey/upload/xlsx" method="post" enctype="multipart/form-data">
		<input id="submit_button" type="file" name="file" size="50" /> <br />
		<input type="submit" value="Upload File" />
	</form>
	<hr />
	<h4 id="status"></h4>
	<div id="results"></div>
</body>
</html>
// ajax request
var getInfo = function(url, id) {
	
	var xmlhttp;
	
	if (window.XMLHttpRequest) {

		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();

	} else {

		// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");

	}
	
	xmlhttp.onreadystatechange=function() {

		if(xmlhttp.readyState==4) {
			
			// set the contents of the infowindow
			setInfo(xmlhttp.responseText);

		}
		
	}

	// make the request
	xmlhttp.open("GET",url+id,true);
	xmlhttp.send(null);

}

var setInfo = function(json) {
	// slurp the incoming json
	var info = eval('(' + json + ')');

	var content = "";

	if(!info.family_name) {
		content += "Sorry. No departure data is currently available for this location.";
	} else {

		content += info.family_name;
		
	}
	
	content += "";

	document.getElementById("details").innerHTML=content;

}
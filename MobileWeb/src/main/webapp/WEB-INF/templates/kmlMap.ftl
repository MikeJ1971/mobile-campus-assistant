<#include "header.ftl"/>


<h1>${it.label}</h1>
<p>${it.description}</p>

<script type="text/javascript"
         src="http://www.google.com/jsapi?key=ABQIAAAAdUlVnqcMy8dOraexYlBKqxRh1v5iCDAAhGHF-igx4vbWbsh0pRS3YUsdA6b16GnF-aM73UdjjBvVSA&sensor=true"></script>

 <script type="text/javascript">

	google.load("maps", "2.x");

	google.setOnLoadCallback(function() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				showMap(position.coords.latitude, position.coords.longitude);
				showDetails(position.coords.latitude, position.coords.longitude, position.coords.accuracy);
			});
		} else {
			notAvailable();
		}
	});

	function showMap(latitude, longitude) {
		var map = new google.maps.Map2(document.getElementById("map"));
        var point = new google.maps.LatLng(latitude, longitude);
        map.setCenter(point, 16);
		map.addOverlay(new GMarker(point));
		map.openInfoWindow(map.getCenter(), "<p class='bubble'>You are here</p>");
		var gx = new GGeoXml("${it.otherSource}");
        map.addOverlay(gx);

	}

	function showDetails(latitude, longitude, accuracy) {
		var msg = document.getElementById("gps");
		msg.innerHTML += "<p>Latitude: " + latitude + "; Longitude: " + longitude + "; Accuracy:" + accuracy + "</p>";
	}

	function notAvailable() {
		var msg = document.getElementById("map");
		msg.innerHTML = "<p>Sorry, your device doesn't support geo-location services.</p>";
	}


 </script>

<div id="map"><p>Searching ...</p></div>
<div id="gps"></div>

<#include "footer.ftl"/>
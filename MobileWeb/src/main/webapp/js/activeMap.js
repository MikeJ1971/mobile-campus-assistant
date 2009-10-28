/**

 This script performs a number of operations:

 1) Displays map data obtained from a KML file provided by a URL.
 2) Uses the geolocation API to display the current position.

 The W3C geolocation specification is used to display the location of the user viewing the map.
 However, a number of things need to be taken into consideration:

 1) The device might not support the geolocation specification.
 2) The user might not want their location revealed.
 3) The device is not providing an accurate location.
 4) The user is not near the campus.

 It was also clear that an iPhone would not give an accurate location with a one-shot
 location request. The script therefore will poll a number of times until it has
 either received a location within set accuracy level (goodAccuracy) or it had reached
 a maximum number of poll requests (maxPoll).

 If the device's current location is a set distance from the campus (maxDistance) then
 the map will centre on the default campus coordinates (defaultLatitude, defaultLongitude).
 If the accuracy is poor (value greater than that set in 'moderateAccuracy') then
 the map will centre on the default campus coordinates. If the accuracy level is moderate
 (moderateAccuracy) then a warning about accuracy is given and the map will centre on
 the device's location. If we have good accuracy (goodAccuracy) the map will centre
 on the device's location without a warning.

 If the device doesn't support the API or the user disallows access, then the
 map will centre on the default campus coordinates.

 Expected parameter values:

 mapElementId            the ID of the HTML element to hold the map
 defaultLatitude            fallback latitude coordinate
 defaultLongitude        fallback longitude coordinate
 maxPoll                    maximum number of times we will try and get an accurate location
 goodAccuracy            maximum value (meters) we view as good for accuracy
 moderateAccuracy        maximum value (meters) we view as moderate accuracy
 maxDistance                maximum distance (meters) from the default location that is acceptable
 defaultZoomLevel        zoomLevel used in google maps
 kmlUrl                    URL of a kml file

 Author: Mike Jones (mike.a.jones@bristol.ac.uk)

 **/
var initializeMap = function(mapElementId, defaultLatitude, defaultLongitude, maxPoll, goodAccuracy, moderateAccuracy, maxDistance, defaultZoomLevel, proxyUrl, markers) {

	var map;
	
	window.onload = function() {
	
		var latlng = new google.maps.LatLng(defaultLatitude, defaultLongitude);
		var myOptions = {
		      zoom: defaultZoomLevel,
		      center: latlng,
		      disableDefaultUI: true,
//		      navigationControl: true,
		      mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		map = new google.maps.Map(document.getElementById(mapElementId), myOptions);

		overlayMarkers();
		findLocation();
	
	}
	
    findLocation = function() {

        // keep track of location calls
        var locationCount = 0;

        // pointer to the watch callback
        var watchId;

        // do we support geolocation?
        if (navigator.geolocation) {
            watchId = navigator.geolocation.watchPosition(successCallback, errorCallBack, {enableHighAccuracy:true});
        } else {
            hideSearchMessage();
            displayMap(defaultLatitude, defaultLongitude);
        }

        // called if we successfully get the current location
        function successCallback(position) {

            locationCount++;

            // check if we need to polling
            if (locationCount >= maxPoll || position.coords.accuracy <= goodAccuracy) {

                navigator.geolocation.clearWatch(watchId);

                hideSearchMessage();

                // how far are we from the default location?
                var defaultPoint = new GLatLng(defaultLatitude, defaultLongitude);
                var currentPoint = new GLatLng(position.coords.latitude, position.coords.longitude);
                var distance = defaultPoint.distanceFrom(currentPoint, undefined);

                // move to default position if more than maxDistance away from campus
                if (distance >= maxDistance) {
                    alert("Your calculated position is " + maxDistance + " meters away from the campus. The map will centre on the campus rather than your current location.");
                    displayMap(defaultLatitude, defaultLongitude);
                    return;
                }

                // move to default position if the final accuracy greater than 1km
                if (position.coords.accuracy >= moderateAccuracy) {
                    alert("Unable to obtain an accurate position. The map will centre on the campus rather than your current location.");
                    displayMap(defaultLatitude, defaultLongitude);
                    return;
                }

                // display warning if we have a poor accuracy but show position
                if (position.coords.accuracy > goodAccuracy && accuracy < moderateAccuracy) {
                    alert("Unable to obtain an accurate position");
                }

            }

            displayMap(position.coords.latitude, position.coords.longitude, undefined);

        }

        function errorCallBack(error) {
            navigator.geolocation.clearWatch(watchId);
            hideSearchMessage();
            displayMap(defaultLatitude, defaultLongitude);
        }

        function displayMap(latitude, longitude) {

            // centre the map on the location and add overlay
            var point = new google.maps.LatLng(latitude, longitude);
            map.setCenter(point);

            var marker = new google.maps.Marker({
                position: point, 
                map: map
            });   
            
        }

    };

    var hideSearchMessage = function () {
        document.getElementById("searching").style.visibility = "hidden";
    };

    var overlayMarkers = function() {
    	
    	var infowindow = new google.maps.InfoWindow();

    	for(var i=0; i<markers.length; i++) {
        	var point = new google.maps.LatLng(markers[i].lat, markers[i].lng);
            var markerId = markers[i].id;
            var marker = new google.maps.Marker({
                position: point, 
                map: map
            });

            attachListener(map, marker, infowindow, markerId, proxyUrl);
    	}
    };
    
};

var attachListener = function(map, mark, infowindow, id, url) {
    google.maps.event.addListener(mark, 'click', function() {
    	// the style should ensure the autopan pans far enough to allow
    	// space for later dynamic content
    	infowindow.setContent("<div id='transport-info-init'>Fetching...</div>");
    	infowindow.open(map,mark);
    	getDepartureInfo(infowindow, id, url);
    });
}

var setInfoContent = function(infowindow, json) {
	var depInfo = eval('(' + json + ')');
	var content = "<div id='transport-info'>";
	if(!depInfo.stop.name) {
		content += "Sorry. No departure data is currently available for this location.";
	} else {
		content += depInfo.stop.name + " at " + depInfo.base_time;
		for(var i=0; i<depInfo.departures.length && i != 6; i++) {
			content += "<br/>" + depInfo.departures[i].service + " <em>" + depInfo.departures[i].destination + "</em> " + depInfo.departures[i].due;
		}
	}
	content += "</div>";
	infowindow.setContent(content);
}

var getDepartureInfo = function(infowindow, id, url) {
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
			setInfoContent(infowindow, xmlhttp.responseText);
		}
	}
	xmlhttp.open("GET",url+id,true);
	xmlhttp.send(null);
}
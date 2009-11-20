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
var map;
var markers = new Array(); // holds stop markers
var proxyUrl;
var icon;

var initializeMap = function(mapElementId, defaultLatitude, defaultLongitude, maxPoll, goodAccuracy, moderateAccuracy, maxDistance, defaultZoomLevel, pUrl, markerIcon, markerUrl) {

    proxyUrl = pUrl;
    icon = markerIcon;

    window.onload = function() {

        var latlng = new google.maps.LatLng(defaultLatitude, defaultLongitude);
        var myOptions = {
            zoom: defaultZoomLevel,
            center: latlng,
            disableDefaultUI: true,
            //		      disableDoubleClickZoom: true,
            //			  keyboardShortcuts: false,
            //			  scrollwheel: false,
            //		      navigationControl: true,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById(mapElementId), myOptions);

        // do the markers
        getMarkerData(markerUrl);

        // we use the loading of viewable tiles to trigger the refreshing of the markers
        google.maps.event.addListener(map, 'tilesloaded', function() {
            overlayMarkers();
        });

        // test - restrict zoom out
        google.maps.event.addListener(map, 'zoom_changed', function() {
            if (map.getZoom() < 16) {
                map.setZoom(16);
            }
        });

        findLocation();

    }

    // refresh the markers display
    var overlayMarkers = function() {

        // calculate a bounding box slightly bigger
        // than the existing viewport
        var map_bounds = map.getBounds();

        var sw = map_bounds.getSouthWest();
        var ne = map_bounds.getNorthEast();

        var new_sw = new google.maps.LatLng(sw.lat() - 0.002, sw.lng() - 0.002);
        var new_ne = new google.maps.LatLng(ne.lat() + 0.002, ne.lng() + 0.002);

        var sub_bounds = new google.maps.LatLngBounds(new_sw, new_ne);

        // step through the markers showing and
        // hiding as necessary
        for (var j = 0; j < markers.length; ++j) {
            if (sub_bounds.contains(markers[j].getPosition())) {
                if (!markers[j].getVisible()) { // only show if not already visible
                    markers[j].setMap(map);
                    markers[j].setVisible(true);
                }
            } else {
                markers[j].setMap(null); // removes from map
                markers[j].setVisible(false);
            }
        }

    };

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
                var defaultPoint = new google.maps.LatLng(defaultLatitude, defaultLongitude);
                var currentPoint = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                //var distance = defaultPoint.distanceFrom(currentPoint, undefined);
                var distance = distHaversine(defaultPoint, currentPoint);


                // move to default position if more than maxDistance away from campus
                if (distance >= maxDistance) {
                    alert("Your calculated position is MORE THAN "  + maxDistance + " meters away from the campus. The map will centre on the campus rather than your current location.");
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
            alert("Unable to obtain an accurate position. The map will centre on the campus rather than your current location.");
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

    /* see http://stackoverflow.com/questions/1502590/calcualte-distance-between-two-points-in-google-maps-v3/1502821#1502821 */
    var rad = function(x) {
        return x * Math.PI / 180;
    }

    var distHaversine = function(p1, p2) {
        var R = 6371; // earth's mean radius in km
        var dLat = rad(p2.lat() - p1.lat());
        var dLong = rad(p2.lng() - p1.lng());

        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.lat())) * Math.cos(rad(p2.lat())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c;

        return d.toFixed(3) * 1000;
    }


};

// listener for stop markers
var attachMarkerListener = function(map, mark, infowindow, id, url) {

    google.maps.event.addListener(mark, 'click', function() {

        // the style should ensure the autopan pans far enough to allow
        // space for later dynamic content
        infowindow.setContent("<div id='transport-info-init'>Fetching...</div>");
        infowindow.open(map, mark);

        // start the process of fetching the live info
        getDepartureInfo(infowindow, id, url);

    });

}

// ajax request for live departure info
var getDepartureInfo = function(infowindow, id, url) {

    var xmlhttp;

    if (window.XMLHttpRequest) {

        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();

    } else {

        // code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");

    }

    xmlhttp.onreadystatechange = function() {

        if (xmlhttp.readyState == 4) {

            // set the contents of the infowindow
            setInfoContent(infowindow, xmlhttp.responseText);

        }

    }

    // make the request
    xmlhttp.open("GET", url + id, true);
    xmlhttp.send(null);

}

// set the contents of the infowindow
var setInfoContent = function(infowindow, json) {

    // slurp the incoming json
    var depInfo = eval('(' + json + ')');

    var content = "<div id='transport-info'>";

    if (!depInfo.stop.name) {
        content += "Sorry. No departure data is currently available for this location.";
    } else {

        content += depInfo.stop.name + " at " + depInfo.base_time;

        for (var i = 0; i < depInfo.departures.length && i != 6; i++) {
            content += "<br/>" + depInfo.departures[i].service + " <em>" + depInfo.departures[i].destination + "</em> " + depInfo.departures[i].due;
        }
    }

    content += "</div>";

    infowindow.setContent(content);
}

//ajax request for marker data
var getMarkerData = function(url) {

    var xmlhttp;

    if (window.XMLHttpRequest) {

        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();

    } else {

        // code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");

    }

    xmlhttp.onreadystatechange = function() {

        if (xmlhttp.readyState == 4) {

            // populate the markers array
            createMarkers(xmlhttp.responseText);

        }

    }

    // make the request
    xmlhttp.open("GET", url, true);
    xmlhttp.send(null);

}

// intialise the markers
var createMarkers = function(markerJson) {

    // slurp the incoming json
    var m = eval('(' + markerJson + ')');

    var markerData = m.markers;

    var infowindow = new google.maps.InfoWindow();

    for (var i = 0; i < markerData.length; i++) {
        var point = new google.maps.LatLng(markerData[i].lat, markerData[i].lng);
        var markerId = markerData[i].id;
        var marker = new google.maps.Marker({
            position: point,
            icon: icon
        });
        // attach the click listener
        attachMarkerListener(map, marker, infowindow, markerId, proxyUrl);
        markers[i] = marker;
    }
};





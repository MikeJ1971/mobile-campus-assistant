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

mapElementId			the ID of the HTML element to hold the map
defaultLatitude			fallback latitude coordinate
defaultLongitude		fallback longitude coordinate
maxPoll					maximum number of times we will try and get an accurate location
goodAccuracy			maximum value (meters) we view as good for accuracy
moderateAccuracy		maximum value (meters) we view as moderate accuracy
maxDistance				maximum distance (meters) from the default location that is acceptable
defaultZoomLevel		zoomLevel used in google maps
kmlUrl					URL of a kml file

Author: Mike Jones (mike.a.jones@bristol.ac.uk)

**/
var initializeMap = function(mapElementId, defaultLatitude, defaultLongitude, maxPoll, goodAccuracy, moderateAccuracy, maxDistance, defaultZoomLevel, kmlUrl) {

    google.load("maps", "2.x");

    findLocation = function() {

        var map = new google.maps.Map2(document.getElementById(mapElementId));

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
            alert(error.message);
            navigator.geolocation.clearWatch(watchId);
        }

        function displayMap(latitude, longitude) {

            // centre the map on the location and add overlay
            var point = new google.maps.LatLng(latitude, longitude);
            map.setCenter(point, defaultZoomLevel);
            map.addOverlay(new GMarker(point));

            // show the map controls
            map.addControl(new GMapTypeControl());

            // UOB Overlay
            // TODO - make this generic
            // var insert = new EInsert(new GLatLng(51.45744317677596, -2.6012063026428223), "http://www.bristol.ac.uk/university/maps/google-precinct/images/precinct.png", new GSize(1536, 1536), 17);
            // map.addOverlay(insert);


            // add the kml overlay
            var kml = new GGeoXml(kmlUrl);
            map.addOverlay(kml);
        }

    };

    var hideSearchMessage = function () {
        document.getElementById("searching").style.visibility = "hidden";
    };


    google.setOnLoadCallback(findLocation);

};
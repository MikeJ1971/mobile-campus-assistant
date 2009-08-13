<#include "header.ftl"/>


<h1>${it.label}</h1>
<p>${it.description}</p>
<p>${it.otherSource}</p>

<script type="text/javascript"
        src="http://www.google.com/jsapi?key=ABQIAAAAdUlVnqcMy8dOraexYlBKqxTwM0brOpm-All5BF6PoaKBxRWWERQnQVgQlfIUBv1t3ZD37AHwsyUjHA"></script>

<script type="text/javascript">
    google.load("maps", "2.x");

    // Call this function when the page has been loaded
    function initialize() {

        var map = new google.maps.Map2(document.getElementById("map"));
        var point = new google.maps.LatLng(51.457825, -2.605437);
        map.setCenter(point, 16);
        var gx = new GGeoXml("${it.otherSource}");
        map.addOverlay(gx);
    }
    google.setOnLoadCallback(initialize);
</script>

<div id="map" style="width: 500px; height: 300px"></div>


        <#include "footer.ftl"/>
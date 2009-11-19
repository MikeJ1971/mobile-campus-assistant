<#include "includes/header.ftl"/>

<h1>${it.label}</h1>
<p>${it.description}</p>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
<script type="text/javascript" src="${contextPath}/js/activeMap.js"></script>

<script type="text/javascript">

initializeMap("map", ${it.latitude?string.computer}, ${it.longitude?string.computer}, 10, 200, 1000, 3000, 17, "${contextPath}/${it.proxyURLStem}", "${contextPath}/${it.markerIconLocation}", "${contextPath}/${it.markersLocation}");

</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>

<#include "includes/footer.ftl"/>
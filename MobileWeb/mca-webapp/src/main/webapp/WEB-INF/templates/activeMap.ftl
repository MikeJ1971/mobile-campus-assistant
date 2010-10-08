<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
<script type="text/javascript" src="${contextPath}/js/activeMap.js"></script>
<script type="text/javascript">
initializeMap("map", ${resource['geo:lat']?first?string.computer}, ${resource['geo:long']?first?string.computer}, 10, 200, 1000, 3000, 17, "${contextPath}/${resource['mca:urlStem']?first}", "${contextPath}/${resource['mca:icon']?first}", "${contextPath}/${resource['mca:markers']?first}");
</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>

<#include "includes/footer.ftl"/>
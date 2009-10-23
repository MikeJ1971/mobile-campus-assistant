<#include "includes/header.ftl"/>


<h1>${it.label}</h1>
<p>${it.description}</p>

<script type="text/javascript"
         src="http://www.google.com/jsapi?key=ABQIAAAAdUlVnqcMy8dOraexYlBKqxTjPSaaEJVc4G5y4wKsZE4UoG3L1hRdcwFcZ2_Cpcpc4Lj8nGsd7ZHLsw&sensor=true"></script>


<script type="text/javascript" src="${contextPath}/js/kmlMap.js"></script>

<script type="text/javascript">

initializeMap("map", ${it.latitude?string.computer}, ${it.longitude?string.computer}, 10, 200, 1000, 3000, 17, "${it.kmlUrl}");

</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>
<script type="text/javascript" src="${contextPath}/js/einsert.js"></script>


<#include "includes/footer.ftl"/>
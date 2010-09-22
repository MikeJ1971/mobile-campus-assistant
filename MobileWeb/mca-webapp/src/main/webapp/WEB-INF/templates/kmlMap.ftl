<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<@Title label="${it.label}" />

<script type="text/javascript"
         src="http://www.google.com/jsapi?key=ABQIAAAAdUlVnqcMy8dOraexYlBKqxTDMCtVt0Z1QKDY6FJUEvi5o6nJzRSVHu1QtUKAj255RXb5v_X_E-R-AQ&sensor=true"></script>


<script type="text/javascript" src="${contextPath}/js/kmlMap.js"></script>

<script type="text/javascript">

initializeMap("map", ${it.latitude?string.computer}, ${it.longitude?string.computer}, 10, 200, 1000, 3000, 17, "${it.kmlUrl}");

</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>
<script type="text/javascript" src="${contextPath}/js/einsert.js"></script>


<#include "includes/footer.ftl"/>
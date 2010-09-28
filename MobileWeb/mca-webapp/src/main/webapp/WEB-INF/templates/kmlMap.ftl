<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<script type="text/javascript"
         src="http://www.google.com/jsapi?key=ABQIAAAAdUlVnqcMy8dOraexYlBKqxTDMCtVt0Z1QKDY6FJUEvi5o6nJzRSVHu1QtUKAj255RXb5v_X_E-R-AQ&sensor=true"></script>


<script type="text/javascript" src="${contextPath}/js/kmlMap.js"></script>

<script type="text/javascript">

initializeMap("map", ${resource['geo:lat']?first?string.computer}, ${resource['geo:long']?first?string.computer}, 10, 200, 1000, 3000, 17, "${resource['rdfs:seeAlso']?first}");

</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>
<script type="text/javascript" src="${contextPath}/js/einsert.js"></script>


<#include "includes/footer.ftl"/>
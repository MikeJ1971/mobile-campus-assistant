<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<#-- url used for getting information via a proxy -->
<#if resource['mca:urlStem']??>
    <#assign urlStem>${contextPath}/${resource['mca:urlStem']?first}</#assign>
<#else>
    <#assign urlStem>undefined</#assign>
</#if>  

<#-- custom icon to display on the map -->
<#if resource['mca:icon']??>
    <#assign mcaIcon>${contextPath}/${resource['mca:icon']?first}</#assign>
<#else>
    <#assign mcaIcon>${contextPath}/images/blackblank.png</#assign>
</#if>

<#if resource['mca:mapZoom']??>
    <#assign mapZoom>${resource['mca:mapZoom']?first}</#assign>
<#else>
    <#assign mapZoom>17</#assign>
</#if>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
<script type="text/javascript" src="${contextPath}/js/activeMap.js"></script>
<script type="text/javascript">
initializeMap("map", ${resource['geo:lat']?first?string.computer}, ${resource['geo:long']?first?string.computer}, 10, 200, 1000, 3000, ${mapZoom}, "${urlStem}", "${mcaIcon}", "${contextPath}/${resource['mca:markers']?first}");
</script>

<div id="searching">Searching for location ...</div>
<div id="map"></div>
<div>&nbsp;</div>

<#include "includes/footer.ftl"/>
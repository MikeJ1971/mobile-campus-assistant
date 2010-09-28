<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

${resource['rdfs:seeAlso']?first['mca:hasHtmlFragment']?first!"<p>Sorry, not data is available</p>"}

<#include "includes/footer.ftl"/>
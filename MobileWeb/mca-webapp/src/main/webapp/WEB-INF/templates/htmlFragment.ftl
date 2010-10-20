<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<#if resource['rdfs:seeAlso']?first['mca:hasHtmlFragment']??>
    ${resource['rdfs:seeAlso']?first['mca:hasHtmlFragment']?first}
<#else>
    <p>Sorry, not data is available</p>
</#if>

<#include "includes/footer.ftl"/>
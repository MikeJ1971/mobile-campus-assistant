<#-- macros for extracting common rdf values-->
<#-- display a default label for a resource -->
<#macro Label resource>
    <#compress>
    <#if resource['rdfs:label']??>
        ${resource['rdfs:label']?first}
    <#elseif resource['foaf:name']??>
        ${resource['foaf:name']?first}
    <#elseif resource['dc:title']??>
        ${resource['dc:title']?first}
    <#elseif resource['rss:title']??>
        ${resource['rss:title']?first}
    <#else>
        Untitled
    </#if>
    </#compress>
</#macro>

<#macro NavLabel label>
    <#compress>
    <#if (label?length > 23)>
        ${label?substring(0,23)} ...
    <#else>
        ${label}
    </#if>
    </#compress>
</#macro>

<#macro Title label>
    <h1 id="title">${label!"Untitled Page"}</h1>
</#macro>

<#macro ParseXsdDate value>
<#assign length>${value?length}</#assign>
${value?substring(0, length?number - 3)}${value?substring(length?number - 2, length?number)}
</#macro>

<#macro EventDate value>
<#compress>
<#assign length>${value?length}</#assign>
<#assign temp>${value?substring(0, length?number - 3)}${value?substring(length?number - 2, length?number)}</#assign>
${temp?datetime("yyyy-MM-dd\'T\'HH:mm:ssZ")?string('E, d MMM yyyy')}&nbsp;<#if temp?datetime("yyyy-MM-dd\'T\'HH:mm:ssZ")?string('HH:mm') != "00:00">${temp?datetime("yyyy-MM-dd\'T\'HH:mm:ssZ")?string('HH:mm')}</#if>
</#compress>
</#macro>

<#-- Email address with mailto link -->
<#macro Email value><a href="mailto:${value}">${value}</a></#macro>

<#-- Phone number with tel link and label -->
<#macro Phone value>
<#compress>
<a href="${value}"><#if value['rdfs:label']??>${value['rdfs:label']?first}<#else>${value}</#if></a>
</#compress>
</#macro>
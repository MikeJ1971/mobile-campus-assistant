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
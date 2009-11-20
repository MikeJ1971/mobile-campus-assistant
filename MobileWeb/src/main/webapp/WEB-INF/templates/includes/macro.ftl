<#macro NavLabel label>
    <#if (label?length > 23)>
        ${label?substring(0,23)} ...
    <#else>
        ${label}
    </#if>
</#macro>

<#macro Title label>
    <h1 id="title">${label!"Untitled Page"}</h1>
</#macro>
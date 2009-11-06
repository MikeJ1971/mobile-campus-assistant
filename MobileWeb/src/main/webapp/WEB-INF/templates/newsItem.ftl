<#include "includes/header.ftl"/>
<div class="group">
    <div class="newsItem">
        <#if it.label??>
            <h3>${it.label}</h3>
        </#if>
        <#if it.date??>
            <p class="smallPublishDate"><em>${it.date?string("dd MMMM yyyy hh:mm:ss")}</em></p>
        </#if>
        <#if it.description??>
            <p>${it.description}</p>
        </#if>
        <#if it.link??>
            <p><strong><a href="${it.link}">Read more...</a></strong>
            <span class="smallNote">(Note: Content might not be optimized for mobile devices.)</span></p>
         </#if>
        <#if it.provenance??>
            <p>Original source: <a href="${it.provenance}">${it.provenance}</a></p>
        </#if>
    </div>
</div>
<#include "includes/footer.ftl"/>
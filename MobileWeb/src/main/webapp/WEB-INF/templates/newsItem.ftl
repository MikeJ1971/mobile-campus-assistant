<#include "includes/header.ftl"/>

<#include "includes/logoSameLevelNav.ftl"/>

<@Title label="News Item" />

    <div class="newsItem">

        <#if it.label??><h2>${it.label}</h2></#if>
        <#if it.date??><p class="publishDate">${it.date?string("dd MMMM yyyy")}</p></#if>
        <#if it.description??><p>${it.description}</p></#if>
        <#if it.link??>
            <p><a href="${it.link}">Read more...</a>
            <span class="contentWarning">(content not optimized for mobile devices)</span></p>
         </#if>
        <#if it.provenance??>
            <p class="contentSource">Source: <a href="${it.provenance}">${it.provenance}</a></p>
        </#if>
    </div>

<#include "includes/footer.ftl"/>
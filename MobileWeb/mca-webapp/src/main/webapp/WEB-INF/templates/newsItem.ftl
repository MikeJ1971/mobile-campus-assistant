<#include "includes/header.ftl"/>

<#include "includes/logoSameLevelNav.ftl"/>

<@Title label="News Item" />

    <div class="newsItem">
        <#if resource['rss:title']??><h2>${resource['rss:title']?first}</h2></#if>
        <#if resource['dc:date']?first??>
            <p class="publishDate">${resource['dc:date']?first?datetime("yyyy-MM-dd\'T\'HH:mm:ss'Z'")?string('dd MMMM yyyy')}</p>
        </#if>
        <#if resource['rss:description']??><p>${resource['rss:description']?first}</p></#if>
        <#if resource['rss:link']??>
            <p><a href="${resource['rss:link']?first}">Read more...</a>
            <span class="contentWarning">(content not optimized for mobile devices)</span></p>
         </#if>
        <#if resource['mca:hasSource']??>
            <p class="contentSource">Source: <a href="${resource['mca:hasSource']?first}">${resource['mca:hasSource']?first}</a></p>
        </#if>
    </div>
<#include "includes/footer.ftl"/>
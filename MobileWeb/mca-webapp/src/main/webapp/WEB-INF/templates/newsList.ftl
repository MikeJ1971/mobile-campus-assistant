<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<#if resource['mca:hasNewsItem']??>
<div class="nav">
    <ul class="nav-list">
        <#list resource['mca:hasNewsItem'] as item>
            <#assign label=item['rss:title']?first/>
            <li><a href="./?item=${item?url("UTF8")}"><span class="news"></span><@NavLabel label="${label}"/></a></li>
        </#list>
    </ul>
<#else>
<p>Sorry, no news items.</p>
</#if>

<#include "includes/footer.ftl"/>
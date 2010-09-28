<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>

<@Title label="${label}" />


<#if resource['mca:hasNewsItem']??>
<p>Yes, there are news items</p>

<#list resource['mca:hasNewsItem'] as item>
    <p>${item}</p>
    <p>${item['rss:title']?first}</p>
</#list>

<#else>
<p>Sorry</p>
</#if>


<#--

<#if it.items?size == 0>
<p>Sorry, no news items.</p>
<#else>
<div class="nav">
    <ul class="nav-list">
        <#list it.items as item>
        <li><a href="./?item=${item.id?url("UTF8")}"><span class="news"></span><@NavLabel label="${item.label}"/>
        </a>
        </li>
        </#list>
    </ul>
</div>
</#if>

-->

<#include "includes/footer.ftl"/>
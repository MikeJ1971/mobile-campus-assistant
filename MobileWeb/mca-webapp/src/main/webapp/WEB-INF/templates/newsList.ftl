<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<@Title label="${it.label}" />

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

<#include "includes/footer.ftl"/>
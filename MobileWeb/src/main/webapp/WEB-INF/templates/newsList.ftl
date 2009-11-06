<#include "includes/header.ftl"/>

<div class="group">
    <h3>${it.label}</h3>
    <#if it.items?size == 0>
    <p>Sorry, no news items.</p>
    </#if>
    <ul class="itemList">
        <#list it.items as item>
        <li class="newsItemTitle"><a href="./?item=${item.id?url("UTF8")}">${item.label}<br/>
            <span class="publishDate">${item.date?string("dd MMMM yyyy hh:mm:ss")}</a></span></li>
        </#list>
    </ul>
</div>

<#include "includes/footer.ftl"/>
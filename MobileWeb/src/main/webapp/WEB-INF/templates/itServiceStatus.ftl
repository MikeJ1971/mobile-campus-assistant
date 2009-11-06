<#include "includes/header.ftl"/>

<div class="contacts">
    <h3>${it.label}</h3>
    <#if it.items?size == 0>
    <p>Sorry, no service status.</p>
    </#if>
    <ul class="itemList">
        <#list it.items as item>
        <li class="newsItemTitle">${item.label}<br/>
            <span class="publishDate">${item.date?string("dd MMMM yyyy hh:mm:ss")}</span></li>
        </#list>
    </ul>
</div>

<#include "includes/footer.ftl"/>
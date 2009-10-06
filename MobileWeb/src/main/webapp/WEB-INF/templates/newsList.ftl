<#include "header.ftl"/>

<div class="group">
    <h3>${it.label}</h3>
    <p>${it.description}</p>
    <ul class="itemList">
        <#list it.items as item>
        <li class="newsItemTitle"><a href="./?item=${item.id}">${item.label}<br/>
            <span class="publishDate">${item.date?string("dd MMMM yyyy hh:mm:ss")}</a></span></li>
        </#list>
    </ul>
</div>

<#include "footer.ftl"/>
<#include "includes/header.ftl"/>

<div class="group">
    <p>${it.label}</p>
    <ul class="itemList">
        <#list it.items as item>
        <li><a href="${contextPath}/${item.path}">${item.label}</a></li>
        </#list>
    </ul>
</div>

<#include "includes/footer.ftl"/>
<#include "header.ftl"/>

<#list it.items as group>
<div class="group">
    <p>${group.label}</p>
    <ul class="itemList">
        <#list group.items as item>
        <li><a href="${contextPath}/${item.path}">${item.label}</a></li>
        </#list>
    </ul>
</div>
</#list>

<#include "footer.ftl"/>
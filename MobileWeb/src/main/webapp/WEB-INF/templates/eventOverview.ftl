<#include "includes/header.ftl"/>
<div class="group">
    <h3>${it.label}</h3>
    <ul class="itemList">
        <#list it.items as item>
        <li class="eventItemTitle"><a href="${contextPath}/${item.path}">${item.label}</a></li>
        </#list>
    </ul></div>
<#include "includes/footer.ftl"/>
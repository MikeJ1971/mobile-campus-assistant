<#include "includes/header.ftl"/>

<div class="group">
    <h3>${it.label}</h3>
    <#if it.items?size == 0>
    <p>Sorry, no upcoming events in this calendar.</p>
    </#if>
    <ul class="itemList eventList">
        <#list it.items as event>
        <li class="eventItemTitle"><a href="./?item=${event.id}">${event.label}<br/>
           <span class="startdate">${event.startDate?string("E, d MMM yyyy HH:mm")}</span></a>
        </li>
        </#list>
    </ul>
</div>

<#include "includes/footer.ftl"/>
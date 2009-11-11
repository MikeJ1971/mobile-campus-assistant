<#include "includes/header.ftl"/>

<div class="group">
    <h3>${it.label}</h3>
    <div class="description">${it.description}</div>
    <div class="calendarlinks">
        <#if it.getHTMLLink()??><div class="calendarlink"><a href="${it.HTMLLink}"><img src="${contextPath}/images/htmlcalicon.png"/><br/>View on web</a></div></#if>
        <#if it.getiCalLink()??><div class="calendarlink"><a href="${it.iCalLink}"><img src="${contextPath}/images/icalicon.png"/><br/>iCal link</a></div></#if>
    </div>
    <br/>
    <#if it.items?size == 0>
    <p>Sorry, there are no upcoming events.</p>
    </#if>
    <ul class="itemList eventList">
        <#list it.items as event>
        <li class="eventItemTitle"><a href="./?item=${event.id}&r=${event.startDateAsTimeStamp}">${event.label}<br/>
           <span class="startdate">${event.startDate?string("E, d MMM yyyy")}<#if event.startDate?string("HH:mm") != "00:00"> ${event.startDate?string("HH:mm")}</#if></span></a>
        </li>
        </#list>
    </ul>
</div>

<#include "includes/footer.ftl"/>
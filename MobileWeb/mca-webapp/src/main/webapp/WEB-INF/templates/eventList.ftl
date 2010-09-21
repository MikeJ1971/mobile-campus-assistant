<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<@Title label="${it.label}" />

<#if it.items?size == 0>
    <p>Sorry, there are no upcoming events.</p>
<#else>
    <div class="nav">
        <ul class="nav-list">
            <#list it.items as item>
                <li><a href="./?item=${item.id?url("UTF8")}"><span class="events"></span><@NavLabel label="${item.label}"/><br/><span class="startdate">${item.startDate?string("E, d MMM yyyy")}<#if item.startDate?string("HH:mm") != "00:00"> ${item.startDate?string("HH:mm")}</#if></span></a></li>
                </a>
            </#list>
        </ul>
    </div>
</#if>

<div class="calendarlinks">
    <#if it.getHTMLLink()??>
    <div class="calendarlink"><a href="${it.HTMLLink}"><img src="${contextPath}/images/htmlcalicon.png" alt="View on the Web"/><br/>View on web</a></div>
    </#if>
    <#if it.getiCalLink()??>
    <div class="calendarlink"><a href="${it.iCalLink}"><img src="${contextPath}/images/icalicon.png" alt="iCal File"/><br/>.ics file</a></div>
    </#if>
</div>

<#include "includes/footer.ftl"/>
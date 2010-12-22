<#compress>
<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<#if resource['mca:hasEventItem']??>
   <div class="nav">
        <ul class="nav-list">
            <#list resource['mca:hasEventItem'] as item>
                <#assign label=item['ical:summary']?first/>
                <li><a href="./?item=${item['ical:uid']?first?url("UTF8")}"><span class="events"></span>${label}<br/><span class="startdate"><@EventDate item['ical:dtstart']?first /></a></li>
            </#list>
        </ul>
    </div>
<#else>
 <p>Sorry, there are no upcoming events.</p>
</#if>

<div class="calendarlinks">
    <#if resource['mca:htmlLink']??>
    <div class="calendarlink"><a href="${resource['mca:htmlLink']?first}"><img src="${contextPath}/images/htmlcalicon.png" alt="View on the Web"/><br/>View on web</a></div>
    </#if>
    <#if resource['mca:icalLink']??>
    <div class="calendarlink"><a href="${resource['mca:icalLink']?first}"><img src="${contextPath}/images/icalicon.png" alt="iCal File"/><br/>.ics file</a></div>
    </#if>
</div>



<#include "includes/footer.ftl"/>
</#compress>

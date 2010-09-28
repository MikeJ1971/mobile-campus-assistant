<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<@Title label="${resource['rdfs:label']?first}" />

<div class="contactDetails">
    <ul>
        <#if resource['foaf:phone']??>
        <li class="phoneContact"><a href="${resource['foaf:phone']?first}">${resource['foaf:phone']?first['rdfs:label']?first}</a></li>
        </#if>
        <#if resource['foaf:mbox']??>
        <li class="emailContact"><a href="mailto:${resource['foaf:mbox']?first}">${resource['foaf:mbox']?first}</a></li>
        </#if>
    </ul>
</div>

<#include "includes/footer.ftl"/>
<#compress>
<#include "includes/header.ftl"/>
<#include "includes/logoOneLevelNav.ftl"/>
<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<#include "includes/staffSearchForm.ftl"/>

<#if resource['dc:description']??>
    <p>${resource['dc:description']?first}</p>
</#if>

<#if resource['mca:hasItem']??>
<div id="search-results">
    <#list resource['mca:hasItem']?reverse as item>
        <div class="contact">

        <#-- name -->
        <#if item['vcard:NAME']??>
            <p class="contact-name"><strong>${item['vcard:NAME']?first}</strong></p>
        </#if>

        <#-- department -->
        <#if item['vcard:Orgname']??>
            <p class="contact-dept">${item['vcard:Orgname']?first}</p>
        </#if>

        <#-- address -->
        <#if item['vcard:ADR']??>
            <p class="contact-address">${item['vcard:ADR']?first}</p>
        </#if>

        <#-- email -->
        <#if item['foaf:mbox']??>
            <p class="contact-email"><@Email item['foaf:mbox']?first/></p>
        </#if>

        <#-- phone -->
        <#if item['foaf:phone']??>
            <p class="contact-phone"><@Phone item['foaf:phone']?first/></p>
        </#if>
        </div>
    </#list>
</div>
<#else>
<p>Sorry, there are no results.</p>
</#if>



<#include "includes/footer.ftl"/>
</#compress>
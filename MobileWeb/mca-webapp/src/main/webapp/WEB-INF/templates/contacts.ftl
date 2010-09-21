<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<@Title label="${it.label}" />

<div class="contactDetails">
    <ul>
        <#if it.phoneNumber??>
        <li class="phoneContact"><a href="${it.phoneNumber}">${it.phoneNumberLabel}</a></li>
        </#if>
        <#if it.email??>
        <li class="emailContact"><a href="mailto:${it.email}">${it.email}</a></li>
        </#if>
    </ul>
</div>

<#include "includes/footer.ftl"/>
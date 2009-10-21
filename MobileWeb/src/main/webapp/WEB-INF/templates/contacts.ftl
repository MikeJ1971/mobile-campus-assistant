<#include "header.ftl"/>

<div class="contacts">
    <h4>${it.label}</h4>
    <ul>
        <#if it.phoneNumber??>
        <li class="phoneContact"><a href="${it.phoneNumber}">${it.phoneNumberLabel}</a></li>
        </#if>
        <#if it.email??>
        <li class="emailContact"><a href="mailto:${it.email}">${it.email}</a></li>
        </#if>
    </ul>
</div>

<#include "footer.ftl"/>
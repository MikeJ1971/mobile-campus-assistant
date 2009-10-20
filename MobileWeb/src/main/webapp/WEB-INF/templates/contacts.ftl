<#include "header.ftl"/>

<p>${it.label}</p>

<#if it.phoneNumber??>
<p><a href="${it.phoneNumber}">${it.phoneNumberLabel}</a></p>
</#if>

<#if it.email??>
<p><a href="mailto:${it.email}">${it.email}</a></p>
</#if>

<#include "footer.ftl"/>
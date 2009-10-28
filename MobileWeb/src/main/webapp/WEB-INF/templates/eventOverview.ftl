<#include "includes/header.ftl"/>
<div class="group">Sources
    <div class="eventSource">
        <h3>${it.label}</h3>
        <p class="smallPublishDate"><em>${it.date?string("dd MMMM yyyy hh:mm:ss")}</em></p>
        <p>${it.description}</p>
        <p><strong><a href="${it.link}">Read more...</a></strong>
            <span class="smallNote">(Note: Content might not be optimized for mobile devices.)</span></p>
        <p>Original source: <a href="${it.provenance}">${it.provenance}</a></p>
    </div>
</div>
<#include "includes/footer.ftl"/>
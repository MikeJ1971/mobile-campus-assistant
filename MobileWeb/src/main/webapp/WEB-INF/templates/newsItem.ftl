<#include "header.ftl"/>
<div class="group">
    <div class="newsItem">
        <h4>${it.label}</h4>
        <p>${it.description}</p>
        <p>Published: <em>${it.date?string("dd MMMM yyyy hh:mm:ss")}</em></p>
    </div>
</div>
<#include "footer.ftl"/>
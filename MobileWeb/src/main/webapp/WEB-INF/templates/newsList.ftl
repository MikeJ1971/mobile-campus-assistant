<#include "header.ftl"/>

<h3>${it.label}</h3>

<p>${it.description}</p>

<#list it.items as item>
    <div class="newsItem">
        <h4>${item.label}</h4>
        <p>${item.description}</p>
        <p><a href="${item.link}">More...</a></p>
    </div>
</#list>

<#include "footer.ftl"/>
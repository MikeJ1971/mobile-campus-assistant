<div class="nav">
    <ul class="nav-list">
        <#list it.items as item>
            <li><a href="${contextPath}/${item.path}"><#if item.style??><span class="${item.style}"></span></#if>${item.label}</a></li>
        </#list>
    </ul>
</div>
<div class="group">
    <h3>${it.label}</h3>
    <ul>
        <#list it.items as item>
            <li><a href="${contextPath}/${item.path}">${item.label}</a></li>
        </#list>
    </ul>
</div>
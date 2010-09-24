<#--
<div class="nav">
    <ul class="nav-list">
        <#list it.items as item>
            <li><a href="${contextPath}/${item.path}"><#if item.style??><span class="${item.style}"></span></#if><@NavLabel label="${item.label}"/></a></li>
        </#list>
    </ul>
</div>
-->

<div class="nav">
    <ul class="nav-list">
    <#list resource['mca:hasItem'] as item>
        <#assign label><@Label resource=item/></#assign>
        <li><a href="${contextPath}/${item?substring(15)}"><#if item['mca:style']??><span class="${item['mca:style']?first}"></span></#if><@NavLabel label=label/></a></li>
    </#list>
    </ul>
</div>
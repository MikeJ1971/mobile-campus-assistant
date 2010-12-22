<div class="nav">
    <ul class="nav-list">
    <#list resource['mca:hasItem'] as item>
        <#assign label><@Label resource=item/></#assign>
        <li><a href="${contextPath}/${item?substring(15)}"><#if item['mca:style']??><span class="${item['mca:style']?first}"></span></#if>${label}<span class="arrow"></span></a></li>
    </#list>
    </ul>
</div>
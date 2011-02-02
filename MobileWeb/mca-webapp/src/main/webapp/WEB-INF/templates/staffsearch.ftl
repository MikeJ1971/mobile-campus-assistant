<#compress>
<#include "includes/header.ftl"/>
<#include "includes/logoOneLevelNav.ftl"/>
<#assign label><@Label resource/></#assign>
<@Title label="${label}" />

<div id="feedback-form">
    <form accept="." method="get">
        <p><strong>Search Term:</strong></p>
        <p><input id="search" type="text" name="search"/>
        <input type="submit" value="Submit" /></p>
    </form>
</div>

<#include "includes/footer.ftl"/>
</#compress>
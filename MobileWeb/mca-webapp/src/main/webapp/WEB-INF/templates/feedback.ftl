<#compress>
<#include "includes/header.ftl"/>
<#include "includes/logoOneLevelNav.ftl"/>
<#assign label><@Label resource/></#assign>
<@Title label="${label}" />
<div id="feedback-form">
    <form accept="." method="get">
        <p><strong>Email Address (Optional)</strong></p>
        <p><input id="form_email" type="text" name="email"/></p>
        <p><strong>Comments</strong></p>
        <p><textarea id="form_comments" name="comment" rows="10" cols="40"></textarea></p>
        <p><input type="submit" value="Submit" /></p>
    </form>
</div>
<#include "includes/footer.ftl"/>
</#compress>
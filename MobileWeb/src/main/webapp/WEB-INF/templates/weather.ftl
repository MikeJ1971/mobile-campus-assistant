<#include "includes/header.ftl"/>

<#include "includes/logoOneLevelNav.ftl"/>

<@Title label="${it.label}" />

<div class="group">
    <div id="weatherData">
        ${it.htmlFragment!"<p>No Weather Data!</p>"}
    </div>
</div>

<#include "includes/footer.ftl"/>
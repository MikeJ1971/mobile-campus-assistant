<#include "includes/header.ftl"/>

<script type="text/javascript" src="${contextPath}/js/directory.js"></script>

<script type="text/javascript">
	init("${contextPath}/${it.queryUrlStem}","${contextPath}/${it.detailsUrlStem}");
</script>

<div class="contacts">
    <h4>${it.label}</h4>

	<form action='#'>
	<input id='search-form-input'/>
	<a style="display:inline;" href='#' onclick='query();return false;'>search</a>
	</form>

	<div id="spinner" style="display:none;"><img src="${contextPath}/images/spinner.gif"/></div>

    <div id="person-details" style="display:none;">
    <ul>
        <li id="name" style="display:none;"></li>
        <li id="job-title" style="display:none;"></li>
        <li id="org-unit" style="display:none;"></li>
        <li class="phoneContact" id="telephone" style="display:none;"></li>
        <li class="emailContact" id="email" style="display:none;"></li>
    </ul>
    </div>

    <div id="message" style="display:none;"></div>
    <div id="query-results" style="display:none;">
    </div>
    
</div>

<#include "includes/footer.ftl"/>
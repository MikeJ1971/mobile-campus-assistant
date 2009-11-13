<#include "includes/header.ftl"/>

<script type="text/javascript" src="${contextPath}/js/directory.js"></script>

<script type="text/javascript">
	init("${contextPath}/${it.queryUrlStem}","${contextPath}/${it.detailsUrlStem}");
</script>

<div class="contacts">
    <h4>${it.label}</h4>

	<form>
	<input id='search-form-input'/>
	<input type="submit" value="Search" onclick='query();return false;'/>
	</form>

	<div id="spinner" style="display:none;"><img src="${contextPath}/images/spinner.gif"/></div>

    <div id="message" style="display:none;"></div>

	<div id="directory-help-refine" style="display:none;">
		<p>
		The first 10 are displayed. To refine your search, try entering more details (e.g. forename and surname). 
		</p>
	</div>

	<div id="directory-help">
		<p>
		Search terms can be surname only, initials and surname or first-name and surname.
		</p>
		<p>
		To search for students, job titles, or organisational titles see the
		<a href="https://www.bris.ac.uk/contact/home">Contact Directory</a> (not optimised for mobiles).
		</p>
	</div>

    <div id="person-details" style="display:none;">
    <ul>
        <li id="name" style="display:none;"></li>
        <li id="job-title" style="display:none;"></li>
        <li id="org-unit" style="display:none;"></li>
        <li class="phoneContact" id="telephone" style="display:none;"></li>
        <li class="emailContact" id="email" style="display:none;"></li>
    </ul>
    </div>

    <div id="query-results" style="display:none;">
    </div>
    
</div>

<#include "includes/footer.ftl"/>
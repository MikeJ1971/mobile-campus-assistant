<#include "includes/header.ftl"/>

<script src="http://jqueryjs.googlecode.com/files/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="http://www.datejs.com/build/date.js" type="text/javascript"></script>

<script>
    jQuery(document).ready(function(){
        jQuery(".date").each(function ()
        {
            // get the start date
            var utcStartDate = jQuery(".dtstart .value", this).attr('title');
            var d1 = Date.parse(utcStartDate);

            // get the end date
            var utcEndDate = jQuery(".dtend .value", this).attr('title');
            var d2 = Date.parse(utcEndDate);

            // get handle to display of datetime
            var humanView = jQuery(".eventtime",this);

            // set the human view
            renderDates(d1, d2, humanView);

        });
    });

    function renderDates(d1, d2, humanView)
    {
        if (!handleSingleDate(d1, d2, humanView) &&
            !handleDatesOnSameDay(d1, d2, humanView))
        {
            jQuery(humanView).html("unable to display dates");
        }
    }

    function handleSingleDate(d1, d2, humanView)
    {
        var d = null;
        if (d1 != null && d2 == null) d = d1;
        else if (d1 == null && d2 != null) d = d2;

        if (d != null)
        {
            jQuery(humanView).html(d.toString("ddd, dd MMM yyyy 'at' HH:mm:ss"));
            return true;
        }

        return false;
    }

    function handleDatesOnSameDay(d1, d2, humanView)
    {
        var d1_c = d1.clone();
        var d2_c = d2.clone();
        d1_c.clearTime();
        d2_c.clearTime();

        if (Date.equals(d1_c,d2_c))
        {
            jQuery(humanView).append("<div class='day row'><span class='label'>Day:</span>"+d1.toString("ddd, dd MMM yyyy")+"</div>");
            jQuery(humanView).append("<div class='time row'><span class='label'>Time:</span>"+d1.toString("HH:mm") + " to " + d2.toString("HH:mm")+"</div>");
            return true;
        }
        return false;
    }
</script>

<div class="event">
    <h3>${it.label}</h3>

    <div class="date">
        <span class="dtstart"><attr class='value' title='${it.startDate?string("yyyy-MM-dd'T'hh:mm:ss'Z'")}'>${it.startDate?string("E, d MMM")}</attr></span>
        <div class="eventtime"></div>
        <span class="dtend"><attr class='value' title='${it.endDate?string("yyyy-MM-dd'T'hh:mm:ss'Z'")}'>${it.endDate?string("E, d MMM")}</attr></span>
    </div>
    <div class="description row">${it.description}</div>
    <div class="location row"><#if it.location?has_content><span class="label">Location</span> ${it.location}<#else></#if></div>
    <div class="organiser row"><#if it.organiser?has_content><span class="label">Organiser</span> ${it.organiser}<#else></#if></div>
    <!-- <div class="provenance row"><#if it.provenance?has_content><span class="label">Provenance</span> ${it.provenance}<#else></#if></div>-->
</div>

<#include "includes/footer.ftl"/>
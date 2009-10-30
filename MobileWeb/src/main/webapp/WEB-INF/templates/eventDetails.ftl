<#include "includes/header.ftl"/>

<script src="http://jqueryjs.googlecode.com/files/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="http://datejs.googlecode.com/files/date.js" type="text/javascript"></script>

<script>
    jQuery(document).ready(function(){
        jQuery(".date .utc:not(:empty)").each(function ()
        {
            // get the value
            var longDate = Date(parseInt(jQuery(this).text()));

            // handle to human view of datetime
            var humanView = jQuery(this).prev();

            // set the human view
            jQuery(humanView).html(longDate.toString("ddd, dd MMM yyyy HH:mm:ss"));
        });
    });
</script>

<div class="group">
    <h3>${it.label}</h3>
    <p>${it.description}</p>
</div>

<#include "includes/footer.ftl"/>
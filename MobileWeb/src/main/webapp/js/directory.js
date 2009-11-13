var qUrl;
var dUrl;
var currentFrag;

var init = function(queryUrl, detailsUrl) {
	qUrl = queryUrl;
	dUrl = detailsUrl;
	
	window.onload = function() {
		setInterval ( "checkFrag()", 250 );
	}
}

var query = function() {

	// get input
	var q = document.getElementById("search-form-input").value;

	addFrag('q=' + encodeURIComponent(q));
	
	checkFrag();
}

var setQueryInput = function(q) {
	document.getElementById("search-form-input").value = q;
}

var getInfo = function(id) {

	addFrag('pk=' + encodeURIComponent(id));

	checkFrag();
}

var addFrag = function(frag) {
	parent.location.hash = frag;
}

var checkFrag = function() {
	if(parent.location.hash == currentFrag) {
		return;
	}

	reset();
	currentFrag = parent.location.hash;
	
	var frag = parent.location.hash;
	
	if(frag) {
		if(/^#pk/.test(frag)) {
			// details
			var pk = frag.substring(4);
			ajax(dUrl, pk);
		} else if(/^#q/.test(frag)) {
			// query
			var q = frag.substring(3);
			setQueryInput(decodeURIComponent(q));
			ajax(qUrl, q);
		}
	} else {
		show('directory-help', true);
	}
}

var reset = function() {

	show('message', false);
	show('person-details', false);
	show('query-results', false);
	document.getElementById("query-results").innerHTML = "";
	show('name', false);
	show('email', false);
	show('telephone', false);
	show('job-title', false);
	show('org-unit', false);
	setQueryInput('');
	show('directory-help-refine',false);

}

var ajax = function(url, id) {

	show('spinner', true, true);

	var xmlhttp;

	if (window.XMLHttpRequest) {

		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();

	} else {

		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");

	}

	xmlhttp.onreadystatechange = function() {

		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				// set the contents of the infowindow
				handleResults(xmlhttp.responseText);
			} else if (xmlhttp.status == 406) {
				// unacceptable search string
				displayMessage("Your search string was unacceptable.");
				show('directory-help', true);
			} else {
				displayMessage("There was an error. Status code: " + xmlhttp.status);
				show('directory-help', true);
			}
		}

	}

	// make the request
	xmlhttp.open("GET", url + id, true);
	xmlhttp.send(null);

}

var handleResults = function(json) {

	// slurp the incoming json
	var results = eval('(' + json + ')');

	if (results.message) {
		// we've got search results
		displayResults(results);
	} else {
		// single person details
		displayDetails(results);
	}
}

var displayMessage = function(message) {

	show('spinner', false);

	if (message) {
		document.getElementById("message").innerHTML = "<p>" + message + "</p>";
		show('message', true);
	}

}

var displayResults = function(info) {

	show('spinner', false);

	if (info.message) {
		displayMessage(info.message);
	}
	
	var count = info.message.match(/\d+/);
	if(count > 10) {
		show('directory-help-refine',true);
	}

	if (info.results.length > 0) {
		
		show('directory-help', false);
		
		var content = '<ul>';

		for ( var i = 0; i < info.results.length; i++) {

			var item = info.results[i];

			content += "<li><a href='#' onclick='getInfo(\"" + item.person_key
				+ "\");return false;'>" + item.family_name + "</a>";
			
			content += item.org_unit + "</li>";
			
		}

		content += '</ul>';

		document.getElementById("query-results").innerHTML = content;

		show('query-results', true);

	} else {
		show('directory-help', true);
	}		

}

var displayDetails = function(info) {

	show('spinner', false);

	show('directory-help', false);

	if (info.family_name) {
		document.getElementById("name").innerHTML = info.title + " "
				+ info.given_name + " " + info.family_name;
		show('name', true);
	}
	if (info.email) {
		document.getElementById("email").innerHTML = "<a href='mailto:"
				+ info.email + "'>" + info.email + "</a>";
		show('email', true);
	}
	if (info.telephone) {
		var tel = info.telephone;
		tel = tel.replace(/[\s+\(\)]/g,'');
		tel = tel.replace(/^0/,'+44');
		document.getElementById("telephone").innerHTML = "<a href='tel:"
				+ tel + "'>" + info.telephone + "</a>";
		show('telephone', true);
	}
	if (info.job_title) {
		document.getElementById("job-title").innerHTML = info.job_title;
		show('job-title', true);
	}
	if (info.org_unit) {
		var address;
		if (info.address) {
			address = '<br/>' + info.address.join('<br/>');
		}
		if (info.post_code) {
			address += '<br/>' + info.post_code;
		}
		document.getElementById("org-unit").innerHTML = info.org_unit + address;
		show('org-unit', true);
	}

	show('person-details', true);

}

var show = function(div, show, inline) {
	if (show) {
		if (inline) {
			document.getElementById(div).style.display = 'inline';
		} else {
			document.getElementById(div).style.display = 'block';
		}
	} else {
		document.getElementById(div).style.display = 'none';
	}
}
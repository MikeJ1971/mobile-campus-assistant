<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
    <title>SPARQL</title>
</head>
<body>

<div id="form">

    <form action="./sparql" method="GET">

        <p><strong>SPARQL query</strong></p>

        <textarea name="query" cols="80" rows="25"></textarea>

        <p>Result Format
            <select name="type">
                <option>xml</option>
                <option>json</option>
            </select>
        </p>

        <p><input type="submit" value="Get Results"/></p>

    </form>

</div>


</body>
</html>
package org.ilrt.mca;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class Common {

    // TODO look at using JodaTime to parse XSD dates

    private Common() {
    }

    public static String parseXsdDate(final Date date) {

        String temp = new SimpleDateFormat(DATE_FORMAT_STRING).format(date);

        return temp.substring(0, temp.length() - 2) + ":"
                + temp.substring(temp.length() - 2, temp.length());
    }

    public static Date parseXsdDate(final String XsdDate) throws ParseException {

        String temp = XsdDate.substring(0, XsdDate.length() - 3)
                + XsdDate.substring(XsdDate.length() - 2, XsdDate.length());

        return new SimpleDateFormat(DATE_FORMAT_STRING).parse(temp);
    }

    public static String parseDate(final Date date) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT_STRING).format(date);
    }

    // TODO ICKY QUICK FIX - LOOK AT JODA TIME FOR XSD DATE FORMATS
    public static Date parseDate(String date) throws ParseException {
        if (date.endsWith("Z")) {
            date = date.substring(0, date.length() - 1);
        }
        if (date.length() == 10) {
            date = date+"T00:00:00Z";
        }
        return new SimpleDateFormat(DATE_FORMAT_STRING_WITHOUT_TZ).parse(date);
    }

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static String DATE_FORMAT_STRING_WITHOUT_TZ = "yyyy-MM-dd'T'HH:mm:ss";

    public static String MCA_STUB = "mca://registry/";
    public static String TEMPLATE_STUB = "template://";
    public static String AUDIT_GRAPH_URI = "mca://audit/";

    /**
     * This method closes all opened tags in the provided string
     * e.g. "hello &lt;b&gt;world" => "hello &lt;b&gt;world&lt;/b&gt;"<br/>
     * Designed for xhtml strings. Has an issue with non-xml tags such as &lt;br&gt;<br/>
     * as having and odd number will case an additional &lt;br&gt; to be added to the end of the string.
     * 
     * @param s
     * @return String with all opened tags closed
     */
    public static String closeAllTags(String s)
    {
        // first clean up any unclosed tag
        s = s.replaceAll("<[^>]*$", "");

        // token matches a word, tag, or special character
        Pattern token = Pattern.compile("<\\/?([^> ]+)[^>]*\\/?>");

        int charCount = 0;
        Stack openTags = new Stack();

        // Set the default for the max number of characters
        // (only counts characters outside of HTML tags)
        int maxChars = s.length();

        Matcher myMatcher = token.matcher(s);

        while ((charCount < maxChars) && (myMatcher.find())) {

            //Find the next tag
            String tag = myMatcher.group(1);

            // if this tag matches a closing tag, remove from stack
            if (!openTags.empty() && openTags.peek().toString().equals(tag)) openTags.pop();

            // else add to stack
            else openTags.push(tag);
        }

        // Close any tags which were left open
        while (!openTags.empty()) s += "</" + openTags.pop().toString() + ">";

        return s;
    }
}

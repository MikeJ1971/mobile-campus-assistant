/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca;

import com.hp.hpl.jena.shared.PrefixMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
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
    // For events, need to handle yyyy-mm-dd, yyyymmddThhmmss and yyyymmdd input formats

    public static Date parseDate(String date) throws ParseException {
        if (date.endsWith("Z")) {
            date = date.substring(0, date.length() - 1);
        }
        if (date.length() == 10) {
            date = date + "T00:00:00";
        }
        date = date.replaceAll("^(\\d{4})(\\d{2})(\\d{2})T(\\d{2})(\\d{2})(\\d{2})$", "$1-$2-$3T$4:$5:$6");
        date = date.replaceAll("^(\\d{4})(\\d{2})(\\d{2})$", "$1-$2-$3T00:00:00");
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
     * as having and odd number will case an additional &lt;br&gt; to be added to the
     * end of the string.
     *
     * @param s string to be parsed
     * @return String with all opened tags closed
     */
    public static String closeAllTags(String s) {
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

    public static PrefixMapping getCommonPrefixes() {

        PrefixMapping prefixMapping = PrefixMapping.Factory.create();
        prefixMapping.setNsPrefixes(PrefixMapping.Standard);
        prefixMapping.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        prefixMapping.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        prefixMapping.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
        prefixMapping.setNsPrefix("mca", "http://vocab.bris.ac.uk/mca/registry#");
        prefixMapping.setNsPrefix("mcageo", "http://vocab.bris.ac.uk/mca/geo#");
        prefixMapping.setNsPrefix("rss", "http://purl.org/rss/1.0/");
        prefixMapping.setNsPrefix("ical", "http://www.w3.org/2002/12/cal/ical#");
        return prefixMapping;
    }
}

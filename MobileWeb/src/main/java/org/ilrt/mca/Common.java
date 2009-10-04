package org.ilrt.mca;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        return new SimpleDateFormat(DATE_FORMAT_STRING_WITHOUT_TZ).parse(date);
    }

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static String DATE_FORMAT_STRING_WITHOUT_TZ = "yyyy-MM-dd'T'HH:mm:ss";

    public static String MCA_STUB = "mca://registry/";
    public static String TEMPLATE_STUB = "template://";
    public static String AUDIT_GRAPH_URI = "mca://audit/";
}

package org.ilrt.mca;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class Common {

    private Common() {
    }

    public static String parseDate(final Date date) {

        String temp = new SimpleDateFormat(DATE_FORMAT_STRING).format(date);

        return temp.substring(0, temp.length() - 2) + ":"
                + temp.substring(temp.length() - 2, temp.length());
    }

    public static Date parseDate(final String XsdDate) throws ParseException {

        String temp = XsdDate.substring(0, XsdDate.length() - 3)
                + XsdDate.substring(XsdDate.length() - 2, XsdDate.length());

        return new SimpleDateFormat(DATE_FORMAT_STRING).parse(temp);
    }

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String MCA_STUB = "mca://registry/";
    public static String TEMPLATE_STUB = "template://";
    public static String AUDIT_GRAPH_URI = "mca://audit/";
}

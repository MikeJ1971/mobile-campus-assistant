package org.ilrt.mca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class Common {

    public Common() {
    }

    public String loadSparql(String path) throws IOException {

        StringBuffer buffer = new StringBuffer();
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }

        return buffer.toString();
    }


    public static String parseDate(final Date date) {

        String temp = new SimpleDateFormat(DATE_FORMAT_STRING).format(date);

        return temp.substring(0, temp.length() - 2) + ":"
                + temp.substring(temp.length() - 2, temp.length());
    }

    public static Date parseDate(final String XsdDate) throws ParseException {

        String temp = XsdDate.substring(0, XsdDate.length() - 3)
                + XsdDate.substring(XsdDate.length() - 2, XsdDate.length());

        Date date = new SimpleDateFormat(DATE_FORMAT_STRING).parse(temp);

        return date;
    }

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String MCA_STUB = "mca://registry/";
    public static String TEMPLATE_STUB = "template://";
    public static String AUDIT_GRAPH_URI = "mca://audit/";
}

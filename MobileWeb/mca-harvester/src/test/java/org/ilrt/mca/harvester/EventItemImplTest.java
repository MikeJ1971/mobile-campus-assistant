/*
 *  © University of Bristol
 */

package org.ilrt.mca.harvester;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.ilrt.mca.harvester.events.EventItemImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventItemImplTest {

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    public EventItemImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Test
    public void testIsRecurring() throws ParseException {
        EventItemImpl item = new EventItemImpl();

        Date startdate = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2009-09-28T08:00:00Z");

        Date until = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2009-12-11T08:00:00Z");

        item.setId("2rds35n4nan737jahku8cgq3nc@google.com");
        item.setLabel("ASSL: Opening times");
        item.setDescription("Issue Desk Service:<P>\nMonday to Wednesday  -  8.45am - 8.00pm<br>\n\nThursday - 9.45am-8.00pm<br>\n\nFriday - 8.45am - 6.00pm <br>\n\nSaturday - 8.45 am - 6.00pm <br>\n\nSunday - No Issue Desk services<br>\n");
        item.setStartOfWeek("MO");
        item.setUntil(until);

        System.out.println(item.isRecurring());
        assertFalse(item.isRecurring());

        item.setFrequency("DAILY");
        assertTrue(item.isRecurring());

    }

    @Test
    public void testGetRecurringDates() throws ParseException {
        EventItemImpl item = new EventItemImpl();
        List<Date> recurringDates;
        
        Date startdate = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2009-09-28T08:00:00Z");

        Date until = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2009-12-11T08:00:00Z");

        item.setId("2rds35n4nan737jahku8cgq3nc@google.com");
        item.setLabel("ASSL: Opening times");
        item.setDescription("Issue Desk Service:<P>\nMonday to Wednesday  -  8.45am - 8.00pm<br>\n\nThursday - 9.45am-8.00pm<br>\n\nFriday - 8.45am - 6.00pm <br>\n\nSaturday - 8.45 am - 6.00pm <br>\n\nSunday - No Issue Desk services<br>\n");
        item.setStartDate(startdate);
        item.setStartOfWeek("MO");
        item.setFrequency("DAILY");
        item.setUntil(until);

        assertTrue(item.isRecurring());

        recurringDates = item.getRecurringDatesUntil(until);

        assertEquals("Number of generated dates don't match",74,recurringDates.size());

        // add a month restriction
        item.setByMonth("1,4,11");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",30,recurringDates.size());

        item.setByMonth("12,9");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",13,recurringDates.size());

        item.setByMonth("11");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",30,recurringDates.size());

        item.setByMonth("");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",74,recurringDates.size());

        System.out.println("Running monthly test");

        item.setFrequency("MONTHLY");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",2,recurringDates.size());

        startdate = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2009-04-28T08:00:00Z");
        until = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2010-12-11T08:00:00Z");
        item.setStartDate(startdate);
        item.setUntil(until);
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",19,recurringDates.size());

        item.setByMonth("11");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",2,recurringDates.size());

        System.out.println("Running count test");

        item.setFrequency("MONTHLY");
        item.setByMonth("");
        item.setCount(10);
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",10,recurringDates.size());

        item.setCount(25);
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",19,recurringDates.size());

        item.setCount(15);
        item.setByMonth("1,4,5,10");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",6,recurringDates.size());

        item.setCount(5);
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",5,recurringDates.size());

        item.setStartDate(new SimpleDateFormat(DATE_FORMAT_STRING).parse("2010-04-02T08:00:00Z"));
        item.setEndDate(new SimpleDateFormat(DATE_FORMAT_STRING).parse("2010-04-03T08:00:00Z"));
        item.setUntil(new SimpleDateFormat(DATE_FORMAT_STRING).parse("2010-08-06T08:00:00Z"));
        item.setCount(-1);
        item.setFrequency("WEEKLY");
        item.setByMonth("");

        until = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2009-12-11T13:51:11Z");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",0,recurringDates.size());

         until = new SimpleDateFormat(DATE_FORMAT_STRING).parse("2011-12-11T13:51:11Z");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",18,recurringDates.size());

        item.setByMonth("1,4,5,10");
        recurringDates = item.getRecurringDatesUntil(until);
        assertEquals("Number of generated dates don't match",8,recurringDates.size());

        item.setByMonth("4,7,10");
        recurringDates = item.getRecurringDatesUntil(until);
//        for (Date d : recurringDates) System.out.println(d);
        assertEquals("Number of generated dates don't match",9,recurringDates.size());
    }

}
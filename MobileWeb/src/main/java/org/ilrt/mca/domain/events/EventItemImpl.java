/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.ilrt.mca.Common;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventItemImpl extends BaseItem implements EventItem, Comparable<Item> {

    public EventItemImpl(){}

    Date startDate;
    Date endDate;
    String location;
    String organiser;
    String provenance;

    // Repreating events specificiation,
    // See http://tools.ietf.org/html/rfc2445
    enum FREQ {DAILY,WEEKLY,MONTHLY,YEARLY } // currently don't support SECONDLY,MINUTELY,HOURLY
    enum DAYS { MO, TU, WE, TH, FR, SA, SU }
    
    ArrayList <DAYS> _byDay = new ArrayList(); /* If present, this indicates the nth occurrence of the specific day within the MONTHLY or YEARLY RRULE. */
    ArrayList <Integer> _byMonth = new ArrayList(); /* The BYMONTH rule part specifies a COMMA character (US-ASCII decimal 44) separated list of months of the year. Valid values are 1 to 12. */
    int _count = -1; /* The COUNT rule part defines the number of occurrences at which to range-bound the recurrence */
    int _interval = 1;/* The INTERVAL rule part contains a positive integer representing how often the recurrence rule repeats */
    Date _until = null; /* The UNTIL rule part defines a date-time value which bounds the recurrence rule in an inclusive manner */
     DAYS _wkst; /* The WKST rule part specifies the day on which the workweek starts. */
     FREQ _freq = null; /* The FREQ rule part identifies the type of recurrence rule. This rule part MUST be specified in the recurrence rule. */
            

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    @Override
    public Date getStartDate()
    {
        return this.startDate;
    }


    public void setEndDate(Date date) {
        this.endDate = date;
    }

    @Override
    public Date getEndDate()
    {
        return this.endDate;
    }

    @Override
    /* Removing any newline characters shown in the description field */
    public void setDescription(String description)
    {
        description = description.replaceAll("\\\\n", "");
        description = Common.closeAllTags(description);
        super.setDescription(description);
    }

    public void setLocation(String s)
    {
        this.location = s;
    }

    @Override
    public String getLocation()
    {
        return this.location;
    }

    public void setOrganiser(String s)
    {
        this.organiser = s;
    }

    @Override
    public String getOrganiser()
    {
        return this.organiser;
    }

    @Override
    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    @Override
    public int compareTo(Item n) {
        if (n instanceof EventItem)
        {
            EventItem event = (EventItem)n;
            return this.getStartDate().compareTo(event.getStartDate());
        }
        return 0;
    }


    public void setCount(int i)
    {
        this._count = 1;
    }

    public void setInterval(int i)
    {
        this._interval = 1;
    }

    public void setUntil(Date date)
    {
        this._until = date;
    }


    public void setStartOfWeek(String wkst)
    {
        try
        {
            this._wkst = DAYS.valueOf(wkst);
        }
        catch (IllegalArgumentException iae)
        {
            iae.printStackTrace();
        }
    }

    public void setFrequency(String freq)
    {
        try
        {
            this._freq = FREQ.valueOf(freq);
        }
        catch (IllegalArgumentException iae)
        {
            iae.printStackTrace();
        }
    }

    public void setByDays(String days)
    {
        this._byDay = new ArrayList();
        String [] selectedDays = days.split(",");
        for(String s : selectedDays)
        {
            try
            {
                DAYS d = DAYS.valueOf(s);
                this._byDay.add(d);
            }
            catch (IllegalArgumentException iae)
            {
                iae.printStackTrace();
            }
        }

        Collections.sort(this._byDay);
    }

    public void setByMonth(String month)
    {
        this._byMonth = new ArrayList();
        String [] selectedMonths = month.split(",");
        for(String s : selectedMonths)
        {
            try
            {
                Integer i = Integer.parseInt(s);
                this._byMonth.add(i);
            }
            catch (NumberFormatException nfe)
            {
                nfe.printStackTrace();
            }
        }

        Collections.sort(this._byMonth);
    }

    public boolean repeatsForever()
    {
        if (_until == null && _count < 0)
        {
            return true;
        }

        return false;
    }

    public boolean isRecurring()
    {
        if (this._freq != null && (_until != null || _count > -1 || this._byDay.size() > 0 || this._byMonth.size() > 0))
        {
            return true;
        }

        return false;
    }

    public List<Date> getRecurringDatesUntil(Date endDate)
    {
        System.out.println("\n\n");
        ArrayList dates = new ArrayList();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        int datesToGenerate = this._count;
        Date nextDate = this.startDate;
        
        int pointer = 0;

        // setup default pointer position
        if (this._freq.equals(FREQ.DAILY) && this._byMonth.size() > 0)
        {
            int currMonth = cal.get(Calendar.MONTH) + 1;
            if (this._byMonth.contains(currMonth)) pointer =  this._byMonth.indexOf(currMonth);
            else
            {
                for (int i : this._byMonth)
                {
                    if (i <= currMonth)
                    {
                        pointer = this._byMonth.indexOf(i);
                    }
                }
            }
            if (this._byMonth.size() == 1) pointer = -1; // as this will automatically get incremented
        }



        // loop through calculating the next date
        while (datesToGenerate > 0 || (nextDate.before(endDate) || (this._until != null && nextDate.compareTo(this._until) < 1)))
        {
            cal.setTime(nextDate);
            switch (this._freq)
            {
                case DAILY:
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    System.out.println("Cal is " + cal.getTime());
                    if (this._byMonth.size() > 0)
                    {
                        int currentMonth = cal.get(Calendar.MONTH)+1;// starting from 0
                        if (!this._byMonth.contains(currentMonth))
                        {
                            // only allow days withing specified months, so in this case, reset
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                            System.out.println("Cal reset to " + cal.getTime());

                            // increament pointer
                            pointer = pointer + 1;
                            if (pointer >=  this._byMonth.size())
                            {
                                pointer = 0;
                                cal.add(Calendar.YEAR, 1);
                            }

                                int nextMonth = this._byMonth.get(pointer);
System.out.println("nextMonth is now " + nextMonth + " " + pointer);


                        // calculate next month
                            cal.set(Calendar.MONTH, nextMonth-1);
                            cal.set(Calendar.DAY_OF_MONTH, 1);

                            System.out.println("Cal now " + cal.getTime());
                        }
                    }
                    break;
            }

            nextDate = cal.getTime();
            if (datesToGenerate > 0) datesToGenerate--;

            if (datesToGenerate > 0 || (nextDate.before(endDate) || (this._until != null && nextDate.compareTo(this._until) < 1)))
            {
                dates.add(nextDate);
            }
        }
        
        return dates;
    }



}

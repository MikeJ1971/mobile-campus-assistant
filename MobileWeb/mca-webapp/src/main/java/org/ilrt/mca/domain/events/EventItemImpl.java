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
package org.ilrt.mca.domain.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.ilrt.mca.Common;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventItemImpl extends BaseItem implements EventItem, Comparable<Item> {
    Logger log = Logger.getLogger(EventItemImpl.class);

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

    public long getStartDateAsTimeStamp()
    {
        return this.startDate.getTime();
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
        if (description == null) return;
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
        this._count = i;
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
            log.info("Unable to convert " + freq + " into a valid frequency");
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
                log.info("Unable to convert " + days + " into a valid day of the week");
            }
        }

        Collections.sort(this._byDay);
    }

    public void setByMonth(String month)
    {
        this._byMonth = new ArrayList();
        if (month != null && month.length() > 0)
        {
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
                    log.info("Unable to convert " + s + " into a valid integer");
                }
            }

            Collections.sort(this._byMonth);
        }
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
        ArrayList dates = new ArrayList();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        int datesToGenerate = this._count;
        Date nextDate = this.startDate;
        
        int pointer = 0;

//System.out.println("Generating ("+datesToGenerate+") dates"+" freq:"+this._freq);
//System.out.println("until "+this._until);
//System.out.println("start:"+this.startDate);
//System.out.println("end "+endDate);
//System.out.print("_byMonth:");
//for (int i : this._byMonth)
//        {
//System.out.print(i+",");
//}
//System.out.print("\n_byDay:");
//for (DAYS i : this._byDay)
//        {
//System.out.print(i+",");
//}
//System.out.print("\n");
        if (!this._freq.equals(FREQ.DAILY) && !this._freq.equals(FREQ.WEEKLY) && !this._freq.equals(FREQ.MONTHLY))
        {
            log.warn("Unable to handle this frequency type - not implemented");
            return dates;
        }
        if (this._byDay.size() > 0)
        {
            log.warn("Unable to handle specific BYDAY restrictions - assuming all days of the week");
        }

        // setup default pointer position
        if ((this._freq.equals(FREQ.DAILY) || this._freq.equals(FREQ.MONTHLY)) && this._byMonth.size() > 0)
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
        while ((datesToGenerate == -1 || datesToGenerate > 0) && (nextDate.compareTo(endDate) <= 0) && (this._until != null && nextDate.compareTo(this._until) <= 0))
        {
            cal.setTime(nextDate);
            switch (this._freq)
            {
                case DAILY:
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    if (this._byMonth.size() > 0)
                    {
                        int currentMonth = cal.get(Calendar.MONTH)+1;// starting from 0
                        if (!this._byMonth.contains(currentMonth))
                        {
                            // only allow days withing specified months, so in this case, reset
                            cal.add(Calendar.DAY_OF_MONTH, -1);

                            // increament pointer
                            pointer = pointer + 1;
                            if (pointer >=  this._byMonth.size())
                            {
                                pointer = 0;
                                cal.add(Calendar.YEAR, 1);
                            }

                             int nextMonth = this._byMonth.get(pointer);

                            // calculate next month
                            cal.set(Calendar.MONTH, nextMonth-1);
                            cal.set(Calendar.DAY_OF_MONTH, 1);
                        }
                    }
                    break;
                case WEEKLY:
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    if (this._byMonth.size() > 0)
                    {
                        pointer = pointer + 1;

                        while (!isAllowed(cal.get(Calendar.MONTH)+1))
                        {
                            cal.add(Calendar.DAY_OF_MONTH, 7);
                        }
                    }
                    break;
                case MONTHLY:
                    if (this._byMonth.size() > 0)
                    {
                            // increament pointer
                            pointer = pointer + 1;
                            if (pointer >=  this._byMonth.size())
                            {
                                pointer = 0;
                                cal.add(Calendar.YEAR, 1);
                            }

                            int nextMonth = this._byMonth.get(pointer);

                            // calculate next month
                            cal.set(Calendar.MONTH, nextMonth-1);
                            // what happends if month over max allowed for this month? - bug to fix
                    }
                    else
                    {
                        cal.add(Calendar.MONTH, 1);
                    }
                    break;
            }

            nextDate = cal.getTime();

            if ((datesToGenerate == -1 || datesToGenerate > 0) && (nextDate.compareTo(endDate) <= 0) && (this._until != null && nextDate.compareTo(this._until) <= 0))
            {
                if (datesToGenerate > 0) datesToGenerate--;
                log.debug("Adding " + nextDate + " datesToGenerate " + datesToGenerate);
                dates.add(nextDate);
            }
        }

        log.info("Generated " + dates.size() + " new dates");
        return dates;
    }


    @Override
    public EventItemImpl clone()
    {
        EventItemImpl item = new EventItemImpl();
        item.setId(this.getId());
        item.setLabel(this.getLabel());
        item.setDescription(this.getDescription());
        item.setLocation(this.getLocation());
        item.setOrganiser(this.getOrganiser());
        item.setProvenance(this.getProvenance());
        item.setOrder(this.getOrder());
        item.setType(this.getType());
        item.setTemplate(this.getTemplate());
        item.setStartDate(this.getStartDate());
        item.setEndDate(this.getEndDate());

        return item;
    }

    /** Simply check if supplied integer is present in the _byMonth array **/
    private boolean isAllowed(int q)
    {
        for (int i : this._byMonth)
        {
            if (i == q) return true;
        }
        return false;
    }
}

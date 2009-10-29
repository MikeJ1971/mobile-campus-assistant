/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.ilrt.mca.domain.BaseItem;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventItemImpl extends BaseItem implements EventItem {

    public EventItemImpl(){}

    Date startDate;
    Date endDate;
    String location;
    String organiser;
    String provenance;

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
}

/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import java.util.Date;
import java.util.List;
import org.ilrt.mca.domain.Item;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public interface EventItem extends Item {

    Date getStartDate();

    Date getEndDate();

    String getLocation();

    String getOrganiser();

    String getProvenance();

    boolean isRecurring();

    List<Date> getRecurringDatesUntil(Date endDate);

    long getStartDateAsTimeStamp();
}

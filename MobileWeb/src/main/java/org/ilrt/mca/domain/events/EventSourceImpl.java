/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import org.ilrt.mca.domain.BaseItem;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventSourceImpl extends BaseItem implements EventSource {

    public EventSourceImpl(){}

    String htmlLocation;
    String icalLocation;

    public void setHTMLLink(String s) {
        this.htmlLocation = s;
    }

    public String getHTMLLink() {
        return this.htmlLocation;
    }


    public void setiCalLink(String s) {
        this.icalLocation = s;
    }

    public String getiCalLink() {
        return this.icalLocation;
    }
}

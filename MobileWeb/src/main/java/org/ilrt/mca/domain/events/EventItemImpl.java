/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import java.util.Date;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ilrt.mca.domain.BaseItem;
import org.ilrt.mca.domain.Item;

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

    @Override
    /* Removing any newline characters shown in the description field */
    public void setDescription(String description)
    {
        description = description.replaceAll("\\\\n", "");
        description = closeAllTags(description);
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

//    @Override
//    public int compareTo(Item item) {
//        return this.getStartDate().compareTo(((EventItem)item).getStartDate());
//    }
//
    protected static String closeAllTags(String s)
    {
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
}

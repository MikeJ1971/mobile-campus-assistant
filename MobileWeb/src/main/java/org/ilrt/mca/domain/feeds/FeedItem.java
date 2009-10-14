package org.ilrt.mca.domain.feeds;

import org.ilrt.mca.domain.Item;

import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface FeedItem extends Item {

    Date getDate();

    String getLink();

    String getProvenance();
}

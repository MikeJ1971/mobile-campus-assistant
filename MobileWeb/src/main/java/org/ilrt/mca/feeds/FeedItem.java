package org.ilrt.mca.feeds;

import org.ilrt.mca.domain.Item;

import java.util.Date;

public interface FeedItem extends Item {

    Date getDate();

    String getLink();
}

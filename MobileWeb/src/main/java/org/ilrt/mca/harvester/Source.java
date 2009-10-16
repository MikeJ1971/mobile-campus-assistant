package org.ilrt.mca.harvester;

import java.util.Date;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class Source {

    public Source() {
    }

    public Source(String url, Date lastVisited) {
        this.url = url;
        this.lastVisited = lastVisited;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(Date lastVisited) {
        this.lastVisited = lastVisited;
    }

    private String url;
    private Date lastVisited;
}


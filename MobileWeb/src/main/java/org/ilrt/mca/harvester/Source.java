package org.ilrt.mca.harvester;

import java.util.Date;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class Source {

    public Source(String url, String label, Date lastVisited) {
        this.url = url;
        this.label = label;
        this.lastVisited = lastVisited;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(Date lastVisited) {
        this.lastVisited = lastVisited;
    }

    private String url;
    private Date lastVisited;
    private String label;
}


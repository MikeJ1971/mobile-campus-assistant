package org.ilrt.mca.harvester.xml;

import org.ilrt.mca.harvester.Source;

import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class XmlSource extends Source {

    public XmlSource() {
    }

    public XmlSource(String url, String xsl, Date lastVisited) {
        super(url, lastVisited);
        this.xsl = xsl;
    }

    public String getXsl() {
        return xsl;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    private String xsl;
}

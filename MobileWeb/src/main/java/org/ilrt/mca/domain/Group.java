package org.ilrt.mca.domain;

public class Group {

    public Group(String label, int order) {
        this.label = label;
        this.order = order;
    }

    public String getLabel() {
        return label;
    }

    public int getOrder() {
        return order;
    }

    private String label;
    private int order;
}

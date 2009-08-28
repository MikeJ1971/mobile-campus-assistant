package org.ilrt.mca.domain;

import org.ilrt.mca.Common;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class BaseItem implements Item, Comparable<Item> {

    public BaseItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getOrder() {
        return order;
    }

    @Override
    public String getPath() {
        return id.substring(Common.MCA_STUB.length());
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BaseItem> getItems() {
        return items;
    }

    @Expose private String id;
    @Expose private String label;
    @Expose private Integer order;
    @Expose private String description;
    private String template;
    @Expose private String type;
    @Expose List<BaseItem> items = new ArrayList<BaseItem>();


    @Override
    public int compareTo(Item item) {

        System.out.println(item.getOrder() + " " + this.getOrder());

        return this.getOrder().compareTo(item.getOrder());
    }
}

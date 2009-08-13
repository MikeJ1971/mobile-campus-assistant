package org.ilrt.mca.domain;

import org.ilrt.mca.Common;

import java.util.List;
import java.util.ArrayList;

public class BaseItem implements Item {

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

    public String getOtherSource() {
        return otherSource;
    }

    public void setOtherSource(String otherSource) {
        this.otherSource = otherSource;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BaseItem> getItems() {
        return items;
    }

    private String id;
    private String label;
    private Integer order;
    private String description;
    private String template;
    private String type;
    private String otherSource;
    List<BaseItem> items = new ArrayList<BaseItem>();
}

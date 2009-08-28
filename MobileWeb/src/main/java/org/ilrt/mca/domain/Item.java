package org.ilrt.mca.domain;

import com.google.gson.annotations.Expose;

import java.util.List;


public interface Item {

    String getId();

    String getLabel();

    Integer getOrder();

    String getPath();

    String getDescription();

    String getTemplate();

    String getType();

    List<BaseItem> getItems();
}

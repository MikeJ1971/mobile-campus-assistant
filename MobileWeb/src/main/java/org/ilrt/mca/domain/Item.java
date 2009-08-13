package org.ilrt.mca.domain;

import java.util.List;


public interface Item {

    String getId();

    String getLabel();

    Integer getOrder();

    String getPath();

    String getDescription();

    String getTemplate();

    String getType();

    String getOtherSource();

    List<BaseItem> getItems();
}

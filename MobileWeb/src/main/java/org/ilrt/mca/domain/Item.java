package org.ilrt.mca.domain;

import java.util.List;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
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

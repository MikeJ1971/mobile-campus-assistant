package org.ilrt.mca.dao;

import org.ilrt.mca.domain.Item;

public interface ItemDao {

    Item findItem(String id);
}

package org.ilrt.mca.dao;

import org.ilrt.mca.domain.Item;

import java.util.List;

public interface ItemDao {

    Item findItem(String id);

    Item findHomePage();
}

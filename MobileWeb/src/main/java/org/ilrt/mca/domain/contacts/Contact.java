package org.ilrt.mca.domain.contacts;

import org.ilrt.mca.domain.Item;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public interface Contact extends Item {

    String getEmail();

    String getPhoneNumber();

    String getPhoneNumberLabel();

}

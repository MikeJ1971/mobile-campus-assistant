package org.ilrt.mca.domain.contacts;

import org.ilrt.mca.domain.BaseItem;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ContactImpl extends BaseItem implements Contact {

    public ContactImpl() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberLabel() {
        return phoneNumberLabel;
    }

    public void setPhoneNumberLabel(String phoneNumberLabel) {
        this.phoneNumberLabel = phoneNumberLabel;
    }

    private String email;

    private String phoneNumber;

    private String phoneNumberLabel;

}

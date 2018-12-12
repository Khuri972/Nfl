package com.Nflicks.model;

/**
 * Created by CRAFT BOX on 9/20/2017.
 */

public class ContactVO {

    public String getContactImage() {
        return ContactImage;
    }

    public void setContactImage(String contactImage) {
        ContactImage = contactImage;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    private String ContactImage;
    private String ContactName;
    private String ContactNumber;
}

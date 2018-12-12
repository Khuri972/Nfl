package com.Nflicks.model;

/**
 * Created by CRAFT BOX on 9/14/2017.
 */

public class NotificationModel {

    String id;
    String user_id;
    String ref_id;
    String ref_table;
    String notification_title;
    String notification_description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public String getRef_table() {
        return ref_table;
    }

    public void setRef_table(String ref_table) {
        this.ref_table = ref_table;
    }

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }

    public String getNotification_description() {
        return notification_description;
    }

    public void setNotification_description(String notification_description) {
        this.notification_description = notification_description;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification_extra() {
        return notification_extra;
    }

    public void setNotification_extra(String notification_extra) {
        this.notification_extra = notification_extra;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getImage_path_inside() {
        return image_path_inside;
    }

    public void setImage_path_inside(String image_path_inside) {
        this.image_path_inside = image_path_inside;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getAdate() {
        return adate;
    }

    public void setAdate(String adate) {
        this.adate = adate;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    String notification_type;
    String notification_extra;
    String image_path;
    String image_path_inside;
    String isDelete;
    String adate;
    String isExpired;
    String isActive;
    String expiry_date;

    public String getAdate_string() {
        return adate_string;
    }

    public void setAdate_string(String adate_string) {
        this.adate_string = adate_string;
    }

    String adate_string;

    public NotificationModel(String id, String user_id, String ref_id, String ref_table, String notification_title, String notification_description, String notification_type, String notification_extra, String image_path, String image_path_inside, String isDelete, String adate, String isExpired, String isActive, String expiry_date,String adate_string) {
        this.id = id;
        this.user_id = user_id;
        this.ref_id = ref_id;
        this.ref_table = ref_table;
        this.notification_title = notification_title;
        this.notification_description = notification_description;
        this.notification_type = notification_type;
        this.notification_extra = notification_extra;
        this.image_path = image_path;
        this.image_path_inside = image_path_inside;
        this.isDelete = isDelete;
        this.adate = adate;
        this.isExpired = isExpired;
        this.isActive = isActive;
        this.expiry_date = expiry_date;
        this.adate_string = adate_string;
    }
}

package com.Nflicks.model;

import java.io.Serializable;

/**
 * Created by CRAFT BOX on 8/17/2017.
 */

public class UserChannelListModel implements Serializable{

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    String id;
    String name;
    String image_path;

    public String getQr_code_path() {
        return qr_code_path;
    }

    public void setQr_code_path(String qr_code_path) {
        this.qr_code_path = qr_code_path;
    }

    String qr_code_path;

    public String getShareable_url() {
        return shareable_url;
    }

    public void setShareable_url(String shareable_url) {
        this.shareable_url = shareable_url;
    }

    String shareable_url;

    public int getUserActive() {
        return UserActive;
    }

    public void setUserActive(int userActive) {
        UserActive = userActive;
    }

    int UserActive;
    public String getChannel_privacy() {
        return channel_privacy;
    }

    public void setChannel_privacy(String channel_privacy) {
        this.channel_privacy = channel_privacy;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTarget_location() {
        return target_location;
    }

    public void setTarget_location(String target_location) {
        this.target_location = target_location;
    }

    public String getTarget_audience() {
        return target_audience;
    }

    public void setTarget_audience(String target_audience) {
        this.target_audience = target_audience;
    }

    String channel_privacy;
    String detail;
    String contact_no;
    String address;
    String email;
    String website;
    String target_location;
    String target_audience;

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    String follower;

    public UserChannelListModel()
    {

    }
}

package com.Nflicks.model;


import java.io.Serializable;

public class FollowingModel implements Serializable{

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getName() {
        return Name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    private String image_path;

    public void setName(String name) {
        Name = name;
    }

    private String Name;

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    private String follower;
    int count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    String details;

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

    public String getChannel_privacy() {
        return channel_privacy;
    }

    public void setChannel_privacy(String channel_privacy) {
        this.channel_privacy = channel_privacy;
    }

    String channel_privacy;

    public String getChannel_owner_id() {
        return channel_owner_id;
    }

    public void setChannel_owner_id(String channel_owner_id) {
        this.channel_owner_id = channel_owner_id;
    }

    String channel_owner_id;

    public int getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(int isFollowing) {
        this.isFollowing = isFollowing;
    }

    int isFollowing;

    public  FollowingModel()
    {

    }

    public FollowingModel(String name, String image_path,int isFollowing,int count) {
        Name = name;
        this.image_path = image_path;
        this.isFollowing = isFollowing;
        this.count=count;

    }
}


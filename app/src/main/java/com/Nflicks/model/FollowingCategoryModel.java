package com.Nflicks.model;


import java.io.Serializable;
import java.util.ArrayList;

public class FollowingCategoryModel implements Serializable{

    public int getmGravity() {
        return mGravity;
    }

    public void setmGravity(int mGravity) {
        this.mGravity = mGravity;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public ArrayList<FollowingModel> getFollowingModels() {
        return followingModels;
    }

    public void setFollowingModels(ArrayList<FollowingModel> followingModels) {
        this.followingModels = followingModels;
    }

    private int mGravity;
    private String mText;
    private ArrayList<FollowingModel> followingModels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getChannel_owner_id() {
        return channel_owner_id;
    }

    public void setChannel_owner_id(String channel_owner_id) {
        this.channel_owner_id = channel_owner_id;
    }

    String channel_owner_id;

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    String contact_no;

    public FollowingCategoryModel() {

    }

    /*public FollowingCategoryModel(int gravity, String text, ArrayList<FollowingModel> apps) {
        mGravity = gravity;
        mText = text;
        followingModels = apps;
    }*/

    public String getText(){
        return mText;
    }

    public int getGravity(){
        return mGravity;
    }

    public ArrayList<FollowingModel> getApps(){
        return followingModels;
    }

}

package com.Nflicks.model;

import java.io.Serializable;

/**
 * Created by CRAFT BOX on 12/26/2016.
 */

public class ChannelDescriptionModel implements Serializable{

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel_owner_id() {
        return channel_owner_id;
    }

    public void setChannel_owner_id(String channel_owner_id) {
        this.channel_owner_id = channel_owner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAdate() {
        return adate;
    }

    public void setAdate(String adate) {
        this.adate = adate;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getEntry_saved_flick_id() {
        return entry_saved_flick_id;
    }

    public void setEntry_saved_flick_id(String entry_saved_flick_id) {
        this.entry_saved_flick_id = entry_saved_flick_id;
    }

    String entry_saved_flick_id;
    String file_name;
    String file_path;
    String id;
    String channel_owner_id;
    String title;
    String detail;
    String tag;
    String image;
    String adate;

    public String getShareable_url() {
        return shareable_url;
    }

    public void setShareable_url(String shareable_url) {
        this.shareable_url = shareable_url;
    }

    String shareable_url;


    public int getIsBookmark() {
        return isBookmark;
    }

    public void setIsBookmark(int isBookmark) {
        this.isBookmark = isBookmark;
    }

    int  isBookmark;

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    String followers;


    /* todo channel detail */


    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_image_path() {
        return channel_image_path;
    }

    public void setChannel_image_path(String channel_image_path) {
        this.channel_image_path = channel_image_path;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    String channel_id;
    String channel_name;
    String channel_image_path;

    public String getCreated_date_string() {
        return created_date_string;
    }

    public void setCreated_date_string(String created_date_string) {
        this.created_date_string = created_date_string;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    String created_date_string;
    String created_date;
    String modified_date;

    public int getIsSpamed() {
        return isSpamed;
    }

    public void setIsSpamed(int isSpamed) {
        this.isSpamed = isSpamed;
    }

    public int getIsInAppropriate() {
        return isInAppropriate;
    }

    public void setIsInAppropriate(int isInAppropriate) {
        this.isInAppropriate = isInAppropriate;
    }

    int isSpamed;

    int isInAppropriate;



    /* todo notifaction */

    public String getC_title() {
        return c_title;
    }

    public void setC_title(String c_title) {
        this.c_title = c_title;
    }

    public String getC_detail() {
        return c_detail;
    }

    public void setC_detail(String c_detail) {
        this.c_detail = c_detail;
    }

    public String getC_tag() {
        return c_tag;
    }

    public void setC_tag(String c_tag) {
        this.c_tag = c_tag;
    }

    public String getC_image_path() {
        return c_image_path;
    }

    public void setC_image_path(String c_image_path) {
        this.c_image_path = c_image_path;
    }

    public String getC_follower() {
        return c_follower;
    }

    public void setC_follower(String c_follower) {
        this.c_follower = c_follower;
    }

    String c_title,c_detail,c_tag,c_image_path,c_follower;

    public String getQr_code_path() {
        return qr_code_path;
    }

    public void setQr_code_path(String qr_code_path) {
        this.qr_code_path = qr_code_path;
    }

    public String getSharable_path() {
        return sharable_path;
    }

    public void setSharable_path(String sharable_path) {
        this.sharable_path = sharable_path;
    }

    String qr_code_path;
    String sharable_path;

    public String getChannel_privacy() {
        return channel_privacy;
    }

    public void setChannel_privacy(String channel_privacy) {
        this.channel_privacy = channel_privacy;
    }

    String channel_privacy;

    public ChannelDescriptionModel()
    {

    }
}

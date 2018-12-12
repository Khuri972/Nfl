package com.Nflicks.model;

/**
 * Created by CRAFT BOX on 7/27/2017.
 */

public class CategoryModel {

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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    String id,name,image_path;
    boolean flag;

    public CategoryModel()
    {

    }
}

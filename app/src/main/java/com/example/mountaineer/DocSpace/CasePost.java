package com.example.mountaineer.DocSpace;

public class CasePost  {

    public String user_id, image_url, desc,title, image_thumb;
    public String timestamp;

    public CasePost() {}

    public CasePost(String user_id, String image_url, String desc, String title, String image_thumb, String timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.title = title;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle(){

        return  title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}


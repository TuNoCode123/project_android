package com.example.formular_cookie;

public class ShareItem {
    private String Name,img_url;
    private String recipe;

    public ShareItem(String name,  String img_url) {
        this.Name = name;
        this.img_url = img_url;
    }
    public String getName() {
        return Name;
    }
    public String getImg_url() {
        return img_url;
    }
}

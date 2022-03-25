package com.appnotification.notificationhistorylog;

public class Notif {

    private String title;
    private String decs;
    private String desc;

    /* public Notif(String title, String decs, String image) {
         this.title = title;
         this.decs = decs;
         this.image = image;
     }*/
    private String image;

    public Notif() {


    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecs() {
        return decs;
    }

    public void setDecs(String decs) {
        this.decs = decs;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}





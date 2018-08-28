package com.joeyzh.ui;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Joey on 2018/8/28.
 */

public class IMessage implements Serializable {

    private String title;
    private String decs;
    private String content;
    private String picUrl;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}

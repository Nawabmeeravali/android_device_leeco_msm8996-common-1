package com.ota.beta.updates;

public class Addon {
    private String mDesc;
    private String mDownloadLink;
    private int mFilesize;
    private int mId;
    private String mPublishedAt;
    private String mTitle;

    public void setTitle(String input) {
        this.mTitle = input;
    }

    public void setDesc(String input) {
        this.mDesc = input;
    }

    public void setPublishedAt(String input) {
        this.mPublishedAt = input;
    }

    public void setFilesize(int input) {
        this.mFilesize = input;
    }

    public void setDownloadLink(String input) {
        this.mDownloadLink = input;
    }

    public void setId(int input) {
        this.mId = input;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getDesc() {
        return this.mDesc;
    }

    public String getPublishedAt() {
        return this.mPublishedAt;
    }

    public String getDownloadLink() {
        return this.mDownloadLink;
    }

    public int getFilesize() {
        return this.mFilesize;
    }

    public int getId() {
        return this.mId;
    }
}

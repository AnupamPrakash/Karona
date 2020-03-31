package com.darkworld.karona;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    String name,userId,emailId,alias;
    Uri photoUrl;

    public User() {
    }

    public User(String name, String userId, String emailId, String alias, Uri photoUrl) {
        this.name = name;
        this.userId = userId;
        this.emailId = emailId;
        this.alias = alias;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }
}

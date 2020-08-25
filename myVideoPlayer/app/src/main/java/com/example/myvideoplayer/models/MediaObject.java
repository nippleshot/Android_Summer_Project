package com.example.myvideoplayer.models;

import java.io.Serializable;

public class MediaObject implements Serializable {

    private long id = -1;
    private String nickname = null;
    private String media_url = null;
    private String thumbnail = null;
    private String description = null;
    private int like_count = -1;
    private long played_until = -1;

    public MediaObject(long id, String nickname, String media_url, String thumbnail, String description, int like_count, long played_until) {
        this.id = id;
        this.nickname = nickname;
        this.media_url = media_url;
        this.thumbnail = thumbnail;
        this.description = description;
        this.like_count = like_count;
        this.played_until = played_until;
    }

    public MediaObject() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlayed_until() {
        return played_until;
    }

    public void setPlayed_until(long played_until) {
        this.played_until = played_until;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
}

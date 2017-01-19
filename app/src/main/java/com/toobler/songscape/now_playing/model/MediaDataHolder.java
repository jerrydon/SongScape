package com.toobler.songscape.now_playing.model;

import java.io.Serializable;

/**
 * Created by Jayaraj on 8/12/16.
 */
public class MediaDataHolder implements Serializable {

    String title,artist,songPath,displayName,songDuration;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongPath() {
        return this.songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSongDuration() {
        return this.songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }
}

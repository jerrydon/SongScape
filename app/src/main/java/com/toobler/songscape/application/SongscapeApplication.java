package com.toobler.songscape.application;

import android.app.Application;

/**
 * Created by Jayaraj on 8/24/16.
 */
public class SongscapeApplication extends Application {

    int repeatSongPosition;

    public int getRepeatSongPosition() {
        return this.repeatSongPosition;
    }

    public void setRepeatSongPosition(int repeatSongPosition) {
        this.repeatSongPosition = repeatSongPosition;
    }
}

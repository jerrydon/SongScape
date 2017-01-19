package com.toobler.songscape.circular_seekBar;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * Created by Jayaraj on 8/24/16.
 */
public class MediaPlayerSingleton {

    private static MediaPlayerSingleton seekBarSingletonClass;
    private static Context context;
    private static SeekBar seekBar;

    MediaPlayer  mMediaPlayer= new MediaPlayer();

    MediaPlayerSingleton() {

    }

    public static MediaPlayerSingleton getInstance() {
        if (seekBarSingletonClass == null) {
            seekBarSingletonClass = new MediaPlayerSingleton();
        }
        return seekBarSingletonClass;
    }

    public MediaPlayer mediaplayerObject() {
        return  mMediaPlayer;
    }
}

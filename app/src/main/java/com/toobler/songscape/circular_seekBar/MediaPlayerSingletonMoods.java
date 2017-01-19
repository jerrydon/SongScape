package com.toobler.songscape.circular_seekBar;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.SeekBar;

/**
 * Created by Jayaraj on 8/24/16.
 */
public class MediaPlayerSingletonMoods {

    private static MediaPlayerSingletonMoods seekBarSingletonClass;
    private static Context context;
    private static SeekBar seekBar;

    MediaPlayer  mMediaPlayer= new MediaPlayer();

    MediaPlayerSingletonMoods() {

    }

    public static MediaPlayerSingletonMoods getInstance() {
        if (seekBarSingletonClass == null) {
            seekBarSingletonClass = new MediaPlayerSingletonMoods();
        }
        return seekBarSingletonClass;
    }

    public MediaPlayer showSeekBar() {
        return  mMediaPlayer;
    }
}

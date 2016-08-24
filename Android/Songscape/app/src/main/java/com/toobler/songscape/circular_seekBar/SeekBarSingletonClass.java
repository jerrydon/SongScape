package com.toobler.songscape.circular_seekBar;

import android.content.Context;
import android.widget.SeekBar;

/**
 * Created by Jayaraj on 8/24/16.
 */
public class SeekBarSingletonClass {

    public static SeekBarSingletonClass seekBarSingletonClass;
    public static Context context;
    public static SeekBar seekBar;

    SeekBarSingletonClass(Context context, SeekBar seekBar) {
        this.context = context;
        this.seekBar = seekBar;
    }

    public static SeekBarSingletonClass getInstance() {
        if (seekBarSingletonClass == null) {
            seekBarSingletonClass = new SeekBarSingletonClass(context, seekBar);
        }
        return seekBarSingletonClass;
    }

    public void showSeekBar() {

    }
}

package com.toobler.songscape.Utilites;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.toobler.songscape.R;

/**
 * Created by Jayaraj on 8/19/16.
 */
public class Constants {
    /**
     * The constant intentDataSrc.
     */
    public static final String intentDataSrc = "dataSource";
    /**
     * The constant intentSongsTitle.
     */
    public static final String intentSongsTitle = "songTitle";

    /**
     * The constant SH_PREFFS.
     */
    public static String SH_PREFFS = "com.toobler.songscape.Utilites.sharedpreffrence";
    /**
     * The constant SH_PREFFS_ISSERVICE_START.
     */
    public static String SH_PREFFS_ISSERVICE_START = "serviceStarted";
    /**
     * The constant BRECIVER_SONG_CHANGE.
     */
    public static String BRECIVER_SONG_CHANGE = "songChange";
    /**
     * The constant CURRENT_SONG_POSITION.
     */
    public static String CURRENT_SONG_POSITION = "currentPosition";
    /**
     * The constant SONG_INDEX.
     */
    public static String SONG_INDEX = "position";
    /**
     * The constant IS_FIRST_LOADING.
     */
    public static String IS_FIRST_LOADING = "firstLoading";
    /**
     * The constant IS_SUFFLE_ON.
     */
    public static String IS_SUFFLE_ON = "suffleTheSong";
    /**
     * The constant IS_REPEAT_ON.
     */
    public static String IS_REPEAT_ON = "REPEATINGTHESONG";

    /**
     * The interface Action.
     */
    public interface ACTION {
        /**
         * The constant MAIN_ACTION.
         */
        public static String MAIN_ACTION = "com.marothiatechs.customnotification.action.main";
        /**
         * The constant INIT_ACTION.
         */
        public static String INIT_ACTION = "com.marothiatechs.customnotification.action.init";
        /**
         * The constant PREV_ACTION.
         */
        public static String PREV_ACTION = "com.marothiatechs.customnotification.action.prev";
        /**
         * The constant PLAY_ACTION.
         */
        public static String PLAY_ACTION = "com.marothiatechs.customnotification.action.play";
        /**
         * The constant NEXT_ACTION.
         */
        public static String NEXT_ACTION = "com.marothiatechs.customnotification.action.next";
        /**
         * The constant STARTFOREGROUND_ACTION.
         */
        public static String STARTFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.startforeground";
        /**
         * The constant STOPFOREGROUND_ACTION.
         */
        public static String STOPFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.stopforeground";

    }

    /**
     * The interface Notification id.
     */
    public interface NOTIFICATION_ID {
        /**
         * The constant FOREGROUND_SERVICE.
         */
        public static int FOREGROUND_SERVICE = 101;
    }

    /**
     * Gets default album art.
     *
     * @param context the context
     * @return the default album art
     */
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory
                .Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    /**
     * Gets user token.
     *
     * @param context the activity
     * @return the user token
     */
    public static SharedPreferences getSharedPreffs(Context context) {
        boolean userDetail = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SH_PREFFS, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    /**
     * Checking conditions boolean.
     *
     * @param context          the context
     * @param conditionToCheck the condition to check (if 0=isSuffle on/off,1=repeat on/off)
     * @return the boolean
     */
    public static boolean checkingConditions(Context context, int conditionToCheck) {
        boolean reutingStatus=false;
        switch (conditionToCheck) {
            case 0:
                reutingStatus = getSharedPreffs(context).getBoolean(IS_SUFFLE_ON, false);
                break;
            case 1:
                reutingStatus = getSharedPreffs(context).getBoolean(IS_REPEAT_ON, false);
                break;
        }
        return reutingStatus;
    }
}

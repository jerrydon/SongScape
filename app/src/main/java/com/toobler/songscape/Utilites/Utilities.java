package com.toobler.songscape.Utilites;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.toobler.songscape.R;
import com.toobler.songscape.now_playing.model.MediaDataHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jayaraj on 8/12/16.
 */
public class Utilities {

    public static void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction =fragment.getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public static void replaceFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction transaction =fragment.getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
         transaction.commit();
    }
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static ArrayList<MediaDataHolder> fetchingSongsFromDevice(Context activity) {
        ArrayList<MediaDataHolder> songsDetailsLis = new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        Cursor cursor = null;
        try {
            MediaDataHolder media = null;
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = activity.getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    media = new MediaDataHolder();
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName = cursor.getString(3);
                    String songDuration = cursor.getString(4);
                    String img = cursor.getString(5);
                    media.setTitle(title);
                    media.setArtist(artist);
                    media.setSongPath(path);
                    media.setDisplayName(displayName);
                    media.setSongDuration(songDuration);
                    songsDetailsLis.add(media);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        /**
         * Sorting the list alphabetically
         * */
        Collections.sort(songsDetailsLis, new Comparator<MediaDataHolder>() {
            public int compare(MediaDataHolder a, MediaDataHolder b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return songsDetailsLis;
    }
}

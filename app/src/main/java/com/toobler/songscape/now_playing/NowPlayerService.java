package com.toobler.songscape.now_playing;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.toobler.songscape.MainActivity;
import com.toobler.songscape.R;
import com.toobler.songscape.Utilites.Constants;
import com.toobler.songscape.Utilites.Utilities;
import com.toobler.songscape.now_playing.model.MediaDataHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Jayaraj on 8/29/16.
 */
public class NowPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    int songPosn;
    private MediaPlayer player = null;
    private MediaPlayer playerMoods = null;
    ArrayList<MediaDataHolder> songs;
    private String songTitle, artistName, albumName;
    private boolean shuffle = false;
    private final IBinder binder = new MusicBinder();
    private Random rand;
    private RemoteViews bigViews;
    private ImageView imvPausePlay;
    private TextView tvSongTitle;
    private int[] moodsSong;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onError callback called");
        songs = Utilities.fetchingSongsFromDevice(this);

        //initialize position
        songPosn = 0;
        rand = new Random();
        player = new MediaPlayer();
        playerMoods = new MediaPlayer();
        moodsSong = new int[]{R.raw.rain, R.raw.nature, R.raw.sea};
        playerMoods.setVolume(0, 0);
        playMoodSong(0);
        initMusicPlayer();
    }

    private void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

        playerMoods.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        playerMoods.setAudioStreamType(AudioManager.STREAM_MUSIC);
        playerMoods.setOnPreparedListener(this);
        playerMoods.setOnCompletionListener(this);
        playerMoods.setOnErrorListener(this);
    }

//    public void setList(ArrayList<MediaDataHolder> theSongs) {
//        songs = theSongs;
//    }

    /**
     * Getting pause/play image button to handle click from notification
     */
    public void setPausePlayBtn(ImageView imvPuasePlayBtn, TextView songTitle, NowPlayerService nowPlayerService) {
        this.imvPausePlay = imvPuasePlayBtn;
        this.tvSongTitle = songTitle;
        nowPlayerService = this;
    }

    public class MusicBinder extends Binder {
        NowPlayerService getService() {
            Log.d("MyService", "MusicBinder callback called");
            return NowPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent bReciver = new Intent();
        bReciver.setAction(Constants.BRECIVER_SONG_CHANGE);

        if (null != intent.getAction()) {
            if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                Log.i(LOG_TAG, "Clicked Previous");
                playPrev();
                bReciver.putExtra("title", songs.get(songPosn).getTitle());
                bReciver.putExtra("isPlaying", player.isPlaying());
                sendBroadcast(bReciver);
//                new NowPlayingActivity().nextOrPrevious(player.isPlaying(), tvSongTitle, getSongTitle());
            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                playNext();
//                new NowPlayingActivity().nextOrPrevious(player.isPlaying(), tvSongTitle, getSongTitle());
                Log.i(LOG_TAG, "Clicked Next");
                bReciver.putExtra("title", songs.get(songPosn).getTitle());
                sendBroadcast(bReciver);
            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                if (player.isPlaying()) {
                    stopForeground(true);
                    pausePlayer();
                    pauseMoodPlayer();
                    bigViews.setImageViewResource(R.id.status_bar_play, R.mipmap.ic_play);
                    new NowPlayingActivity().isPlayingOrPause(true, imvPausePlay);//Calling the interface for the changing the pause/play icon in activity
                } else {
                    stopForeground(false);
                    showNotification();
                    startSong();
                    startMoodSong();
                    bigViews.setImageViewResource(R.id.status_bar_play, R.mipmap.ic_pause);
                    new NowPlayingActivity().isPlayingOrPause(false, imvPausePlay);
                }
                Log.i(LOG_TAG, "Clicked Play");
            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyService", "onBind callback called");
        return binder;
    }

//    //release resources when unbind
//    @Override
//    public boolean onUnbind(Intent intent) {
//        player.stop();
//        player.release();
//        return false;
//    }

    public void playSong() {
        SharedPreferences.Editor shPref = Constants.getSharedPreffs(this).edit();
        shPref.putInt(Constants.SONG_INDEX, songPosn).apply();
        shPref.putInt(Constants.CURRENT_SONG_POSITION, player.getCurrentPosition()).apply();
        songTitle = songs.get(songPosn).getTitle();
        artistName = songs.get(songPosn).getArtist();
        String songsSrc = songs.get(songPosn).getSongPath();
        try {
            player.reset();
            player.setDataSource(this, Uri.parse(songsSrc));
            player.prepareAsync();
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    /**
     * Playing for the moods song
     */
    public void playMoodSong(int position) {
        try {
            playerMoods.reset();
            playerMoods.setDataSource(this, Uri.parse("android.resource://com.toobler.songscape/" + moodsSong[position]));
            playerMoods.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public int getSongPosn() {
        return songPosn;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
            mp.reset();
//            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("MyService", "onError callback called");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        showNotification();

    /*    Intent notIntent = new Intent(this, NowPlayingActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(1, not);*/
    }

    //playback methods
    public int getPosn() {

        return player.getCurrentPosition();
    }

    public String getSongTitle() {
        return songTitle;
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {

        player.pause();
    }

    public void pauseMoodPlayer() {
        playerMoods.pause();

    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void startSong() {

        player.start();
    }

    public void startMoodSong() {
        playerMoods.start();

    }

    public MediaPlayer moodsPlayer() {
        return playerMoods;

    }

    //skip to previous track
    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
    }

    /**
     * For playing the next song
     */
    public void playNext() {

        try {

            if (!Constants.checkingConditions(this, 1)) {
                //If repeat is of increment the song position else play the same
                songPosn++;
            }
            if (Constants.checkingConditions(this, 0)) {
                long seed = System.nanoTime();
                Collections.shuffle(songs, new Random(seed));
            }else{
                songs=Utilities.fetchingSongsFromDevice(this);
            }
            if (songPosn <= songs.size()) {
                playSong();

            } else {
                playerMoods.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    //skip to next
//    public void playNext() {
//        if (shuffle) {
//            int newSong = songPosn;
//            while (newSong == songPosn) {
//                newSong = rand.nextInt(songs.size());
//            }
//            songPosn = newSong;
//        } else {
//            songPosn++;
//            if (songPosn >= songs.size()) songPosn = 0;
//        }
//        playSong();
//    }

    //toggle shuffle
    public void setShuffle() {
        if (shuffle) shuffle = false;
        else shuffle = true;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        player.stop();
    }

    Notification status;
    private final String LOG_TAG = "NotificationService";

    public void showNotification() {
        // Using RemoteViews to bind custom layouts into Notification

        bigViews = new RemoteViews(getPackageName(), R.layout.notification_bar);
        // showing default album image
        bigViews.setImageViewBitmap(R.id.status_bar_album_art, Constants.getDefaultAlbumArt(this));
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, NowPlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, NowPlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, NowPlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent closeIntent = new Intent(this, NowPlayerService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        bigViews.setTextViewText(R.id.status_bar_track_name, getSongTitle());
        bigViews.setTextViewText(R.id.status_bar_artist_name, artistName);
//        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");


        status = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Songscape").build();

        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_launcher;
        status.contentIntent = pendingIntent;

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }
}

package com.toobler.songscape.now_playing;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.toobler.songscape.SongsListFragment;
import com.toobler.songscape.Utilites.Constants;
import com.toobler.songscape.now_playing.model.MediaDataHolder;
import com.toobler.songscape.R;
import com.toobler.songscape.Utilites.Utilities;
import com.toobler.songscape.circular_seekBar.CircularSeekBar;

import java.util.ArrayList;

/**
 * The type Now playing activity.
 */
public class NowPlayingActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaController.MediaPlayerControl, PausePlayListnerInterFace {
    /**
     * The Tv percentage.
     */
    TextView tvPercentage;
    private int positionOfSong, moodsPagerPosition;
    /**
     * The Moods volume.
     */
    int moodsVolume = 0;
    private Handler mHandler = new Handler();
    private MediaPlayer media;
    private SeekBar musicSeekBar;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageView btnPlay, imvMoods, btnNext, btnPrevious, imvSuffle, imvRepeat;
    //    private MediaPlayer mediaForMoods = null;
    private String songName1, dataSorce;
    private CircularSeekBar seekbar;
    private TextView tvMixPercentage, tvSongsName;
    private TextView tvMixPlaceHolder, tvCompletedDuration, tvTotalDuration;
    private ArrayList<MediaDataHolder> mediaList;

    private ViewPager viewpage;
    private boolean isRepeat = false;
    private boolean isSuffle = false;
    private NowPlayerService musicSrv;
    private boolean serviceBond = false;
    Intent startServiceIntent;
    private boolean /*paused = false,*/ playbackPaused = false;
    private MusicController controller;
    private boolean isFromNotifintent = false;
    public static NowPlayingActivity nowPlayingActivity;
    private BroadcastReceiver bReciver;

    @Override
    public void onCompletion(MediaPlayer mp) {
        //If the song completes will play the next
//        forchangingNextSong();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            gettingDataThrougIntent(data);
            songPicked(positionOfSong);
            tvSongsName.setText(musicSrv.getSongTitle());
            if (mediaPlayer.isPlaying()) {
                btnPlay.setImageResource(R.mipmap.ic_pause);
            }
        }
    }

    /**
     * For playing the next song
     */
//    private void forchangingNextSong() {
//
//        try {
//
//            if (!isRepeat) {
//                //If suffle is of increment the song position else play the same
//                positionOfSong++;
//            }
//            if (positionOfSong <= mediaList.size()) {
//                songName = mediaList.get(positionOfSong).getTitle();
//                dataSorce = mediaList.get(positionOfSong).getSongPath();
//                tvSongsName.setText(songName);
//                playSong(dataSorce);
//
//                if (mediaPlayer.isPlayingOrPause()) {
//                    btnPlay.setImageResource(R.mipmap.ic_pause);
//                } else {
//                    btnPlay.setImageResource(R.mipmap.ic_play);
//                }
//            } else {
//                mediaForMoods.pause();
//                btnPlay.setImageResource(R.mipmap.ic_play);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * For playing the previous song
     */
//    private void forchangingPreviousSong() {
//        positionOfSong--;
//        songName = mediaList.get(positionOfSong).getTitle();
//        dataSorce = mediaList.get(positionOfSong).getSongPath();
//        tvSongsName.setText(songName);
////        playSong(dataSorce);
//        if (mediaPlayer.isPlayingOrPause()) {
//            btnPlay.setImageResource(R.mipmap.ic_pause);
//        } else {
//            btnPlay.setImageResource(R.mipmap.ic_play);
//        }
//    }


    /**
     * checking if the paused from the notification bar
     */
    @Override
    public void isPlayingOrPause(boolean playing, ImageView imageView) {
        if (playing && null != imageView) {
            imageView.setImageResource(R.mipmap.ic_play);
        } else if (null != imageView) {
            imageView.setImageResource(R.mipmap.ic_pause);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
//        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_activity);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("SONGSCAPE");
        Intent intent = getIntent();
        gettingDataThrougIntent(intent);
        intiateUI();
        btnPlay.setImageResource(R.mipmap.ic_pause);
        imvMoods.setImageResource(R.mipmap.ic_rain);
        nowPlayingActivity = this;


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BRECIVER_SONG_CHANGE);
        bReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = "";
                boolean isPlaying = true;
                if (intent.hasExtra("title")) {
                    title = intent.getStringExtra("title");
                    isPlaying = intent.getBooleanExtra("isPlaying", false);
                }
                tvSongsName.setText(intent.getStringExtra("title"));
                if (isPlaying) {
                    btnPlay.setImageResource(R.mipmap.ic_play);
                } else {
                    btnPlay.setImageResource(R.mipmap.ic_pause);
                }
            }
        };
        registerReceiver(bReciver, intentFilter);

//        if ((mediaPlayer != null && mediaForMoods != null) && (mediaPlayer.isPlayingOrPause() && mediaForMoods.isPlayingOrPause())) {
//            //For stoping the song on new song selection
//            mediaPlayer.release();
//            mediaForMoods.release();
//        }
        /**
         * Playing the background moods and songs
         **/
//        songPicked(positionOfSong);
//        mediaForMoods = new MediaPlayer();
        moodsList(viewpage);
//        playSong(dataSorce);
//        playMoods(R.raw.rain);
//        mediaForMoods.setVolume(0, 0);//Setting the volume mute for the moods during the first loading
        seekbar.setOnSeekBarChangeListener(new SeekBarImplementation());
        mediaPlayer.setOnCompletionListener(NowPlayingActivity.this);
        imvRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    imvRepeat.setImageResource(R.mipmap.ic_repeat);
                    isRepeat = false;
                } else {
                    imvRepeat.setImageResource(R.mipmap.ic_repeat_red);
                    isRepeat = true;
                }
//                ((SongscapeApplication) getApplication()).setRepeatSongPosition(positionOfSong);
            }
        });
        imvSuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.setShuffle();
                if (isSuffle) {
                    imvSuffle.setImageResource(R.mipmap.ic_suffle_white);
                    isSuffle = false;
                } else {
                    imvSuffle.setImageResource(R.mipmap.ic_suffle_red);
                    isSuffle = true;
                }

//                if (isSuffle) {
//                    imvSuffle.setImageResource(R.mipmap.ic_suffle_white);
//                    isSuffle = false;
//                } else {
//                    long seed = System.nanoTime();
//                    Collections.shuffle(mediaList, new Random(seed));
//                    imvSuffle.setImageResource(R.mipmap.ic_suffle_red);
//                    isSuffle = true;
//                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playNext();
                tvSongsName.setText(musicSrv.getSongTitle());
                btnPlay.setImageResource(R.mipmap.ic_pause);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playPrev();
                tvSongsName.setText(musicSrv.getSongTitle());
                btnPlay.setImageResource(R.mipmap.ic_pause);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mediaPlayer.isPlayingOrPause() && mediaForMoods.isPlayingOrPause()) {
//                    mediaPlayer.pause();
//                    mediaForMoods.pause();
//                    btnPlay.setImageResource(R.mipmap.ic_play);
//                } else if (mediaPlayer.isPlayingOrPause()) {
//                    mediaPlayer.pause();
//                    btnPlay.setImageResource(R.mipmap.ic_play);
//                } else {
//                    btnPlay.setImageResource(R.mipmap.ic_pause);
//                    mediaPlayer.start();
////                    mediaForMoods.start();
//                }
                if (playbackPaused) {
                    start();
                    btnPlay.setImageResource(R.mipmap.ic_pause);
                    musicSrv.showNotification();

                } else {
                    btnPlay.setImageResource(R.mipmap.ic_play);
                    pause();

//                    mediaForMoods.start();
                }


            }
        });
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
//                int totalDuration = mediaPlayer.getDuration();
                int totalDuration = musicSrv.getDur();
//                int currentPosition=musicSrv.getPosn();
                int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);
                musicSrv.seek(currentPosition);

                updateProgressBar();
            }
        });
        this.tvPercentage = tvMixPercentage;


        viewpage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                moodsPagerPosition = position;//setting moods pager position for scroll on button click

                switch (position) {
                    case 0:
                        musicSrv.playMoodSong(0);
                        imvMoods.setImageResource(R.mipmap.ic_rain);

                        break;
                    case 1:
                        musicSrv.playMoodSong(1);
                        imvMoods.setImageResource(R.mipmap.ic_forest);
                        break;
                    case 2:
                        musicSrv.playMoodSong(2);
                        imvMoods.setImageResource(R.mipmap.ic_sea);

                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * connect to the service
     */
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NowPlayerService.MusicBinder binder = (NowPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
//            musicSrv.setList(mediaList);
            musicSrv.setPausePlayBtn(btnPlay, tvSongsName, musicSrv);
            serviceBond = true;
            songPicked(positionOfSong);
            Log.d("Service_checker", "Connected to service");
            tvSongsName.setText(musicSrv.getSongTitle());
            updateProgressBar();
            musicSeekBar.setProgress(0);
            musicSeekBar.setMax(100);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBond = false;
            Log.d("Service_checker", "Failed to Connected to service");
        }
    };


    /**
     * connect to the service if instance is killed
     */
    private ServiceConnection musicConnectionIfInstanceKilled = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NowPlayerService.MusicBinder binder = (NowPlayerService.MusicBinder) service;
            musicSrv = binder.getService();
            serviceBond = true;
            updateProgressBar();
            musicSeekBar.setProgress(0);
            musicSeekBar.setMax(100);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBond = false;
            Log.d("Service_checker", "Failed to Connected to service");
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bReciver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Overrinding onstart to start the Service in it
     */
    @Override
    protected void onStart() {
        super.onStart();
         SharedPreferences shPref=Constants.getSharedPreffs(this);
        startServiceIntent = new Intent(this, NowPlayerService.class);
        if (shPref.getBoolean(Constants.SH_PREFFS_ISSERVICE_START,false)) {
            shPref.edit().putBoolean(Constants.SH_PREFFS_ISSERVICE_START, false).apply();
            bindService(startServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(startServiceIntent);
        } else {
            bindService(startServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
        /**setting song name after starting the service */
            /*else {
            tvSongsName.setText(musicSrv.getSongTitle());
            updateProgressBar();
            musicSeekBar.setProgress(0);
            musicSeekBar.setMax(100);
            }*/
    }

    //user song select
    public void songPicked(int position) {
        musicSrv.setSong(position);
        musicSrv.playSong();
    }

    /**
     * Getting Datas From throght thee intent
     */
    void gettingDataThrougIntent(Intent intent) {
        positionOfSong = intent.getIntExtra("position", 0);
        mediaList = Utilities.fetchingSongsFromDevice(this);
        dataSorce = mediaList.get(positionOfSong).getSongPath();
//        songName = mediaList.get(positionOfSong).getTitle();
    }

    /**
     * initilizing UI componants
     */
    void intiateUI() {
        viewpage = (ViewPager) findViewById(R.id.pgr_moods);
        tvMixPercentage = (TextView) findViewById(R.id.tv_mix_percentage);
        tvSongsName = (TextView) findViewById(R.id.tv_song_name);
        tvMixPlaceHolder = (TextView) findViewById(R.id.tv_mix_place_holder);
        tvCompletedDuration = (TextView) findViewById(R.id.tv_current_position);
        tvTotalDuration = (TextView) findViewById(R.id.tv_total_position);
        seekbar = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        musicSeekBar = (SeekBar) findViewById(R.id.music_seek);
        btnPlay = (ImageView) findViewById(R.id.btn_play);
        imvMoods = (ImageView) findViewById(R.id.imv_mood_imgs);
        btnNext = (ImageView) findViewById(R.id.btn_next);
        btnPrevious = (ImageView) findViewById(R.id.btn_previous);
        btnPrevious = (ImageView) findViewById(R.id.btn_previous);
        imvSuffle = (ImageView) findViewById(R.id.imv_suffle);
        imvRepeat = (ImageView) findViewById(R.id.imv_repeat);
    }

    /**
     * Setting up adapter for moods list
     */
    private void moodsList(ViewPager viewPager) {
        final String array[] = getResources().getStringArray(R.array.moods);
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "RAIN";
                    case 1:
                        return "FOREST";
                    case 2:
                        return "SEA";
                    default:
                        return "";
                }
            }

            @Override
            public int getCount() {
                return array.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.pager_item_moods_list, null);
                TextView tvMoods = (TextView) layout.findViewById(R.id.tv_moods);
                tvMoods.setText(array[position]);
                container.addView(layout);
                return layout;
            }


            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView((View) arg2);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((View) object);
            }
        };
        viewPager.setAdapter(adapter);
    }


    /**
     * Function to play a song from local
     *
     * @param fileSrc the file src
     */
//    public void playSong(String fileSrc) {
//////            if (mediaPlayer != null && mediaPlayer.isPlayingOrPause()) {
//////                mediaPlayer.stop();
//////                mediaPlayer.release();
//////                mediaPlayer = null;
//////            }
//        //           mediaPlayer= MediaPlayerSingleton.getInstance().mediaplayerObject(NowPlayingActivity.this,fileSrc);
////            mediaPlayer.setOnCompletionListener(NowPlayingActivity.this);
//////            mediaPlayer.reset();
////       /*     mediaPlayer.setDataSource(NowPlayingActivity.this, Uri.parse(fileSrc));
////            mediaPlayer.prepare();
////            mediaPlayer.start();*/
//        try {
//            mediaPlayer = MediaPlayerSingleton.getInstance().mediaplayerObject();
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(NowPlayingActivity.this, Uri.parse(fileSrc));
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            musicSeekBar.setProgress(0);
//            musicSeekBar.setMax(100);
//            /**Updating progress bar*/
//            updateProgressBar();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = musicSrv.getDur();
            int currentDuration = musicSrv.getPosn();
            // Displaying Total Duration time
            tvTotalDuration.setText("" + Utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            tvCompletedDuration.setText("" + Utilities.milliSecondsToTimer(currentDuration));
            // Updating progress bar
            int progress = (int) (Utilities.getProgressPercentage(currentDuration, totalDuration));
//            Log.d("Progress", "" + progress);
            musicSeekBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     * Implimenting the seekbar for the moods volume
     */
    public class SeekBarImplementation implements CircularSeekBar.OnCircularSeekBarChangeListener {
        int maxVolume = 50;
        Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);

        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            moodsVolume = progress;
            tvMixPercentage.setVisibility(View.VISIBLE);
            tvMixPlaceHolder.setVisibility(View.VISIBLE);
            tvMixPercentage.setText(progress * 2 + "%");
            float log1 = (float) (Math.log(maxVolume - moodsVolume) / Math.log(maxVolume));
            musicSrv.moodsPlayer().setVolume(1 - log1, 1 - log1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvMixPercentage.setVisibility(View.GONE);
                    tvMixPlaceHolder.setVisibility(View.GONE);
//                    tvMixPercentage.startAnimation(bottomUp);
//                    tvMixPlaceHolder.startAnimation(bottomUp);
                }
            }, 5000);

        }

        @Override
        public void onStopTrackingTouch(CircularSeekBar seekBar) {


        }

        @Override

        public void onStartTrackingTouch(CircularSeekBar seekBar) {


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.songs_list:
                Intent i = new Intent(NowPlayingActivity.this, SongsListFragment.class);
                i.putExtra("isFromNowPayin", true);
                startActivityForResult(i, 100);
                return true;

            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * for checking wheather the service is runing or not
     */
    private boolean isServiceRunning() {
        boolean isRuning = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.toobler.songscape.now_playing.NowPlayerService".equals(service.service.getClassName())) {
                isRuning = true;
            } else {
                bindService(startServiceIntent, musicConnectionIfInstanceKilled, Context.BIND_AUTO_CREATE);
                isRuning = false;
            }
        }
        return isRuning;
    }

    private void playNext() {
        musicSrv.playNext();
        tvSongsName.setText(musicSrv.getSongTitle());
        if (playbackPaused) {
            playbackPaused = false;
        }
//        controller.show(0);
    }

    private void playPrev() {
        musicSrv.playPrev();
        tvSongsName.setText(musicSrv.getSongTitle());
        if (playbackPaused) {
            playbackPaused = false;
        }
//        controller.show(0);
    }


    @Override
    public void start() {
        musicSrv.startSong();
        musicSrv.startMoodSong();
        tvSongsName.setText(musicSrv.getSongTitle());
        playbackPaused = false;
    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();
        musicSrv.pauseMoodPlayer();
        playbackPaused = true;

    }

    @Override
    public int getDuration() {
        if (musicSrv != null && serviceBond && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && serviceBond && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);

    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && serviceBond)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


}

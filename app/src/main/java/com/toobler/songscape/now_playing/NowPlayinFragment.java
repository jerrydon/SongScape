package com.toobler.songscape.now_playing;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.toobler.songscape.R;
import com.toobler.songscape.Utilites.Constants;
import com.toobler.songscape.Utilites.Utilities;
import com.toobler.songscape.circular_seekBar.CircularSeekBar;
import com.toobler.songscape.now_playing.model.MediaDataHolder;

import java.util.ArrayList;

/**
 * Created by Jayaraj on 9/5/16.
 */
public class NowPlayinFragment extends Fragment implements MediaPlayer.OnCompletionListener,
        MediaController.MediaPlayerControl, PausePlayListnerInterFace {
    /**
     * The Tv percentage.
     */
    TextView tvPercentage;
    private int positionOfSong=0, moodsPagerPosition;
    /**
     * The Moods volume.
     */
    int moodsVolume = 0;
    private Handler mHandler = new Handler();
    private MediaPlayer media;
    private SeekBar musicSeekBar;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageView btnPlay, imvMoods, btnNext, btnPrevious, imvSuffle, imvRepeat;
     private String songName, dataSorce;
    private Activity activity;
    private CircularSeekBar seekbar;
    private TextView tvMixPercentage, tvSongsName;
    private TextView tvMixPlaceHolder, tvCompletedDuration, tvTotalDuration;
//    private ArrayList<MediaDataHolder> mediaList;

    private ViewPager viewpage;
    private boolean isRepeat = false;
    private boolean isSuffle = false;
    private NowPlayerService musicSrv;
    private boolean serviceBond = false;
    Intent startServiceIntent;
    private boolean /*paused = false,*/ playbackPaused = false;
    private MusicController controller;
//    private boolean isFromNotifintent = false;
    private BroadcastReceiver bReciver;

    @Override
    public void onCompletion(MediaPlayer mp) {
        //If the song completes will play the next
         tvSongsName.setText(musicSrv.getSongTitle());
    }


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
    /**
     * for gettign the selection from the songslist
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_now_playing, container, false);
        activity = getActivity();
//        mediaList = Utilities.fetchingSongsFromDevice(activity);
        intiateUI(view);
        positionOfSong=getArguments().getInt("position");
        btnPlay.setImageResource(R.mipmap.ic_pause);
        imvMoods.setImageResource(R.mipmap.ic_rain);
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
        activity.registerReceiver(bReciver, intentFilter);
        moodsList(viewpage);
        seekbar.setOnSeekBarChangeListener(new SeekBarImplementation());
        mediaPlayer.setOnCompletionListener(this);
        imvRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    imvRepeat.setImageResource(R.mipmap.ic_repeat);
                    isRepeat = false;
                    Constants.getSharedPreffs(activity).edit().putBoolean(Constants.IS_REPEAT_ON,false).apply();

                } else {
                    imvRepeat.setImageResource(R.mipmap.ic_repeat_red);
                    isRepeat = true;
                    Constants.getSharedPreffs(activity).edit().putBoolean(Constants.IS_REPEAT_ON,true).apply();
                }
            }
        });


        imvSuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.setShuffle();
                if (isSuffle) {
                    imvSuffle.setImageResource(R.mipmap.ic_suffle_white);
                    isSuffle = false;
                    Constants.getSharedPreffs(activity).edit().putBoolean(Constants.IS_SUFFLE_ON,false).apply();
                } else {
                    imvSuffle.setImageResource(R.mipmap.ic_suffle_red);
                    isSuffle = true;
                    Constants.getSharedPreffs(activity).edit().putBoolean(Constants.IS_SUFFLE_ON,true).apply();
                }
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
        listners();
        int currentPosition=Constants.getSharedPreffs(activity).getInt("currentPosition",0);
        musicSeekBar.setProgress(currentPosition);
        startService();
        return view;
    }

    void listners() {

        /**Seekbar for the music */

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


    //user song select
    public void songPicked(int position) {
        musicSrv.setSong(position);
        musicSrv.playSong();
    }


    /**
     * initilizing UI componants
     */
    void intiateUI(View view) {
        viewpage = (ViewPager) view.findViewById(R.id.pgr_moods);
        tvMixPercentage = (TextView) view.findViewById(R.id.tv_mix_percentage);
        tvSongsName = (TextView) view.findViewById(R.id.tv_song_name);
        tvMixPlaceHolder = (TextView) view.findViewById(R.id.tv_mix_place_holder);
        tvCompletedDuration = (TextView) view.findViewById(R.id.tv_current_position);
        tvTotalDuration = (TextView) view.findViewById(R.id.tv_total_position);
        seekbar = (CircularSeekBar) view.findViewById(R.id.circularSeekBar1);
        musicSeekBar = (SeekBar) view.findViewById(R.id.music_seek);
        btnPlay = (ImageView) view.findViewById(R.id.btn_play);
        imvMoods = (ImageView) view.findViewById(R.id.imv_mood_imgs);
        btnNext = (ImageView) view.findViewById(R.id.btn_next);
        btnPrevious = (ImageView) view.findViewById(R.id.btn_previous);
        btnPrevious = (ImageView) view.findViewById(R.id.btn_previous);
        imvSuffle = (ImageView) view.findViewById(R.id.imv_suffle);
        imvRepeat = (ImageView) view.findViewById(R.id.imv_repeat);
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
            Log.d("Progress", "" + progress);
            musicSeekBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroyView();
        try {
            activity.unregisterReceiver(bReciver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }
private void startService(){
    SharedPreferences sharedPreferences = Constants.getSharedPreffs(activity);
    startServiceIntent = new Intent(activity, NowPlayerService.class);
    if (sharedPreferences.getBoolean(Constants.SH_PREFFS_ISSERVICE_START,false)) {
        sharedPreferences.edit().putBoolean(Constants.SH_PREFFS_ISSERVICE_START, false).apply();
        activity.bindService(startServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        activity.startService(startServiceIntent);
    } else {
        activity.bindService(startServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
    }
}

    /**
     * Implimenting the seekbar for the moods volume
     */
    public class SeekBarImplementation implements CircularSeekBar.OnCircularSeekBarChangeListener {
        int maxVolume = 50;
        Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.zoom_in);

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


    /**
     * for checking wheather the service is runing or not
     */
    private boolean isServiceRunning() {
        boolean isRuning = false;
        ActivityManager manager = (ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.toobler.songscape.now_playing.NowPlayerService".equals(service.service.getClassName())) {
                isRuning = true;
            } else {
                activity.bindService(startServiceIntent, musicConnectionIfInstanceKilled, Context.BIND_AUTO_CREATE);
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

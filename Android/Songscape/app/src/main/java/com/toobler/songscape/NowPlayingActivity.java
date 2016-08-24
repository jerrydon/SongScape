package com.toobler.songscape;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.toobler.songscape.Utilites.Constants;
import com.toobler.songscape.Utilites.ListnerToFinishActiForNewSong;
import com.toobler.songscape.Utilites.Utilities;
import com.toobler.songscape.circular_seekBar.CircularSeekBar;

import java.io.IOException;
import java.util.ArrayList;


/**
 * The type Now playing activity.
 */
public class NowPlayingActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
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
    private MediaPlayer mediaPlayer;
    private ImageView btnPlay, imvMoods, btnNext, btnPrevious, imvBtnRightArrow, imvBtnLeftArraow;
    private MediaPlayer mediaForMoods = null;
    private String songName, dataSorce;
    private CircularSeekBar seekbar;
    private TextView tvMixPercentage, tvSongsName;
    private TextView tvMixPlaceHolder, tvCompletedDuration, tvTotalDuration;
    private ArrayList<MediaDataHolder> mediaList;
    private int[] moodsSong;
    private ViewPager viewpage;

    @Override
    public void onCompletion(MediaPlayer mp) {
//If the song completes will play the next
        forchangingNextSong();
    }

    /**
     * For playing the next song
     */
    private void forchangingNextSong() {
        try {
            positionOfSong++;
            if (positionOfSong <= mediaList.size()) {
                songName = mediaList.get(positionOfSong).getTitle();
                dataSorce = mediaList.get(positionOfSong).getSongPath();
                tvSongsName.setText(songName);
                playSong(dataSorce);

                if (mediaPlayer.isPlaying()) {
                    btnPlay.setImageResource(R.mipmap.ic_pause);
                } else {
                    btnPlay.setImageResource(R.mipmap.ic_play);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For playing the previous song
     */
    private void forchangingPreviousSong() {
        positionOfSong--;
        songName = mediaList.get(positionOfSong).getTitle();
        dataSorce = mediaList.get(positionOfSong).getSongPath();
        tvSongsName.setText(songName);
        playSong(dataSorce);
        if (mediaPlayer.isPlaying()) {
            btnPlay.setImageResource(R.mipmap.ic_pause);
        } else {
            btnPlay.setImageResource(R.mipmap.ic_play);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);

        gettingDataThrougIntent();
        intiateUI();

        tvSongsName.setText(songName);
        moodsSong = new int[]{R.raw.rain, R.raw.nature, R.raw.sea};
        if ((mediaPlayer != null && mediaForMoods != null) && (mediaPlayer.isPlaying() && mediaForMoods.isPlaying())) {
            //For stoping the song on new song selection
            mediaPlayer.release();
            mediaForMoods.release();
        }
        btnPlay.setImageResource(R.mipmap.ic_pause);
        imvMoods.setImageResource(R.mipmap.ic_rain);
        imvBtnLeftArraow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpage.setCurrentItem(moodsPagerPosition--, true);
            }
        });
        imvBtnRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpage.setCurrentItem(moodsPagerPosition++, true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionOfSong - 1 < mediaList.size()) {
                    forchangingNextSong();
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionOfSong != 0) {
                    forchangingPreviousSong();
                }
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying() && mediaForMoods.isPlaying()) {
                    mediaPlayer.pause();
                    mediaForMoods.pause();
                    btnPlay.setImageResource(R.mipmap.ic_play);
                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.mipmap.ic_play);
                } else {
                    btnPlay.setImageResource(R.mipmap.ic_pause);
                    mediaPlayer.start();
                    mediaForMoods.start();
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
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);
                mediaPlayer.seekTo(currentPosition);
                updateProgressBar();
            }
        });
        this.tvPercentage = tvMixPercentage;

        /**
         * Playing the background moods and songs
         **/
//        mediaForMoods = new MediaPlayer();
        moodsList(viewpage);
        playSong(dataSorce);
        playMoods(R.raw.rain);
        mediaForMoods.setVolume(0, 0);//Setting the volume mute for the moods during the first loading
        seekbar.setOnSeekBarChangeListener(new SeekBarImplementation());
        viewpage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                moodsPagerPosition = position;//setting moods pager position for scroll on button click

                switch (position) {
                    case 0:
                        playMoods(moodsSong[0]);
                        imvMoods.setImageResource(R.mipmap.ic_rain);

                        break;
                    case 1:
                        playMoods(moodsSong[1]);
                        imvMoods.setImageResource(R.mipmap.ic_forest);
                        break;
                    case 2:
                        playMoods(moodsSong[2]);
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
     * Getting Datas From throght thee intent
     */
    void gettingDataThrougIntent() {
        Intent intent = getIntent();
        dataSorce = intent.getStringExtra(Constants.intentDataSrc);
        positionOfSong = intent.getIntExtra("position", 0);
        songName = intent.getStringExtra(Constants.intentSongsTitle);
        Bundle bundle = intent.getBundleExtra("bundle");
        mediaList = (ArrayList<MediaDataHolder>) bundle.getSerializable("arrayList");
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
        imvBtnLeftArraow = (ImageView) findViewById(R.id.imv_left_arrow);
        imvBtnRightArrow = (ImageView) findViewById(R.id.imv_right_arrow);
    }

    /**
     * Setting up adapter for moods list
     */
    private void moodsList(ViewPager viewPager) {
        final String array[] = getResources().getStringArray(R.array.moods);
        PagerAdapter adapter = new PagerAdapter() {
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
     * Playing songs for the moods
     *
     * @param sourceFile src of songs
     */
    private void playMoods(int sourceFile) {
        if (mediaForMoods != null && mediaForMoods.isPlaying()) {
            mediaForMoods.stop();
            mediaForMoods.release();
            mediaForMoods = null;
        }
        mediaForMoods = new MediaPlayer();
        try {
            Uri uri = Uri.parse("android.resource://com.toobler.songscape/" + sourceFile);
            mediaForMoods.reset();
            mediaForMoods.setDataSource(NowPlayingActivity.this, uri);
            mediaForMoods.setVolume(moodsVolume / 10.0f, moodsVolume / 10.0f);
            mediaForMoods.prepare();
            if (mediaPlayer.isPlaying()) {
                mediaForMoods.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to play a song from local
     *
     * @param fileSrc the file src
     */
    public void playSong(String fileSrc) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(NowPlayingActivity.this);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(NowPlayingActivity.this, Uri.parse(fileSrc));
            mediaPlayer.prepare();
            mediaPlayer.start();
            musicSeekBar.setProgress(0);
            musicSeekBar.setMax(100);
            /**Updating progress bar*/
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
            // Displaying Total Duration time
            tvTotalDuration.setText("" + Utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            tvCompletedDuration.setText("" + Utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (Utilities.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            musicSeekBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    /**
     * Implimenting the seekbar for the moods volume
     */
    public class SeekBarImplementation implements CircularSeekBar.OnCircularSeekBarChangeListener {
        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            moodsVolume = progress;
            tvMixPercentage.setVisibility(View.VISIBLE);
            tvMixPlaceHolder.setVisibility(View.VISIBLE);
            tvMixPercentage.setText(progress + "%");
            if (mediaForMoods != null) {
                mediaForMoods.setVolume((progress / 10.0f), (progress / 10.0f));
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvMixPercentage.setVisibility(View.GONE);
                    tvMixPlaceHolder.setVisibility(View.GONE);
                }
            }, 3000);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mediaPlayer.release();
        mediaForMoods.release();
    }

}

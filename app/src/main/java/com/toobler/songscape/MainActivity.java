package com.toobler.songscape;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.toobler.songscape.Utilites.Constants;
import com.toobler.songscape.now_playing.NowPlayinFragment;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements SongsListFragment.OnFragmentInteractionListener {
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SONGSCAPE");


        SharedPreferences shPreffs = Constants.getSharedPreffs(MainActivity.this);

        ImageView imvSongsList = (ImageView) toolbar.findViewById(R.id.imv_songs_list);
        ImageView imvNowPlaying = (ImageView) toolbar.findViewById(R.id.imv_now_playing);


        if (shPreffs.getBoolean(Constants.IS_FIRST_LOADING, true)) {
            showSongsList();
            shPreffs.edit().putBoolean(Constants.IS_FIRST_LOADING, false).apply();
        } else {
            position = shPreffs.getInt("position", 0);
            showNowPlaying(position);
        }
        imvSongsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fragName = getFragmentManager().findFragmentById(R.id.container).getClass().getName();
                if (!fragName.equals("com.toobler.songscape.now_playing.SongsListFragment")) {
                    showSongsList();
                }
            }
        });
        imvNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fragName = getFragmentManager().findFragmentById(R.id.container).getClass().getName();
                if (!fragName.equals("com.toobler.songscape.now_playing.NowPlayinFragment")) {
                    showNowPlaying(position);
                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(int position) {
        showNowPlaying(position);
    }

    private void showSongsList() {
        SongsListFragment fragSongList = new SongsListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragSongList);
        transaction.commit();
    }

    private void showNowPlaying(int position) {
        NowPlayinFragment fragPlayer = new NowPlayinFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragPlayer.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragPlayer);
//        transaction.addToBackStack(fragPlayer.getFragmentManager()  getTag().toString());
        transaction.commit();
    }
}

package com.toobler.songscape;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toobler.songscape.Utilites.Constants;
import com.toobler.songscape.Utilites.Utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SongsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        RecyclerView rvSongsList = (RecyclerView) findViewById(R.id.rv_songs_list);
        rvSongsList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<MediaDataHolder> songsList = Utilities.fetchingSongsFromDevice(this);

        rvSongsList.setAdapter(new SongsListAdapter(songsList));
    }


    private class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.Viewholder> {
        ArrayList<MediaDataHolder> mediaDataHolders;

        public SongsListAdapter(ArrayList<MediaDataHolder> mediaDataHolders) {
            this.mediaDataHolders = mediaDataHolders;
        }

        @Override
        public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list_item, parent, false);
            Viewholder viewHolder = new Viewholder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(Viewholder holder, final int position) {
            holder.tvSongsNAme.setText(mediaDataHolders.get(position).getDisplayName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    new NowPlayingActivity().newSongListner();
                    Intent i = new Intent(SongsListActivity.this, NowPlayingActivity.class);
                    i.putExtra(Constants.intentDataSrc, mediaDataHolders.get(position).getSongPath());
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("arrayList",mediaDataHolders);
                    i.putExtra("bundle",bundle);
                    i.putExtra("position",position);
                    i.putExtra(Constants.intentSongsTitle, mediaDataHolders.get(position).getTitle());
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mediaDataHolders.size();
        }

        class Viewholder extends RecyclerView.ViewHolder {
            TextView tvSongsNAme;

            public Viewholder(View itemView) {
                super(itemView);
                tvSongsNAme = (TextView) itemView.findViewById(R.id.tv_song_name);
            }
        }
    }
}

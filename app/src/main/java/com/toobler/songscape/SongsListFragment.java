package com.toobler.songscape;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toobler.songscape.Utilites.Utilities;
import com.toobler.songscape.now_playing.NowPlayerService;
import com.toobler.songscape.now_playing.NowPlayingActivity;
import com.toobler.songscape.now_playing.model.MediaDataHolder;

import java.util.ArrayList;

public class SongsListFragment extends Fragment {
//    boolean isFromNowPayin;
    private NowPlayerService musicSrv;
    private Activity activity;
    private OnFragmentInteractionListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_songs_list, container, false);
        activity = getActivity();
        RecyclerView rvSongsList = (RecyclerView) view.findViewById(R.id.rv_songs_list);
        rvSongsList.setLayoutManager(new LinearLayoutManager(activity));
        ArrayList<MediaDataHolder> songsList = Utilities.fetchingSongsFromDevice(activity);
//        isFromNowPayin = activity.getIntent().getBooleanExtra("isFromNowPayin", false);
//        musicSrv = new NowPlayerService();
        rvSongsList.setAdapter(new SongsListAdapter(songsList));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
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
            holder.tvSongsNAme.setText(mediaDataHolders.get(position).getTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(position);
                    }



//                    new NowPlayingActivity().newSongListner();
//                    Intent i = new Intent(activity, NowPlayingActivity.class);
//                    i.putExtra("position", position);
//                    if (isFromNowPayin) {
//                        activity.setResult(100, i);
//                        activity.finish();
//                    } else {
//                        startActivity(i);
//                    }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int position);
    }

}

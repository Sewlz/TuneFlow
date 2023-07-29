package com.example.musicstreaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.Model.Playlist;
import com.example.musicstreaming.R;

import java.util.ArrayList;

public class CustomPlaylistDetailAdapter extends ArrayAdapter<Music> {
    private Context context;
    private ArrayList<Music> musicArrayList;

    public CustomPlaylistDetailAdapter(Context context, ArrayList<Music> musicArrayList) {
        super(context, R.layout.fragment_playlist_details, musicArrayList);
        this.context = context;
        this.musicArrayList = musicArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_playlist_details, parent, false);
        }
        Music currentMusic = musicArrayList.get(position);

        TextView songTitleTV = listItemView.findViewById(R.id.songTitleTV);
        ImageView songImgView = listItemView.findViewById(R.id.songImgView);
        songTitleTV.setText(currentMusic.getTITLE());

        Glide.with(context)
                .load(currentMusic.getTHUMBNAIL_URL())
                .into(songImgView);
        return listItemView;
    }
}

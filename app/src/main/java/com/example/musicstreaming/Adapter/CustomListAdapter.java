package com.example.musicstreaming.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.musicstreaming.Model.Music; // Replace with your data model class
import com.example.musicstreaming.Model.Playlist;
import com.example.musicstreaming.R;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Playlist> {
    private Context context;
    private ArrayList<Playlist> playlistList;

    public CustomListAdapter(Context context, ArrayList<Playlist> playlistList) {
        super(context, R.layout.list_item_layout, playlistList);
        this.context = context;
        this.playlistList = playlistList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        // Get the current Music object from the ArrayList
        Playlist currentPlaylist = playlistList.get(position);

        // Find the TextView in the list_item_layout.xml layout and set the music title
        TextView nameTextView = listItemView.findViewById(R.id.nameTextView);
        nameTextView.setText(currentPlaylist.getPLAYLIST_NAME());

        // Customize other views if needed

        return listItemView;
    }
}
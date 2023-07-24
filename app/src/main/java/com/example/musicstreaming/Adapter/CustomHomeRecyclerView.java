package com.example.musicstreaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreaming.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class CustomHomeRecyclerView extends RecyclerView.Adapter<CustomHomeRecyclerView.MusicViewHolder>{
    Context context;
    List<Music> musicItemList;
    public CustomHomeRecyclerView(Context context, List<Music> musicItemList) {
        this.context = context;
        this.musicItemList = musicItemList;
    }
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_home_layout, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music musicItem = musicItemList.get(position);
        // Load and display the image using Glide
        Glide.with(holder.itemView.getContext())
                .load(musicItem.getTHUMBNAIL_URL()) // Assuming you have an 'imageUrl' field in your Music class
                .into(holder.musicImageView);
//        holder.musicImageView.setImageResource(musicItem.getImageResource());
        holder.musicNameTextView.setText(musicItem.getTITLE());
    }

    @Override
    public int getItemCount() {
        return musicItemList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView musicImageView;
        TextView musicNameTextView;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            musicImageView = itemView.findViewById(R.id.musicImageView);
            musicNameTextView = itemView.findViewById(R.id.musicNameTextView);
        }
    }
}

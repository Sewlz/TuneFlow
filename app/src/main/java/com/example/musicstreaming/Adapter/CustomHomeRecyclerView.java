package com.example.musicstreaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class CustomHomeRecyclerView extends RecyclerView.Adapter<CustomHomeRecyclerView.MusicViewHolder> implements RecyclerInterface {
    Context context;
    private final List<Music> musicItemList;
    private final RecyclerInterface recyclerInterface;
    @Override
    public void onClick(int position) {

    }
    public CustomHomeRecyclerView(Context context, List<Music> musicItemList, RecyclerInterface recyclerInterface) {
        this.context = context;
        this.musicItemList = musicItemList;
        this.recyclerInterface = recyclerInterface;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_home_layout, parent, false);
        return new MusicViewHolder(view, recyclerInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music musicItem = musicItemList.get(position);
        // Load and display the image using Glide
        Glide.with(holder.itemView.getContext())
                .load(musicItem.getTHUMBNAIL_URL())
                .into(holder.musicImageView);
        holder.musicNameTextView.setText(musicItem.getTITLE());
    }

    @Override
    public int getItemCount() {
        return musicItemList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView musicImageView;
        TextView musicNameTextView;

        public MusicViewHolder(@NonNull View itemView, RecyclerInterface recyclerInterface) {
            super(itemView);
            musicImageView = itemView.findViewById(R.id.musicImageView);
            musicNameTextView = itemView.findViewById(R.id.musicNameTextView);
            //onclick for recyleView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerInterface.onClick(pos);
                        }
                    }
                }
            });
        }
    }
}

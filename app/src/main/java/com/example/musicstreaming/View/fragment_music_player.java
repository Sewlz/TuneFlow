package com.example.musicstreaming.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_music_player#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_music_player extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_music_player() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_music_player.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_music_player newInstance(String param1, String param2) {
        fragment_music_player fragment = new fragment_music_player();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private Integer positon;
    ImageButton btn_play_pause;
    TextView text_music_title,text_music_artist;
    ImageView image_music_thumbnail;
    private boolean paused = true;
    private List<Music> musicArrayList;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            positon = arguments.getInt("Positon");
        }
        return view;
    }
    private void EventChangeListener(){
        db.collection("music")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error !=null){
                            Log.e("FireBase error", error.getMessage());
                            return;
                        }
                        for(DocumentChange dc: value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                musicArrayList.add(dc.getDocument().toObject(Music.class));
                            }
                        }
                        Log.d("testMusicArray",musicArrayList.toString());
                        if (!musicArrayList.isEmpty()) {
                            Music musicItem = musicArrayList.get(positon);
                            updateUI(musicItem);
                        }
                    }
                });
    }
    private void updateUI(Music musicItem) {
        // ... update the UI with the musicItem ...//
        text_music_title.setText(musicItem.getTITLE());
        text_music_artist.setText(musicItem.getARTIST());
        Glide.with(getActivity())
                .load(musicItem.getTHUMBNAIL_URL()) // Assuming you have an 'imageUrl' field in your Music class
                .into(image_music_thumbnail);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        btn_play_pause = (ImageButton) view.findViewById(R.id.btn_play_pause);
        text_music_title = (TextView) view.findViewById(R.id.text_music_title);
        text_music_artist = (TextView) view.findViewById(R.id.text_music_artist);
        image_music_thumbnail = (ImageView) view.findViewById(R.id.image_music_thumbnail);
        
        musicArrayList = new ArrayList<>();
        EventChangeListener();
        addEvent();
    }
    private void addEvent(){
        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int icon;
                if (paused) {
                    paused = false;
                    icon = R.drawable.baseline_pause_24;
                }
                else {
                    paused = true;
                    icon = R.drawable.baseline_play_arrow_24;
                }
                btn_play_pause.setImageDrawable(ContextCompat.getDrawable(getActivity(), icon));
            }
        });
    }
}
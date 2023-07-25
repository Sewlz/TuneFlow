package com.example.musicstreaming.View;

import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
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

import java.io.IOException;
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
    ImageButton btn_play_pause, btn_next, btn_previous;
    TextView text_music_title,text_music_artist;
    ImageView image_music_thumbnail;
    private boolean paused = true;
    private List<Music> musicArrayList;
    FirebaseFirestore db;
    private MediaPlayer mediaPlayer;
    private SeekBar music_progress_bar;
    private Handler handler = new Handler();
    private String music_url;
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int totalDuration = mediaPlayer.getDuration();
                music_progress_bar.setProgress((currentPosition * 100) / totalDuration);

                // Schedule the update every 1 second
                handler.postDelayed(this, 1000);
            }
        }
    };
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
                        // Clear the list before adding new items
                        musicArrayList.clear();
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
        music_url = musicItem.getMUSIC_URL();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        btn_play_pause = (ImageButton) view.findViewById(R.id.btn_play_pause);
        text_music_title = (TextView) view.findViewById(R.id.text_music_title);
        text_music_artist = (TextView) view.findViewById(R.id.text_music_artist);
        image_music_thumbnail = (ImageView) view.findViewById(R.id.image_music_thumbnail);
        music_progress_bar = (SeekBar) view.findViewById(R.id.music_progress_bar);
        btn_next = (ImageButton) view.findViewById(R.id.btn_next);
        btn_previous = (ImageButton) view.findViewById(R.id.btn_previous);

        musicArrayList = new ArrayList<>();
        EventChangeListener();
        addEvent();
    }
    private void addEvent(){
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positon + 1 < musicArrayList.size()) {
                    positon++;

                    stopAudio();
                    music_progress_bar.setProgress(0);

                    Music musicItem = musicArrayList.get(positon);
                    updateUI(musicItem);

                    int icon = R.drawable.baseline_play_arrow_24;
                    btn_play_pause.setImageDrawable(ContextCompat.getDrawable(getActivity(), icon));
                }
                else {
                    Toast.makeText(getActivity(), "No more songs", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positon - 1 >= 0) {
                    positon--;

                    stopAudio();
                    music_progress_bar.setProgress(0);

                    Music musicItem = musicArrayList.get(positon);
                    updateUI(musicItem);

                    int icon = R.drawable.baseline_play_arrow_24;
                    btn_play_pause.setImageDrawable(ContextCompat.getDrawable(getActivity(), icon));
                }
                else {
                    Toast.makeText(getActivity(), "No more songs", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int icon;
                if (paused) {
                    paused = false;
                    playAudio(music_url);
                    icon = R.drawable.baseline_pause_24;
                }
                else {
                    paused = true;
                    pauseAudio();
                    icon = R.drawable.baseline_play_arrow_24;
                }
                btn_play_pause.setImageDrawable(ContextCompat.getDrawable(getActivity(), icon));
            }
        });
        // Set up MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Start playing audio when prepared
                mediaPlayer.start();
                paused = false;
            }
        });
        music_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update audio playback position when the SeekBar is changed by the user
                if (fromUser && mediaPlayer != null) {
                    int newPosition = (mediaPlayer.getDuration() * progress) / 100;
                    mediaPlayer.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Remove seek bar update callbacks while the user is interacting with the SeekBar
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add seek bar update callbacks when the user stops interacting with the SeekBar
                handler.post(updateSeekBar);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Reset the seek bar to 0 when the media playback completes
                music_progress_bar.setProgress(0);
                int icon = R.drawable.baseline_play_arrow_24;
                btn_play_pause.setImageDrawable(ContextCompat.getDrawable(getActivity(), icon));
            }
        });
    }

    private void playAudio(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // Prepare the MediaPlayer asynchronously
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            paused = true;
        }
    }

    private void stopAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            paused = true;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release resources when the fragment is destroyed
        mediaPlayer.release();
        mediaPlayer = null;
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Resume seek bar updates when the fragment is resumed
        handler.postDelayed(updateSeekBar, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove seek bar update callbacks when the fragment is paused
        handler.removeCallbacks(updateSeekBar);
    }
}
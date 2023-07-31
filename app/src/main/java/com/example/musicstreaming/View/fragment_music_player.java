package com.example.musicstreaming.View;

import android.app.Notification;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicstreaming.Adapter.CustomListAdapter;
import com.example.musicstreaming.Adapter.PlaylistIdCallback;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.Model.Playlist;
import com.example.musicstreaming.Model.PlaylistMusic;
import com.example.musicstreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    private Integer positon, musicID;
    ImageButton btn_play_pause, btn_next, btn_previous, btn_add_playlist;
    TextView text_music_title,text_music_artist;
    ImageView image_music_thumbnail;
    private boolean paused = true;
//    private List<Music> musicArrayList;
    ArrayList<Music> arrayList = new ArrayList<>();
    FirebaseFirestore db;
    private MediaPlayer mediaPlayer;
    private SeekBar music_progress_bar;
    private Handler handler = new Handler();
    private String music_url;
    String username;
    int playlistId;
    ArrayList<Playlist> playlistArrayList = new ArrayList<>();
    ArrayAdapter<Playlist> adapter;
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
        username = ((MainActivity)getActivity()).username;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            positon = arguments.getInt("Positon");
            arrayList = arguments.getParcelableArrayList("MusicList");
        }
        return view;
    }
    private void updateUI(Music musicItem) {
        // ... update the UI with the musicItem ...//
        text_music_title.setText(musicItem.getTITLE());
        text_music_artist.setText(musicItem.getARTIST());
        Glide.with(getActivity())
                .load(musicItem.getTHUMBNAIL_URL())
                .into(image_music_thumbnail);
        music_url = musicItem.getMUSIC_URL();
        musicID = musicItem.getMUSIC_ID();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        btn_add_playlist = (ImageButton) view.findViewById(R.id.btn_add_playlist);
        btn_play_pause = (ImageButton) view.findViewById(R.id.btn_play_pause);
        text_music_title = (TextView) view.findViewById(R.id.text_music_title);
        text_music_artist = (TextView) view.findViewById(R.id.text_music_artist);
        image_music_thumbnail = (ImageView) view.findViewById(R.id.image_music_thumbnail);
        music_progress_bar = (SeekBar) view.findViewById(R.id.music_progress_bar);
        btn_next = (ImageButton) view.findViewById(R.id.btn_next);
        btn_previous = (ImageButton) view.findViewById(R.id.btn_previous);

        if (!arrayList.isEmpty()) {
            Music musicItem = arrayList.get(positon);
            updateUI(musicItem);
        }
        addEvent();
        getLastedID(new PlaylistIdCallback() {
            @Override
            public void onPlaylistIdFetched(int playlistId) {
                fragment_music_player.this.playlistId = playlistId;
                Log.d("PlaylistID", "Latest Playlist ID: " + playlistId);
            }
        });
        Toast.makeText(getActivity(), ""+musicID, Toast.LENGTH_SHORT).show();
        Log.d("testMusicArray",arrayList.toString());
    }
    private void addEvent(){
        btn_add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positon + 1 < arrayList.size()) {
                    positon++;

                    stopAudio();
                    music_progress_bar.setProgress(0);

                    Music musicItem = arrayList.get(positon);
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

                    Music musicItem = arrayList.get(positon);
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
                handler.post(updateSeekBar);
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
    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.BlackDialogTheme);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_playlist_dialog,null);
        alertDialogBuilder.setView(view);

        ListView lstPlaylist = view.findViewById(R.id.lstPlaylist);
        Button  buttonCreate = view.findViewById(R.id.buttonCreate);

        getPlaylistByUNM(username);
        adapter = new CustomListAdapter(getActivity(),playlistArrayList);
        lstPlaylist.setAdapter(adapter);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        lstPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist = playlistArrayList.get(position);
                addToPlaylist(musicID,playlist.getPLAYLIST_ID());
            }
        });
        lstPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist = playlistArrayList.get(position);
                showDeleteDialog(playlist.getPLAYLIST_ID(),position);

                return true;
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //get user playlist
    public void getPlaylistByUNM(String username) {
        CollectionReference searchRef = db.collection("playlist");
        playlistArrayList.clear();
        searchRef.whereEqualTo("USERNAME", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot dc : task.getResult()) {
                                playlistArrayList.add(dc.toObject(Playlist.class));
                            }
                            adapter.notifyDataSetChanged();
                        } else {}
                    }
                });
    }
    //add song to playlist
    private void addToPlaylist(Integer music_id, Integer playlist_id){
        CollectionReference collectionRef = db.collection("playlist_music");
        PlaylistMusic playlistMusic = new PlaylistMusic(music_id,playlist_id);
        collectionRef.whereEqualTo("MUSIC_ID",music_id)
                .whereEqualTo("PLAYLIST_ID", playlist_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(querySnapshot != null && !querySnapshot.isEmpty()){
                            Toast.makeText(getActivity(), "Song has already in this playlist", Toast.LENGTH_SHORT).show();
                        }else {
                            collectionRef.add(playlistMusic)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            showNotiDialog();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firestore", "Error adding ", e);
                                        }
                                    });
                        }
                    }
                });
    }
    private void showNotiDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Notification");
        alertDialogBuilder.setMessage("Add Song Successfully");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //Create new Playlist
    private void addNewPlaylist(String playlistName){
        playlistId++;
        CollectionReference collectionRef = db.collection("playlist");
        Playlist playlist = new Playlist(username,playlistName,playlistId);
        collectionRef.document()
                .set(playlist)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        playlistArrayList.add(playlist);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding user", e);
                    }
                });
    }
    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Notification");
        alertDialogBuilder.setMessage("Create new playlist");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text,null);
        alertDialogBuilder.setView(view);
        EditText edtPlaylistName = view.findViewById(R.id.edtPlaylistName);
        // Set the positive button (OK button)
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewPlaylist(edtPlaylistName.getText().toString());
                dialog.dismiss(); // Close the dialog
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void getLastedID(PlaylistIdCallback callback) {
        CollectionReference collectionRef = db.collection("playlist");
        collectionRef.orderBy("PLAYLIST_ID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                Playlist playlist = documentSnapshot.toObject(Playlist.class);
                                int playlistId = playlist.getPLAYLIST_ID();
                                callback.onPlaylistIdFetched(playlistId);
                            } else {}
                        } else {}
                    }
                });
    }
    //delete dialog
    private void deletePlaylist(Integer playlist_Id){
        CollectionReference collectionRef = db.collection("playlist");
        collectionRef.whereEqualTo("PLAYLIST_ID",playlist_Id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                        else {}
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding user", e);
                    }
                });
    }
    private void showDeleteDialog(Integer playlist_Id, final int pos) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Notification");
        alertDialogBuilder.setMessage("Are you sure you want to delete this playlist");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePlaylist(playlist_Id);
                playlistArrayList.remove(pos);
                adapter.notifyDataSetChanged();
                dialog.dismiss(); // Close the dialog
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
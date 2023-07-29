package com.example.musicstreaming.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicstreaming.Adapter.CustomPlaylistDetailAdapter;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.Model.Playlist;
import com.example.musicstreaming.Model.PlaylistMusic;
import com.example.musicstreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_playlist_details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_playlist_details extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Integer playlistId;
    ArrayList<Music> musicArrayList = new ArrayList<>();
    ArrayList<PlaylistMusic> playlistMusics = new ArrayList<>();
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayAdapter<Music> adapter;

    public fragment_playlist_details() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_playlist_details.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_playlist_details newInstance(String param1, String param2) {
        fragment_playlist_details fragment = new fragment_playlist_details();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist_details, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            playlistId = arguments.getInt("id");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lstView_playlist_details = (ListView) view.findViewById(R.id.lstView_playlist_details);
        adapter = new CustomPlaylistDetailAdapter(getActivity(), musicArrayList);
        lstView_playlist_details.setAdapter(adapter);
        getUserPlaylist();
        lstView_playlist_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendDataToDestinationFragment(position);
            }
        });
    }

    private void getUserPlaylist(){
        playlistMusics.clear();
        CollectionReference searchRef = db.collection("playlist_music");
        searchRef.whereEqualTo("PLAYLIST_ID", playlistId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot dc : task.getResult()) {
                                playlistMusics.add(dc.toObject(PlaylistMusic.class));
                            }
                            Log.d("ARRAY_PLAYLIST", "getUserPlaylist: " + playlistMusics.toString());
                            getSong(playlistMusics);
                        } else {
                        }
                    }
                });
    }
    private void getSong(ArrayList<PlaylistMusic> playlistMusics) {
        musicArrayList.clear();
        int totalQueries = playlistMusics.size();
        AtomicInteger completedQueries = new AtomicInteger(0);

        for (int i = 0; i < totalQueries; i++) {

            PlaylistMusic playlistMusic = playlistMusics.get(i);
            int musicId = playlistMusic.getMUSIC_ID();
            CollectionReference searchRef = db.collection("music");
            searchRef.whereEqualTo("MUSIC_ID", musicId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot dc : task.getResult()) {
                                    musicArrayList.add(dc.toObject(Music.class));
                                }

                                // Increase the completedQueries counter
                                int completedCount = completedQueries.incrementAndGet();

                                // Check if all queries have completed
                                if (completedCount == totalQueries) {
                                    // All queries have completed, update the adapter and the ListView
                                    Log.d("ARRAY_MUSIC", "getUserPlaylist: " + musicArrayList.toString());
                                    adapter.notifyDataSetChanged();
                                }
                            } else {}
                        }
                    });
        }
    }
    private void sendDataToDestinationFragment(Integer positon) {
        // Create a new instance of the destination fragment
        fragment_music_player destinationFragment = new fragment_music_player();

        // Create a Bundle to pass the data
        Bundle bundle = new Bundle();
        bundle.putInt("Positon", positon); // Replace "KEY_DATA" with your desired key

        // Set the arguments to the destination fragment
        destinationFragment.setArguments(bundle);
        bundle.putParcelableArrayList("MusicList", musicArrayList);
        // Replace the current fragment with the destination fragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameMain, destinationFragment);
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack if you want to navigate back
        fragmentTransaction.commit();
    }
}
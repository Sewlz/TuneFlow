package com.example.musicstreaming.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicstreaming.Adapter.CustomListAdapter;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.Model.Playlist;
import com.example.musicstreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_playlist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_playlist extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayList<Playlist> playlistArrayList= new ArrayList<>();
    ArrayAdapter<Playlist> adapter;
    String username;
    public fragment_playlist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_playlist.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_playlist newInstance(String param1, String param2) {
        fragment_playlist fragment = new fragment_playlist();
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
        username = ((MainActivity)getActivity()).username;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView_playlist = (ListView) view.findViewById(R.id.listView_playlist);
        getPlaylistByUNM(username);
        adapter = new CustomListAdapter(getActivity(),playlistArrayList);
        listView_playlist.setAdapter(adapter);

        listView_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist = playlistArrayList.get(position);
                int playlistId = playlist.getPLAYLIST_ID();
                sendDataToDestinationFragment(playlistId);
                Toast.makeText(getActivity(),"Name: "+ playlist.getPLAYLIST_NAME(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }
    private void getPlaylistByUNM(String username) {
        CollectionReference searchRef = db.collection("playlist");
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
                        } else {
                        }
                    }
                });
    }
    private void sendDataToDestinationFragment(Integer playlistId) {
        // Create a new instance of the destination fragment
        fragment_playlist_details destinationFragment = new fragment_playlist_details();

        // Create a Bundle to pass the data
        Bundle bundle = new Bundle();
        bundle.putInt("id", playlistId); // Replace "KEY_DATA" with your desired key

        // Set the arguments to the destination fragment
        destinationFragment.setArguments(bundle);
        // Replace the current fragment with the destination fragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameMain, destinationFragment);
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack if you want to navigate back
        fragmentTransaction.commit();
    }

}
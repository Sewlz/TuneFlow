package com.example.musicstreaming.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.musicstreaming.Adapter.CustomHomeRecyclerView;
import com.example.musicstreaming.Adapter.RecyclerInterface;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_home extends Fragment implements RecyclerInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_home newInstance(String param1, String param2) {
        fragment_home fragment = new fragment_home();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    //khai bao bien
    private RecyclerView recycler_featured_playlists;
    private CustomHomeRecyclerView adapter;

    private ArrayList<Music> musicArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    //on created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler_featured_playlists = (RecyclerView) view.findViewById(R.id.recycler_featured_playlists);
        recycler_featured_playlists.setHasFixedSize(true);

        // Set up the grid layout manager
        int spanCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        recycler_featured_playlists.setLayoutManager(layoutManager);

        musicArrayList = new ArrayList<>();
        adapter = new CustomHomeRecyclerView(getActivity(),musicArrayList, this);
        recycler_featured_playlists.setAdapter(adapter);
        EventChangeListener();
    }
    //load firestore data to array
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
                        Log.d("testarraycker",musicArrayList.toString());
                        adapter.notifyDataSetChanged();
                    }
                });
    }
    //on recyler itemclick (interface)
    @Override
    public void onClick(int position) {
        sendDataToDestinationFragment(position);
        Toast.makeText(getActivity(), "Test Item"+position, Toast.LENGTH_SHORT).show();
    }
    //send data to another fragment
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
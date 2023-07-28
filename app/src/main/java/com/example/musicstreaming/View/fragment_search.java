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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicstreaming.Adapter.CustomHomeRecyclerView;
import com.example.musicstreaming.Adapter.RecyclerInterface;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_search extends Fragment implements RecyclerInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recycler_search;
    EditText search_edt;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayList<Music> musicArrayList = new ArrayList<>();
    private CustomHomeRecyclerView adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_search.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_search newInstance(String param1, String param2) {
        fragment_search fragment = new fragment_search();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_edt = (EditText) view.findViewById(R.id.search_edt);
        recycler_search = (RecyclerView) view.findViewById(R.id.recycler_search);
        recycler_search.setHasFixedSize(true);

        int spanCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        recycler_search.setLayoutManager(layoutManager);

        adapter = new CustomHomeRecyclerView(getActivity(),musicArrayList, this);
        recycler_search.setAdapter(adapter);
        search_edt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search(search_edt.getText().toString());
                    Toast.makeText(getActivity(), "TEST SUBMIT", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onComplete: "+musicArrayList.toString());
                    return true;
                }
                return false;
            }
        });
    }
private void search(String inputSearch) {
    CollectionReference searchRef = db.collection("music");
    musicArrayList.clear();

    searchRef.whereEqualTo("TITLE", inputSearch)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot dc : task.getResult()) {
                            musicArrayList.add(dc.toObject(Music.class));
                        }
                        searchByArtist(inputSearch);
                    } else {
                        // Handle the failure case here, if needed
                    }
                }
            });
}

    private void searchByArtist(String inputSearch) {
        CollectionReference searchRef = db.collection("music");
        searchRef.whereEqualTo("ARTIST", inputSearch)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot dc : task.getResult()) {
                                musicArrayList.add(dc.toObject(Music.class));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                        }
                    }
                });
    }

    @Override//problem get position 0 so always get the wrong song because playsong has array full song to fix this issuse intent sent the array
    public void onClick(int position) {
        sendDataToDestinationFragment(position);
        Toast.makeText(getActivity(), "Test Item"+position, Toast.LENGTH_SHORT).show();
    }
    private void sendDataToDestinationFragment(Integer positon) {
        // Create a new instance of the destination fragment
        fragment_music_player destinationFragment = new fragment_music_player();

        // Create a Bundle to pass the data
        Bundle bundle = new Bundle();
        bundle.putInt("Positon", positon); // Replace "KEY_DATA" with your desired key
        bundle.putParcelableArrayList("MusicList", musicArrayList);
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
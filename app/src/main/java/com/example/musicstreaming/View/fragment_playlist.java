package com.example.musicstreaming.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicstreaming.Adapter.CustomListAdapter;
import com.example.musicstreaming.Adapter.PlaylistIdCallback;
import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.Model.Playlist;
import com.example.musicstreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_playlist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_playlist extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static fragment_playlist instance = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listView_playlist;
    Button btnCreatePL;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayList<Playlist> playlistArrayList= new ArrayList<>();
    ArrayList<Playlist> newPlaylist = new ArrayList<>();
    ArrayAdapter<Playlist> adapter;
    String username;
    int playlistId;
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
        instance = this;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        username = ((MainActivity)getActivity()).username;
    }
    public static fragment_playlist getInstance() {
        return instance;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView_playlist = (ListView) view.findViewById(R.id.listView_playlist);
        btnCreatePL = (Button) view.findViewById(R.id.btnCreatePL);
        getPlaylistByUNM(username);
        adapter = new CustomListAdapter(getActivity(),playlistArrayList);
        listView_playlist.setAdapter(adapter);
        addEvent();
        getLastedID(new PlaylistIdCallback() {
            @Override
            public void onPlaylistIdFetched(int playlistId) {
                fragment_playlist.this.playlistId = playlistId;
                Log.d("PlaylistID", "Latest Playlist ID: " + playlistId);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);

    }
    private void addEvent(){
        listView_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist = playlistArrayList.get(position);
                int playlistId = playlist.getPLAYLIST_ID();
                sendDataToDestinationFragment(playlistId);
                Toast.makeText(getActivity(),"Name: "+ playlist.getPLAYLIST_NAME(), Toast.LENGTH_SHORT).show();
                Log.d("PLAYLIST_ID", "onViewCreated: "+playlistId);
            }
        });
        listView_playlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //delete method
                Playlist playlist = playlistArrayList.get(position);
                showDeleteDialog(playlist.getPLAYLIST_ID(),position);
                return true;
            }
        });
        btnCreatePL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    public void getPlaylistByUNM(String username) {
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
        bundle.putInt("id", playlistId);

        // Set the arguments to the destination fragment
        destinationFragment.setArguments(bundle);
        // Replace the current fragment with the destination fragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameMain, destinationFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
    //add playlist
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
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
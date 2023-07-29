package com.example.musicstreaming.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicstreaming.Model.Music;
import com.example.musicstreaming.Model.User;
import com.example.musicstreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    Button buttonLogin;
    TextView textViewRegister;
    EditText edtUserName, edtPass;
    String user_name;
    ArrayList<User> userArrayList = new ArrayList<>();
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControl();
        addEvent();
    }

    private void addEvent() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUserName.getText().toString();
                String password = edtPass.getText().toString();
                loginAuth(username,password);
            }
        });
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void addControl() {
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPass = (EditText) findViewById(R.id.edtPass);
    }

    private void loginAuth(String username, String password){
        CollectionReference userRef = db.collection("user");
        userRef.whereEqualTo("USERNAME", username)
                .whereEqualTo("PASSWORD", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                userArrayList.add(documentSnapshot.toObject(User.class));
                                User userItem = userArrayList.get(0);
                                user_name = userItem.getUSERNAME();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("USERNAME",user_name);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(Login.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
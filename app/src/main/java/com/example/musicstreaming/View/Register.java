package com.example.musicstreaming.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button buttonRegister;
    EditText edtNameReg, edtUserReg, edtPassReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControl();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add registration logic here
                String name = edtNameReg.getText().toString();
                String user = edtUserReg.getText().toString();
                String pass = edtPassReg.getText().toString();
                register(name,user,pass);
            }
        });

        TextView textViewLogin = findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }

    private void addControl() {
        buttonRegister = findViewById(R.id.buttonRegister);
        edtNameReg = (EditText) findViewById(R.id.edtNameReg);
        edtUserReg = (EditText) findViewById(R.id.edtUserReg);
        edtPassReg = (EditText) findViewById(R.id.edtPassReg);
    }

    private void register(String name, String username, String password) {
        CollectionReference collectionRef = db.collection("user");
        User user = new User(name, username, password);

        collectionRef.whereEqualTo("USERNAME", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            Toast.makeText(Register.this, "Username has already existed!", Toast.LENGTH_SHORT).show();
                        } else {
                            collectionRef.document()
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showAlertDialog();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firestore", "Error adding user", e);
                                        }
                                    });
                        }
                    }
                });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification");
        alertDialogBuilder.setMessage("Register successfully!");

        // Set the positive button (OK button)
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Register.this, Login.class));
                dialog.dismiss(); // Close the dialog
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edtNameReg.getText().clear();
                edtUserReg.getText().clear();
                edtPassReg.getText().clear();
                dialog.dismiss(); // Close the dialog
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
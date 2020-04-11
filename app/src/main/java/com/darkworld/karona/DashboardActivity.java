package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DashboardActivity extends AppCompatActivity {

    ImageView createGame,joinGame,profile;
    FirebaseAuth firebaseAuth;
    boolean lobbyExists;
    FirebaseUser currentUser;
//    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        createGame = findViewById(R.id.create_game);
        lobbyExists=true;
        joinGame = findViewById(R.id.join_game);
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this,ProfilePage.class));
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
//        user = (User) getIntent().getSerializableExtra("User");
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser==null)
        {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(DashboardActivity.this,GameSelectionActivity.class));
            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinLobby();
            }
        });
    }



    private void joinLobby() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Lobby");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 if(input.getText().toString().length()==6) {
                    doesLobbyExists(input.getText().toString());
                }
                else
                {
                    Toast.makeText(DashboardActivity.this, "Enter 6 digit Lobby Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void doesLobbyExists(final String lobbyCode)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child("Lobbies").child(lobbyCode);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Joining Lobby");
        progressDialog.show();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    joinLobbyonServer(lobbyCode);
                }
                else
                {
                    Toast.makeText(DashboardActivity.this, "No lobby with this code exists!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void joinLobbyonServer(final String lobbyCode) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child("Lobbies").child(lobbyCode);
        dbRef.child("Players").push().setValue(currentUser.getUid());
        Intent intent = new Intent(DashboardActivity.this,Lobby.class);
        intent.putExtra("Activity","JoinGame");
        intent.putExtra("LobbyCode",lobbyCode);
        startActivity(intent);
        }
}

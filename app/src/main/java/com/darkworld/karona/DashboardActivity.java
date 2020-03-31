package com.darkworld.karona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class DashboardActivity extends AppCompatActivity {

    Button createGame,joinGame;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        createGame = findViewById(R.id.create_game);
        joinGame = findViewById(R.id.join_game);
        firebaseAuth = FirebaseAuth.getInstance();
        user = (User) getIntent().getSerializableExtra("User");
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser==null)
        {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               generateLobby();
            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinLobby();
            }
        });
    }

    private void generateLobby() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Lobby");
        final TextView textView = new TextView(this);
        builder.setView(textView);
        Random random = new Random(System.currentTimeMillis());
        final int rnd = 100000+random.nextInt(500000);
        textView.setText(""+rnd);
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DashboardActivity.this,Lobby.class);
                intent.putExtra("Activity","CreateGame");
                intent.putExtra("LobbyCode",rnd);
                createLobbyonServer(rnd);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void createLobbyonServer(int rnd) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child("Lobbies").child(""+rnd);
        dbRef.push().setValue(user);
        Toast.makeText(this, "Successful: "+dbRef.getDatabase().toString(), Toast.LENGTH_SHORT).show();
    }

    private void joinLobby() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Lobby");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DashboardActivity.this,Lobby.class);
                intent.putExtra("Activity","JoinGame");
                intent.putExtra("LobbyCode",input.getText().toString());
                joinLobbyonServer(input.getText().toString());
                startActivity(intent);
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

    private void joinLobbyonServer(String lobbyCode) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child("Lobbies").child(lobbyCode);
        dbRef.push().setValue(user);
        Toast.makeText(this, "Successful: "+dbRef.getDatabase().toString(), Toast.LENGTH_SHORT).show();
    }
}

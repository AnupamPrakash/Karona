package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Lobby extends AppCompatActivity {

    Button startGame;
    RecyclerView playerList;
    String LobbyCode;
    List<User> playersinLobby;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        startGame = findViewById(R.id.start_game);
        playerList = findViewById(R.id.playerList);
        playersinLobby = new ArrayList<User>();
        String callingActivity = getIntent().getStringExtra("Activity");
        Toast.makeText(this, "Calling Activity: "+callingActivity, Toast.LENGTH_SHORT).show();
        if(callingActivity.equals("CreateGame"))
        {
            startGame.setVisibility(View.VISIBLE);
        }
        LobbyCode = getIntent().getStringExtra("LobbyCode");
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Lobby.this, GamePlayActivity.class));
            }
        });
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    loadPlayers(dataSnapshot1.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addValueEventListener(valueEventListener);
        Toast.makeText(this, ""+playersinLobby, Toast.LENGTH_SHORT).show();
        playerList.setAdapter(new PlayerListAdapter(this,playersinLobby));
        playerList.setLayoutManager(new GridLayoutManager(Lobby.this,4));
    }

    private void loadPlayers(String userId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                playersinLobby.add(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }
}

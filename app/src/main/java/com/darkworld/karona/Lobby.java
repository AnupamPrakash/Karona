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
import com.google.firebase.database.core.UserWriteRecord;

public class Lobby extends AppCompatActivity {

    Button startGame;
    RecyclerView playerList;
    String LobbyCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        startGame = findViewById(R.id.start_game);
        playerList = findViewById(R.id.playerList);
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
                startActivity(new Intent(Lobby.this,Game.class));
            }
        });
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode);
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
//                    User user = dataSnapshot1.getValue(User.class);
//                    Toast.makeText(Lobby.this, ""+user.getName(), Toast.LENGTH_SHORT).show();
//                }
////                playerList.setAdapter();
//                playerList.setLayoutManager(new GridLayoutManager(Lobby.this,4));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        dbRef.addValueEventListener(valueEventListener);
    }
}

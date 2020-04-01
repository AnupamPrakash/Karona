package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameSelectionActivity extends AppCompatActivity {


    List<Game> gameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);
        gameList = new ArrayList<Game>();
        loadGames();
    }

    private void loadGames() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Games");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Game game = new Game();
              game.setGameId(dataSnapshot.getKey());
              game.setGameName((String) dataSnapshot.child("ganeName").getValue());
              game.setGameLogo((String) dataSnapshot.child("gameLogo").getValue());
              gameList.add(game);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }
}

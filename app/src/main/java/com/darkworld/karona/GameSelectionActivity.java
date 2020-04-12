package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameSelectionActivity extends AppCompatActivity {


    List<Game> gameList;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);
        gameList = new ArrayList<Game>();
        recyclerView = findViewById(R.id.gameList);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Games");
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        loadGames();
    }

    private void loadGames() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Games");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Game game = new Game();
                    game.setGameId(dataSnapshot1.getKey());
                    game.setGameName((String) dataSnapshot1.child("name").getValue());
                    game.setGameLogo((String) dataSnapshot1.child("logo").getValue());
                    gameList.add(game);
//                    Toast.makeText(GameSelectionActivity.this, ""+game.getGameName(), Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(GameSelectionActivity.this, ""+gameList.size(), Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(new GameListAdapter(GameSelectionActivity.this,gameList,currentUser));
                recyclerView.setLayoutManager(new LinearLayoutManager(GameSelectionActivity.this));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(valueEventListener);

    }

}

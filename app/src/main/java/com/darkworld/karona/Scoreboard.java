package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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

public class Scoreboard extends AppCompatActivity {

    String lobbyCode,gameName;
    Button done;
    List<Long> scores,prevScores;
    ProgressDialog progressDialog;
    ValueEventListener valueEventListener;
    List<User> players;
    ScoreListAdapter scoreListAdapter;
    RecyclerView scorelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        done = findViewById(R.id.doneBtn);
        scorelist = findViewById(R.id.scoreList);
        scores = new ArrayList<Long>();
        players = new ArrayList<User>();
        prevScores = new ArrayList<Long>();
        scoreListAdapter = new ScoreListAdapter(Scoreboard.this,scores,players);
        scorelist.setLayoutManager(new LinearLayoutManager(this));
        scorelist.setAdapter(scoreListAdapter);
        progressDialog = new ProgressDialog(this);
        lobbyCode = getIntent().getStringExtra("LobbyCode");
        gameName = getIntent().getStringExtra("GameName");
        progressDialog.setMessage("Loading Scores...");
        progressDialog.show();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode);
        dbRef.child("Scores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    scores.add((Long) dataSnapshot1.getValue());
                    loadPlayer(dataSnapshot1.getKey());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users");
                for(int i=0;i<players.size();i++)
                {
                    getScore(players.get(i).getUserId());
                }
                DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users");
                dbRef.removeEventListener(valueEventListener);
                deleteLobby(lobbyCode);

            }
        });
    }

    private void deleteLobby(String lobbyCode) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode);
        dbRef.removeValue();
        startActivity(new Intent(Scoreboard.this,DashboardActivity.class));
        finish();
    }

    private void getScore(final String uid) {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Scores").child(gameName);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               prevScores.add((long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(Scoreboard.this,DashboardActivity.class));
//        finish();
    }
    private void loadPlayer(String uid) {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
         valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                players.add(user);
                Toast.makeText(Scoreboard.this, "" + user.getUserId(), Toast.LENGTH_SHORT).show();
                scoreListAdapter.notifyItemInserted(scores.size() - 1);
                scorelist.setAdapter(scoreListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

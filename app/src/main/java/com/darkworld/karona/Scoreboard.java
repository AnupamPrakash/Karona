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
    long score;
    List<Long> scores;
    ProgressDialog progressDialog;
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
                    scoreListAdapter.notifyDataSetChanged();
                    scorelist.setAdapter(scoreListAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 dbRef.child("Scores").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
//                        {
//                            int currscore = (int) dataSnapshot1.getValue();
////                            Toast.makeText(Scoreboard.this, "", Toast.LENGTH_SHORT).show();
//                            DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot1.getKey());
//                            getScore(dataSnapshot1.getKey());
//                            dbRef2.child("Scores").child(gameName).setValue(currscore+score);
//                        }
//                        deleteLobby(lobbyCode);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
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

    private void getScore(String uid) {

        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child(gameName);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               score = (int) dataSnapshot.getValue();
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
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                players.add(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

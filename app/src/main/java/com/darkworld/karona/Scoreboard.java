package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scoreboard extends AppCompatActivity {

    String lobbyCode, gameName;
    Button done;
    ProgressDialog progressDialog;
    ScoreListAdapter scoreListAdapter;
    List<Pair<User, Long>> gameScores;
    RecyclerView scorelist;
    int playerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        done = findViewById(R.id.doneBtn);
        scorelist = findViewById(R.id.scoreList);
        gameScores = new ArrayList<Pair<User, Long>>();
        progressDialog = new ProgressDialog(this);
        lobbyCode = getIntent().getStringExtra("LobbyCode");
        gameName = getIntent().getStringExtra("GameName");
        playerCount = getIntent().getIntExtra("PlayerCount", 5);
        progressDialog.setMessage("Waiting for others to submit responses...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        scoreListAdapter = new ScoreListAdapter(Scoreboard.this, gameScores);
        scorelist.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode);
        dbRef.child("Submits").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        dbRef.child("Submits").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadPlayer(dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<gameScores.size();i++)
                {
                    updateScore(gameScores.get(i));
                }
//                FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).removeValue();
//                dbRef.removeValue();
                Intent intent = new Intent(Scoreboard.this,DashboardActivity.class);
//                intent.putExtra("Activity","ScoreBoard");
//                intent.putExtra("LobbyCode",lobbyCode);
                startActivity(intent);
               finish();
            }
        });
//
    }


    private void updateScore(final Pair<User, Long> pair) {
        Log.d("Pair",pair.first.toString());
        String uid = pair.first.getUserId();
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Scores").child(gameName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long temp = (long) dataSnapshot.getValue();
                dataSnapshot.getRef().setValue(temp+pair.second);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        Toast.makeText(this, "Redirecting", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(Scoreboard.this,DashboardActivity.class));
//        finish();
    }
    private void loadPlayer(final String uid) {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
         dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                getcurrentScore(user,uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getcurrentScore(final User user, String uid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode);
        dbRef.child("Scores").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
//                {
////                    scores.add((Long) dataSnapshot1.getValue());
//                    loadPlayer(dataSnapshot1.getKey());
//                }
//                Collections.reverse(scores);
//                Collections.reverse(players);
//                Toast.makeText(Scoreboard.this, "Scores"+scores, Toast.LENGTH_SHORT).show();
//   +             Toast.makeText(Scoreboard.this, "Players"+players, Toast.LENGTH_SHORT).show();
//                scoreListAdapter.notifyItemInserted(scores.size() - 1);
//                scorelist.setAdapter(scoreListAdapter);
//                progressDialog.dismiss();
                Pair<User, Long> pair = new Pair(user, dataSnapshot.getValue());
                gameScores.add(pair);
                if (gameScores.size() == playerCount) {
//                    gameScores.sort(new Comparator<Pair<User, Long>>() {
//                        @Override
//                        public int compare(Pair<User, Long> o1, Pair<User, Long> o2) {
//                            if (o1.second > o2.second)
//                                return -1;
//                            else if (o1.second == o2.second)
//                                return 0;
//                            else
//                                return 1;
//                        }
//                    });
//                    Toast.makeText(Scoreboard.this, "Map: "+gameScores, Toast.LENGTH_SHORT).show();
                    scoreListAdapter.notifyItemInserted(gameScores.size() - 1);
                    scorelist.setAdapter(scoreListAdapter);
                    done.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

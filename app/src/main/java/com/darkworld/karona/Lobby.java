package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Lobby extends AppCompatActivity {

    Button startGame;
    RecyclerView playerList;
    List<User> playersinLobby;
    int rounds;
    PlayerListAdapter playerListAdapter;
//    ProgressDialog progressDialog;
    List<String> playerNames,gameQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        startGame = findViewById(R.id.start_game);
        final String LobbyCode = getIntent().getStringExtra("LobbyCode");
        playerList = findViewById(R.id.playerList);
        playersinLobby = new ArrayList<User>();
        playerNames = new ArrayList<String>();
        TextView textView = findViewById(R.id.LobbyCodeText);
        textView.setText(LobbyCode);
        String GameName = getIntent().getStringExtra("GameName");
        gameQuestions = new ArrayList<String>();
        final String callingActivity = getIntent().getStringExtra("Activity");
//        Toast.makeText(this, "Calling Activity: "+callingActivity, Toast.LENGTH_SHORT).show();
        if(callingActivity.equals("CreateGame"))
        {
            startGame.setVisibility(View.VISIBLE);
            loadQuestions(GameName);
            Toast.makeText(this, "Questions: "+gameQuestions.size(), Toast.LENGTH_SHORT).show();
        }
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode).child("Start");
                dbRef2.setValue("True");
                Intent intent = new Intent(Lobby.this,GamePlayActivity.class);
                intent.putExtra("Players", (Serializable) playerNames);
                intent.putExtra("Questions", (Serializable) gameQuestions);
                intent.putExtra("Rounds",5);
                intent.putExtra("LobbyCode",LobbyCode);
                intent.putExtra("Activity",callingActivity);
                startActivity(intent);
                Toast.makeText(Lobby.this, "Players: "+playerNames, Toast.LENGTH_SHORT).show();
                Toast.makeText(Lobby.this, "Questions: "+gameQuestions.size(), Toast.LENGTH_SHORT).show();
            }
        });
        getPlayers(LobbyCode);
//        Toast.makeText(Lobby.this, "Ids"+playerNames, Toast.LENGTH_SHORT).show();
//        playerList.setAdapter(new PlayerListAdapter(Lobby.this,playersinLobby));
//        playerList.setLayoutManager(new GridLayoutManager(Lobby.this,4));
//        Toast.makeText(Lobby.this, ""+playersinLobby, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Ids"+playerNames, Toast.LENGTH_SHORT).show();
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode).child("Players");
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.getValue().toString());
//                ValueEventListener valueEventListener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        Toast.makeText(Lobby.this, "Name: "+user.getName(), Toast.LENGTH_SHORT).show();
//                        playersinLobby.add(user);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                };
//                dbRef2.addListenerForSingleValueEvent(valueEventListener);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        dbRef.addChildEventListener(childEventListener);
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading Questions");
//        progressDialog.show();
        playerListAdapter = new PlayerListAdapter(this,playersinLobby);
        playerList.setLayoutManager(new GridLayoutManager(this,2));
        if(callingActivity.equals("JoinGame")) {
            DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode).child("Start");
            dbRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue().toString().equals("True")) {
                        Intent intent = new Intent(Lobby.this, GamePlayActivity.class);
//                    intent.putExtra("Players", (Serializable) playerNames);
//                    intent.putExtra("Questions", (Serializable) gameQuestions);
//                    intent.putExtra("Rounds",rounds);
                        intent.putExtra("Activity", callingActivity);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }

    private void getPlayers(String lobbyCode) {
//        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Players");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                   loadPlayer(dataSnapshot1.getValue().toString());
                }
                Toast.makeText(Lobby.this, "Players: "+playersinLobby.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void loadPlayer(String uid) {
//        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
//                Toast.makeText(Lobby.this, "Name: "+user.getName(), Toast.LENGTH_SHORT).show();
                playersinLobby.add(user);
                playerNames.add(user.getAlias());
//                Toast.makeText(Lobby.this, ""+user, Toast.LENGTH_SHORT).show();
                playerListAdapter.notifyItemInserted(playersinLobby.size()-1);
                playerList.setAdapter(playerListAdapter);
                Toast.makeText(Lobby.this, ""+playersinLobby.get(playersinLobby.size()-1).getAlias(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        try {
//            done.await(); //it will wait till the response is received from firebase.
//        } catch(InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void loadQuestions(String gameName) {
        Toast.makeText(this, ""+gameName, Toast.LENGTH_SHORT).show();
//        CountDownLatch done = new CountDownLatch(1);
        DatabaseReference quesRef = FirebaseDatabase.getInstance().getReference().child(gameName);
        quesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
//                    Toast.makeText(Lobby.this, ""+dataSnapshot1.getValue().toString(), Toast.LENGTH_SHORT).show();
                    gameQuestions.add(dataSnapshot1.getValue().toString());
                }
//                progressDialog.dismiss();
//                Toast.makeText(Lobby.this, ""+gameQuestions.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        try {
//            done.await(); //it will wait till the response is received from firebase.
//        } catch(InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}

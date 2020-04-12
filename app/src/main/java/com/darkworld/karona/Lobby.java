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
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Lobby extends AppCompatActivity {

    Button startGame,shareCode;
    RecyclerView playerList;
    List<User> playersinLobby;
    int rounds;
    PlayerListAdapter playerListAdapter;
    ProgressDialog progressDialog;
    List<String> playerNames,gameQuestions;
    String LobbyCode,GameName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        startGame = findViewById(R.id.start_game);
        LobbyCode = getIntent().getStringExtra("LobbyCode");
        playerList = findViewById(R.id.playerList);
        playersinLobby = new ArrayList<User>();
        playerNames = new ArrayList<String>();
        shareCode = findViewById(R.id.shareCode);
        progressDialog = new ProgressDialog(this);
        rounds=5;
        rounds=10;
        TextView textView = findViewById(R.id.LobbyCodeText);
        textView.setText(LobbyCode);
//        GameName = getIntent().getStringExtra("GameName");
        GameName = getIntent().getStringExtra("GameName");
        gameQuestions = new ArrayList<String>();
        final String callingActivity = getIntent().getStringExtra("Activity");
//        Toast.makeText(this, "Calling Activity: "+callingActivity, Toast.LENGTH_SHORT).show();
        shareCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Let's get started.Join Lobby on Karona with the Secret LobbyCode: "+LobbyCode);
                startActivity(Intent.createChooser(intent,"Share Lobby Code via"));
//
            }
        });
        if(callingActivity.equals("CreateGame"))
        {
            startGame.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "Questions: "+gameQuestions.size(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Questions: "+gameQuestions.size(), Toast.LENGTH_SHORT).show();
        }
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode);
                if(startGame.getText().equals("PREPARE GAME"))
                {
                    progressDialog.setMessage("Preparing Game...");
                    progressDialog.show();
                    loadQuestions(GameName);
                    for(int i=0;i<playersinLobby.size();i++)
                    {
                        dbRef2.child("Scores").child(playersinLobby.get(i).getUserId()).setValue(0);
                    }
//                    Toast.makeText(Lobby.this, "Questions: "+dbRef2.child("Questions").toString(), Toast.LENGTH_SHORT).show();
                }else {

                    dbRef2.child("Start").setValue("True");
                    Intent intent = new Intent(Lobby.this, GamePlayActivity.class);
                    intent.putExtra("LobbyCode", LobbyCode);
                    intent.putExtra("Activity", callingActivity);
                    intent.putExtra("GameName",GameName);
                    intent.putExtra("Activity", "Lobby");
                    startActivity(intent);
//                    Toast.makeText(Lobby.this, "Players: " + playerNames, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Lobby.this, "Questions: " + gameQuestions.size(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Lobby.this, "Players: " + playerNames, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Lobby.this, "Questions: " + gameQuestions.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        getPlayers(LobbyCode);
        playerListAdapter = new PlayerListAdapter(this,playersinLobby);
        playerList.setLayoutManager(new GridLayoutManager(this,2));
        playerList.setAdapter(playerListAdapter);
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
                        intent.putExtra("PlayersCount",playersinLobby.size());
                        intent.putExtra("LobbyCode",LobbyCode);
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
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadPlayer(dataSnapshot.getValue().toString());
                playerListAdapter.notifyItemInserted(playersinLobby.size()-1);
                playerList.setAdapter(playerListAdapter);
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
//                Toast.makeText(Lobby.this, ""+playersinLobby.get(playersinLobby.size()-1).getAlias(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(Lobby.this, ""+playersinLobby.get(playersinLobby.size()-1).getAlias(), Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(this, ""+gameName, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, ""+gameName, Toast.LENGTH_SHORT).show();
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
                shuffle_and_upload_questions();
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
    private void shuffle_and_upload_questions() {
        for(int i=0;i<rounds;i++) {
//                Toast.makeText(this, "Round: "+i, Toast.LENGTH_SHORT).show();
            Random random = new Random();
            String caughtPlayer = playerNames.get(random.nextInt((playerNames.size())));
            random = new Random();
            String caughtQuestion = gameQuestions.get(random.nextInt(gameQuestions.size()));
//                Toast.makeText(this, "PLayer:" + caughtPlayer + ",Question:" + caughtQuestion, Toast.LENGTH_SHORT).show();
//                caughtQuestion.replace("{0}",caughtPlayer);
//            if(caughtQuestion.contains("{0}"))
//                Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
            caughtQuestion = caughtQuestion.replace("{0}",caughtPlayer);
//                Toast.makeText(this, ""+caughtQuestion, Toast.LENGTH_SHORT).show();
//                question.setText(caughtQuestion);
            uploadQuestion(i,caughtQuestion);
        }
        startGame.setText("Start Game");
        progressDialog.dismiss();
    }
    private void uploadQuestion(int count, String caughtQuestion) {
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(LobbyCode).child("Questions");
        dbRef2.push().setValue(caughtQuestion);
    }
}

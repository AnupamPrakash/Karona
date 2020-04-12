package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SubmitLobby extends AppCompatActivity {

    String lobbyCode,round,ownResponse,currQuestion,UserId;
    User currentUser;
    TextView roundQuestion,roundResponse,playersCounter;
    RecyclerView submitList;
    ImageView userDP;
    DatabaseReference dbRef;
    List<String> responses;
    SubmitListAdapter submitListAdapter;
    List<User> players;
    List<Long> scores;
    int playersCount;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_lobby);
        lobbyCode = getIntent().getStringExtra("LobbyCode");
        round = getIntent().getStringExtra("Round");
        currentUser = (User) getIntent().getSerializableExtra("User");
        currQuestion = getIntent().getStringExtra("Question");
        ownResponse = getIntent().getStringExtra("Response");
        dbRef= FirebaseDatabase.getInstance().getReference().child("Users");
        roundQuestion = findViewById(R.id.roundQuestion);
        roundResponse = findViewById(R.id.roundResponse);
        submitList = findViewById(R.id.submitList);
        roundQuestion.setText(currQuestion);
        roundResponse.setText(ownResponse);
        playersCount = getIntent().getIntExtra("PlayersCount",5);
        responses = new ArrayList<String>();
        players = new ArrayList<User>();
        scores = new ArrayList<Long>();
        submitList.setLayoutManager(new LinearLayoutManager(this));
        submitListAdapter = new SubmitListAdapter(SubmitLobby.this,players,responses, scores, lobbyCode,playersCount);
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Rounds").child(round).child("Responses");
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                responses.add(dataSnapshot.getValue().toString());
                if(dataSnapshot.getKey().equals(currentUser.getUserId())==false)
                    loadPlayer(dataSnapshot.getKey());
                    loadScore(dataSnapshot.getKey());
//                playersCounter.setText(""+players.size());
//                Toast.makeText(SubmitLobby.this, ""+dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                submitListAdapter.notifyItemInserted(responses.size()-1);
                submitList.setAdapter(submitListAdapter);
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
    private void loadScore(String uid) {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Scores").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scores.add((Long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        dbRef.child(UserId).removeEventListener(valueEventListener);
    }

    private void loadPlayer(String uid) {
        UserId = uid;
        valueEventListener=dbRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                players.add(user);
//                Glide.with(SubmitLobby.this).load(Uri.parse(user.getPhotoUrl())).into(userDP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubmitLobby extends AppCompatActivity {

    String lobbyCode,round,ownResponse,currQuestion,UserId;
    User currentUser;
    TextView roundQuestion,roundResponse,playersCounter;
    RecyclerView submitList,submitPlayers;
    ImageView userDP;
    DatabaseReference dbRef;
    List<String> responses;
    SubmitListAdapter submitListAdapter;
    List<User> players;
    PlayerListAdapter playerListAdapter;
    Set<String> hash_resp;
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
        hash_resp = new HashSet<String>();
        currQuestion = getIntent().getStringExtra("Question");
        ownResponse = getIntent().getStringExtra("Response");
        dbRef= FirebaseDatabase.getInstance().getReference().child("Users");
        roundQuestion = findViewById(R.id.roundQuestion);
        roundResponse = findViewById(R.id.roundResponse);
        submitList = findViewById(R.id.submitList);
        submitPlayers = findViewById(R.id.submitPlayers);
        roundQuestion.setText(currQuestion);
        roundResponse.setText(ownResponse);
        playersCount = getIntent().getIntExtra("PlayersCount",5);
        responses = new ArrayList<String>();
        players = new ArrayList<User>();
        scores = new ArrayList<Long>();
        playerListAdapter = new PlayerListAdapter(this,players,false);
        submitPlayers.setLayoutManager(new GridLayoutManager(this,2));
        getSubmits(lobbyCode);
        submitList.setLayoutManager(new LinearLayoutManager(this));
        submitListAdapter = new SubmitListAdapter(SubmitLobby.this,players,responses, scores, lobbyCode,playersCount);
    }

    private void getSubmits(final String lobbyCode) {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Rounds").child(round).child("Responses");
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(!dataSnapshot.getKey().equals(currentUser.getUserId()))
//                {responses.add(dataSnapshot.getValue().toString());}
                responses.add(dataSnapshot.getValue().toString());
                loadPlayer(dataSnapshot.getKey());
                loadScore(dataSnapshot.getKey());

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

    private void loadPlayer(final String uid) {
        UserId = uid;
        dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
//                if(!uid.equals(currentUser.getUserId()))
                players.add(user);
                playerListAdapter.notifyItemInserted(players.size()-1);
                submitPlayers.setAdapter(playerListAdapter);
//                Glide.with(SubmitLobby.this).load(Uri.parse(user.getPhotoUrl())).into(userDP);
//                Toast.makeText(SubmitLobby.this, "Total Players"+playersCount+", Got Players:"+players.size(), Toast.LENGTH_SHORT).show();
                if(players.size()==playersCount)
                {
//                    Toast.makeText(SubmitLobby.this, ""+players.size()+","+playersCount, Toast.LENGTH_SHORT).show();
                    submitPlayers.setVisibility(View.GONE);
                    submitList.setVisibility(View.VISIBLE);
                    ProgressDialog progressDialog = new ProgressDialog(SubmitLobby.this);
                    progressDialog.setMessage("Shuffling responses...");
                    progressDialog.show();
                    submitList.setLayoutManager(new LinearLayoutManager(SubmitLobby.this));
//                    Toast.makeText(SubmitLobby.this, ""+responses, Toast.LENGTH_SHORT).show();
                    submitListAdapter = new SubmitListAdapter(SubmitLobby.this,players,responses, scores, lobbyCode,playersCount);
                    submitList.setAdapter(submitListAdapter);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void backPress(){
        super.onBackPressed();
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Toast.makeText(this, "Wait for others to submit answer.", Toast.LENGTH_SHORT).show();
    }
}

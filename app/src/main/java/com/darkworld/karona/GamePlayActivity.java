package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GamePlayActivity extends AppCompatActivity {

    List<String> players,questions,roundQuestions;
    int rounds;
    boolean isAdmin;
    EditText response;
    TextView question,countdownTime;
    Timer timer;
    Button submit;
    User currentUser;
    String lobbyCode;
    int countdown=0,round=0;
//    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        players = (List<String>) getIntent().getSerializableExtra("Players");
        questions = (List<String>) getIntent().getSerializableExtra("Questions");
        rounds = getIntent().getIntExtra("Rounds",5);
        submit = findViewById(R.id.submitResponse);
        countdownTime = findViewById(R.id.countdownTime);
        roundQuestions = new ArrayList<String>();
        lobbyCode = getIntent().getStringExtra("LobbyCode");
        response = findViewById(R.id.response);
//        i=0;
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
//            }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(getIntent().getStringExtra("Activity").equals("CreateGame"))
            isAdmin=true;
        else
            isAdmin=false;
        if(isAdmin) {
//            Toast.makeText(this, "" + players.size() + questions.size(), Toast.LENGTH_SHORT).show();
//            subbmit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    i++;
//                }
//            });
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading Game...");
            progressDialog.show();
            for(int i=0;i<rounds;i++) {
                question = findViewById(R.id.txtQuestion);
                question.setText("");
//                Toast.makeText(this, "Round: "+i, Toast.LENGTH_SHORT).show();
                Random random = new Random();
                String caughtPlayer = players.get(random.nextInt((players.size())));
                random = new Random();
                String caughtQuestion = questions.get(random.nextInt(questions.size()));
//                Toast.makeText(this, "PLayer:" + caughtPlayer + ",Question:" + caughtQuestion, Toast.LENGTH_SHORT).show();
//                caughtQuestion.replace("{0}",caughtPlayer);
                if(caughtQuestion.contains("{0}"))
                    Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
                caughtQuestion = caughtQuestion.replace("{0}","Default");
//                Toast.makeText(this, ""+caughtQuestion, Toast.LENGTH_SHORT).show();
//                question.setText(caughtQuestion);
                uploadQuestion(i,caughtQuestion);
            }
            progressDialog.dismiss();
        }
        loadQuestions(lobbyCode);
//        startGame();
    }

    private void startGame() {
        for(int i=0;i<10;i++) {
//            String ques = roundQuestions.get(i);
//             round=i;/
//            question.setText(ques);
//            new CountDownTimer(5000,1000){
//
//                @Override
//                public void onTick(long millisUntilFinished) {
////                    Toast.makeText(GamePlayActivity.this, "Timer: "+millisUntilFinished, Toast.LENGTH_SHORT).show();
//                    submit.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String resp = response.getText().toString();
//                            if(resp.length()==0)
//                                resp="No Comments";
//                            DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Rounds");
//                            dbRef.child("Round "+r).child("Responses").child(currentUser.getAlias()).setValue(resp);
//                        }
//                    });
//                }
//
//                @Override
//                public void onFinish() {
//                    Toast.makeText(GamePlayActivity.this, "Time Up", Toast.LENGTH_SHORT).show();
//                }
//            }.start();
//            countdown=0;
            countdownTime.setText(""+i);
            CountDownTimer countDownTimer = new CountDownTimer(5000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    countdown++;
                }

                @Override
                public void onFinish() {
                    countdownTime.setText(""+countdown);
                }
            }.start();
        }

    }

    private void loadQuestions(String lobbyCode) {
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Rounds");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                   roundQuestions.add(dataSnapshot1.child("Question").getValue().toString());
                }
                startGame();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef2.addValueEventListener(valueEventListener);
    }


    private void uploadQuestion(int count, String caughtQuestion) {
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Rounds").child("Round "+count);
        dbRef2.child("Question").setValue(caughtQuestion);
    }
}

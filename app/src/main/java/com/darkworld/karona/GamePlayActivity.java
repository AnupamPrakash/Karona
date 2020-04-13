package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GamePlayActivity extends AppCompatActivity {

    List<String> players,questions,roundQuestions;
    int rounds;
    boolean isAdmin;
    EditText response;
    TextView question,countdownTime,roundCounter;
    Timer timer;
    ImageView userdp;
    Button submit;
    private static final long COUNTDOWN_IN_MILLIS = 30000;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    User currentUser;
    long score;
    String lobbyCode,callingActivity,gameName;
    int countPlayers=0,round=0;
//    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        rounds=10;
        callingActivity = getIntent().getStringExtra("Activity");
        lobbyCode = getIntent().getStringExtra("LobbyCode");
        countPlayers = getIntent().getIntExtra("PlayersCount", 5);
//            rounds = getIntent().getIntExtra("Rounds", 5);
//        Toast.makeText(this, "Players Count"+countPlayers, Toast.LENGTH_SHORT).show();
        gameName = getIntent().getStringExtra("GameName");
        loadQuestions(lobbyCode);

        submit = findViewById(R.id.submitResponse);
        roundCounter =  findViewById(R.id.roundCounter);
        userdp = findViewById(R.id.userDpGamePlay);
        countdownTime = findViewById(R.id.countdownTime);
        question = findViewById(R.id.txtQuestion);
        roundQuestions = new ArrayList<String>();
        response = findViewById(R.id.response);
//        i=0;
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if(!currentUser.getPhotoUrl().equals("Null"))
                    Glide.with(GamePlayActivity.this).load(Uri.parse(currentUser.getPhotoUrl())).into(userdp);

//            }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateScore(String userId) {
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Scores").child(userId);
        dbRef.setValue(score+4);
    }

    private void startGame() {
      showNextQuestion();
      submit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              submitResponse();
//              showNextQuestion();
          }
      });
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeLeftInMillis=COUNTDOWN_IN_MILLIS;
//        startGame();
    }

    private void submitResponse() {
        countDownTimer.cancel();
        String resp = response.getText().toString();
        if(resp.length()==0)
            resp="No Comments";
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Rounds").child("Round "+round);
        dbRef2.child("Question").setValue(question.getText().toString());
        dbRef2.child("Responses").child(currentUser.getUserId()).setValue(resp);
//        dbRef2.child("Responses").child(currentUser.getAlias()).child("UserDp").setValue(currentUser.getPhotoUrl());
        Intent intent = new Intent(GamePlayActivity.this,SubmitLobby.class);
        intent.putExtra("LobbyCode",lobbyCode);
        intent.putExtra("Round","Round "+round);
        intent.putExtra("User",currentUser);
        intent.putExtra("PlayersCount",countPlayers);
        intent.putExtra("Question",roundQuestions.get(round-1));
        intent.putExtra("Response",resp);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        countDownTimer.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this, "Round "+round+", Out of "+rounds, Toast.LENGTH_SHORT).show();
        showNextQuestion();
    }

    private void showNextQuestion() {
        if(round<rounds)
        {
            roundCounter.setText("Round "+(round+1));
            String ques = roundQuestions.get(round);
            question.setText(ques);
            response.setText("");
            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        }
        else
        {
            finishGame();
        }
        round++;
    }

    private void startCountDown() {

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                countDownTimer.cancel();
                updateCountDownText();
                submitResponse();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countdownTime.setText(timeFormatted);

        if (timeLeftInMillis < 5000) {
            countdownTime.setTextColor(Color.RED);
        } else {
            countdownTime.setTextColor(Color.BLUE);
        }
    }

    private void finishGame() {
        Intent intent = new Intent(GamePlayActivity.this,Scoreboard.class);
        intent.putExtra("LobbyCode",lobbyCode);
        intent.putExtra("GameName",gameName);
        startActivity(intent);
    }

    private void loadQuestions(String lobbyCode) {
        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Lobbies").child(lobbyCode).child("Questions");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                   roundQuestions.add(dataSnapshot1.getValue().toString());
                }
                startGame();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef2.addValueEventListener(valueEventListener);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Toast.makeText(this, "Can't exit game during play", Toast.LENGTH_SHORT).show();
    }
}

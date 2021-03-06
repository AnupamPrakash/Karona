package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button login_here;
    EditText register_name, register_email, register_password;
    Button registerButton;
    ProgressDialog progressdialog;
    List<String> gameNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        gameNames = new ArrayList<String>();
        login_here = findViewById(R.id.alreadyAccount);
        login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finish();
            }
        });
        register_name = findViewById(R.id.input_alias);
        register_email = findViewById(R.id.input_email);
        register_password = findViewById(R.id.input_password);
        registerButton = findViewById(R.id.signupBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, password;
                name = register_name.getText().toString();
                email = register_email.getText().toString();
                password = register_password.getText().toString();
                if(name.length()==0)
                {
                    Toast.makeText(SignupActivity.this, "UserAlias cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(email.length()==0){
                    Toast.makeText(SignupActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(!isEmailValid(email))
                {
                    Toast.makeText(SignupActivity.this, "Enter valid EmailId", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(SignupActivity.this, "Enter password of more than 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressdialog = new ProgressDialog(SignupActivity.this);
                    progressdialog.setMessage("Registering...");
                    progressdialog.show();
                    registerUser(name, email, password);
                }
            }
        });
        getGames();
    }
    private void getGames() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Games");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    gameNames.add((String) dataSnapshot1.child("name").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    boolean isEmailValid(CharSequence email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void registerUser(final String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, "Successfully registered ", Toast.LENGTH_SHORT).show();
                            dbUserEntry(name, user);
                            progressdialog.dismiss();
                            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                            finish();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void dbUserEntry(String name, FirebaseUser user) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        mDatabase.child("name").setValue(name);
        mDatabase.child("userId").setValue(user.getUid());
        mDatabase.child("emailId").setValue(user.getEmail());
        mDatabase.child("alias").setValue("Default");
        mDatabase.child("photoUrl").setValue("Null");
        for(int i=0;i<gameNames.size();i++)
        {
            mDatabase.child("Scores").child(gameNames.get(i)).setValue(0);
        }
    }
}

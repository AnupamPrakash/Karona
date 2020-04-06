package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button login_here;
    EditText register_name, register_email, register_password;
    Button registerButton;
    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
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
                progressdialog = new ProgressDialog(SignupActivity.this);
                progressdialog.setMessage("Registering...");
                progressdialog.show();
                registerUser(name, email, password);
            }
        });
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
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
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
    }
}

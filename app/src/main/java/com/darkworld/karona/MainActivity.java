package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    EditText email,password;
    Button loginBtn,newSignup;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressdialog;
    SignInButton signInButton;
    FirebaseUser currentUser;
    String alias="Default";
    User customUser;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth= FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null)
            startActivity(new Intent(MainActivity.this,DashboardActivity.class));
        newSignup = findViewById(R.id.newAccount);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signInButton = findViewById(R.id.googleSignIn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail,strPassword;
                strEmail = email.getText().toString();
                strPassword = password.getText().toString();
                if(strEmail.length()==0 || strPassword.length()<6 || !isEmailValid(strEmail))
                {
                    Toast.makeText(MainActivity.this, "Enter Correct Credentials", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressdialog = new ProgressDialog(MainActivity.this);
                    progressdialog.setMessage("Authenticating...");
                    progressdialog.show();
                    loginEmailPassword(strEmail, strPassword);
                }
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressdialog = new ProgressDialog(MainActivity.this);
                progressdialog.setMessage("Authenticating...");
                progressdialog.show();
                loginWithGoogle();
            }
        });
        newSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignupActivity.class));
                finish();
            }
        });
    }
    boolean isEmailValid(CharSequence email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void loginEmailPassword(String strEmail, String strPassword) {
        firebaseAuth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            progressdialog.dismiss();
                            dbUserEntry(user);
                            Snackbar.make(findViewById(R.id.linearlayout), "Succesfully signed in", Snackbar.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            progressdialog.dismiss();
                            Snackbar.make(findViewById(R.id.linearlayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void dbUserEntry(FirebaseUser user) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
//        getAlias();
        customUser= new User(user.getDisplayName(),user.getUid(),user.getEmail(),alias,user.getPhotoUrl().toString());
        mDatabase.setValue(customUser);
    }

    private void getAlias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alias Name");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                alias = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            updateUI(user);
                            progressdialog.dismiss();
                            dbUserEntry(user);
                            Snackbar.make(findViewById(R.id.linearlayout), "Succesfully signed in", Snackbar.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.linearlayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);

                        }

                        // ...
                    }
                });
    }


}

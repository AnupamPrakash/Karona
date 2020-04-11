package com.darkworld.karona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfilePage extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ImageView img;
    Uri imageUri;
    Uri downloadUrl;
    Uri resultUri;
    User user;
    ImageView back;
    TextView username,useremail,useralias;
    MaterialCardView editAlias;
    RecyclerView scoreList;
    List<Long> scores;
    List<String> games;
    ProgressDialog progressDialog;
    ProfileScoreListAdapter profileScoreListAdapter;
    StorageReference firebaseStorage;
    ValueEventListener userEventListener,scoreEventListener;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        back=findViewById(R.id.back);
        username = findViewById(R.id.userName);
        useremail = findViewById(R.id.userEmail);
        img = findViewById(R.id.profile_pic);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Profile...");
        progressDialog.show();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        games = new ArrayList<String>();
        scores = new ArrayList<Long>();
        useralias = findViewById(R.id.userAlias);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editAlias = findViewById(R.id.aliascard);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        editAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePage.this);
                builder.setTitle("Join Lobby");
                final EditText input = new EditText(ProfilePage.this);
                builder.setView(input);
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(input.getText().toString().length()>0) {
                          databaseReference.child("alias").setValue(input.getText().toString());
                          getUser();
                        }
                        else
                        {
                            Toast.makeText(ProfilePage.this, "Alias Name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
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
        });
        games.clear();
        scores.clear();
        getUser();
        scoreList = findViewById(R.id.profileScores);
        profileScoreListAdapter = new ProfileScoreListAdapter(ProfilePage.this,scores,games);
        scoreList.setLayoutManager(new LinearLayoutManager(this));
        scoreList.setAdapter(profileScoreListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(userEventListener);
        databaseReference.child("Scores").removeEventListener(scoreEventListener);
    }

    private void getUser() {
        userEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
//                Toast.makeText(ProfilePage.this, "UserName:"+user.getName()+user.getAlias()+user.getPhotoUrl(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                useralias.setText(user.getAlias());
                useremail.setText(user.getEmailId());
                username.setText(user.getName());
                if(!user.getPhotoUrl().equals("Null"))
                    Glide.with(ProfilePage.this).load(Uri.parse(user.getPhotoUrl())).into(img);
                scoreEventListener=databaseReference.child("Scores").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            games.add(dataSnapshot1.getKey());
                            scores.add((Long) dataSnapshot1.getValue());
//                            profileScoreListAdapter.notifyItemInserted(scores.size()-1);
                        }
                        scoreList.setAdapter(profileScoreListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {
                resultUri = result.getUri();
            }
            try {
                progressDialog.setMessage("Updating Profile Pic");
                progressDialog.show();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                img = (ImageView) findViewById(R.id.profile_pic);
                img.setImageBitmap(bitmap);
                firebaseStorage = FirebaseStorage.getInstance().getReference("android/profile_pics/"+user.getUserId());
                UploadTask uploadTask = firebaseStorage.putFile(resultUri);
                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(ProfilePage.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();
                        }
                        return firebaseStorage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            String photoUrl = task.getResult().toString();
                            databaseReference.child("photoUrl").setValue(photoUrl);
                            progressDialog.dismiss();
                        }
                    }
                });


            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

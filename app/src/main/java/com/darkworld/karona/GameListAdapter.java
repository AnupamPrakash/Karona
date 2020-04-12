package com.darkworld.karona;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHelper> {
    Context context;
    List<Game> gamelist;
    FirebaseUser currentUser;

    public GameListAdapter(Context context, List<Game> gamelist, FirebaseUser currentUser) {
        this.context = context;
        this.gamelist = gamelist;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public GameViewHelper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.game_item_card,parent,false);
        return new GameViewHelper(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHelper holder, int position) {
        final Game game = gamelist.get(position);
        holder.gameName.setText(game.getGameName());
        Bitmap imageBitmap = null;
        try
        {
            imageBitmap = decodeFromFirebase64(game.getGameLogo());
        } catch (Exception e){
            e.printStackTrace();
        }
        holder.linearLayout.setBackground(new BitmapDrawable(imageBitmap));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateLobby(game);
            }
        });
    }

    private Bitmap decodeFromFirebase64(String gameLogo) {
        byte [] decodebyteArray = android.util.Base64.decode(gameLogo, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodebyteArray,0,decodebyteArray.length);
    }

    @Override
    public int getItemCount() {
        return gamelist.size();
    }
    private void generateLobby(final Game game) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create Lobby");
        Random random = new Random(System.currentTimeMillis());
        final int rnd = 100000+random.nextInt(500000);
        builder.setMessage(""+rnd);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context,Lobby.class);
                intent.putExtra("Activity","CreateGame");
                intent.putExtra("LobbyCode",""+rnd);
                intent.putExtra("GameName",game.getGameName());
                createLobbyonServer(rnd,game);
                context.startActivity(intent);
            }
        });
        builder.show();
    }

    private void createLobbyonServer(int rnd, Game game) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = firebaseDatabase.getReference().child("Lobbies").child(""+rnd);
        dbRef.child("GameCode").setValue(game.getGameId());
        dbRef.child("Players").push().setValue(currentUser.getUid());
        dbRef.child("Start").setValue("False");
//        Toast.makeText(context, "Successful: "+dbRef.getDatabase().toString(), Toast.LENGTH_SHORT).show();
    }
    public class GameViewHelper extends RecyclerView.ViewHolder {
        TextView gameName;
        LinearLayout linearLayout;
        public GameViewHelper(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            linearLayout = itemView.findViewById(R.id.cardLogo);
        }
    }
}

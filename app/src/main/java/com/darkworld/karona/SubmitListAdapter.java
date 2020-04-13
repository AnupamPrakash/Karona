package com.darkworld.karona;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SubmitListAdapter extends RecyclerView.Adapter<SubmitListAdapter.SubmitListView> {
    Context context;
    List<User> players;
    List<String> responses;
    List<Long> scores;
    String lobbyCode;
    int playersCount;
    DatabaseReference databaseReference;

    public SubmitListAdapter(Context context, List<User> players, List<String> responses, List<Long> scores, String lobbyCode, int playersCount) {
        this.context = context;
        this.players = players;
        this.responses = responses;
        this.scores = scores;
        this.lobbyCode = lobbyCode;
        this.playersCount = playersCount;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public SubmitListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.submit_card,parent,false);
        return new SubmitListView(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SubmitListView holder, final int position) {
        if(players.size()>0) {
            holder.response.setClickable(true);
            holder.layout.setBackgroundColor(R.color.white);
        }
        final User player = players.get(position);
        String response = responses.get(position);
        Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
        holder.response.setText(response);
        holder.response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You selected "+player.getAlias()+"'s answer", Toast.LENGTH_SHORT).show();
                databaseReference.child("Lobbies").child(lobbyCode).child("Scores").child(player.getUserId()).setValue(scores.get(position)+4);
                ((SubmitLobby)context).backPress();
            }
        });
    }


    @Override
    public int getItemCount() {
        return players.size();
    }

    public class SubmitListView extends RecyclerView.ViewHolder{

        TextView response;
        LinearLayout layout;
        public SubmitListView(@NonNull View itemView) {
            super(itemView);
            response = itemView.findViewById(R.id.userResponse);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
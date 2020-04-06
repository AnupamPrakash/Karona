package com.darkworld.karona;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    public SubmitListAdapter(Context context, List<User> players, List<String> responses, List<Long> scores,String lobbyCode) {
        this.context = context;
        this.players = players;
        this.responses = responses;
        this.scores = scores;
        this.lobbyCode = lobbyCode;
    }

    @NonNull
    @Override
    public SubmitListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.submit_card,parent,false);
        return new SubmitListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmitListView holder, final int position) {
        final User player = players.get(position);
        String response = responses.get(position);
        holder.response.setText(response);
        holder.userAlias.setText(player.getAlias());
        Glide.with(context).load(Uri.parse(player.getPhotoUrl())).into(holder.userDp);
        holder.vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long score = scores.get(position);
                score+=4;
                Intent intent = new Intent(context,GamePlayActivity.class);

            }
        });
    }


    @Override
    public int getItemCount() {
        return players.size();
    }

    public class SubmitListView extends RecyclerView.ViewHolder{

        TextView userAlias,response;
        ImageView userDp;
        Button vote;
        public SubmitListView(@NonNull View itemView) {
            super(itemView);
            userAlias = itemView.findViewById(R.id.userAlias);
            response = itemView.findViewById(R.id.userResponse);
            userDp = itemView.findViewById(R.id.userDp);
            vote = itemView.findViewById(R.id.vote);
        }
    }
}
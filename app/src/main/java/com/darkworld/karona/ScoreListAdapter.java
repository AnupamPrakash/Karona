package com.darkworld.karona;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ScoreViewHelper> {
    Context context;
    List<Long> scores;
    List<User> players;

    public ScoreListAdapter(Context context, List<Long> scores, List<User> players) {
        this.context = context;
        this.scores = scores;
        this.players = players;
    }

    @NonNull
    @Override
    public ScoreViewHelper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.score_card,parent,false);
        return new ScoreViewHelper(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHelper holder, int position) {
        Toast.makeText(context, "PlayerScore:"+players.get(position).getAlias()+"="+scores.get(position).toString(), Toast.LENGTH_SHORT).show();
        holder.userScore.setText(""+scores.get(position));
        holder.userAlias.setText(""+players.get(position).getAlias());
        Glide.with(context).load(Uri.parse(players.get(position).getPhotoUrl())).into(holder.userDp);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class ScoreViewHelper extends RecyclerView.ViewHolder {
        TextView userAlias,userScore;
        ImageView userDp;
        public ScoreViewHelper(@NonNull View itemView) {
            super(itemView);
            userAlias = itemView.findViewById(R.id.userAlias);
            userScore = itemView.findViewById(R.id.userScore);
            userDp = itemView.findViewById(R.id.userDP);
        }
    }
}

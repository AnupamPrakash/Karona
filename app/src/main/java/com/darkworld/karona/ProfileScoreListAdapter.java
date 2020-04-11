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


public class ProfileScoreListAdapter extends RecyclerView.Adapter<ProfileScoreListAdapter.ScoreViewHelper> {
    Context context;
    List<Long> scores;
    List<String> games;

    public ProfileScoreListAdapter(Context context, List<Long> scores, List<String> games) {
        this.context = context;
        this.scores = scores;
        this.games = games;
    }

    @NonNull
    @Override
    public ScoreViewHelper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.profile_score_card,parent,false);
        return new ScoreViewHelper(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHelper holder, int position) {
        holder.gameName.setText(games.get(position));
        holder.gameScore.setText(""+scores.get(position));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ScoreViewHelper extends RecyclerView.ViewHolder {
        TextView gameName,gameScore;
        public ScoreViewHelper(@NonNull View itemView) {
            super(itemView);
           gameName = itemView.findViewById(R.id.gameName);
           gameScore = itemView.findViewById(R.id.gameScore);
        }
    }
}

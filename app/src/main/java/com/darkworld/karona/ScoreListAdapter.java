package com.darkworld.karona;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
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
    List<Pair<User, Long>> gameScores;

    public ScoreListAdapter(Context context, List<Pair<User, Long>> gameScores) {
        this.context = context;
        this.gameScores = gameScores;
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
//        Toast.makeText(context, "PlayerScore:"+players.get(position).getAlias()+"="+scores.get(position).toString(), Toast.LENGTH_SHORT).show();
        holder.userScore.setText(""+gameScores.get(position).second);
        holder.userAlias.setText(""+gameScores.get(position).first.getAlias());
        if(!gameScores.get(position).first.getPhotoUrl().equals("Null"))
            Glide.with(context).load(Uri.parse(gameScores.get(position).first.getPhotoUrl())).into(holder.userDp);
    }

    @Override
    public int getItemCount() {
        return gameScores.size();
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

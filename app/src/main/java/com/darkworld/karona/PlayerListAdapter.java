package com.darkworld.karona;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder> {
    Context context;
    List<User> players;
    boolean isLobby;
    public PlayerListAdapter(Context context, List<User> players,boolean isLobby) {
        this.context = context;
        this.players = players;
        this.isLobby=isLobby;
    }

    @NonNull
    @Override
    public PlayerListAdapter.PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.player_item_card,parent,false);
        return new PlayerListAdapter.PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListAdapter.PlayerViewHolder holder, int position) {
        User player = players.get(position);
        if(isLobby)
        holder.userAlias.setText(player.getAlias());
        if(!player.getPhotoUrl().toString().equals("Null"))
            Glide.with(context).load(Uri.parse(player.getPhotoUrl())).into(holder.userDp);
        Log.d("PlayerListSize",""+players.size());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView userAlias;
        ImageView userDp;
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            userAlias = itemView.findViewById(R.id.playername);
            userDp = itemView.findViewById(R.id.playerdp);
        }
    }
}

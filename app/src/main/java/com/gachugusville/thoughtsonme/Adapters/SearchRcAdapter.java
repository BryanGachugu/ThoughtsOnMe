package com.gachugusville.thoughtsonme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gachugusville.thoughtsonme.OtherActivities.TargetDetailsActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRcAdapter extends RecyclerView.Adapter<SearchRcAdapter.SearchRCViewHolder> {

    Context context;
    List<User> usersList;

    public SearchRcAdapter(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @NotNull
    @Override
    public SearchRCViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_review_layout, parent, false);
        return new SearchRCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchRCViewHolder holder, int position) {

        holder.item_layout.setOnClickListener(v -> {
            Intent targetDetails = new Intent(context, TargetDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user_instance", usersList.get(position));
            targetDetails.putExtras(bundle);
            context.startActivity(targetDetails);

        });

        Picasso.get()
                .load(usersList.get(position).getUser_profile_pic())
                .placeholder(R.drawable.person_icon)
                .into(holder.user_profile);

        holder.txt_user_name.setText(usersList.get(position).getUser_name());
        if (usersList.get(position).getTag_line() != null) holder.user_tagLine.setText(usersList.get(position).getTag_line());
        holder.user_num_of_reviews.setText(String.valueOf(usersList.get(position).getNum_reviews()));
        holder.num_positivity_rate.setText(String.valueOf(usersList.get(position).getPositivity_rate()));

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class SearchRCViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout item_layout;
        CircleImageView user_profile;
        TextView user_num_of_reviews, txt_user_name, user_tagLine, num_positivity_rate;

        public SearchRCViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txt_user_name = itemView.findViewById(R.id.txt_target_name);
            user_profile = itemView.findViewById(R.id.user_profile);
            item_layout = itemView.findViewById(R.id.item_layout);
            user_tagLine = itemView.findViewById(R.id.user_tagLine);
            num_positivity_rate = itemView.findViewById(R.id.num_positivity_rate);
            user_num_of_reviews = itemView.findViewById(R.id.target_num_of_reviews);


        }
    }
}

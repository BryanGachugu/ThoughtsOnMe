package com.gachugusville.thoughtsonme.CHAT.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachugusville.thoughtsonme.CHAT.ChatModel;
import com.gachugusville.thoughtsonme.CHAT.MessagesActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatModel> chatList;
    private final Context context;
    private User opposite_details;
    private String image_url;
    private String name_to_display;
    private String userToPassID;
    private int unread_count;

    public ChatAdapter(List<ChatModel> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        FirebaseDatabase.getInstance().getReference("chats").child(chatList.get(position).getChat_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        ChatAdapter.this.unread_count = (int) snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Unread").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        chatList.get(position).setHis_details(chatList.get(position).getHis_details());
        holder.last_message.setText(chatList.get(position).getLast_message());


        if (chatList.get(position).getFirst_sender().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            image_url = chatList.get(position).getHis_details().getUser_profile_pic();
            name_to_display = chatList.get(position).getHis_details().getUser_name();
            userToPassID = chatList.get(position).getHis_details().getUser_doc_id();
            Picasso.get().load(image_url).placeholder(R.drawable.person_icon).fit().centerInside().into(holder.sender_dp);
            holder.sender_nickname.setText(chatList.get(position).getHis_details().getUser_name());
        }else {
            FirebaseFirestore.getInstance().collection("Users").document(chatList.get(position).getFirst_sender()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        this.opposite_details =  documentSnapshot.toObject(User.class);
                        if (this.opposite_details != null) {

                            image_url = opposite_details.getPrivate_profile_pic();
                            name_to_display = opposite_details.getPrivate_name();
                            userToPassID = opposite_details.getUser_doc_id();
                            holder.sender_nickname.setText(name_to_display);
                            Picasso.get().load(image_url).placeholder(R.drawable.person_icon).into(holder.sender_dp);


                        }
                    });
        }

        if (unread_count > 1){
            holder.txt_unread_count.setText(unread_count);
            holder.txt_unread_count.setVisibility(View.VISIBLE);
        }else holder.txt_unread_count.setVisibility(View.GONE);

        holder.chat_layout.setOnClickListener(view -> {

            Intent intent = new Intent(context, MessagesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("chat_instance", chatList.get(position));
            bundle.putString("image_url", image_url);
            bundle.putString("name_to_display", name_to_display);
            bundle.putString("passed_user", userToPassID);
            bundle.putString("FROM_A", "chat");
            intent.putExtras(bundle);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView sender_nickname, last_message;
        CircleImageView sender_dp;
        TextView txt_unread_count;
        LinearLayout chat_layout;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            sender_dp = itemView.findViewById(R.id.sender_dp);
            chat_layout = itemView.findViewById(R.id.chat_layout);
            sender_nickname = itemView.findViewById(R.id.sender_nickname);
            last_message = itemView.findViewById(R.id.last_message);
            txt_unread_count = itemView.findViewById(R.id.txt_unread_count);

        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }
}

package com.gachugusville.thoughtsonme.CHAT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachugusville.thoughtsonme.CHAT.Adapters.ChatAdapter;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationListActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private List<ChatModel> chatList;
    private ChatAdapter rcAdapter;
    private User current_user;
    private RecyclerView conversations_rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        conversations_rc = findViewById(R.id.conversations_rc);
        CircleImageView small_dp = findViewById(R.id.small_dp);
        findViewById(R.id.img_back).setOnClickListener(view -> super.onBackPressed());

        getExtras();

        Picasso.get().load(current_user.getPrivate_profile_pic()).placeholder(R.drawable.person_icon).into(small_dp);
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }

        chatList = new ArrayList<>();

    }

    private void loadAllChats() {
        FirebaseFirestore.getInstance()
                .collection("Chats")
                .whereArrayContains("users_in_chat", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(this, (value, error) -> {
                    if (value != null){
                        chatList.clear();
                        conversations_rc.removeAllViews();
                        for (DocumentChange documentChange : value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                {
                                    database.getReference("Chats").child(documentChange.getDocument().getId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @SuppressLint("NotifyDataSetChanged")
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {

                                                        ChatModel chatModel = new ChatModel();
                                                        chatModel.setChat_id(documentChange.getDocument().getId());
                                                        chatModel.setLast_message((String) snapshot.child("last_message").getValue());
                                                        chatModel.setHis_details(snapshot.child("his_details").getValue(User.class));
                                                        chatModel.setFirst_sender((String) snapshot.child("first_sender").getValue());

                                                        chatList.add(chatModel);

                                                        findViewById(R.id.anim_loading).setVisibility(View.GONE);
                                                        findViewById(R.id.lyt_empty_inbox).setVisibility(View.GONE);

                                                        LinearLayoutManager layoutManager = new LinearLayoutManager(ConversationListActivity.this, LinearLayoutManager.VERTICAL, false);
                                                        rcAdapter = new ChatAdapter(chatList, ConversationListActivity.this);
                                                        conversations_rc.setHasFixedSize(true);
                                                        conversations_rc.setAdapter(rcAdapter);
                                                        conversations_rc.setNestedScrollingEnabled(false);
                                                        conversations_rc.setLayoutManager(layoutManager);



                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        }

                    }else {
                        findViewById(R.id.anim_loading).setVisibility(View.GONE);
                        findViewById(R.id.lyt_empty_inbox).setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        current_user = (User) bundle.getSerializable("current_user");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatList.clear();
        conversations_rc.removeAllViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllChats();
    }
}
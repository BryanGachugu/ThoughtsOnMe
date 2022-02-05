package com.gachugusville.thoughtsonme.CHAT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachugusville.thoughtsonme.CHAT.Adapters.MessageAdapter;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.LinearLayoutWrapper;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    private static final String TAG = "MessagesError";
    private static ChatModel chatModel;
    private TextView his_nickname, his_online_status;
    private RecyclerView messages_rc;
    private static List<Message> messages;
    private List<String> users_in_chat;
    private EditText edt_message_text_input;
    private MessageAdapter messageAdapter;
    private ProgressBar progress_circular_messages;
    private User his_details;
    private DatabaseReference messagesReference, chatRef;
    private LinearLayout reply_view;
    private String passed_user;
    private User passedUser;
    private CircleImageView his_dp;
    private long unread_count1, unread_count2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

         his_dp = findViewById(R.id.his_dp);
        his_nickname = findViewById(R.id.his_nickname);
        his_online_status = findViewById(R.id.his_online_status);
        messages_rc = findViewById(R.id.messages_rc);
        edt_message_text_input = findViewById(R.id.edt_message_text_input);
        progress_circular_messages = findViewById(R.id.progress_circular_messages);
        progress_circular_messages.setVisibility(View.VISIBLE);
        reply_view = findViewById(R.id.reply_view);

        chatModel = new ChatModel();
        users_in_chat = new ArrayList<>();

        getExtras();

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(passed_user);

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                passedUser = snapshot.toObject(User.class);
                assert passedUser != null;
               if (passedUser.getUser_online_status() != null){
                   if (passedUser.getUser_online_status().equals("online")){
                       his_online_status.setText(MessagesActivity.this.getString(R.string.online));
                   }else his_online_status.setText(MessagesActivity.this.getString(R.string.offline));
               } else his_online_status.setText("");

            } else {
                Log.d(TAG, "Current data: null");
            }
        });



        messagesReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatModel.getChat_id()).child("Messages");
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatModel.getChat_id());

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        unread_count1 = snapshot.child(his_details.getUser_doc_id()+"Unread").getValue(Integer.class);
                        unread_count2 = snapshot.child(chatModel.getFirst_sender()+"Unread").getValue(Integer.class);
                    }catch (Exception e){
                        Log.d("MCount", e.getLocalizedMessage());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.back_layout).setOnClickListener(view -> super.onBackPressed());
        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        messages_rc.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom && Objects.requireNonNull(messages_rc.getAdapter()).getItemCount() > 1) {
                messages_rc.smoothScrollToPosition(Objects.requireNonNull(messages_rc.getAdapter()).getItemCount() - 1);
            }
        });

        messages = new ArrayList<>();

        readMessages();

        LinearLayoutManager layoutManager = new LinearLayoutWrapper(this, LinearLayoutManager.VERTICAL, false);
        messageAdapter = new MessageAdapter(MessagesActivity.this, messages, reply_view, messages_rc, chatModel.getChat_id());
        messages_rc.setAdapter(messageAdapter);
        messages_rc.setHasFixedSize(true);
        messages_rc.setLayoutManager(layoutManager);

    }

    private void readMessages() {
        progress_circular_messages.setVisibility(View.VISIBLE);
        messagesReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    messages.clear();
                    for (DataSnapshot messageSnap : snapshot.getChildren()){

                        Message new_message = messageSnap.getValue(Message.class);
                        assert new_message != null;
                        new_message.setMessage_id(messageSnap.getKey());
                        messages.add(new_message);
                        messageAdapter.notifyDataSetChanged();

                        chatRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"Unread").setValue(0);

                            //Ensuring the scroll to bottom works
                            if (messages.size() > 3) messages_rc.smoothScrollToPosition(Objects.requireNonNull(messages_rc.getAdapter()).getItemCount() - 1);
                            progress_circular_messages.setVisibility(View.GONE);

                        //Update message status to read if it was sent by him
                        if (!(new_message.getSender_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))) {
                            messagesReference.child(Objects.requireNonNull(messageSnap.getKey())).child("DELIVERED_READ_STATUS").setValue(3);
                        }
                    }
                }else progress_circular_messages.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage() {

        Message message = new Message();
        message.setSender_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        message.setMessage_content(edt_message_text_input.getText().toString());
        message.setText_replied_to(messageAdapter.getRepliedText());
        message.setDELIVERED_READ_STATUS(1);

        Map<String, Object> messageTime = new HashMap<>();
        messageTime.put("timestamp", Timestamp.now());
        messageTime.put("sender_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        messageTime.put("message_content", edt_message_text_input.getText().toString());
        messageTime.put("his_details", his_details);
        messageTime.put("sender_name", User.getInstance().getUser_name());
        messageTime.put("text_replied_to", messageAdapter.getRepliedText());
        messageTime.put("deleted", "");
        messageTime.put("DELIVERED_READ_STATUS", 1);
        edt_message_text_input.setText("");
        reply_view.setVisibility(View.GONE);

        messages.add(message);
        messageAdapter.notifyItemInserted(messages.indexOf(message));
        if (Objects.requireNonNull(messages_rc.getAdapter()).getItemCount() > 2) {
            messages_rc.smoothScrollToPosition(messages_rc.getAdapter().getItemCount());
        }

        Map<String, Object> chatModelMap = new HashMap<>();
        chatModelMap.put("his_details", his_details);
        chatModelMap.put("VISIBILITY_STATUS", chatModel.getVISIBILITY_STATUS());
        chatModelMap.put("chat_id", chatModel.getChat_id());
        chatModelMap.put("sender_id", chatModel.getHis_details());
        chatModelMap.put("first_sender", chatModel.getFirst_sender());
        chatModelMap.put("users_in_chat", users_in_chat);
        chatModelMap.put(his_details.getUser_doc_id(), Boolean.TRUE);
        chatModelMap.put("last_message", message.getMessage_content());
        chatModelMap.put("time", Timestamp.now());

        Map<String, Object> collectionUpdates = new HashMap<>();
        collectionUpdates.put("users_in_chat", users_in_chat);
        collectionUpdates.put("time", Timestamp.now());

        FirebaseFirestore.getInstance()
                .collection("Chats")
                .document(chatModel.getChat_id())
                .set(collectionUpdates)
                .addOnSuccessListener(runnable -> {

                    Log.d("Success", "sucess");
                    if (!his_details.getUser_doc_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        chatModelMap.put(his_details.getUser_doc_id() + "Unread", unread_count1 + 1);
                    } else {
                        chatModelMap.put(chatModel.getFirst_sender() + "Unread", unread_count2 + 1);
                    }

                    chatRef.updateChildren(chatModelMap).addOnSuccessListener(runnable2 -> messagesReference
                            .push()
                            .setValue(messageTime)
                            .addOnSuccessListener(runnable1 -> Toast.makeText(MessagesActivity.this, "sent", Toast.LENGTH_SHORT).show()));
                }).addOnFailureListener(runnable -> Log.d("FailedSend", runnable.getMessage()));

    }

    public void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("FROM_A").equals("details")) {

            his_details = (User) getIntent().getSerializableExtra("user_instance");
            chatModel.setChat_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + his_details.getUser_doc_id());
            chatModel.setFirst_sender(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Picasso.get().load(his_details.getUser_profile_pic()).placeholder(R.drawable.person_icon).into(his_dp);
            users_in_chat.add(his_details.getUser_doc_id());
            users_in_chat.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
            passed_user = his_details.getUser_doc_id();
            his_nickname.setText(his_details.getUser_name());
            progress_circular_messages.setVisibility(View.GONE);

        } else {
            chatModel = (ChatModel) getIntent().getSerializableExtra("chat_instance");
            Picasso.get().load(getIntent().getStringExtra("image_url")).placeholder(R.drawable.person_icon).into(his_dp);
            his_details = chatModel.getHis_details();
            users_in_chat.add(his_details.getUser_doc_id());
            users_in_chat.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
            passed_user = getIntent().getStringExtra("passed_user");
            his_nickname.setText(getIntent().getStringExtra("name_to_display"));

        }
    }


}
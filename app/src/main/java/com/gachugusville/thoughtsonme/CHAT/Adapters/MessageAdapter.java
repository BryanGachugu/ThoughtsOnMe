package com.gachugusville.thoughtsonme.CHAT.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gachugusville.thoughtsonme.CHAT.Message;
import com.gachugusville.thoughtsonme.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    public List<Message> messages;
    public Message message_to_reply;
    public RecyclerView messages_rc;
    public String chatModelID;
    public View reply_view;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    public static final int VIEW_TYPE_SENT_EMOJI = 3;
    public static final int VIEW_TYPE_RECEIVED_EMOJI = 4;
    public static final int VIEW_TYPE_REPLY_SENT = 7;
    public static final int VIEW_TYPE_REPLY_RECEIVED = 8;


    public MessageAdapter(Context context, List<Message> messages, View reply_view, RecyclerView messages_rc, String chatModelID) {
        this.context = context;
        this.messages = messages;
        this.reply_view = reply_view;
        this.messages_rc = messages_rc;
        this.chatModelID = chatModelID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case VIEW_TYPE_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_layout, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_SENT_EMOJI:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_emoji_layout, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_REPLY_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_reply_layout, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_RECEIVED_EMOJI:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_emoji_layout, parent, false);
                return new ReceivedMessageViewHolder(view);
            case VIEW_TYPE_REPLY_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_reply_layout, parent, false);
                return new ReceivedMessageViewHolder(view);
            case VIEW_TYPE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_message_layout, parent, false);
                return new ReceivedMessageViewHolder(view);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {

            case VIEW_TYPE_SENT:
                new SentMessageViewHolder(holder.itemView).setData(messages.get(position));
                break;

            case VIEW_TYPE_SENT_EMOJI:
                new SentMessageViewHolder(holder.itemView).setEmoji(messages.get(position));
                break;

            case VIEW_TYPE_REPLY_SENT:
                new SentMessageViewHolder(holder.itemView).setSentReply(messages.get(position));
                break;

            case VIEW_TYPE_RECEIVED:
                new ReceivedMessageViewHolder(holder.itemView).setData(messages.get(position));
                break;

            case VIEW_TYPE_RECEIVED_EMOJI:
                new ReceivedMessageViewHolder(holder.itemView).setEmoji(messages.get(position));
                break;

            case VIEW_TYPE_REPLY_RECEIVED:
                new ReceivedMessageViewHolder(holder.itemView).setReceivedReply(messages.get(position));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (messages.get(position).getSender_id().equals(user_id) && isEmoji(messages.get(position).getMessage_content()) && messages.get(position).getText_replied_to() == null) {
            return VIEW_TYPE_SENT_EMOJI;
        } else if (messages.get(position).getSender_id().equals(user_id) && (messages.get(position).getText_replied_to() != null || messages.get(position).getMedia_urls() != null)) {
            return VIEW_TYPE_REPLY_SENT;
        } else if (messages.get(position).getSender_id().equals(user_id)) {
            return VIEW_TYPE_SENT;
        } else if (!((messages.get(position).getSender_id().equals(user_id))) && isEmoji(messages.get(position).getMessage_content())) {
            return VIEW_TYPE_RECEIVED_EMOJI;
        } else if (!(messages.get(position).getSender_id().equals(user_id)) && (messages.get(position).getText_replied_to() != null || messages.get(position).getMedia_urls() != null)) {
            return VIEW_TYPE_REPLY_RECEIVED;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_sent_message, txt_delivery_status;
        private final LinearLayout emoji_item_layout, reply_view, reply_sent_layout;
        private final LinearLayout message_item_layout;
        private final TextView txt_sent_emoji;
        private final ImageView reply_image_replied, reply_image_sent;
        private final TextView txt_reply_to_replied;
        private final TextView txt_sent_message_to_reply;
        private final TextView txt_message_content_replied;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_sent_emoji = itemView.findViewById(R.id.txt_sent_emoji);
            txt_sent_message = itemView.findViewById(R.id.txt_sent_message);
            txt_delivery_status = itemView.findViewById(R.id.txt_delivery_status);
            emoji_item_layout = itemView.findViewById(R.id.emoji_item_layout);
            message_item_layout = itemView.findViewById(R.id.message_item_layout);
            txt_reply_to_replied = itemView.findViewById(R.id.txt_reply_to_replied);
            reply_view = itemView.findViewById(R.id.reply_view_sent);
            reply_image_sent = itemView.findViewById(R.id.reply_image);
            reply_image_replied = itemView.findViewById(R.id.reply_image_replied);
            txt_sent_message_to_reply = itemView.findViewById(R.id.txt_sent_message_to_reply);
            txt_message_content_replied = itemView.findViewById(R.id.txt_message_content_replied);
            reply_sent_layout = itemView.findViewById(R.id.reply_sent_layout);

        }

        public void setData(Message data) {
            txt_sent_message.setText(data.getMessage_content());
            textLongPressListener(message_item_layout, data);
            configureDeliveryStatus(data, txt_delivery_status);
        }

        public void setEmoji(Message message) {
            txt_sent_emoji.setText(message.getMessage_content());
            configureDeliveryStatus(message, txt_delivery_status);
            textLongPressListener(emoji_item_layout, message);
        }

        public void setSentReply(Message message) {
            reply_view.setOnClickListener(view -> messages_rc.post(() -> {
                for (Message base_text : messages){
                    if (message.getText_replied_to().getMessage_id().equals(base_text.getMessage_id())){
                        messages_rc.smoothScrollToPosition(messages.indexOf(base_text));
                        try {
                            Objects.requireNonNull(Objects.requireNonNull(messages_rc.getLayoutManager()).findViewByPosition(messages.indexOf(base_text)))
                                    .startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink_anim));
                        }
                        catch (Exception e){
                            Log.d("MessageScroll", "Failed to blink text");
                        }
                    }
                }
            }));

            textLongPressListener(reply_sent_layout, message);
            configureDeliveryStatus(message, txt_delivery_status);

            if (message.getText_replied_to() != null) {
                reply_view.post(() -> reply_view.setVisibility(View.VISIBLE));

                //Set reply title according to who is replying
                if (user_id.equals(message.getText_replied_to().getSender_id())) {
                    txt_reply_to_replied.setText(R.string.replying_to_yourself);
                } else {
                    txt_reply_to_replied.setText(MessageFormat.format("{0}{1}", R.string.replying_to, message.getText_replied_to().getSender_name()));
                }
                //Tweak the image view
                if (message.getText_replied_to().getMedia_urls() != null) {
                    Picasso.get().load(message.getText_replied_to().getMedia_urls().get(0)).into(reply_image_replied);
                    txt_message_content_replied.setText(R.string.photo);
                } else {
                    txt_message_content_replied.setText(message.getText_replied_to().getMessage_content());
                    reply_image_replied.setVisibility(View.GONE);
                }

                if (message.getMedia_urls() != null) {
                    reply_view.setVisibility(View.VISIBLE);
                    Glide.with(context).asGif().load(message.getMedia_urls().get(0)).into(reply_image_sent);
                } else {
                    reply_view.setVisibility(View.GONE);
                    txt_sent_message_to_reply.setText(message.getMessage_content());
                }

            } else {
                reply_view.setVisibility(View.GONE);
                Log.d("MESSAGE'S", "null");
                Picasso.get().load(message.getMedia_urls().get(0)).into(reply_image_sent);
                txt_sent_message_to_reply.setText(message.getMessage_content());
            }

        }
    }

    private void configureDeliveryStatus(Message data, TextView textView) {
        if (data.getDELIVERED_READ_STATUS() == 1) {
            textView.setText(R.string.sent);
        } else if (data.getDELIVERED_READ_STATUS() == 2) {
            textView.setText(R.string.delivered);
        } else if (data.getDELIVERED_READ_STATUS() == 3) {
            textView.setText(R.string.read);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txt_received_message, txt_received_emoji, txt_reply_to_replied, txt_received_message_to_reply, txt_message_content_replied;
        ImageView reply_image_received, reply_image_replied;
        LinearLayout emoji_received_layout, received_message_layout, reply_view_received, reply_received_layout;


        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_received_message = itemView.findViewById(R.id.txt_received_message);
            txt_received_message_to_reply = itemView.findViewById(R.id.txt_received_message_to_reply);
            txt_received_emoji = itemView.findViewById(R.id.txt_received_emoji);
            emoji_received_layout = itemView.findViewById(R.id.emoji_received_layout);
            received_message_layout = itemView.findViewById(R.id.received_message_layout);
            reply_image_received = itemView.findViewById(R.id.reply_image_received);
            txt_reply_to_replied = itemView.findViewById(R.id.txt_reply_to_replied);
            txt_message_content_replied = itemView.findViewById(R.id.txt_message_content_replied);
            reply_image_replied = itemView.findViewById(R.id.reply_image_replied);
            reply_view_received = itemView.findViewById(R.id.reply_view_received);
            reply_received_layout = itemView.findViewById(R.id.reply_received_layout);
        }

        public void setData(Message data) {
            txt_received_message.setText(data.getMessage_content());
            textLongPressListener(received_message_layout, data);
        }

        public void setEmoji(Message message) {
            textLongPressListener(emoji_received_layout, message);
            txt_received_emoji.setText(message.getMessage_content());
        }

        public void setReceivedReply(Message message) {

            reply_view_received.setOnClickListener(view -> messages_rc.smoothScrollToPosition(messages.indexOf(message.getText_replied_to())));

            textLongPressListener(reply_received_layout, message);
            if (message.getText_replied_to() != null) {
                reply_view_received.post(() -> reply_view_received.setVisibility(View.VISIBLE));
                //Set reply title according to who is replying
                if (user_id.equals(message.getText_replied_to().getSender_id())) {
                    txt_reply_to_replied.setText(R.string.replying_to_you);
                } else {
                    txt_reply_to_replied.setText(MessageFormat.format("Replying to {0}", message.getText_replied_to().getSender_name()));
                }
                //Tweak the image view
                if (message.getText_replied_to().getMedia_urls() != null) {
                    Picasso.get().load(message.getText_replied_to().getMedia_urls().get(0)).into(reply_image_replied);
                    txt_message_content_replied.setText(R.string.photo);
                } else {
                    txt_message_content_replied.setText(message.getText_replied_to().getMessage_content());
                    reply_image_replied.post(() -> reply_image_replied.setVisibility(View.GONE));
                }

                if (message.getMedia_urls() != null) {
                    reply_image_received.post(() -> reply_image_received.setVisibility(View.VISIBLE));
                    Glide.with(context).asGif().load(message.getMedia_urls().get(0)).into(reply_image_received);
                } else {
                    reply_image_received.post(() -> reply_image_received.setVisibility(View.GONE));
                    txt_received_message_to_reply.setText(message.getMessage_content());
                }

            } else {
                reply_view.setVisibility(View.GONE);
                Picasso.get().load(message.getMedia_urls().get(0)).into(reply_image_received);
                txt_received_message_to_reply.setText(message.getMessage_content());
            }

        }
    }

    @SuppressWarnings("RegExpDuplicateAlternationBranch")
    private static boolean isEmoji(String message) {
        return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" + "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
                "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" + "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
                "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
                "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
                "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
                "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
                "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
    }

    @SuppressWarnings("ConstantConditions")
    public void textLongPressListener(View view, Message message) {

        view.setOnLongClickListener(view12 -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.text_bottom_sheet_options);
            bottomSheetDialog.setCanceledOnTouchOutside(true);

            bottomSheetDialog.findViewById(R.id.reply_layout).setOnClickListener(view1 -> {
                bottomSheetDialog.dismiss();
                replyMessage(message, reply_view, messages_rc);
            });

            bottomSheetDialog.findViewById(R.id.copy_layout).setOnClickListener(view1 -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", message.getMessage_content());
                clipboard.setPrimaryClip(clip);
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "copied!", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.findViewById(R.id.remove_layout).setOnClickListener(view1 -> {
                bottomSheetDialog.dismiss();
                deleteMessage(message);
            });

            bottomSheetDialog.show();
            return true;
        });
    }

    private void replyMessage(Message message, View view, RecyclerView messages_rc) {

        view.setVisibility(View.VISIBLE);
        TextView reply_to = view.findViewById(R.id.txt_reply_to);
        TextView txt_message_content = view.findViewById(R.id.txt_message_content);

        if (user_id.equals(message.getSender_id()))
            reply_to.setText(R.string.replying_to_yourself);
        else
            reply_to.setText(MessageFormat.format("{0}{1}", R.string.replying_to, message.getSender_name()));

        view.findViewById(R.id.btn_cancel_reply).setOnClickListener(view1 -> view.setVisibility(View.GONE));

        if (message.getMedia_urls() == null) {
            view.findViewById(R.id.reply_image).setVisibility(View.GONE);
            txt_message_content.setText(message.getMessage_content());
        } else txt_message_content.setText(R.string.photo);

        this.message_to_reply = message;

        view.setOnClickListener(view1 -> messages_rc.smoothScrollToPosition(messages.indexOf(message)));


    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void deleteMessage(Message message) {

        messages.remove(message);
        notifyItemRemoved(messages.indexOf(message));
        notifyItemRangeChanged(messages.indexOf(message), messages.size());


        if (message.getDeleted().length() > 4) {
            FirebaseDatabase.getInstance().getReference("Chats").child(chatModelID).child("Messages")
                    .child(message.getMessage_id())
                    .removeValue().addOnSuccessListener(runnable -> Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show());
        } else {
            FirebaseDatabase.getInstance().getReference("Chats").child(chatModelID).child("Messages")
                    .child(message.getMessage_id())
                    .child("deleted")
                    .setValue(user_id)
                    .addOnSuccessListener(runnable -> {
                        Map<String, Object> chatModelMap = new HashMap<>();
                        if (!messages.isEmpty()){
                            chatModelMap.put("last_message" + user_id, messages.get(getItemCount() - 1).getMessage_content());
                        }else {
                            chatModelMap.put("last_message" + user_id, "");
                        }
                    });
        }
    }

    public Message getRepliedText() {
        if (reply_view.getVisibility() == View.VISIBLE) {
            return message_to_reply;
        }
        return null;
    }

}
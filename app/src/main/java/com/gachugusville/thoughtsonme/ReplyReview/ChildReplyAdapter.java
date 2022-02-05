package com.gachugusville.thoughtsonme.ReplyReview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gachugusville.thoughtsonme.Adapters.UserReviewsAdapter;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.ReviewReply;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildReplyAdapter extends RecyclerView.Adapter<ChildReplyAdapter.ChildReplyViewHolder> {

    private Context context;
    private List<ReviewReply> childRepliesList;
    private RecyclerView comment_replies_rc;
    private ReviewReply to_be_replied;
    private TextView addReply;
    ReviewModel reviewModel;
    User user_to_be_texted, current_user;
    private String parentCommentID;
    private LinearLayout lyt_replying_to;
    private EditText edt_message_text_input;

    public ChildReplyAdapter() {
        //very much needed
    }

    public ChildReplyAdapter(Context context, List<ReviewReply> childRepliesList, User user_to_be_texted, User current_user , ReviewModel reviewModel, EditText edt_message_text_input, TextView addReply, RecyclerView comment_replies_rc, String parentCommentID, LinearLayout lyt_replying_to) {
        this.context = context;
        this.childRepliesList = childRepliesList;
        this.user_to_be_texted = user_to_be_texted;
        this.current_user = current_user;
        this.reviewModel = reviewModel;
        this.edt_message_text_input = edt_message_text_input;
        this.addReply = addReply;
        this.comment_replies_rc = comment_replies_rc;
        this.parentCommentID = parentCommentID;
        this.lyt_replying_to = lyt_replying_to;
    }

    //Just before adding the comment
    public ReviewReply getCommentToReplyTo() {
        if (lyt_replying_to.getVisibility() == View.VISIBLE){
            return to_be_replied;
        }
        return null;
    }

    @NonNull
    @Override
    public ChildReplyAdapter.ChildReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_reply, parent, false);
        return new ChildReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildReplyAdapter.ChildReplyViewHolder holder, int position) {

        Picasso.get().load(childRepliesList.get(position).getPublisher_profile_url()).into(holder.publisher_profile_url);
        holder.publisher_nick_name.setText(childRepliesList.get(position).getPublisher_nick_name());
        holder.txt_reply_to_nickname.setText(childRepliesList.get(position).getReplying_to_nickname());
        holder.txt_comment.setText(childRepliesList.get(position).getComment());
        if (childRepliesList.get(position).getMedia_url() != null && !(childRepliesList.get(position).getMedia_url().isEmpty())){
            Glide.with(context).asGif().load(childRepliesList.get(position).getMedia_url()).into(holder.media_url_img);
        }else {
            holder.media_url_img.setVisibility(View.GONE);
        }

        holder.txt_reply.setOnClickListener(view -> {
            prepareReply(childRepliesList.get(position), lyt_replying_to);
        });

    }

    private void prepareReply(ReviewReply reviewReply, LinearLayout lyt_replying_to){
        this.to_be_replied = reviewReply;

        TextView txt_replying_to_nickname = lyt_replying_to.findViewById(R.id.txt_replying_to_nickname);

        lyt_replying_to.findViewById(R.id.txt_cancel_reply).setOnClickListener(view -> {
            lyt_replying_to.setVisibility(View.GONE);
        });

        txt_replying_to_nickname.setText("Replying to " + reviewReply.getPublisher_nick_name());
        lyt_replying_to.setVisibility(View.VISIBLE);
        edt_message_text_input.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //REPLY TO COMMENT REPLIES
        addReply.setOnClickListener(view -> {
            if (edt_message_text_input != null && edt_message_text_input.getText().toString().trim().isEmpty()){
                Toast.makeText(context, "Please type something in child", Toast.LENGTH_SHORT).show();
            }else if (edt_message_text_input != null && lyt_replying_to.getVisibility() == View.VISIBLE){
                ReviewReply new_reply = new ReviewReply();

                new_reply.setComment(edt_message_text_input.getText().toString());
                new_reply.setParentCommentID(reviewReply.getParentCommentID());
                new_reply.setReplying_to_nickname(to_be_replied.getPublisher_nick_name());
                new_reply.setPublisher_nick_name(current_user.getPrivate_name());
                new_reply.setPublisher_profile_url(current_user.getPrivate_profile_pic());

                edt_message_text_input.setText("");
                lyt_replying_to.setVisibility(View.GONE);

                FirebaseFirestore.getInstance().collection("Users").document(user_to_be_texted.getUser_doc_id())
                        .collection("Reviews").document(reviewModel.getReview_id())
                        .collection("ReviewReplies")
                        .add(new_reply)
                        .addOnSuccessListener(runnable -> Toast.makeText(context, "From Child Success", Toast.LENGTH_SHORT).show());
            }else {
                ReviewReply new_comment = new ReviewReply();

                assert edt_message_text_input != null;
                new_comment.setComment(edt_message_text_input.getText().toString());
                new_comment.setPublisher_nick_name(User.getInstance().getPrivate_name());
                new_comment.setPublisher_profile_url(User.getInstance().getPrivate_profile_pic());

                edt_message_text_input.setText("");
                lyt_replying_to.setVisibility(View.GONE);

                CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users").document(user_to_be_texted.getUser_doc_id())
                        .collection("Reviews").document(reviewModel.getReview_id())
                        .collection("ReviewReplies");

                //The parent Id is assigned to the first comment in a thread ONLY. All the discussions under this comment take this as their parent.
                collectionReference.add(new_comment)
                        .addOnSuccessListener(runnable -> {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return childRepliesList.size();
    }


    public static class ChildReplyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView publisher_profile_url;
        TextView publisher_nick_name, txt_reply_to_nickname, txt_comment, txt_reply;
        ImageView media_url_img;

        public ChildReplyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_reply_to_nickname = itemView.findViewById(R.id.txt_reply_to_nickname);
            publisher_profile_url = itemView.findViewById(R.id.publisher_profile_url);
            publisher_nick_name = itemView.findViewById(R.id.publisher_nick_name);
            media_url_img = itemView.findViewById(R.id.media_url_img);
            txt_comment = itemView.findViewById(R.id.txt_comment);
            txt_reply = itemView.findViewById(R.id.txt_reply);




        }
    }
}

package com.gachugusville.thoughtsonme.ReplyReview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gachugusville.thoughtsonme.Adapters.UserReviewsAdapter;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.ReviewReply;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewReplyAdapter extends RecyclerView.Adapter<ReviewReplyAdapter.ReplyViewHolder> {

    Context context;
    List<ReviewReply> reviewReplyList;
    ReviewReply to_be_replied;
    ChildReplyAdapter childRepliesAdapter;
    EditText edt_message_text_input;
    ReviewModel reviewModel;
    TextView addReply;
    User this_user, current_user;
    RecyclerView review_replies_rc;
    LinearLayout lyt_replying_to;

    public ReviewReplyAdapter(Context context, List<ReviewReply> reviewReplyList, EditText edt_message_text_input, TextView addReply, ReviewModel reviewModel, User this_user, User current_user , RecyclerView review_replies_rc, LinearLayout lyt_replying_to) {
        this.context = context;
        this.reviewReplyList = reviewReplyList;
        this.edt_message_text_input = edt_message_text_input;
        this.addReply = addReply;
        this.reviewModel = reviewModel;
        this.this_user = this_user;
        this.current_user = current_user;
        this.review_replies_rc = review_replies_rc;
        this.lyt_replying_to = lyt_replying_to;

    }

    @NonNull
    @Override
    public ReviewReplyAdapter.ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_reply_layout, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewReplyAdapter.ReplyViewHolder holder, int position) {

        Picasso.get().load(reviewReplyList.get(position).getPublisher_profile_url()).into(holder.publisher_profile_url);
        holder.publisher_nick_name.setText(reviewReplyList.get(position).getPublisher_nick_name());
        holder.txt_comment.setText(reviewReplyList.get(position).getComment());
        Glide.with(context).asGif().load(reviewReplyList.get(position).getMedia_url()).into(holder.media_url_img);

        /*
        if (reviewReplyList.get(position).getReplying_to_id() != null && reviewReplyList.get(position).getReplying_to_id().length() > 5){
            holder.txt_reply_to_nickname.setText(reviewReplyList.get(position).getReplying_to_nickname());
            holder.reply_to_layout.setVisibility(View.VISIBLE);

            holder.reply_to_layout.setOnClickListener(view -> {
                review_replies_rc.post(() -> {
                    for (ReviewReply repliedTo : reviewReplyList){
                        if (repliedTo.getReplying_to_id().equals(reviewReplyList.get(position).getReplying_to_id())){
                            review_replies_rc.smoothScrollToPosition(reviewReplyList.indexOf(repliedTo));
                            try {
                                Objects.requireNonNull(Objects.requireNonNull(review_replies_rc.getLayoutManager()).findViewByPosition(reviewReplyList.indexOf(repliedTo)))
                                        .startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink_anim));
                            }
                            catch (Exception e){
                                Log.d("MessageScroll", "Failed to blink reply");
                            }
                        }
                    }
                });
            });

        }else holder.reply_to_layout.setVisibility(View.GONE);
         */

        List<ReviewReply> childRepliesList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Users").document(this_user.getUser_doc_id())
                .collection("Reviews").document(reviewModel.getReview_id())
                .collection("ReviewReplies")
                .whereEqualTo("parentCommentID", reviewReplyList.get(position).getCommentReplyID())
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        childRepliesList.clear();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                                ReviewReply reviewReply = documentSnapshot.toObject(ReviewReply.class);
                                childRepliesList.add(reviewReply);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            childRepliesAdapter = new ChildReplyAdapter(context, childRepliesList, this_user,current_user , reviewModel, edt_message_text_input, addReply, holder.comment_replies_rc, reviewReplyList.get(position).getCommentReplyID(), lyt_replying_to);
                            holder.comment_replies_rc.setAdapter(childRepliesAdapter);
                            holder.comment_replies_rc.setHasFixedSize(true);
                            holder.comment_replies_rc.suppressLayout(true);
                            holder.comment_replies_rc.setLayoutManager(layoutManager);
                        }
                    }
                });




        holder.txt_reply.setOnClickListener(view -> {
            prepareReply(reviewReplyList.get(position), lyt_replying_to);
        });
    }

    public void prepareReply(ReviewReply reviewReply, LinearLayout lyt_replying_to) {
        this.to_be_replied = reviewReply;

        TextView txt_replying_to_nickname = lyt_replying_to.findViewById(R.id.txt_replying_to_nickname);

        //Reply to original comment
        View.OnClickListener addButtonClickListener = view -> {
            if (edt_message_text_input != null && edt_message_text_input.getText().toString().trim().isEmpty()){
                Toast.makeText(context, "Please type something in parent reply", Toast.LENGTH_SHORT).show();
            }else if (edt_message_text_input != null && lyt_replying_to.getVisibility() == View.VISIBLE){
                ReviewReply new_reply = new ReviewReply();

                new_reply.setComment(edt_message_text_input.getText().toString());
                new_reply.setParentCommentID(reviewReply.getCommentReplyID());
                new_reply.setReplying_to_nickname(to_be_replied.getPublisher_nick_name());
                new_reply.setPublisher_nick_name(this_user.getPrivate_name());
                new_reply.setPublisher_profile_url(this_user.getPrivate_profile_pic());

                edt_message_text_input.setText("");
                lyt_replying_to.setVisibility(View.GONE);

                FirebaseFirestore.getInstance().collection("Users").document(this_user.getUser_doc_id())
                        .collection("Reviews").document(reviewModel.getReview_id())
                        .collection("ReviewReplies")
                        .add(new_reply)
                        .addOnSuccessListener(runnable -> Toast.makeText(context, "From Parent Success", Toast.LENGTH_SHORT).show());
            }else {
                ReviewReply new_comment = new ReviewReply();

                assert edt_message_text_input != null;
                new_comment.setComment(edt_message_text_input.getText().toString());
                new_comment.setPublisher_nick_name(User.getInstance().getPrivate_name());
                new_comment.setPublisher_profile_url(User.getInstance().getPrivate_profile_pic());

                edt_message_text_input.setText("");
                lyt_replying_to.setVisibility(View.GONE);

                CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users").document(this_user.getUser_doc_id())
                        .collection("Reviews").document(reviewModel.getReview_id())
                        .collection("ReviewReplies");

                //The parent Id is assigned to the first comment in a thread ONLY. All the discussions under this comment take this as their parent.
                collectionReference.add(new_comment)
                        .addOnSuccessListener(runnable -> {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        });
            }
        };

        txt_replying_to_nickname.setText("Replying to " + reviewReply.getPublisher_nick_name());
        lyt_replying_to.setVisibility(View.VISIBLE);
        edt_message_text_input.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);



        addReply.setOnClickListener(addButtonClickListener);
    }

    @Override
    public int getItemCount() {
        return reviewReplyList.size();
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView publisher_profile_url;
        TextView publisher_nick_name, txt_comment, txt_reply;
        ImageView media_url_img;
        RecyclerView comment_replies_rc;


        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);

            publisher_profile_url = itemView.findViewById(R.id.publisher_profile_url_mc);
            publisher_nick_name = itemView.findViewById(R.id.publisher_nick_name_mc);
            txt_comment = itemView.findViewById(R.id.txt_comment_mc);
            txt_reply = itemView.findViewById(R.id.txt_reply_mc);
            media_url_img = itemView.findViewById(R.id.media_url_img_mc);
            comment_replies_rc = itemView.findViewById(R.id.comment_replies_rc_mc);

        }
    }
}

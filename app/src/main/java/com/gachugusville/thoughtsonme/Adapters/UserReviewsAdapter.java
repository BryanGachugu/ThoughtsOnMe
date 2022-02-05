package com.gachugusville.thoughtsonme.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachugusville.thoughtsonme.CHAT.MessagesActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.ReplyReview.ChildReplyAdapter;
import com.gachugusville.thoughtsonme.ReplyReview.ReviewReplyAdapter;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.ReviewReply;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ConstantConditions")
public class UserReviewsAdapter extends RecyclerView.Adapter<UserReviewsAdapter.UserReviewViewHolder> {

    Context context;
    List<ReviewModel> reviewModelList;
    User user_to_be_texted, current_user;

    public UserReviewsAdapter(Context context, User current_user, List<ReviewModel> reviewModelList) {
        this.context = context;
        this.reviewModelList = reviewModelList;
        this.current_user = current_user;
    }


    @NonNull
    @Override
    public UserReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false);
        return new UserReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewViewHolder holder, int position) {

        Picasso.get().load(reviewModelList.get(position).getPublisher_profile_url()).into(holder.reviewer_dp);
        holder.review_publisher_name.setText(reviewModelList.get(position).getPublisher_name()); //reviewModelList.get(position).getPublisher_name()
        holder.review_post_time.setText(MessageFormat.format("{0} days ago", 5));
        holder.txt_review_content.setText(reviewModelList.get(position).getReview_content());

        FirebaseFirestore.getInstance().collection("Users").document(reviewModelList.get(position).getPublisherUid()).get().
                addOnSuccessListener(documentSnapshot -> {
                    this.user_to_be_texted = documentSnapshot.toObject(User.class);
                    holder.btn_text_reviewer.setVisibility(View.VISIBLE);
                    Log.d("UserTexted", "DONE");
                });

        holder.btn_reply.setOnClickListener(view -> createBottomSheetForComments(reviewModelList.get(position)));
        holder.review_reply_count.setOnClickListener(view -> createBottomSheetForComments(reviewModelList.get(position)));

        holder.btn_text_reviewer.setOnClickListener(view -> {
           try {
               if (!(user_to_be_texted.getUser_doc_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))){
                   Intent intent = new Intent(context, MessagesActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putSerializable("user_instance", user_to_be_texted);
                   bundle.putString("FROM_A", "details");
                   intent.putExtras(bundle);
                   context.startActivity(intent);
               }else Toast.makeText(context, "Cannot text yourself", Toast.LENGTH_LONG).show();
           }catch (Exception e){
               Toast.makeText(context, "Error fetching user", Toast.LENGTH_SHORT).show();
           }


        });



    }

    @SuppressLint("NotifyDataSetChanged")
    private void createBottomSheetForComments(ReviewModel reviewModel) {
        BottomSheetDialog commentsBottomSheet = new BottomSheetDialog(context);
        commentsBottomSheet.setContentView(R.layout.comments_bottom_sheet);
        commentsBottomSheet.setCanceledOnTouchOutside(true);
        commentsBottomSheet.show();

        TextView addButton = commentsBottomSheet.findViewById(R.id.txt_add_comment);

        List<ReviewReply> reviewReplyList = new ArrayList<>();

        TextView txt_num_review_approvals = commentsBottomSheet.findViewById(R.id.txt_num_review_approvals);
        LinearLayout lyt_replying_to = commentsBottomSheet.findViewById(R.id.lyt_replying_to);
        EditText edt_message_text_input = commentsBottomSheet.findViewById(R.id.edt_message_text_input);

        assert txt_num_review_approvals != null;
        txt_num_review_approvals.setText(MessageFormat.format("{0} approvals", reviewModel.getNum_seconders()));

        RecyclerView review_replies_rc = commentsBottomSheet.findViewById(R.id.review_replies_rc);


        FirebaseFirestore.getInstance().collection("Users").document(user_to_be_texted.getUser_doc_id())
                .collection("Reviews").document(reviewModel.getReview_id())
                .collection("ReviewReplies")
                .whereEqualTo("parentCommentID", "")
                .addSnapshotListener((value, error) -> {
                        if (value != null){
                            Log.d("CommentSize", String.valueOf(value.size()));
                            reviewReplyList.clear();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()){
                                ReviewReply reviewReply = documentSnapshot.toObject(ReviewReply.class);
                                reviewReply.setCommentReplyID(documentSnapshot.getId());
                                reviewReplyList.add(reviewReply);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                ReviewReplyAdapter reviewReplyAdapter = new ReviewReplyAdapter(context, reviewReplyList, edt_message_text_input, addButton, reviewModel, user_to_be_texted, current_user, review_replies_rc, lyt_replying_to);
                                review_replies_rc.setAdapter(reviewReplyAdapter);
                                review_replies_rc.setLayoutManager(layoutManager);
                            }
                        }else {
                            Log.d("ErrorPass", "Null Comment read");
                        }

                    Toast.makeText(context, reviewReplyList.size() + " comments", Toast.LENGTH_SHORT).show();
                });

        lyt_replying_to.findViewById(R.id.txt_cancel_reply).setOnClickListener(view -> {
            lyt_replying_to.setVisibility(View.GONE);
        });

        addButton.setOnClickListener(view -> {
            if (edt_message_text_input != null && !(edt_message_text_input.getText().toString().trim().isEmpty())){
                ReviewReply new_comment = new ReviewReply();

                assert edt_message_text_input != null;
                new_comment.setComment(edt_message_text_input.getText().toString());
                new_comment.setPublisher_nick_name(current_user.getPrivate_name());
                new_comment.setPublisher_profile_url(current_user.getPrivate_profile_pic());

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
            }else {
                Toast.makeText(context, "Please type something in comment", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void calculateApprovalRate(ReviewModel reviewModel, TextView rate_display) {
        try {
            int approval_percentage = (reviewModel.getNum_seconders() / (reviewModel.getNum_seconders() + reviewModel.getNum_disapproval())) * 100;
            rate_display.setText(MessageFormat.format("{0} approval rate", approval_percentage));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviewModelList.size();
    }

    public static class UserReviewViewHolder extends RecyclerView.ViewHolder {

        CircleImageView reviewer_dp;
        TextView review_reply_count;
        TextView review_publisher_name, review_post_time, txt_review_content;
        LinearLayout btn_reply, btn_text_reviewer;

        public UserReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewer_dp = itemView.findViewById(R.id.reviewer_dp);
            review_publisher_name = itemView.findViewById(R.id.review_publisher_name);
            review_post_time = itemView.findViewById(R.id.review_post_time);
            txt_review_content = itemView.findViewById(R.id.txt_review_content);
            btn_reply = itemView.findViewById(R.id.btn_reply);
            review_reply_count = itemView.findViewById(R.id.review_reply_count);
            btn_text_reviewer = itemView.findViewById(R.id.btn_text_reviewer);


        }
    }
}

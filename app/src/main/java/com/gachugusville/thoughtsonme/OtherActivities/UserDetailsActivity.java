package com.gachugusville.thoughtsonme.OtherActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachugusville.thoughtsonme.Adapters.UserReviewsAdapter;
import com.gachugusville.thoughtsonme.CHAT.MessagesActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailsErrors";
    private List<ReviewModel> reviewModelList;
    private UserReviewsAdapter reviewsAdapter;
    private RecyclerView user_reviews_rc;
    private LinearLayoutManager layoutManager;
    private String passed_link_uid;
    private DocumentReference docRef;
    private User current_user, user_details;
    private CircleImageView user_details_dp;
    private TextView user_details_name, user_details_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        user_details_dp = findViewById(R.id.user_details_dp);
        user_details_name = findViewById(R.id.user_details_name);
        user_details_description = findViewById(R.id.user_details_description);


        reviewModelList = new ArrayList<>();
        user_reviews_rc = findViewById(R.id.recyclerView);

        getExtras();

        getUserDetails();
    }

    private void getUserDetails() {
        docRef = FirebaseFirestore.getInstance().collection("Users").document(passed_link_uid);

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                UserDetailsActivity.this.user_details = snapshot.toObject(User.class);
                assert user_details != null;
                user_details_name.setText(user_details.getUser_name());
                findViewById(R.id.btn_add).setVisibility(View.VISIBLE);

                Picasso.get().load(user_details.getUser_profile_pic()).placeholder(R.drawable.person_icon).into(user_details_dp);

                if (user_details.getTag_line() != null){
                    user_details_description.setText(user_details.getTag_line());
                }else{
                    user_details_description.setVisibility(View.GONE);
                }

                findViewById(R.id.text_message).setOnClickListener(view -> {
                    Intent intent = new Intent(this, MessagesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user_instance", user_details);
                    bundle.putString("FROM_A", "details");
                    intent.putExtras(bundle);
                    startActivity(intent);
                });

                findViewById(R.id.btn_add).setOnClickListener(view -> {
                    Intent intent = new Intent(UserDetailsActivity.this, AddReviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("current_user", current_user);
                    bundle.putSerializable("user_details", user_details);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                });

                fetchReviews();

            } else {
                Log.d(TAG, "Current data: null");
            }
        });

    }

    private void getExtras() {
        Bundle user_bundle = getIntent().getExtras();
        current_user = (User) user_bundle.getSerializable("current_user");
        passed_link_uid = user_bundle.getString("passed_link_uid");
    }


    private void fetchReviews() {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(user_details.getUser_doc_id());
            docRef.collection("Reviews")
                    .addSnapshotListener((value, error) -> {
                        assert value != null;
                        reviewModelList.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                ReviewModel reviewModel = documentChange.getDocument().toObject(ReviewModel.class);
                                reviewModel.setReview_id(documentChange.getDocument().getId());
                                reviewModelList.add(reviewModel);

                                layoutManager = new LinearLayoutManager(UserDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
                                reviewsAdapter = new UserReviewsAdapter(UserDetailsActivity.this, current_user, reviewModelList);
                                user_reviews_rc.setHasFixedSize(true);
                                user_reviews_rc.setAdapter(reviewsAdapter);
                                user_reviews_rc.setLayoutManager(layoutManager);

                            }
                        }

                    });
        }
}
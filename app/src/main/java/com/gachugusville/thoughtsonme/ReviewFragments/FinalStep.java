package com.gachugusville.thoughtsonme.ReviewFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gachugusville.thoughtsonme.OtherActivities.AddReviewActivity;
import com.gachugusville.thoughtsonme.OtherActivities.DashboardActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class FinalStep extends Fragment {

    private TextInputLayout topic_holder, review_text_holder;
    private TextInputEditText edt_review_topic, edt_review_text;
    private RatingBar target_rating_bar;
    private CheckBox checkbox_rated, checkbox_un_rated;
    private FirebaseFirestore database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_final_step, container, false);

        database = FirebaseFirestore.getInstance();
        Toast.makeText(getContext(), AddReviewActivity.reviewModel.getTargetUID(), Toast.LENGTH_SHORT).show();

        CircleImageView target_dp = view.findViewById(R.id.target_dp);
        topic_holder = view.findViewById(R.id.topic_holder);
        review_text_holder = view.findViewById(R.id.review_text_holder);
        edt_review_topic = view.findViewById(R.id.edt_review_topic);
        edt_review_text = view.findViewById(R.id.edt_review_text);
        target_rating_bar = view.findViewById(R.id.target_rating_bar);
        TextView txt_rating = view.findViewById(R.id.txt_rating);
        edt_review_topic = view.findViewById(R.id.edt_review_topic);
        checkbox_rated = view.findViewById(R.id.checkbox_rated);
        checkbox_un_rated = view.findViewById(R.id.checkbox_un_rated);

        AddReviewActivity.reviewModel.setPublisherUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        AddReviewActivity.reviewModel.setPublisher_profile_url(User.getInstance().getUser_profile_pic());

        target_rating_bar.setOnRatingBarChangeListener((ratingBar, v, b) -> txt_rating.setText(String.valueOf(v)));


        Picasso.get().load(AddReviewActivity.reviewModel.getTarget_pic_url()).into(target_dp);

        view.findViewById(R.id.btn_publish).setOnClickListener(view1 -> validateInputs());


        return view;
    }

    private void validateInputs() {
        if (Objects.requireNonNull(edt_review_topic.getText()).toString().trim().equals("")) {
            topic_holder.setError("Please add topic");
        } else if (!(target_rating_bar.getRating() > 0)) {
            Toast.makeText(getContext(), "Rating needed", Toast.LENGTH_SHORT).show();
        } else if (Objects.requireNonNull(edt_review_text.getText()).toString().trim().equals("")) {
            review_text_holder.setError("Field needed");
        } else if (!(checkbox_rated.isChecked()) && !(checkbox_un_rated.isChecked())) {
            Toast.makeText(getContext(), "Please specify age restriction", Toast.LENGTH_LONG).show();
        } else {
            AddReviewActivity.reviewModel.setReview_topic(edt_review_topic.getText().toString());
            AddReviewActivity.reviewModel.setReview_content(edt_review_text.getText().toString());
            AddReviewActivity.reviewModel.setRating(target_rating_bar.getRating());
            publishReview();
        }
    }

    private void publishReview() {
        AddReviewActivity.reviewModel.setPublisherUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (AddReviewActivity.reviewModel.getTargetUID() != null) {
            database.collection("Users").document(Objects.requireNonNull(AddReviewActivity.reviewModel.getTargetUID()))
                    .collection("Reviews")
                    .add(AddReviewActivity.reviewModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            database.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("MyReviews")
                                    .add(AddReviewActivity.reviewModel)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getContext(), "Review Added Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(), DashboardActivity.class));
                                        requireActivity().finish();
                                    });
                        }
                    });
        } else {
            database.collection("Reviews")
                    .add(AddReviewActivity.reviewModel)
                    .addOnSuccessListener(documentReference ->
                            database.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .collection("MyReviews")
                                    .add(AddReviewActivity.reviewModel)
                                    .addOnSuccessListener(documentReference1 -> {
                                        Toast.makeText(getContext(), "Review Added Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(), DashboardActivity.class));
                                        requireActivity().finish();
                                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add review", Toast.LENGTH_LONG).show());

        }

    }
}
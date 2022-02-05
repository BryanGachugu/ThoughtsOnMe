package com.gachugusville.thoughtsonme.ReviewFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import com.gachugusville.thoughtsonme.OtherActivities.AddReviewActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Step2 extends Fragment {

    private TextView text_searching_updates;
    private TextView txt_default_explanation;
    private TextView target_num_of_reviews;
    private CircleImageView target_image;
    private TextView target_rating;
    private LottieAnimationView lottieAnimationView;
    private LinearLayout layout_user_found, layout_user_not_found;
    private CircleImageView target_profile_pic;
    private boolean validated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step2, container, false);

        TextView txt_step2_title = view.findViewById(R.id.txt_step2_title);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        text_searching_updates = view.findViewById(R.id.text_searching_updates);
        target_rating = view.findViewById(R.id.target_rating);
        txt_default_explanation = view.findViewById(R.id.txt_default_explanation);
        layout_user_found = view.findViewById(R.id.layout_user_found);
        target_num_of_reviews = view.findViewById(R.id.target_num_of_reviews);
        target_image = view.findViewById(R.id.target_image);
        MaterialButton btn_add_target_photo = view.findViewById(R.id.btn_add_target_photo);
        target_profile_pic = view.findViewById(R.id.target_profile_pic);
        layout_user_not_found = view.findViewById(R.id.layout_user_not_found);

        txt_step2_title.setText("Check if " + AddReviewActivity.reviewModel.getTarget_name() + " exists in the database");
        btn_add_target_photo.setOnClickListener(view1 -> setTargetDP());

        return view;
    }

    public boolean searchedUser(List<String> phone_numbers) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("Users")
                .whereArrayContainsAny("phone_numbers", phone_numbers)
                .limit(2)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Toast.makeText(getContext(), String.valueOf(queryDocumentSnapshots.size()), Toast.LENGTH_SHORT).show();
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()) {

                        lottieAnimationView.setVisibility(View.GONE);
                        layout_user_found.setVisibility(View.VISIBLE);
                        text_searching_updates.setTextSize(22);
                        text_searching_updates.setText(R.string.text_user_found);
                        txt_default_explanation.setVisibility(View.VISIBLE);
                        txt_default_explanation.setText(R.string.user_s_profile_picture_and_details_will_be_saved_as_default_for_this_review);

                        AddReviewActivity.reviewModel
                                .setTarget_pic_url(Objects.requireNonNull(queryDocumentSnapshots.getDocuments()
                                        .get(0).toObject(User.getInstance().getClass())).getUser_profile_pic());
                        Picasso.get().load(AddReviewActivity.reviewModel.getTarget_pic_url()).into(target_profile_pic);

                        target_num_of_reviews
                                .setText(String.valueOf(Objects.requireNonNull(queryDocumentSnapshots.getDocuments()
                                        .get(0).toObject(User.getInstance().getClass())).getNum_reviews()));


                        AddReviewActivity.reviewModel.setTargetUID
                                (Objects.requireNonNull(queryDocumentSnapshots.getDocuments()
                                        .get(0).toObject(AddReviewActivity.reviewModel.getClass())).getTargetUID());

                        validated = true;
                    } else {
                        lottieAnimationView.setVisibility(View.GONE);
                        layout_user_found.setVisibility(View.GONE);
                        text_searching_updates.setTextSize(22);
                        text_searching_updates.setText(R.string.text_user_not_found);
                        layout_user_not_found.setVisibility(View.VISIBLE);
                        validated = true;

                    }

                }).addOnFailureListener(e -> {

            lottieAnimationView.setVisibility(View.INVISIBLE);
            validated = false;
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

        });

        return validated;
    }

    private void setTargetDP() {
        Toast.makeText(getContext(), "To Gallery", Toast.LENGTH_SHORT).show();
    }

}
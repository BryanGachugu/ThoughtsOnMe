package com.gachugusville.thoughtsonme.OtherActivities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddReviewActivity extends AppCompatActivity {

    public static ReviewModel reviewModel;
    private EditText edt_review_text_input;
    private CircleImageView user_details_dp;
    private TextView user_details_name;
    private User current_user, user_details;
    private String publisher_profile, publisher_name, publisher_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        edt_review_text_input = findViewById(R.id.edt_review_text_input);
        user_details_name = findViewById(R.id.user_details_name);
        user_details_dp = findViewById(R.id.user_details_dp);

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    this.current_user = documentSnapshot.toObject(User.class);
                    findViewById(R.id.btn_publish).setEnabled(true);
                });

        getExtras();

        findViewById(R.id.img_back).setOnClickListener(view -> AddReviewActivity.super.onBackPressed());
        findViewById(R.id.btn_publish).setOnClickListener(view -> publishReview());


    }

    private void getExtras() {
        Bundle user_bundle = getIntent().getExtras();
        user_details = (User) user_bundle.getSerializable("user_details");

        user_details_name.setText(user_details.getUser_name());
        Picasso.get().load(user_details.getUser_profile_pic()).into(user_details_dp);
    }

    private void publishReview() {
        if (!TextUtils.isEmpty(edt_review_text_input.getText().toString())){
            if (current_user != null && user_details != null){
                SweetAlertDialog sweetAlertDialog =  new SweetAlertDialog(AddReviewActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setTitleText(AddReviewActivity.this.getString(R.string.publishing));
                sweetAlertDialog.show();

                HashMap<String, Object> reviewObjectMap = new HashMap<>();
                reviewObjectMap.put("review_content", edt_review_text_input.getText().toString());
                reviewObjectMap.put("publisher_profile_url", current_user.getPrivate_profile_pic());
                reviewObjectMap.put("publisher_name", current_user.getPrivate_name());
                reviewObjectMap.put("publisherUid", current_user.getUser_doc_id());
                reviewObjectMap.put("time", Timestamp.now());


                FirebaseFirestore.getInstance().collection("Users").document(user_details.getUser_doc_id())
                        .collection("Reviews")
                        .add(reviewObjectMap)
                        .addOnCompleteListener(runnable -> {
                            sweetAlertDialog.dismiss();
                            Toast.makeText(AddReviewActivity.this, AddReviewActivity.this.getString(R.string.success), Toast.LENGTH_SHORT).show();
                            AddReviewActivity.super.onBackPressed();
                        }).addOnFailureListener(runnable -> Toast.makeText(AddReviewActivity.this, AddReviewActivity.this.getString(R.string.error_publishing_thought), Toast.LENGTH_SHORT).show());
            }else Toast.makeText(AddReviewActivity.this, AddReviewActivity.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }Toast.makeText(AddReviewActivity.this, AddReviewActivity.this.getString(R.string.please_write_something), Toast.LENGTH_SHORT).show();
    }

}
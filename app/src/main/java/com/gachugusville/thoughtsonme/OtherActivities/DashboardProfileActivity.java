package com.gachugusville.thoughtsonme.OtherActivities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gachugusville.thoughtsonme.Adapters.UserReviewsAdapter;
import com.gachugusville.thoughtsonme.CHAT.ConversationListActivity;
import com.gachugusville.thoughtsonme.Profile.EditProfileActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardProfileActivity extends AppCompatActivity {

    private static final String TAG = "DashError";
    private List<ReviewModel> reviewModelList;
    private UserReviewsAdapter reviewsAdapter;
    private TextView user_link;
    private RecyclerView user_reviews_rc;
    private User current_user;
    private CollectionReference userCollectionRef;
    private LinearLayoutManager layoutManager;
    private Intent intent;
    private TextView textName, textDescription;
    private CircleImageView user_dp, img_user_settings;
    private DocumentReference docRef;
    private String passed_link_uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_profile);

        user_dp = findViewById(R.id.user_dp);
        textName = findViewById(R.id.user_details_name);
        textDescription  = findViewById(R.id.user_details_description);
         user_link = findViewById(R.id.user_link);
        user_reviews_rc = findViewById(R.id.user_reviews_rc);
        img_user_settings = findViewById(R.id.img_user_settings);
        reviewModelList = new ArrayList<>();
        current_user = new User();

        userCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        //GET CURRENT USER DETAILS
        getUser();

        findViewById(R.id.img_settings).setOnClickListener(view -> {
            if (current_user != null){
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("current_user", current_user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        findViewById(R.id.img_search).setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("current_user", current_user);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        findViewById(R.id.lyt_copy_link).setOnClickListener(view -> {
            if (current_user != null){
                if (current_user.getAccount_link() == null || current_user.getAccount_link().isEmpty()){
                    createUserLink();
                }else {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", user_link.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), DashboardProfileActivity.this.getString(R.string.link_copied), Toast.LENGTH_SHORT).show();
                }
            }
        });

        user_dp.setOnClickListener(toProfileClickListener);

    }

    private void createUserLink() {
        user_link.setText(DashboardProfileActivity.this.getString(R.string.creating_link));
        findViewById(R.id.lyt_copy_link).setEnabled(false);
        String share_link_description = this.getString(R.string.send_link_description);

        if (current_user.getUser_profile_pic() != null && !current_user.getUser_profile_pic().isEmpty()){
            String share_link = "https://thoughtsonme.page.link/?"+ //link from firebase
                    "link=https://www.thoughtsonme.com/myllink.php?profileid="+ current_user.getUser_doc_id()+//link that opens first
                    "&apn="+ getPackageName() + //package name
                    "&st=" + current_user.getUser_name() +
                    "&sd=" + share_link_description +
                    "&si=" + current_user.getUser_profile_pic();

            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(Uri.parse(share_link))
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            //Uri flowchartLink = task.getResult().getPreviewLink();

                            if (shortLink != null){
                                FirebaseFirestore.getInstance().collection("Users").document(current_user.getUser_doc_id())
                                        .update("account_link", shortLink.toString())
                                        .addOnSuccessListener(runnable -> user_link.setText(shortLink.toString())).addOnFailureListener(runnable ->{
                                    Toast.makeText(DashboardProfileActivity.this, DashboardProfileActivity.this.getString(R.string.failed_to_create_link), Toast.LENGTH_SHORT).show();
                                    user_link.setText(DashboardProfileActivity.this.getString(R.string.tap_to_restart));
                                    findViewById(R.id.lyt_copy_link).setEnabled(true);

                                });
                            }
                        } else {
                            Toast.makeText(DashboardProfileActivity.this, DashboardProfileActivity.this.getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.lyt_copy_link).setEnabled(true);
                        }
                    });

        }else Toast.makeText(DashboardProfileActivity.this, DashboardProfileActivity.this.getString(R.string.please_set_profile_image), Toast.LENGTH_SHORT).show();

    }

    private void getUser() {
        Bundle user_bundle = getIntent().getExtras();
        String user_id = user_bundle.getString("user_id");
        passed_link_uid = user_bundle.getString("passed_link_uid");

        docRef = FirebaseFirestore.getInstance().collection("Users").document(user_id);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {

                DashboardProfileActivity.this.current_user = snapshot.toObject(User.class);
                assert current_user != null;

                if (passed_link_uid != null){
                    Intent intent = new Intent(DashboardProfileActivity.this, UserDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("current_user", current_user);
                    bundle.putString("passed_link_uid", passed_link_uid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                textName.setText(current_user.getUser_name());

                if (current_user.getTag_line() != null){
                    textDescription.setText(current_user.getTag_line());
                }else{
                    textDescription.setText(DashboardProfileActivity.this.getText(R.string.no_tagline_set));
                    textDescription.setTextColor(getResources().getColor(R.color.title_text_color));
                    textDescription.setOnClickListener(toProfileClickListener);
                }

                if (current_user.getAccount_link() == null || current_user.getAccount_link().isEmpty()){
                    createUserLink();
                }else user_link.setText(current_user.getAccount_link());

                findViewById(R.id.img_toInbox).setOnClickListener(view -> {
                    intent = new Intent(getApplicationContext(), ConversationListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("current_user", current_user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });

                Log.d("UserImage", current_user.getUser_profile_pic());

                if (current_user.getUser_profile_pic() != null){
                    Glide.with(getApplicationContext()).load(current_user.getUser_profile_pic()).into(user_dp);
                    Picasso.get().load(current_user.getUser_profile_pic()).into(img_user_settings);
                }else Picasso.get().load(R.drawable.person_icon).into(user_dp);

                fetchReviews();
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

    }

    private void fetchReviews() {
        DocumentReference docRef = userCollectionRef.document(current_user.getUser_doc_id());
        docRef.collection("Reviews")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        if (!value.getDocuments().isEmpty()) {
                            reviewModelList.clear();
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    ReviewModel reviewModel = documentChange.getDocument().toObject(ReviewModel.class);
                                    reviewModel.setReview_id(documentChange.getDocument().getId());
                                    reviewModelList.add(reviewModel);

                                    findViewById(R.id.empty_reviews_layout).setVisibility(View.GONE);

                                    layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                    reviewsAdapter = new UserReviewsAdapter(DashboardProfileActivity.this, current_user, reviewModelList);
                                    user_reviews_rc.setHasFixedSize(true);
                                    user_reviews_rc.setAdapter(reviewsAdapter);
                                    user_reviews_rc.setLayoutManager(layoutManager);

                                }
                            }
                        } else {
                            findViewById(R.id.empty_reviews_layout).setVisibility(View.VISIBLE);
                        }
                    }else findViewById(R.id.empty_reviews_layout).setVisibility(View.VISIBLE);
                });
    }

    private final View.OnClickListener toProfileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("current_user", current_user);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };


}
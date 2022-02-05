package com.gachugusville.thoughtsonme.OtherActivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gachugusville.thoughtsonme.CHAT.MessagesActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TargetDetailsActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_details);

        FloatingActionButton btn_text_user = findViewById(R.id.btn_text_user);
        CircleImageView target_image = findViewById(R.id.target_image);
        TextView txt_target_name = findViewById(R.id.txt_target_name);
        TextView txt_provider_rating = findViewById(R.id.txt_provider_rating);
        TextView txt_provider_numOf_reviews = findViewById(R.id.txt_provider_numOf_reviews);

        findViewById(R.id.btn_back).setOnClickListener(view -> TargetDetailsActivity.super.onBackPressed());

        user = new User();

        User.getInstance().setPrivate_name("Hacker");
        User.getInstance().setPrivate_profile_pic(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl()).toString());

        /*
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    documentSnapshot.toObject(User.getInstance().getClass());
                    User.getInstance().setPrivate_name(Objects.requireNonNull(documentSnapshot.toObject(User.class)).getPrivate_name());
                    User.getInstance().setPrivate_profile_pic(Objects.requireNonNull(documentSnapshot.toObject(User.class)).getPrivate_profile_pic());

                });

         */

        getDetails();

        ViewPager view_pager = findViewById(R.id.view_pager);
        TabLayout tab_layout = findViewById(R.id.tab_layout);

        Picasso.get().load(user.getUser_profile_pic()).into(target_image);
        txt_target_name.setText(user.getUser_name());
        txt_provider_numOf_reviews.setText(String.valueOf(user.getNum_reviews()));

        ReviewAdapter reviewAdapter = new ReviewAdapter(getSupportFragmentManager());

        reviewAdapter.addFragment(new AllReviewsFragment(user), "All");
        reviewAdapter.addFragment(new PositiveReviewsFragment(user), "Positive");
        reviewAdapter.addFragment(new NegativeReviewsFragment(user), "Negative");
        reviewAdapter.addFragment(new AgeRestrictedReviewFragment(user), "Age restricted");

        view_pager.setAdapter(reviewAdapter);
        view_pager.setOffscreenPageLimit(3);
        tab_layout.setupWithViewPager(view_pager);


        btn_text_user.setOnClickListener(view -> {


        });

    }


    private void getDetails() {
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user_instance");
    }


    private static class ReviewAdapter extends FragmentPagerAdapter {

        List<Fragment> reviewFragments = new ArrayList<>();
        List<String> reviewFragmentsTags = new ArrayList<>();

        public ReviewAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return reviewFragments.get(position);
        }

        @Override
        public int getCount() {
            return reviewFragments.size();
        }

        public void addFragment(Fragment fragment, String tag) {
            reviewFragments.add(fragment);
            reviewFragmentsTags.add(tag);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return reviewFragmentsTags.get(position);
        }
    }

}
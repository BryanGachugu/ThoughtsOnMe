package com.gachugusville.thoughtsonme.OtherActivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gachugusville.thoughtsonme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home_nav);

        final int home_id = R.id.home_nav;
        final int search_id = R.id.search_nav;
        final int inbox_id = R.id.inbox_nav;
        final int account_id = R.id.account_nav;

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("user_online_status", 1);

        CustomViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(DashboardActivity.this.getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "home_fragment");
        adapter.addFragment(new SearchFragment(), "search");
        adapter.addFragment(new InboxFragment(), "inbox_fragment");
        adapter.addFragment(new AccountFragment(), "account");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);



        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case home_id:
                    viewPager.setCurrentItem(0);
                    break;
                case search_id:
                    viewPager.setCurrentItem(1);
                    break;
                case inbox_id:
                    viewPager.setCurrentItem(2);
                    break;
                case account_id:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("user_online_status", 1);
    }

    @Override
    protected void onDestroy() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("user_online_status", 0);
        super.onDestroy();
    }
}
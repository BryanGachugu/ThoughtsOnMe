package com.gachugusville.thoughtsonme.OtherActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gachugusville.thoughtsonme.Intro.FourthIntroFragment;
import com.gachugusville.thoughtsonme.Intro.IntroViewPager;
import com.gachugusville.thoughtsonme.MainActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.Constants;

public class IntroFragmentsActivity extends AppCompatActivity {

    CustomViewPager intro_view_pager;
    IntroViewPager pagerAdapter;
    private static String passed_user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_fragments);
        intro_view_pager = findViewById(R.id.intro_view_pager);

        getPassedLink();

        findViewById(R.id.txt_skip).setOnClickListener(view -> {
            toLogin();
        });

        findViewById(R.id.btn_getStarted).setOnClickListener(view -> toLogin());

        pagerAdapter = new IntroViewPager(getSupportFragmentManager(), 0);

        if (pagerAdapter.isLastFragmentVisible()) {
            findViewById(R.id.btn_getStarted).setVisibility(View.VISIBLE);
        }


        intro_view_pager.setPagingEnabled(true);
        intro_view_pager.setAdapter(pagerAdapter);

    }

    private void toLogin() {
        Intent intent = new Intent(IntroFragmentsActivity.this, LogInActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("passed_link_uid", passed_user_uid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getPassedLink() {
        Bundle user_bundle = getIntent().getExtras();
        passed_user_uid = user_bundle.getString("passed_link_uid");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
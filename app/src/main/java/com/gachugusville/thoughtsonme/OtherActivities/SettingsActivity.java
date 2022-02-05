package com.gachugusville.thoughtsonme.OtherActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Settings.AccountSettingsActivity;
import com.gachugusville.thoughtsonme.Utils.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends Activity {

    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getExtras();

        CircleImageView img_dp = findViewById(R.id.img_dp);
        Picasso.get().load(current_user.getUser_profile_pic()).into(img_dp);

        findViewById(R.id.img_back).setOnClickListener(view -> SettingsActivity.super.onBackPressed());
        findViewById(R.id.btn_to_settings).setOnClickListener(view -> toAccountSettings());

    }

    private void toAccountSettings() {
        Intent intent = new Intent(SettingsActivity.this, AccountSettingsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("current_user", current_user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        current_user = (User) bundle.getSerializable("current_user");
    }
}
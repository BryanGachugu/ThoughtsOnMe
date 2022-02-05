package com.gachugusville.thoughtsonme.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private TextView user_details_name, txt_user_email;
    private CircleImageView user_dp;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        user_dp = findViewById(R.id.user_dp);
        user_details_name = findViewById(R.id.user_details_name);
        txt_user_email = findViewById(R.id.txt_user_email);
        getExtras();

        findViewById(R.id.img_back).setOnClickListener(view -> super.onBackPressed());

        Picasso.get().load(current_user.getUser_profile_pic()).into(user_dp);
        user_details_name.setText(current_user.getUser_name());
        txt_user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        current_user = (User) bundle.getSerializable("current_user");
    }
}
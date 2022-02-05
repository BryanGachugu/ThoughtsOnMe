package com.gachugusville.thoughtsonme.OtherActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.gachugusville.thoughtsonme.Profile.EditProfileActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    private EditText edt_search_text_input;
    private CircleImageView small_dp;
    private User current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        small_dp = findViewById(R.id.small_dp);
        edt_search_text_input = findViewById(R.id.edt_search_text_input);

        findViewById(R.id.img_back).setOnClickListener(view -> super.onBackPressed());
        getUser();

        Picasso.get().load(current_user.getUser_profile_pic()).into(small_dp);
        small_dp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("current_user", current_user);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        edt_search_text_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void performSearch() {
        //ADAPTER IS SearchRcAdapter
        //yuvytvyvtyg
        System.out.println("Whyyyyyyy");
    }

    private void getUser() {
        Bundle bundle = getIntent().getExtras();
        current_user = (User) bundle.getSerializable("current_user");
    }
}
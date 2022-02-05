package com.gachugusville.thoughtsonme.OtherActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gachugusville.thoughtsonme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private TextInputEditText edt_emailLogIn, edt_passwordLogIn;
    private String passed_link_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getExtras();

        edt_emailLogIn = findViewById(R.id.edt_emailLogIn);
        edt_passwordLogIn = findViewById(R.id.edt_passwordLogIn);

        findViewById(R.id.btn_toSignUp).setOnClickListener(view -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("passed_link_uid", passed_link_uid);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.txt_forgot_password).setOnClickListener(view -> startActivity(new Intent(LogInActivity.this, ForgotPasswordActivity.class)));

        findViewById(R.id.btn_logIn).setOnClickListener(view -> logInUser());

    }

    private void getExtras() {
        Bundle user_bundle = getIntent().getExtras();
        passed_link_uid = user_bundle.getString("passed_link_uid");
    }

    private void logInUser() {
        if (TextUtils.isEmpty(Objects.requireNonNull(edt_emailLogIn.getText()).toString())) edt_emailLogIn.setError(LogInActivity.this.getString(R.string.input_error));
        else if (TextUtils.isEmpty(Objects.requireNonNull(edt_passwordLogIn.getText()).toString())) edt_passwordLogIn.setError(LogInActivity.this.getString(R.string.input_error));
        else
            FirebaseAuth.getInstance().signInWithEmailAndPassword(edt_emailLogIn.getText().toString(), edt_passwordLogIn.getText().toString())
            .addOnSuccessListener(runnable -> nextActivity()).addOnFailureListener(runnable -> findViewById(R.id.txt_logInError).setVisibility(View.VISIBLE));
    }

    private void nextActivity() {
        Intent intent = new Intent(LogInActivity.this, DashboardProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        bundle.putString("passed_link_uid", passed_link_uid);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}
package com.gachugusville.thoughtsonme.OtherActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gachugusville.thoughtsonme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText edt_emailLogIn;
    private SweetAlertDialog sweetAlertDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();

        edt_emailLogIn = findViewById(R.id.edt_emailLogIn);

        String dialogTitle = String.valueOf(R.string.send_link_dialog_title);
        String dialogContent = String.valueOf(R.string.send_link_dialog_text);

        sweetAlertDialog = new SweetAlertDialog(ForgotPasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(dialogTitle);
        sweetAlertDialog.setContentText(dialogContent);

        findViewById(R.id.img_back).setOnClickListener(view -> super.onBackPressed());
        findViewById(R.id.btn_sendLink).setOnClickListener(view -> sendLink());
    }

    private void sendLink() {
        sweetAlertDialog.show();
        firebaseAuth.sendPasswordResetEmail(Objects.requireNonNull(edt_emailLogIn.getText()).toString())
                .addOnSuccessListener(runnable -> {
                    String dialogSuccessTitle = ForgotPasswordActivity.this.getString(R.string.send_link_success_title);
                    String dialogSuccessText = ForgotPasswordActivity.this.getString(R.string.send_link_success_text);
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText(dialogSuccessTitle);
                    sweetAlertDialog.setContentText(dialogSuccessText);
                    sweetAlertDialog.setConfirmText(String.valueOf(R.string.okay));
                    sweetAlertDialog.setConfirmClickListener(sweetAlertDialog1 -> {
                        sweetAlertDialog.dismissWithAnimation();
                        super.onBackPressed();
                    });
                });


    }
}
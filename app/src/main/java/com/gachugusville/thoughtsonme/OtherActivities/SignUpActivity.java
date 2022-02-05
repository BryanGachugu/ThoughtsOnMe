package com.gachugusville.thoughtsonme.OtherActivities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gachugusville.thoughtsonme.Profile.EditProfileActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends Activity {


    private TextInputEditText edt_username, edt_email, edt_password, edt_password_confirm;
    String dialogContent2;
    private SweetAlertDialog sweetAlertDialog;
    private CircleImageView img_userDp;
    private TextView txt_photo_error;
    private String passed_user_uid;
    private FirebaseAuth mAuth;
    private Uri image_uri;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getExtras();

        mAuth = FirebaseAuth.getInstance();
        dialogContent2 = this.getString(R.string.setting_up_photo);


        edt_username = findViewById(R.id.edt_username);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_password_confirm = findViewById(R.id.edt_password_confirm);
        txt_photo_error = findViewById(R.id.txt_photo_error);
        img_userDp = findViewById(R.id.img_userDp);

        findViewById(R.id.btn_signUp).setOnClickListener(view -> signUpUser());
        findViewById(R.id.btn_toLogIn).setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            finish();
        });
        img_userDp.setOnClickListener(view1 -> openGallery());

    }

    private void getExtras() {
        Bundle user_bundle = getIntent().getExtras();
        passed_user_uid = user_bundle.getString("passed_link_uid");
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
    }

    private void signUpUser() {
        if (TextUtils.isEmpty(Objects.requireNonNull(edt_username.getText()).toString()))
            edt_username.setError(this.getString(R.string.input_error));
        else if (TextUtils.isEmpty(Objects.requireNonNull(edt_email.getText()).toString()))
            edt_email.setError(this.getString(R.string.input_error));
        else if (Objects.requireNonNull(edt_password.getText()).toString().length() < 6)
            edt_password.setError(this.getString(R.string.password_length_error));
        else if (!Objects.requireNonNull(edt_password.getText()).toString().equals(Objects.requireNonNull(edt_password_confirm.getText()).toString()))
            edt_password_confirm.setError(this.getString(R.string.password_matching_error));
        else if (image_uri == null)
            txt_photo_error.setVisibility(View.VISIBLE);
        else {
            txt_photo_error.setVisibility(View.GONE);

            sweetAlertDialog = new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText(this.getString(R.string.creating_account_dialog_title));
            sweetAlertDialog.setContentText(this.getString(R.string.account_dialog_content));
            sweetAlertDialog.show();

            mAuth.createUserWithEmailAndPassword(edt_email.getText().toString(), edt_password.getText().toString())
                    .addOnSuccessListener(runnable -> {
                        this.userID = Objects.requireNonNull(runnable.getUser()).getUid();
                        Log.d("CREATE_UPDATES", "userID: " + userID);
                        User user = new User();

                        user.setUser_name(edt_username.getText().toString());
                        user.setUser_doc_id(userID);

                        FirebaseFirestore.getInstance().collection("Users").document(userID).set(user)
                                .addOnSuccessListener(runnable1 -> setDP())
                                .addOnFailureListener(runnable2 -> nextActivity());
                    }).addOnFailureListener(e -> {
                sweetAlertDialog.dismissWithAnimation();
                Toast.makeText(SignUpActivity.this, "Authentication failed. Try again later", Toast.LENGTH_SHORT).show();
                Log.d("CREATE_UPDATES: ", "Failed to create user because " + e.getMessage());
            });


        }
    }

    private void setDP() {
        Log.d("CREATE_UPDATES: ", "photo started to upload");
        sweetAlertDialog.setContentText(dialogContent2);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("public_dp");
        final StorageReference image_name = storageReference.child("image_public_dp");

        try {
            image_name.putFile(image_uri).addOnSuccessListener(taskSnapshot -> image_name.getDownloadUrl().addOnSuccessListener(uri ->
                    FirebaseFirestore.getInstance().collection("Users")
                            .document(this.userID).update("user_profile_pic", uri.toString())
                            .addOnSuccessListener(unused -> {
                                Log.d("CREATE_UPDATES: ", "photo saved, registration complete");
                                Toast.makeText(SignUpActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                                sweetAlertDialog.dismiss();
                                nextActivity();

                            }).addOnFailureListener(runnable1 -> {
                        Log.d("CREATE_UPDATES: ", "photo failed to upload because " + runnable1.getMessage());
                    })
            ));
        } catch (Exception e) {
            Log.d("CREATE_UPDATES: ", "failed to push file to firebase because " + e.getMessage());
            nextActivity();
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(SignUpActivity.this, DashboardProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userID);
        bundle.putString("passed_user_uid", passed_user_uid);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            image_uri = data.getData();
            Picasso.get().load(image_uri).into(img_userDp);

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
        finish();
    }
}
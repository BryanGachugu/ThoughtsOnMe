package com.gachugusville.thoughtsonme.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gachugusville.thoughtsonme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class PrivateDetailsFragment extends Fragment {

    private CircleImageView user_private_dp;
    private TextView txt_upload_image;
    private EditText edt_private_name;
    private static Uri private_image_uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_details, container, false);

        user_private_dp = view.findViewById(R.id.user_private_dp);
        txt_upload_image = view.findViewById(R.id.txt_upload_image);
        edt_private_name = view.findViewById(R.id.edt_private_name);

        Picasso.get().load(EditProfileActivity.currentUser.getPrivate_profile_pic()).into(user_private_dp);
        edt_private_name.setText(EditProfileActivity.currentUser.getPrivate_name());

        edt_private_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (EditProfileActivity.currentUser.getPrivate_name() != null){
                    if (!EditProfileActivity.currentUser.getPrivate_name().equals(edt_private_name.getText().toString())) {
                        EditProfileActivity.currentUser.setPrivate_name(edt_private_name.getText().toString());
                    }
                }else EditProfileActivity.currentUser.setPrivate_name(edt_private_name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        user_private_dp.setOnClickListener(view1 -> openGallery());
        txt_upload_image.setOnClickListener(view1 -> addToStorage(private_image_uri));

        return view;
    }

    private void addToStorage(Uri image_uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("private_dp");
        final StorageReference image_name = storageReference.child("image_private_dp");

        try {
            image_name.delete().addOnCompleteListener(runnable ->
                    image_name.putFile(image_uri).addOnSuccessListener(taskSnapshot -> image_name.getDownloadUrl().addOnSuccessListener(uri ->
                    {
                        txt_upload_image.setVisibility(View.GONE);

                        FirebaseFirestore.getInstance().collection("Users")
                                .document(EditProfileActivity.currentUser.getUser_doc_id()).update("private_profile_pic", uri.toString())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(runnable1 ->{
                            Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                        });
                    }
            )));
        }catch (Exception e){
            Log.d("Fatal", e.getMessage());
            Toast.makeText(getContext(), "FATALITY", Toast.LENGTH_SHORT).show();
        }


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 200);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null){
            private_image_uri = data.getData();

            Picasso.get().load(private_image_uri).into(user_private_dp);
            txt_upload_image.setVisibility(View.VISIBLE);

        }
    }
}
package com.gachugusville.thoughtsonme.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gachugusville.thoughtsonme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicDetailsFragment extends Fragment {

    private CircleImageView user_public_dp;
    private TextView txt_upload_image;
    private EditText edt_username, user_tag;
    public Uri image_uri;
    private NachoTextView nachoTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_details, container, false);

        user_public_dp = view.findViewById(R.id.user_public_dp);
        txt_upload_image = view.findViewById(R.id.txt_upload_image);
        edt_username = view.findViewById(R.id.edt_username);
        user_tag = view.findViewById(R.id.user_tag);
        nachoTextView = view.findViewById(R.id.user_nick_names);

        nachoTextView.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);

        if (EditProfileActivity.currentUser.getNicknames() != null){
            nachoTextView.setText(EditProfileActivity.currentUser.getNicknames());
        }

        nachoTextView.enableEditChipOnTouch(true, false);
        nachoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EditProfileActivity.currentUser.setNicknames(nachoTextView.getChipValues());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Picasso.get().load(EditProfileActivity.currentUser.getUser_profile_pic()).into(user_public_dp);
        edt_username.setText(EditProfileActivity.currentUser.getUser_name());
        if (EditProfileActivity.currentUser.getTag_line() != null){
            user_tag.setText(EditProfileActivity.currentUser.getTag_line());
        }

        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!EditProfileActivity.currentUser.getUser_name().equals(edt_username.getText().toString())){
                    EditProfileActivity.currentUser.setUser_name(edt_username.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        user_tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (EditProfileActivity.currentUser.getTag_line() != null){
                    if (!EditProfileActivity.currentUser.getTag_line().equals(user_tag.getText().toString())){
                        EditProfileActivity.currentUser.setTag_line(user_tag.getText().toString());
                    }
                }else {
                    EditProfileActivity.currentUser.setTag_line(user_tag.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        user_public_dp.setOnClickListener(view1 -> openGallery());
        txt_upload_image.setOnClickListener(view1 -> addToStorage(image_uri));

        return view;
    }

    private void addToStorage(Uri image_uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("public_dp");
        final StorageReference image_name = storageReference.child("image_public_dp");

        try {
            image_name.delete().addOnSuccessListener(runnable ->
                    image_name.putFile(image_uri).addOnSuccessListener(taskSnapshot -> image_name.getDownloadUrl().addOnSuccessListener(uri ->
                            {
                                txt_upload_image.setVisibility(View.GONE);
                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(EditProfileActivity.currentUser.getUser_doc_id()).update("user_profile_pic", uri.toString())
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(getContext(), requireContext().getString(R.string.success), Toast.LENGTH_SHORT).show();
                                        }).addOnFailureListener(runnable1 ->{
                                    Toast.makeText(getContext(), requireContext().getString(R.string.failed_to_upload_image), Toast.LENGTH_SHORT).show();
                                });
                            }
                    ))).addOnFailureListener(e -> {
                Toast.makeText(getContext(), requireContext().getString(R.string.failed_to_upload_image), Toast.LENGTH_SHORT).show();
            });
        }catch (Exception e){
            Log.d("Fatal", e.getMessage());
            Toast.makeText(getContext(), requireContext().getString(R.string.failed_to_upload_image), Toast.LENGTH_SHORT).show();
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
            image_uri = data.getData();

            Picasso.get().load(image_uri).into(user_public_dp);
            txt_upload_image.setVisibility(View.VISIBLE);

        }
    }

}
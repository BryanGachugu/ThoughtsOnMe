package com.gachugusville.thoughtsonme.OtherActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.gachugusville.thoughtsonme.Profile.EditProfileActivity;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private DocumentReference accountDocRef;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            accountDocRef = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        TextView txt_user_name =  view.findViewById(R.id.txt_user_name);
        TextView txt_secret_name = view.findViewById(R.id.txt_secret_name);
        CircleImageView user_profile = view.findViewById(R.id.user_profile);
        CircleImageView user_private_profile = view.findViewById(R.id.user_private_profile);

        accountDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) currentUser = value.toObject(User.class);

                Picasso.get().load(currentUser.getUser_profile_pic()).into(user_profile);
                txt_user_name.setText(currentUser.getUser_name());
                txt_secret_name.setText(currentUser.getPrivate_name());
                Picasso.get().load(currentUser.getPrivate_profile_pic()).into(user_private_profile);

            }
        });


        view.findViewById(R.id.btn_toSettings).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("currentUser", currentUser);
            intent.putExtras(bundle);
            startActivity(intent);
        });


        view.findViewById(R.id.btn_edit_profile).setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("currentUser", currentUser);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        List<String> phone_numbers = User.getInstance().getPhone_numbers();

        Log.d("MY_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        view.findViewById(R.id.user_profile).setOnClickListener(view1 -> searchUserReviews(phone_numbers));


        return view;

    }

    public void searchUserReviews(List<String> phone_numbers) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        try {
            database.collection("Reviews")
                    .whereArrayContainsAny("target_phone_numbers", phone_numbers)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                            //txt_mesg.setText("Changing ownership...");
                            for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                ReviewModel reviewModel = queryDocumentSnapshots.getDocuments().get(i).toObject(ReviewModel.class);
                                int finalI = i;
                                database.collection("Users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Reviews")
                                        .add(reviewModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        database.collection("Reviews")
                                                .document(queryDocumentSnapshots.getDocuments().get(finalI).getId())
                                                .delete();
                                    }
                                });
                            }
                        } //else txt_mesg.setText("No reviews found");
                    }).addOnCompleteListener(task -> {
                //txt_mesg.setText("Done!!!!!!!!!!!");
               // lottieAnimationView.setVisibility(View.GONE);
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
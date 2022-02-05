package com.gachugusville.thoughtsonme.OtherActivities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gachugusville.thoughtsonme.Adapters.UserReviewsAdapter;
import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.ReviewModel;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NegativeReviewsFragment extends Fragment {

    private List<ReviewModel> negativeReviewsList;
    private UserReviewsAdapter reviewsAdapter;
    private RecyclerView negative_reviews_rc;
    private View view;
    private final User user;

    public NegativeReviewsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_negative_reviews, container, false);
        return view;
    }

}
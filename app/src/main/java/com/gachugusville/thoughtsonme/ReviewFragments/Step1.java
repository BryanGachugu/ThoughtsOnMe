package com.gachugusville.thoughtsonme.ReviewFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import com.gachugusville.thoughtsonme.OtherActivities.AddReviewActivity;
import com.gachugusville.thoughtsonme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;


public class Step1 extends Fragment {

    private TextInputEditText edt_target_name_add_review;
    private NachoTextView nacho_text_view_nicknames, nacho_text_view_phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step1, container, false);


        edt_target_name_add_review = view.findViewById(R.id.edt_target_name_add_review);
        nacho_text_view_nicknames = view.findViewById(R.id.nacho_text_view_nicknames);
        nacho_text_view_phone = view.findViewById(R.id.nacho_text_view_phone);

        edt_target_name_add_review.setText(AddReviewActivity.reviewModel.getTarget_name());
        nacho_text_view_phone.setText(AddReviewActivity.reviewModel.getTarget_phone_numbers());
        nacho_text_view_nicknames.setText(AddReviewActivity.reviewModel.getTarget_nicknames());


        nacho_text_view_nicknames.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        nacho_text_view_phone.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);


        return view;
    }

    public boolean toSecondStepValidated() {
        AddReviewActivity.reviewModel.setTarget_pic_url("https://pyxis.nymag.com/v1/imgs/153/0e8/1276bbaf07a699499ad1a24c4a3a1faebb-3-kristen-stewart.rvertical.w1200.jpg");
        if (edt_target_name_add_review.getText().toString().trim().equals("")) {
            edt_target_name_add_review.setError("Field needed");
        } else if (AddReviewActivity.reviewModel.getTarget_pic_url().trim().equals("")) {
            Toast.makeText(getContext(), "Image needed", Toast.LENGTH_SHORT).show();
        } else if (nacho_text_view_phone.getChipValues().isEmpty()) {
            nacho_text_view_phone.setError("Provide at least 1");
        } else {
            AddReviewActivity.reviewModel.setTarget_name(edt_target_name_add_review.getText().toString());
            edt_target_name_add_review.setText(AddReviewActivity.reviewModel.getTarget_name());
            AddReviewActivity.reviewModel.setTarget_nicknames(nacho_text_view_nicknames.getChipValues());
            nacho_text_view_nicknames.setText(AddReviewActivity.reviewModel.getTarget_nicknames());
            AddReviewActivity.reviewModel.setTarget_phone_numbers(nacho_text_view_phone.getChipValues());
            nacho_text_view_phone.setText(AddReviewActivity.reviewModel.getTarget_phone_numbers());

            return true;
        }
        return false;
    }
}
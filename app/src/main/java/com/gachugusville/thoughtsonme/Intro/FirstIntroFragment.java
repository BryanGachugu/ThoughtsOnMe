package com.gachugusville.thoughtsonme.Intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.Constants;

public class FirstIntroFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_first_intro, container, false);
    }
}
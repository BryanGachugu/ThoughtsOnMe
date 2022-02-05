package com.gachugusville.thoughtsonme.OtherActivities;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gachugusville.thoughtsonme.R;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 3003;
    private EditText edt_search_text_input;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Log.d("ViewPager", "SearchCreated");
        ImageView search_voice_btn = view.findViewById(R.id.search_voice_btn);
        edt_search_text_input = view.findViewById(R.id.edt_search_text_input);
        search_voice_btn.setOnClickListener(v -> displaySpeechRecognizer());

        return view;
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            edt_search_text_input.setText(MessageFormat.format("{0}{1}", Objects.requireNonNull(edt_search_text_input.getText()).toString(), spokenText));

        }
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
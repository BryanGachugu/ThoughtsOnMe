package com.gachugusville.thoughtsonme.OtherActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gachugusville.thoughtsonme.R;

public class InboxFragment extends Fragment {

    private TextView txt_inbox_selection, txt_request_selection;
    private CustomViewPager chat_view_pager;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        Log.d("ViewPager", "InBox");
        chat_view_pager = view.findViewById(R.id.chat_view_pager);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        Toast.makeText(getContext(), "Created", Toast.LENGTH_SHORT).show();

        txt_inbox_selection = view.findViewById(R.id.txt_inbox_selection);
        txt_request_selection = view.findViewById(R.id.txt_request_selection);

        adapter.addFragment(new MessageListFragment(), "messages");
        adapter.addFragment(new RequestsFragment(), "requests");
        chat_view_pager.setPagingEnabled(true);
        chat_view_pager.setAdapter(adapter);


        return view;
    }
}
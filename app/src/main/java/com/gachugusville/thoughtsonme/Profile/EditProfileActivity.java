package com.gachugusville.thoughtsonme.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gachugusville.thoughtsonme.R;
import com.gachugusville.thoughtsonme.Utils.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfileActivity extends AppCompatActivity {

    public static User currentUser;
    private PublicDetailsFragment publicDetailsFragment;
    private PrivateDetailsFragment privateDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getExtras();
        privateDetailsFragment = new PrivateDetailsFragment();
        publicDetailsFragment = new PublicDetailsFragment();

        findViewById(R.id.bck_btn).setOnClickListener(view -> super.onBackPressed());

        TabLayout tab_layout = findViewById(R.id.tab_layout);
        ViewPager profile_vp = findViewById(R.id.profile_vp);

        findViewById(R.id.btn_done).setOnClickListener(view -> updateUserDetails());

        //Initialize adapter
        VpAdapter vpAdapter = new VpAdapter(getSupportFragmentManager());
        //add fragments
        vpAdapter.addFragment(publicDetailsFragment, "Public Details");
        vpAdapter.addFragment(privateDetailsFragment, "Mask Details");
        //set adapter
        profile_vp.setAdapter(vpAdapter);
        //set up Tab layout
        tab_layout.setupWithViewPager(profile_vp);

    }

    private void updateUserDetails() {
        Map<String, Object> userUpdateFields = new HashMap<>();
        userUpdateFields.put("user_name", currentUser.getUser_name());
        userUpdateFields.put("tag_line", currentUser.getTag_line());
        userUpdateFields.put("private_name", currentUser.getPrivate_name());
        userUpdateFields.put("nicknames", currentUser.getNicknames());

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setContentText(EditProfileActivity.this.getString(R.string.loading));

        if (publicDetailsFragment.requireView().findViewById(R.id.txt_upload_image).getVisibility() != View.VISIBLE
        && privateDetailsFragment.requireView().findViewById(R.id.txt_upload_image).getVisibility() !=View.VISIBLE){
            sweetAlertDialog.show();
            FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUser_doc_id())
                    .update(userUpdateFields)
                    .addOnSuccessListener(unused -> {
                        sweetAlertDialog.dismissWithAnimation();
                        Toast.makeText(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.success), Toast.LENGTH_SHORT).show();
                        super.onBackPressed();
                    }).addOnFailureListener(e -> {
                        sweetAlertDialog.dismissWithAnimation();
                        Toast.makeText(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.failed_try_again_later), Toast.LENGTH_SHORT).show();
                    });
        }else {
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitleText(EditProfileActivity.this.getString(R.string.failed));
            sweetAlertDialog.setCancelable(true);
            sweetAlertDialog.setContentText(EditProfileActivity.this.getString(R.string.please_upload_your_profile_picture_first));
            sweetAlertDialog.setConfirmText(EditProfileActivity.this.getString(R.string.okay));
            sweetAlertDialog.setConfirmClickListener(sweetAlertDialog1 -> sweetAlertDialog.dismissWithAnimation());
            sweetAlertDialog.show();

        }



    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        currentUser = (User) bundle.getSerializable("current_user");
    }

    private static class VpAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> fragmentsTitleList = new ArrayList<>();

        public VpAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String fragmentTitle){
            fragments.add(fragment);
            fragmentsTitleList.add(fragmentTitle);
        }

        public CharSequence getPageTitle(int position){
            return fragmentsTitleList.get(position);
        }
    }
}
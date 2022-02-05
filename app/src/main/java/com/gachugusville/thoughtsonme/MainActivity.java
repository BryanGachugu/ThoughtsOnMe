package com.gachugusville.thoughtsonme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gachugusville.thoughtsonme.OtherActivities.DashboardProfileActivity;
import com.gachugusville.thoughtsonme.OtherActivities.IntroFragmentsActivity;
import com.gachugusville.thoughtsonme.OtherActivities.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String user_link;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("com.gachugusville.thoughtsonme", MODE_PRIVATE);
        ImageView img_app_logo = findViewById(R.id.img_app_logo);

        detectDynamicLink();

        //Switch logo according to the them
    }

    private void detectDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(MainActivity.this, pendingDynamicLinkData -> {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink;
                    if (pendingDynamicLinkData != null){
                        deepLink = pendingDynamicLinkData.getLink();
                        assert deepLink != null;
                        Log.d("ShareLink", deepLink.toString());

                        try {
                            user_link = deepLink.toString().substring(deepLink.toString().lastIndexOf("=") + 1);
                            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                                redirectUserIfExists(user_link);
                            }else {
                                redirectNewUser(user_link);
                            }

                        }catch (Exception e){
                            Log.d("LinkError", e.getMessage());
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_fetching_user), Toast.LENGTH_SHORT).show();

                            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                                redirectUserIfExists(null);
                            }else {
                                redirectNewUser(null);
                            }

                        }

                    }else { //NO PASSED DYNAMIC LINK
                        if (FirebaseAuth.getInstance().getCurrentUser() != null){
                            redirectUserIfExists(null);
                        }else {
                            redirectNewUser(null);
                        }
                    }

                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error_fetching_user), Toast.LENGTH_SHORT).show();
                    if (FirebaseAuth.getInstance().getCurrentUser() != null){
                        redirectUserIfExists(null);
                    }else {
                        redirectNewUser(null);
                    }
                });
    }

    private void redirectNewUser(String passed_user_uid) {
        if (prefs.getBoolean("first_run", true)) {
            prefs.edit().putBoolean("first_run", false).apply();
            Intent intent = new Intent(MainActivity.this, IntroFragmentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("passed_link_uid", passed_user_uid);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("passed_link_uid", passed_user_uid);
            intent.putExtras(bundle);
            startActivity(intent);

        }
        finish();
    }

    public void redirectUserIfExists(String passed_user_uid){
            Intent intent = new Intent(MainActivity.this, DashboardProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            bundle.putString("passed_link_uid", passed_user_uid);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
    }
}
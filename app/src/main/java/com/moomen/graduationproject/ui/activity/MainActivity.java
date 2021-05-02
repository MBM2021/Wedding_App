package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity {

    private BadgeDrawable badgeNotification;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        firebaseFirestore = FirebaseFirestore.getInstance();

        getNotSeenNumberNotification();


        badgeNotification = navView.getOrCreateBadge(R.id.navigation_notification_user);
        badgeNotification.setBackgroundColor(getResources().getColor(R.color.purple_500));
        badgeNotification.setBadgeTextColor(getResources().getColor(R.color.white));


    }


    private void getNotSeenNumberNotification() {
        if (PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty()) {
            firebaseFirestore.collection("Notifications")
                    .whereEqualTo("seen", false)
                    .whereEqualTo("userTypeNotification", "user")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (!task.getResult().isEmpty()) {
                        int size = task.getResult().size();
                        //textView.setText(size + "");
                        if (size > 0) {
                            badgeNotification.setVisible(true);
                            badgeNotification.setNumber(size);
                        } else {
                            badgeNotification.setVisible(false);
                        }

                        refreshNotification();
                    }
                }
            });
        }
    }

    private void refreshNotification() {
        if (PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty()) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    getNotSeenNumberNotification();
                }
            };
            handler.postDelayed(runnable, 500);
        }
    }

    public void registerButtonOnClick(View view) {
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }
}
package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

        getAllNotification();



        badgeNotification = navView.getOrCreateBadge(R.id.navigation_notification_user);
        badgeNotification.setBackgroundColor(getResources().getColor(R.color.purple_500));
        badgeNotification.setBadgeTextColor(getResources().getColor(R.color.white));



    }


    private void getAllNotification() {
        firebaseFirestore.collection("Notifications").whereEqualTo("seen",true).whereEqualTo("user_seen", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
        });

    }

    private void refreshNotification() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getAllNotification();
            }
        };
        handler.postDelayed(runnable, 500);
    }


    public void registerButtonOnClick(View view) {
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
    }
}
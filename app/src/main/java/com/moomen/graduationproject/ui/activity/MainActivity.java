package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
        badgeNotification = navView.getOrCreateBadge(R.id.navigation_notification_user);
        badgeNotification.setBackgroundColor(getResources().getColor(R.color.purple_500));
        badgeNotification.setBadgeTextColor(getResources().getColor(R.color.white));
        badgeNotification.setVisible(false);
        getNotSeenNumberNotification();
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

    private void checkUserTypeToSignIn() {
        if (PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty()) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String userType = documentSnapshot.getString("userType");
                    if (userType != null || !userType.isEmpty()) {
                        switch (userType) {
                            case "user":
                                //startActivity(new Intent(MainActivity.this, MainActivity.class));
                                break;
                            case "company":
                                startActivity(new Intent(MainActivity.this, MainActivityCompany.class));
                                finish();
                                break;
                            case "admin":
                                startActivity(new Intent(MainActivity.this, MainActivityAdmin.class));
                                finish();
                                break;
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserTypeToSignIn();
    }
}
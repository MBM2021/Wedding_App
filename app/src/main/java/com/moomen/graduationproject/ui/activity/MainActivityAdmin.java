package com.moomen.graduationproject.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;

public class MainActivityAdmin extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        firebaseFirestore = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.textView_count_notification);
        getAllNotification();

    }

    private void getAllNotification() {
        firebaseFirestore.collection("Notifications").whereEqualTo("seen", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int size = task.getResult().size();
                textView.setText(size + "");
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getAllNotification();
    }


    @Override
    protected void onPause() {
        super.onPause();
        getAllNotification();
    }
}
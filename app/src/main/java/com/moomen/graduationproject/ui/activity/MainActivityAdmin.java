package com.moomen.graduationproject.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.ui.fragment.AccountFragment;
import com.moomen.graduationproject.ui.fragment.admin.ChatAdminFragment;
import com.moomen.graduationproject.ui.fragment.admin.ConsoleAdminFragment;
import com.moomen.graduationproject.ui.fragment.admin.NotificationAdminFragment;
import com.moomen.graduationproject.ui.fragment.admin.UsersAdminFragment;
import com.moomen.graduationproject.utils.PreferenceUtils;

public class MainActivityAdmin extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private TextView textView;
    private Fragment fragment;
    private BadgeDrawable badgeNotification;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupWithNavController(navView, navController);
        firebaseFirestore = FirebaseFirestore.getInstance();

       /* FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
*/
        fragment = new ConsoleAdminFragment();
        setFragment(fragment);
        getAllNotification();

        badgeNotification = navView.getOrCreateBadge(R.id.navigation_notification_admin);
        badgeNotification.setBackgroundColor(getResources().getColor(R.color.purple_500));
        badgeNotification.setBadgeTextColor(getResources().getColor(R.color.white));
        //badgeNotification.setNumber(100000);
        //badgeNotification.setVisible(true);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_console_admin:
                        fragment = new ConsoleAdminFragment();
                        break;
                    case R.id.navigation_users_admin:
                        fragment = new UsersAdminFragment();
                        break;
                    case R.id.navigation_chat_admin:
                        fragment = new ChatAdminFragment();
                        break;
                    case R.id.navigation_profile:
                        fragment = new AccountFragment();
                        break;
                    case R.id.navigation_notification_admin:
                        fragment = new NotificationAdminFragment();
                        BadgeDrawable badgeNotification = navView.getBadge(R.id.navigation_notification_admin);
                        badgeNotification.clearNumber();
                        break;
                }
                setFragment(fragment);
                return true;
            }
        });
    }


    private void getAllNotification() {
        firebaseFirestore.collection("Notifications").whereEqualTo("seen", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int size = task.getResult().size();
                //textView.setText(size + "");
                if (size > 0) {
                    badgeNotification.setVisible(true);
                    badgeNotification.setNumber(size);
                } else
                    badgeNotification.setVisible(false);
                refreshNotification();
            }
        });
        if (PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty()) {
            firebaseFirestore.collection("Notifications").whereEqualTo("status", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    int size = task.getResult().size();
                    //textView.setText(size + "");
                    if (size > 0) {
                        badgeNotification.setVisible(true);
                        badgeNotification.setNumber(size);
                    } else
                        badgeNotification.setVisible(false);
                    refreshNotification();
                }
            });

        }
    }

    private void refreshNotification() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty())
                    getAllNotification();
            }
        };
        handler.postDelayed(runnable, 500);
    }

    private void setFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit();
    }


}
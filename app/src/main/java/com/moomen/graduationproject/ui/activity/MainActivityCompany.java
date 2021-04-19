package com.moomen.graduationproject.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.NotifcationHelper;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.ui.fragment.HomeFragment;
import com.moomen.graduationproject.ui.fragment.AccountFragment;
import com.moomen.graduationproject.ui.fragment.company.ChatCompanyFragment;
import com.moomen.graduationproject.ui.fragment.company.ConsoleCompanyFragment;
import com.moomen.graduationproject.ui.fragment.company.NotificationCompanyFragment;

public class MainActivityCompany extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private Fragment fragmentCompany;
    private BadgeDrawable badgeNotification;
    private FragmentTransaction fragmentTransactionCompany;
    private NotifcationHelper notifcationHelper;
    private int number = 0;
    private String userID ;
    int n = 0;
    int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        notifcationHelper = new NotifcationHelper(this);
        /*fragmentCompany = new HomeFragment();
        setFragmentCompany(fragmentCompany);
        */getAllNotification();






        badgeNotification = navView.getOrCreateBadge(R.id.navigation_notification_company);
        badgeNotification.setBackgroundColor(getResources().getColor(R.color.purple_500));
        badgeNotification.setBadgeTextColor(getResources().getColor(R.color.white));


       /* navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragmentCompany = new HomeFragment();
                        break;
                    case R.id.navigation_console_company:
                        fragmentCompany = new ConsoleCompanyFragment();
                        break;
                    case R.id.navigation_chat_company:
                        fragmentCompany = new ChatCompanyFragment();
                        break;
                    case R.id.navigation_profile:
                        fragmentCompany = new AccountFragment();
                        break;
                    case R.id.navigation_notification_company:
                        fragmentCompany = new NotificationCompanyFragment();
                        BadgeDrawable badgeNotification = navView.getBadge(R.id.navigation_notification_company);
                        badgeNotification.clearNumber();
                        break;
                }
                setFragmentCompany(fragmentCompany);
                return true;
            }
        });

*/
    }

    private void getAllNotification() {
        firebaseFirestore.collection("Notifications").whereEqualTo("userUid",userID).whereEqualTo("status",true).whereEqualTo("seen", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int size = task.getResult().size();
                //textView.setText(size + "");
                if (size > 0) {
                    badgeNotification.setVisible(true);
                    badgeNotification.setNumber(size);

                    runNotification(size);

                } else {
                    badgeNotification.setVisible(false);
                }
                if (size == 0){
                    n = 0 ;
                    id = 1;
                }



                refreshNotification();

            }
        });

    }

    private void runNotification(int size){
        if (size > n){

            n = size ;

//            firebaseFirestore.collection("Notifications").document("MUK1FqueoG3n3jhxpTHN").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    Notification notification = task.getResult().toObject(Notification.class);
//                    Notification(notification.getTitle(), "new not");
//
//                }
//            });

            Notification("Mohammed"+id, "new not");
            id++;
        }

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

    private void setFragmentCompany(Fragment fragmentCompany) {
        fragmentTransactionCompany = getSupportFragmentManager().beginTransaction();
        fragmentTransactionCompany.replace(R.id.nav_host_fragment, fragmentCompany).commit();
    }

    private void Notification(String title , String message){
       NotificationCompat.Builder m = notifcationHelper.getCannel1Notification(title,message);
       notifcationHelper.getManager().notify(id,m.build());

    }


}
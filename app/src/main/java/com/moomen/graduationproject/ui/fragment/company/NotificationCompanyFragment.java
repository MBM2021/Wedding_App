package com.moomen.graduationproject.ui.fragment.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.NotificationAdapter;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.ui.activity.ViewServiceActivity;

public class NotificationCompanyFragment extends Fragment {


    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String HALL_ID = "HALL_ID";
    public static final String USER_ID = "USER_ID";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String Notification_ID = "Notification_ID";


    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;


    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_company, container, false);
    }

    private String userId;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_notification);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getAllNotificationAccepted();
    }

    private void getAllNotificationAccepted() {
        Query query = FirebaseFirestore.getInstance().collection("Notifications")
                .whereEqualTo("userUid", userID)
                .whereEqualTo("userTypeNotification", "company");
        //.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();
        fillNotificationRecycleAdapter(options);
    }
    private String serviceId;
    private String hallId;

    private void fillNotificationRecycleAdapter(FirestoreRecyclerOptions<Notification> options) {
        NotificationAdapter notificationAdapter = new NotificationAdapter(options);
        notificationAdapter.onItemSetOnClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Notification notification = documentSnapshot.toObject(Notification.class);
                userId = notification.getUserUid();
                serviceId = notification.getServiceUid();
                hallId = notification.getHallUid();

                String notificationUid = documentSnapshot.getId();
                updateStatusValueNotification(notificationUid);


            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(notificationAdapter);
        notificationAdapter.startListening();


    }

    private void updateStatusValueNotification(String notificationUid) {
        firebaseFirestore.collection("Notifications").document(notificationUid).update("status", true, "seen", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getContext(), ViewServiceActivity.class);
                intent.putExtra(SERVICE_ID, serviceId);
                intent.putExtra(HALL_ID, hallId);
                intent.putExtra(USER_ID, userId);
                intent.putExtra(USER_TYPE, "company");
                startActivity(intent);
            }
        });
    }

}
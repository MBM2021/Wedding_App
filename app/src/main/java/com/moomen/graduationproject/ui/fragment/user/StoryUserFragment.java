package com.moomen.graduationproject.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.moomen.graduationproject.adapter.NotificationAdapterUser;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.ui.activity.ViewServiceActivity;
import com.moomen.graduationproject.viewModel.DashboardViewModel;

public class StoryUserFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String HALL_ID = "HALL_ID";
    public static final String USER_ID = "USER_ID";
    public static final String USER_TYPE = "USER_TYPE";

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
/*
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
*/
        View root = inflater.inflate(R.layout.fragment_story_user, container, false);
       /* final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_notification);
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getAllNotificationAccepted();
    }
    private void getAllNotificationAccepted() {
        Query query = FirebaseFirestore.getInstance().collection("Notifications").whereEqualTo("status",true).whereEqualTo("seen",true)
                .orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();
        fillNotificationRecycleAdapter(options);

    }
    private  String userId;
    private String serviceId;
    private String hallId;

    private void fillNotificationRecycleAdapter(FirestoreRecyclerOptions<Notification> options) {
        NotificationAdapterUser notificationAdapter = new NotificationAdapterUser(options);
        notificationAdapter.onItemSetOnClickListener(new NotificationAdapterUser.OnItemClickListener() {
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
        firebaseFirestore.collection("Notifications").document(notificationUid).update("user_seen", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getContext(), ViewServiceActivity.class);
                intent.putExtra(SERVICE_ID, serviceId);
                intent.putExtra(HALL_ID, hallId);
                intent.putExtra(USER_ID, userId);
                intent.putExtra(USER_TYPE,"user");
                startActivity(intent);
            }
        });
    }
}
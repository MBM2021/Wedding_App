package com.moomen.graduationproject.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.NotificationAdapter;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.ui.activity.SignInActivity;
import com.moomen.graduationproject.ui.activity.ViewBookingDetailsUserActivity;
import com.moomen.graduationproject.ui.activity.ViewServiceDetailsActivity;
import com.moomen.graduationproject.utils.PreferenceUtils;
import com.moomen.graduationproject.viewModel.DashboardViewModel;

public class NotificationUserFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String BOOKING_ID = "BOOKING_ID";

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private String userId;
    private String bookingId;
    private String typeNotification = "";

    private View root;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_notification);
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (isLogin()) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getAllNotificationAccepted();
        } else
            showSnackBar();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notification_user, container, false);
        return root;
    }

    private void getAllNotificationAccepted() {
        Query query = FirebaseFirestore.getInstance()
                .collection("Notifications")
                .whereEqualTo("userTypeNotification", "user");
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();
        fillNotificationRecycleAdapter(options);
    }

    private String serviceId;

    private void fillNotificationRecycleAdapter(FirestoreRecyclerOptions<Notification> options) {
        NotificationAdapter notificationAdapter = new NotificationAdapter(options);
        notificationAdapter.onItemSetOnClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Notification notification = documentSnapshot.toObject(Notification.class);
                userId = notification.getUserUid();
                serviceId = notification.getServiceUid();
                bookingId = notification.getHallUid();
                typeNotification = notification.getNotificationType();
                String notificationUid = documentSnapshot.getId();
                updateStatusValueNotification(notificationUid);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(notificationAdapter);
        notificationAdapter.startListening();

    }

    private void updateStatusValueNotification(String notificationUid) {
        firebaseFirestore.collection("Notifications").document(notificationUid).update("status", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getContext(), ViewServiceDetailsActivity.class);
                if (typeNotification.equals("Booking"))
                    intent = new Intent(getContext(), ViewBookingDetailsUserActivity.class);
                intent.putExtra(SERVICE_ID, serviceId);
                intent.putExtra(BOOKING_ID, bookingId);
                startActivity(intent);
            }
        });
    }

    private void showSnackBar() {
        ConstraintLayout parentLayout = root.findViewById(R.id.fragment_notification_user_constraint);
        Snackbar snackbar = Snackbar.make(parentLayout, "You must sign in!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Sign in", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
                snackbar.dismiss();
            }
        }).setActionTextColor(getResources().getColor(R.color.purple_700)).show();
    }

    private boolean isLogin() {
        return PreferenceUtils.getEmail(getContext()) != null && !PreferenceUtils.getEmail(getContext()).isEmpty();
    }
}
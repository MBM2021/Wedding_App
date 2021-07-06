package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.ActivityViewBookingDetailsBinding;
import com.moomen.graduationproject.model.Booking;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.fragment.company.NotificationCompanyFragment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

public class ViewBookingDetailsActivity extends AppCompatActivity {
    private String serviceId;
    private String bookingId;
    private String userBookingId;
    private String bookingServiceId;
    private String userId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ActivityViewBookingDetailsBinding binding;
    private String date;
    private boolean isCancelBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking_details);
        binding = ActivityViewBookingDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(NotificationCompanyFragment.SERVICE_ID)
                && intent.hasExtra(NotificationCompanyFragment.BOOKING_ID) && intent.hasExtra(NotificationCompanyFragment.USER_BOOKING_ID)) {
            serviceId = intent.getStringExtra(NotificationCompanyFragment.SERVICE_ID);
            bookingId = intent.getStringExtra(NotificationCompanyFragment.BOOKING_ID);
            userBookingId = intent.getStringExtra(NotificationCompanyFragment.USER_BOOKING_ID);
            getBookingDetails();
            getServiceDetails();
            getUserDetails();
        }
        acceptOrRejectBooking();
    }

    private void acceptOrRejectBooking() {
        binding.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Services").document(serviceId)
                        .collection("Booking").document(bookingId)
                        .update("status", true, "inReview", false)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_SHORT).show();
                            }

                        });
                updateUserBookingStatus(true);
                sendNotificationToUser("Accepted");
            }
        });

        binding.buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Services").document(serviceId).collection("Booking").document(bookingId).update("status", false, "inReview", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Rejected", Toast.LENGTH_SHORT).show();
                    }
                });
                updateUserBookingStatus(false);
                sendNotificationToUser("Rejected");
            }
        });
    }

    private void updateUserBookingStatus(boolean status) {
        firebaseFirestore.collection("Users").document(userBookingId)
                .collection("Booking")
                .document(bookingServiceId).update("status", status, "inReview", false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getBookingDetails();
            }
        });
    }

    private void getBookingDetails() {
        firebaseFirestore.collection("Services").document(serviceId).collection("Booking").document(bookingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Booking booking = task.getResult().toObject(Booking.class);
                bookingServiceId = booking.getBookingServiceId();
                isCancelBooking = booking.isCancelBooking();
                binding.textViewBookingDateId.setText(booking.getBookingDate());
                binding.textViewDateId.setText(booking.getDate());
                if (booking.isCancelBooking()) {
                    binding.textViewStatusId.setText("The user canceled this book");
                    binding.buttonAccept.setVisibility(View.GONE);
                    binding.buttonReject.setVisibility(View.GONE);
                } else {
                    if (booking.isInReview())
                        binding.textViewStatusId.setText("In Review");
                    else {
                        if (booking.isStatus())
                            binding.textViewStatusId.setText("Accepted");
                        else
                            binding.textViewStatusId.setText("Rejected");
                    }
                }
            }
        });
    }

    private void getServiceDetails() {
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                binding.textViewServiceName.setText(service.getName());
                Picasso.get().load(service.getImage()).into(binding.imageViewServiceImageId);
            }
        });
    }

    private void getUserDetails() {
        firebaseFirestore.collection("Users").document(userBookingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);

                binding.textViewUserNameId.setText(user.getName());
                binding.textViewUserEmailId.setText(user.getEmail());
                Picasso.get().load(user.getUserImage()).into(binding.imageViewUserId);
            }
        });
    }

    private void sendNotificationToUser(String status) {
        Notification notification = new Notification("", "", "Your Booking was " + status, "", date, serviceId, bookingId, userBookingId, "Booking", false, false, false, "user", userId);
        firebaseFirestore.collection("Notifications").add(notification);
    }
}
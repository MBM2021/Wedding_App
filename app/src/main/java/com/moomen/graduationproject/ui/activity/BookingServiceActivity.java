package com.moomen.graduationproject.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Booking;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.model.Service;

import java.text.DateFormat;
import java.util.Calendar;

public class BookingServiceActivity extends AppCompatActivity {

    private String bookingId;
    private CalendarView calendarView;
    private Button bookingButton;
    private Button cancelButton;
    private String bookingDate;
    private String date;
    private String serviceId;
    private String userId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_service);
        calendarView = findViewById(R.id.calendarView);
        bookingButton = findViewById(R.id.book_button);
        cancelButton = findViewById(R.id.cancel_button);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                bookingDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                //calendarView.setSelectedWeekBackgroundColor(getResources().getColor(R.color.gray));
                //calendarView.setSelected();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ViewServiceDetailsActivity.SERVICE_ID))
            serviceId = intent.getStringExtra(ViewServiceDetailsActivity.SERVICE_ID);

        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCurrentDateIfBooking();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });

    }

    private void cancelBooking() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookingServiceActivity.this);
        dialogBuilder.setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel booking?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeBooking();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void removeBooking() {
        firebaseFirestore.collection("Services").document(serviceId).collection("Booking").document(bookingId).delete();
    }

    private void checkCurrentDateIfBooking() {
        firebaseFirestore.collection("Services")
                .document(serviceId)
                .collection("Booking")
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookingDate", bookingDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty())
                    Toast.makeText(getApplicationContext(), "Please book new date", Toast.LENGTH_SHORT).show();
                else {
                    firebaseFirestore.collection("Services")
                            .document(serviceId)
                            .collection("Booking")
                            .whereEqualTo("bookingDate", bookingDate)
                            .whereEqualTo("status", true)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!task.getResult().isEmpty())
                                Toast.makeText(getApplicationContext(), "Already booked, Select another date!", Toast.LENGTH_LONG).show();
                            else
                                confirmBookingDialog();
                        }
                    });
                }
            }
        });
    }

    private void confirmBookingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookingServiceActivity.this);
        dialogBuilder.setTitle("Confirm Booking")
                .setMessage("Are you sure you want to book the service in this date " + bookingDate + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createBooking();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void createBooking() {
        if (bookingDate.isEmpty())
            Toast.makeText(getApplicationContext(), "You must select booking date", Toast.LENGTH_SHORT).show();
        else {
            Booking booking = new Booking(bookingDate, date, serviceId, userId, "2-days", "", false, true);
            firebaseFirestore.collection("Services").document(serviceId).collection("Booking").add(booking).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    bookingId = task.getResult().getId();
                    firebaseFirestore.collection("Users")
                            .document(userId).collection("Booking")
                            .add(booking).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            firebaseFirestore.collection("Services").document(serviceId).collection("Booking").document(bookingId).update("bookingServiceId", task.getResult().getId());
                        }
                    });
                    sendNotificationToCompany();
                }
            });
        }
    }

    private void sendNotificationToCompany() {
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                String companyId = service.getCompanyId();
                Notification notification = new Notification("", "", "This User need to booking your service", "Check the order", date, serviceId, bookingId, companyId, "Booking", false, false, false, "company", userId);
                firebaseFirestore.collection("Notifications").add(notification).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(getApplicationContext(), "Your booking in process!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), BookingListActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}

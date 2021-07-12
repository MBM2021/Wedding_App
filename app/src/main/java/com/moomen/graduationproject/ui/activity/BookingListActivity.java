package com.moomen.graduationproject.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.BookingAdapter;
import com.moomen.graduationproject.model.Booking;

public class BookingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private ImageView backImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        recyclerView = findViewById(R.id.recyclerView_booking_list_id);
        backImage = findViewById(R.id.imageView_back);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        getAllBooking();
        backButton();
    }

    private void backButton() {
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getAllBooking() {
        Query query = FirebaseFirestore.getInstance().collection("Users")
                .document(userId).collection("Booking");
        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(query, Booking.class)
                .build();
        fillBookingRecycleAdapter(options);
    }

    private void fillBookingRecycleAdapter(FirestoreRecyclerOptions<Booking> options) {
        BookingAdapter bookingAdapter = new BookingAdapter(options);
        bookingAdapter.onItemSetOnClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Booking booking = documentSnapshot.toObject(Booking.class);
                firebaseFirestore.collection("Services")
                        .document(booking.getServiceId())
                        .collection("Booking")
                        .document(booking.getBookingServiceId()).update("cancelBooking", true);
                firebaseFirestore.collection("Users")
                        .document(userId)
                        .collection("Booking")
                        .document(documentSnapshot.getId()).update("cancelBooking", true);
                Toast.makeText(getApplicationContext(), "Booking canceled Successfully!", Toast.LENGTH_LONG).show();
                bookingAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.startListening();
    }
}
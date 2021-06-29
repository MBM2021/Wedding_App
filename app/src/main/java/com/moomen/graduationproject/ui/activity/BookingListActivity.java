package com.moomen.graduationproject.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;

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
    private ImageView bachImage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        recyclerView = findViewById(R.id.recyclerView_booking_list_id);
        bachImage = findViewById(R.id.imageView_back);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        getAllBooking();
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
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, int id) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.startListening();


    }
}
package com.moomen.graduationproject.ui.fragment.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class MyActivityUserFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageView bachImage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_activity_user, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_booking_list_id);
        bachImage = view.findViewById(R.id.imageView_back);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.startListening();


    }
}
package com.moomen.graduationproject.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.BookingAdapter;
import com.moomen.graduationproject.model.Booking;
import com.moomen.graduationproject.ui.activity.SignInActivity;
import com.moomen.graduationproject.utils.PreferenceUtils;

public class MyActivityUserFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageView bachImage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_my_activity_user, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_booking_list_id);
        bachImage = view.findViewById(R.id.imageView_back);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if (isLogin()) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getAllBooking();
        } else
            showSnackBar();

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
                        .whereEqualTo("bookingServiceId", documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        firebaseFirestore.collection("Services")
                                .document(booking.getServiceId())
                                .collection("Booking")
                                .document(task.getResult().getDocuments().get(0).getId()).update("cancelBooking", true);
                    }
                });
                firebaseFirestore.collection("Users")
                        .document(userId)
                        .collection("Booking")
                        .document(documentSnapshot.getId()).update("cancelBooking", true);
                Toast.makeText(getContext(), "Booking canceled Successfully!", Toast.LENGTH_LONG).show();
                bookingAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.startListening();


    }

    private void showSnackBar() {
        ConstraintLayout parentLayout = root.findViewById(R.id.fragment_constraint);
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
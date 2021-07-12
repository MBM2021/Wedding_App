package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.ActivityViewBookingDetailsUserBinding;
import com.moomen.graduationproject.model.Booking;
import com.moomen.graduationproject.model.Favourite;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.ui.fragment.HomeFragment;
import com.moomen.graduationproject.ui.fragment.user.NotificationUserFragment;
import com.moomen.graduationproject.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

public class ViewBookingDetailsUserActivity extends AppCompatActivity {

    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String RECEIVER_ID = "RECEIVER_ID";
    public static final String SERVICE_TYPE_ID = "SERVICE_TYPE_ID";
    public static final String CATEGORY_TYPE = "CATEGORY_TYPE";

    private ActivityViewBookingDetailsUserBinding binding;
    private String serviceId;
    private String receiverId;
    private String serviceTypeId;
    private String categoryType;
    private String bookingId;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewBookingDetailsUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(HomeFragment.SERVICE_ID)
                && intent.hasExtra(HomeFragment.CATEGORY_TYPE)) {
            serviceId = intent.getStringExtra(HomeFragment.SERVICE_ID);
            categoryType = intent.getStringExtra(HomeFragment.CATEGORY_TYPE);
        } else if (intent != null && intent.hasExtra(NotificationUserFragment.SERVICE_ID)) {
            serviceId = intent.getStringExtra(NotificationUserFragment.SERVICE_ID);
            categoryType = "Services";
            bookingId = intent.getStringExtra(NotificationUserFragment.BOOKING_ID);
        } else if (intent != null && intent.hasExtra(SignInActivity.SERVICE_ID)) {
            serviceId = intent.getStringExtra(SignInActivity.SERVICE_ID);
            categoryType = "Services";
        }
        getServiceInfo();
        addToFavouriteOnClick();
        backButton();
    }

    private void backButton() {
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getServiceInfo() {
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                receiverId = service.getCompanyId();
                Picasso.get().load(service.getImage()).into(binding.imageViewServiceImageDetailsActivity);
                binding.textViewServiceNameDetailsActivity.setText(service.getName());
                binding.textViewServiceDetailsDetailsActivity.setText(service.getDetail());
                binding.textViewServicePriceDetailsActivity.setText(service.getPrice() + " $");
                getBookingDetails();
            }
        });
    }

    private void getBookingDetails() {
        firebaseFirestore.collection("Services").document(serviceId).collection("Booking").document(bookingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Booking booking = task.getResult().toObject(Booking.class);
                if (booking.isStatus())
                    binding.textViewBookingStatus.setText("Accepted");
                else
                    binding.textViewBookingStatus.setText("Rejected");
            }
        });
    }

    private void addToFavouriteOnClick() {
        binding.imageViewFavouriteIconDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {
                    Favourite favourite = new Favourite(serviceId);
                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    firebaseFirestore.collection("Users").document(userUid)
                            .collection("Favourite")
                            .add(favourite).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(getApplicationContext(), "Add to Favourite List", Toast.LENGTH_SHORT).show();
                            binding.imageViewFavouriteIconDetailsActivity.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                        }
                    });
                } else {
                    showSnackBar();
                }
            }
        });
    }

    private void showSnackBar() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "You must sign in!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Sign in", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.putExtra(SERVICE_ID, serviceId);
                startActivity(intent);
                snackbar.dismiss();
            }
        }).setActionTextColor(getResources().getColor(R.color.purple_700)).show();
    }

    private boolean isLogin() {
        return PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty();
    }

}
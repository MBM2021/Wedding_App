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
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.ActivityViewServiceBinding;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.fragment.admin.NotificationAdminFragment;
import com.squareup.picasso.Picasso;

public class ViewServiceActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private String serviceId;
    private String hallId;
    private ActivityViewServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewServiceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(NotificationAdminFragment.USER_ID)
                && intent.hasExtra(NotificationAdminFragment.SERVICE_ID)
                && intent.hasExtra(NotificationAdminFragment.HALL_ID)) {
            userId = intent.getStringExtra(NotificationAdminFragment.USER_ID);
            serviceId = intent.getStringExtra(NotificationAdminFragment.SERVICE_ID);
            hallId = intent.getStringExtra(NotificationAdminFragment.HALL_ID);
        }
        getServiceInfo();
        getUserInfo();
        bottomNavigationOnClickItem();
    }

    int select = 0;

    private void getServiceInfo() {
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                binding.textViewServiceType.setText(service.getType());
                binding.textViewServiceName.setText(service.getName());
                binding.textViewServiceOwnerName.setText(service.getOwnerName());
                binding.textViewServiceContactPhone.setText(service.getPhone());
                binding.textViewServiceLocation.setText(service.getLocation());
                binding.textViewServiceCity.setText(service.getCity());
                binding.textViewServiceDetails.setText(service.getDetail());
                Picasso.get().load(service.getImage()).into(binding.serviceImage);
                //Service status
                if (service.isStatus())
                    binding.textViewServiceStatus.setText(getString(R.string.accepted));
                else
                    binding.textViewServiceStatus.setText(getString(R.string.rejected));
                binding.textViewServiceCreatedDate.setText(service.getDate());
            }
        });
    }

    private void getUserInfo() {
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                Picasso.get().load(user.getUserImage()).into(binding.imageViewUserId);
                binding.textViewUserNameId.setText(user.getName());
                binding.textViewUserEmailId.setText(user.getEmail());
                binding.textViewUserPhoneId.setText(user.getPhone());

            }
        });
    }

    private void bottomNavigationOnClickItem() {
        binding.linearLayoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.linearLayoutAcceptService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Services").document(serviceId).update("status", true);
                firebaseFirestore.collection("Halls").document(hallId).update("status", true);
                firebaseFirestore.collection("Dresses").document(hallId).update("status", true);
                Toast.makeText(getApplicationContext(), getString(R.string.accepted), Toast.LENGTH_SHORT).show();
                getServiceInfo();
            }
        });
        binding.linearLayoutCancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Services").document(serviceId).update("status", false);
                firebaseFirestore.collection("Halls").document(hallId).update("status", false);
                firebaseFirestore.collection("Dresses").document(hallId).update("status", false);
                Toast.makeText(getApplicationContext(), getString(R.string.rejected), Toast.LENGTH_SHORT).show();
                getServiceInfo();
            }
        });
        pushNotification();
    }

    private void pushNotification() {

    }
}
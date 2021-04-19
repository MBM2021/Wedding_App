package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.databinding.ActivityViewServiceDetailsBinding;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.ui.fragment.HomeFragment;
import com.squareup.picasso.Picasso;

public class ViewServiceDetailsActivity extends AppCompatActivity {

    private ActivityViewServiceDetailsBinding binding;
    private String serviceId;
    private String categoryType;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewServiceDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(HomeFragment.SERVICE_ID)
                && intent.hasExtra(HomeFragment.CATEGORY_TYPE)) {
            serviceId = intent.getStringExtra(HomeFragment.SERVICE_ID);
            categoryType = intent.getStringExtra(HomeFragment.CATEGORY_TYPE);
        }
        getServiceInfo();
    }

    private void getServiceInfo() {
        firebaseFirestore.collection(categoryType).document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                Picasso.get().load(service.getImage()).into(binding.imageViewServiceImageDetailsActivity);
                binding.textViewServiceNameDetailsActivity.setText(service.getName());
                binding.textViewServiceDetailsDetailsActivity.setText(service.getDetail());
            }
        });
    }
}
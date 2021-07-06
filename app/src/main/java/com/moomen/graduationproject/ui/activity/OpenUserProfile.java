package com.moomen.graduationproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.ServicesAdapter;
import com.moomen.graduationproject.databinding.ActivityOpenUserProfileBinding;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.fragment.admin.UsersAdminFragment;
import com.squareup.picasso.Picasso;


public class OpenUserProfile extends AppCompatActivity {
    public final static String USER_ID = "USER_ID";
    private ImageView userImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView dateCreateTextView;
    private TextView aboutTextView;
    private TextView genderTextView;
    private TextView addressTextView;
    private RecyclerView recyclerViewActivity;
    private FirebaseFirestore firebaseFirestore;
    private String userName;
    private String userEmail;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String gender;
    private String userType;
    private String dateCreate;
    private String userImage;
    private boolean status;
    private String aboutUser;
    private String userID = "";
    private ActivityOpenUserProfileBinding binding;
    private RecyclerView recyclerViewUserActivity;
    private ServicesAdapter servicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_user_profile);
        binding = ActivityOpenUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userImageView = findViewById(R.id.imageView_user_id);
        nameTextView = findViewById(R.id.text_view_user_name_id);
        emailTextView = findViewById(R.id.text_view_user_email_id);
        phoneTextView = findViewById(R.id.textView_user_phone_id);
        genderTextView = findViewById(R.id.textView_user_gender_id);
        dateCreateTextView = findViewById(R.id.text_view_date_user_create_id);
        addressTextView = findViewById(R.id.text_view_user_location_id);
        aboutTextView = findViewById(R.id.textView_about_user_id);
        recyclerViewUserActivity = findViewById(R.id.recycler_view_activity_user_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(UsersAdminFragment.USER_ID)) {
            userID = intent.getStringExtra(UsersAdminFragment.USER_ID);
        } else if (intent != null && intent.hasExtra(ViewServiceDetailsActivity.COMPANY_ID)) {
            userID = intent.getStringExtra(ViewServiceDetailsActivity.COMPANY_ID);
            dateCreateTextView.setVisibility(View.GONE);
            genderTextView.setVisibility(View.GONE);
            binding.imageView29.setVisibility(View.GONE);
            binding.textView55.setVisibility(View.GONE);
            binding.imageView28.setVisibility(View.GONE);
            binding.textView53.setVisibility(View.GONE);
        }
        getUserInfo();
        getUserActivities();
    }

    private void getUserInfo() {
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                userName = user.getName();
                userImage = user.getUserImage();
                userEmail = user.getEmail();
                dateOfBirth = user.getDateOfBirth();
                address = user.getAddress();
                phone = user.getPhone();
                gender = user.getGender();
                userType = user.getUserType();
                dateCreate = user.getDateCreate();
                aboutUser = user.getAboutCompany();
                //status = task.getResult().getBoolean("status");

                nameTextView.setText(userName);
                Picasso.get()
                        .load(userImage)
                        .into(userImageView);
                emailTextView.setText(userEmail);
                dateCreateTextView.setText(dateCreate);
                addressTextView.setText(address);
                phoneTextView.setText(phone);
                genderTextView.setText(gender);
                dateCreateTextView.setText(dateCreate);
                aboutTextView.setText(aboutUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void getUserActivities() {
        Query query = FirebaseFirestore.getInstance().collection("Services")
                //.whereEqualTo("visibility",true)
                //.whereEqualTo("newsStatus",true)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("companyId", userID);
        FirestoreRecyclerOptions<Service> options = new FirestoreRecyclerOptions.Builder<Service>()
                .setQuery(query, Service.class)
                .build();
        servicesAdapter = new ServicesAdapter(options);
        servicesAdapter.setContext(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewUserActivity.setLayoutManager(gridLayoutManager);
        recyclerViewUserActivity.setAdapter(servicesAdapter);
        recyclerViewUserActivity.setItemViewCacheSize(20);
        recyclerViewUserActivity.setDrawingCacheEnabled(true);
        recyclerViewUserActivity.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        servicesAdapter.startListening();
    }
}
package com.moomen.graduationproject.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.ServicesAdapter;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.utils.PreferenceUtils;

public class FavoriteActivity extends AppCompatActivity {

    public static final String NEWS_ID = "NEWS_ID";
    public static final String USER_ID = "USER_ID";
    private String userUid;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty()) {
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getFavoriteNews();
        }
    }

    private void getFavoriteNews() {
        Query query = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userUid)
                .collection("Favorite");
        FirestoreRecyclerOptions<Service> options = new FirestoreRecyclerOptions.Builder<Service>()
                .setQuery(query, Service.class)
                .build();
        fillRecycleAdapter(options);
    }

    private void fillRecycleAdapter(FirestoreRecyclerOptions<Service> options) {
        ServicesAdapter servicesAdapter = new ServicesAdapter(options);
        servicesAdapter.setContext(getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.recycler_view_favorite_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(servicesAdapter);
        servicesAdapter.startListening();
    }


}
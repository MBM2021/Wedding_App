package com.moomen.graduationproject.ui.fragment.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.ServicesAdapter;
import com.moomen.graduationproject.adapter.UsersAdapter;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.activity.OpenUserProfile;
import com.moomen.graduationproject.ui.activity.ViewServiceDetailsActivity;

public class UsersAdminFragment extends Fragment {

    public static final String SERVICE_UID = "SERVICE_UID";
    public static final String USER_ID = "USER_ID";
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private String userType = "admin";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_users_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_users_fragment);
        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Services"));
        tabLayout.addTab(tabLayout.newTab().setText("Admins"));
        tabLayout.addTab(tabLayout.newTab().setText("Companies"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Services":
                        getAllServices();
                        break;
                    case "Admins":
                        userType = "admin";
                        getAllUsers();
                        break;
                    case "Companies":
                        userType = "company";
                        getAllUsers();
                        break;
                    case "Users":
                        userType = "user";
                        getAllUsers();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //getAllUsers();
        getAllServices();
    }

    private void getAllServices() {
        Query query = FirebaseFirestore.getInstance()
                .collection("Services");
        FirestoreRecyclerOptions<Service> options = new FirestoreRecyclerOptions.Builder<Service>()
                .setQuery(query, Service.class)
                .build();
        fillServicesRecycleAdapter(options);
    }

    private void fillServicesRecycleAdapter(FirestoreRecyclerOptions<Service> options) {
        ServicesAdapter servicesAdapter = new ServicesAdapter(options);
        servicesAdapter.setAdmin(true);
        servicesAdapter.onItemSetOnClickListener(new ServicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String serviceId = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), ViewServiceDetailsActivity.class);
                intent.putExtra(SERVICE_UID, serviceId);
                startActivity(intent);
            }
        });
        servicesAdapter.setContext(getContext());
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(servicesAdapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        servicesAdapter.startListening();
    }


    private void getAllUsers() {
        Query query = FirebaseFirestore.getInstance()
                .collection("Users")
                .whereEqualTo("userType", userType);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        fillUsersRecycleAdapter(options);
    }

    private void fillUsersRecycleAdapter(FirestoreRecyclerOptions<User> options) {
        UsersAdapter usersAdapter = new UsersAdapter(options);
        usersAdapter.onItemSetOnClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String userId = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), OpenUserProfile.class);
                intent.putExtra(USER_ID, userId);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(usersAdapter);
        usersAdapter.startListening();
    }
}
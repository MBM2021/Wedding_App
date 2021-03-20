package com.moomen.graduationproject.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.CategoryAdapter;
import com.moomen.graduationproject.model.Category;
import com.moomen.graduationproject.viewModel.HomeViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView categoryRecyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoryRecyclerView = view.findViewById(R.id.recyclerView);
        super.onViewCreated(view, savedInstanceState);
        getAllCategory();
    }

    private void getAllCategory() {
        Query query = FirebaseFirestore.getInstance().collection("Category");
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        fillRecycleAdapter(options);
    }

    private void fillRecycleAdapter(FirestoreRecyclerOptions<Category> options) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(options);

       /* newsAdapter.onUserNameSetOnClickListener(new NewsAdapter.OnUserNameClickListener() {
            @Override
            public void onUserNameClick(String userID, int position) {
                Intent intent = new Intent(getContext(), OpenUserProfile.class);
                intent.putExtra(USER_ID, userID);
                startActivity(intent);
            }
        });*/

        categoryAdapter.setContext(getContext());
        categoryAdapter.setAdmin(false);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
        //recyclerViewCategory.setHasFixedSize(true);
        categoryAdapter.startListening();
    }
}
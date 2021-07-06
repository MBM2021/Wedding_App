package com.moomen.graduationproject.ui.fragment.company;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.CategoryAdapter;
import com.moomen.graduationproject.model.Category;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.fragment.categories.DressesFragment;
import com.moomen.graduationproject.ui.fragment.categories.HallsFragment;
import com.moomen.graduationproject.ui.fragment.categories.RentingCarsFragment;

import org.jetbrains.annotations.NotNull;

public class ConsoleCompanyFragment extends Fragment {
    private RecyclerView categoryRecyclerView;
    private Fragment fragment;
    private ConstraintLayout constraintLayoutBlock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_console_company, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryRecyclerView = view.findViewById(R.id.recyclerView_category);
        constraintLayoutBlock = view.findViewById(R.id.constraintLayout_block_user);
        fragment = new HallsFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container_category_fragment, fragment, "HallsFragment")
                .addToBackStack(null).commit();
        getUserInfo();
    }

    private void getUserInfo() {
        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                if (user.isStatus()) {
                    constraintLayoutBlock.setVisibility(View.GONE);
                    getAllCategory();
                } else
                    constraintLayoutBlock.setVisibility(View.VISIBLE);
            }
        });
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
        categoryAdapter.onItemSetOnClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, int id) {
                Category category = documentSnapshot.toObject(Category.class);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                switch (category.getName()) {
                    case "Halls":
                        fragment = new HallsFragment();
                        break;
                    case "Dresses":
                        fragment = new DressesFragment();
                        break;
                    case "Cars":
                        fragment = new RentingCarsFragment();
                        break;
                }
                fragmentTransaction.replace(R.id.container_category_fragment, fragment).addToBackStack(null).commit();
            }
        });

        categoryAdapter.setContext(getContext());
        categoryAdapter.setAdmin(false);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
        //recyclerViewCategory.setHasFixedSize(true);
        categoryAdapter.startListening();
    }

}
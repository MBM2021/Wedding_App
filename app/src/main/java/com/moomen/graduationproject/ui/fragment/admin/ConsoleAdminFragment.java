package com.moomen.graduationproject.ui.fragment.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.CategoryAdapter;
import com.moomen.graduationproject.model.Category;
import com.moomen.graduationproject.ui.activity.SignInActivity;

public class ConsoleAdminFragment extends Fragment {


    private ImageButton buttonCreateCategory;
    private String categoryImageUrl;
    private String categoryName;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private RecyclerView recyclerViewCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_console_admin, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerViewCategory = view.findViewById(R.id.recyclerView_category);
        buttonCreateCategory = view.findViewById(R.id.button_admin_create_category);
        buttonCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategoryBottomSheet();
            }
        });
        getAllCategory();
        /*buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.create_category_item:
                                createCategoryBottomSheet();
                                break;
                            case R.id.create_admin_item:
                                createAdminBottomSheet();
                                break;
                            case R.id.create_ads_item:
                                createAdsBottomSheet();
                                break;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });*/
    }

    private BottomSheetDialog bottomSheetDialog;
    private ProgressBar progressBarBottomSheet;

    private void createAdsBottomSheet() {

    }

    private void createAdminBottomSheet() {
    }

    private void createCategoryBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_category, null);
        Button buttonCreate = view.findViewById(R.id.button_create);
        ImageView imageViewBack = view.findViewById(R.id.imageView_back);
        progressBarBottomSheet = view.findViewById(R.id.progressBar);
        progressBarBottomSheet.setVisibility(View.GONE);
        ImageView imageViewCategory = view.findViewById(R.id.imageView_category_image);
        EditText editTextCategoryName = view.findViewById(R.id.editText_category_name);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarBottomSheet.setVisibility(View.VISIBLE);
                categoryImageUrl = "https://i.ibb.co/4V5tZ9K/gift.webp";
                categoryName = editTextCategoryName.getText().toString().trim();
                if (categoryName.isEmpty()) {
                    editTextCategoryName.setError("The category name is required!");
                    editTextCategoryName.requestFocus();
                    return;
                }
                Category category = new Category(categoryName, categoryImageUrl);
                postCategoryOnFireBase(category);
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void postCategoryOnFireBase(Category category) {
        firebaseFirestore.collection("Category").add(category).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Published", Toast.LENGTH_SHORT).show();
                    progressBarBottomSheet.setVisibility(View.GONE);
                    bottomSheetDialog.dismiss();
                    //getAllCategory();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to publish, try again!", Toast.LENGTH_SHORT).show();
                progressBarBottomSheet.setVisibility(View.GONE);
            }
        });
    }

    private void getAllCategory() {
        Query query = FirebaseFirestore.getInstance().collection("Category");
        /*query.whereEqualTo("visibility", true)
                .whereEqualTo("newsStatus", true)
                .orderBy("date", Query.Direction.DESCENDING);*/
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
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setAdapter(categoryAdapter);
        //recyclerViewCategory.setHasFixedSize(true);
        categoryAdapter.startListening();
    }
}
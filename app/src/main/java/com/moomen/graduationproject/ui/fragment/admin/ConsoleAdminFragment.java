package com.moomen.graduationproject.ui.fragment.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.AdsAdapter;
import com.moomen.graduationproject.adapter.CategoryAdapter;
import com.moomen.graduationproject.model.Ads;
import com.moomen.graduationproject.model.Category;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class ConsoleAdminFragment extends Fragment {


    private static final int MAX_LENGTH = 100;
    Button buttonCreate;
    private String categoryName;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    ImageView imageViewBack;
    ImageView editCategoryImage;
    EditText editTextCategoryName;
    private ImageButton buttonCreateCategory, buttonCreateAds;
    private String categoryImageUrl, AdsImageUrl;
    private RecyclerView recyclerViewCategory, recyclerAds;
    private String imageName;
    private Bitmap compressor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_console_admin, container, false);

    }

    private String downloadUri;

    private void postAdsOnFireBase(Ads ads) {
        firebaseFirestore.collection("Ads").add(ads).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Published", Toast.LENGTH_SHORT).show();

                    //getAllCategory();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to publish, try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BottomSheetDialog bottomSheetDialog;
    private ProgressBar progressBarBottomSheet;

    private void createAdsBottomSheet() {

    }

    private void createAdminBottomSheet() {
    }

    private Uri imageUri = null;
    private ImageView imageViewCategory;
    private Intent intent;
    private boolean isCategoryEdit = false;
    private String categoryId;

    //Name image
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerViewCategory = view.findViewById(R.id.recyclerView_category);
        buttonCreateCategory = view.findViewById(R.id.button_admin_create_category);
        recyclerAds = view.findViewById(R.id.recyclerView_ads);
        buttonCreateAds = view.findViewById(R.id.button_admin_create_ads);

        buttonCreateAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsImageUrl = "https://i.ibb.co/4V5tZ9K/gift.webp";
                Ads ads = new Ads(AdsImageUrl);
                postAdsOnFireBase(ads);
            }
        });
        buttonCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategoryBottomSheet();
            }
        });
        getAllCategory();
        getAllAds();
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

    private void createCategoryBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_category, null);
        buttonCreate = view.findViewById(R.id.button_create);
        imageViewBack = view.findViewById(R.id.imageView_back);
        editCategoryImage = view.findViewById(R.id.imageView_edit_image);
        progressBarBottomSheet = view.findViewById(R.id.progressBar);
        progressBarBottomSheet.setVisibility(View.GONE);
        imageViewCategory = view.findViewById(R.id.imageView_category_image);
        editTextCategoryName = view.findViewById(R.id.editText_category_name);
        if (isCategoryEdit) {
            editCategoryImage.setVisibility(View.VISIBLE);
        } else
            editCategoryImage.setVisibility(View.GONE);
        imageUri = null;
        imageName = "";
        downloadUri = "";

        imageViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                //.setMinCropResultSize(512,512)
                                .setAspectRatio(2, 2)
                                .start(getContext(), ConsoleAdminFragment.this);
                    }
                }
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryName = editTextCategoryName.getText().toString().trim();
                if (categoryName.isEmpty()) {
                    Toast.makeText(getContext(), "The category name is required!", Toast.LENGTH_LONG).show();
                    return;
                }
                postImageCategoryInFireStorage();

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

    private void getAllCategory() {
        Query query = FirebaseFirestore.getInstance().collection("Category");
        /*query.whereEqualTo("visibility", true)
                .whereEqualTo("newsStatus", true)
                .orderBy("date", Query.Direction.DESCENDING);*/
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        fillCategoryRecycleAdapter(options);
    }

    private void postImageCategoryInFireStorage() {
        if (imageUri != null) {
            progressBarBottomSheet.setVisibility(View.VISIBLE);
            imageName = random() + ".jpg";
            File imageFile = new File(imageUri.getPath());
            try {
                compressor = new Compressor(getContext())
                        .setMaxHeight(240)
                        .setMaxWidth(360)
                        .setQuality(5)
                        .compressToBitmap(imageFile);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            compressor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
            byte[] thumpData = byteArrayInputStream.toByteArray();
            StorageReference filePath = storageReference.child("category_image/").child(imageName);
            UploadTask uploadTask = filePath.putBytes(thumpData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult().toString();
                                if (!isCategoryEdit) {
                                    postNewCategory();
                                } else
                                    updateCategoryOnFirebase();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } else if (downloadUri != null || !downloadUri.isEmpty()) {
            updateCategoryOnFirebase();
        } else {
            Toast.makeText(getContext(), "The category image is required!", Toast.LENGTH_LONG).show();
        }
    }

    private void postNewCategory() {
        Category category = new Category(categoryName, downloadUri);
        firebaseFirestore.collection("Category").add(category).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Published", Toast.LENGTH_SHORT).show();
                    progressBarBottomSheet.setVisibility(View.GONE);
                    bottomSheetDialog.dismiss();
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

    private void updateCategoryOnFirebase() {
        categoryName = editTextCategoryName.getText().toString().trim();
        firebaseFirestore.collection("Category").document(categoryId).update("name", categoryName, "image", downloadUri)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        bottomSheetDialog.dismiss();
                        isCategoryEdit = false;
                    }
                });
    }

    private void fillCategoryRecycleAdapter(FirestoreRecyclerOptions<Category> options) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(options);
        categoryAdapter.setAdmin(true);
        categoryAdapter.onItemSetOnClickListener(new CategoryAdapter.OnItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, int id) {
                categoryId = documentSnapshot.getId();
                switch (id) {
                    case R.id.imageView_edit:
                        isCategoryEdit = true;
                        getThisCategory();
                        break;
                    case R.id.imageView_delete:
                        firebaseFirestore.collection("Category").document(categoryId).delete();
                        break;
                }
            }
        });

        categoryAdapter.setContext(getContext());
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setAdapter(categoryAdapter);
        //recyclerViewCategory.setHasFixedSize(true);
        categoryAdapter.startListening();
    }

    private void getAllAds() {
        Query query = FirebaseFirestore.getInstance().collection("Ads");
        /*query.whereEqualTo("visibility", true)
                .whereEqualTo("newsStatus", true)
                .orderBy("date", Query.Direction.DESCENDING);*/
        FirestoreRecyclerOptions<Ads> options = new FirestoreRecyclerOptions.Builder<Ads>()
                .setQuery(query, Ads.class)
                .build();
        fillAdsRecycleAdapter(options);
    }

    private void fillAdsRecycleAdapter(FirestoreRecyclerOptions<Ads> options) {
        AdsAdapter adsAdapter = new AdsAdapter(options);
        adsAdapter.setContext(getContext());
        recyclerAds.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerAds.setAdapter(adsAdapter);
        //recyclerViewCategory.setHasFixedSize(true);
        adsAdapter.startListening();
    }

    private void getThisCategory() {
        firebaseFirestore.collection("Category").document(categoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Category category = task.getResult().toObject(Category.class);
                createCategoryBottomSheet();
                Picasso.get().load(category.getImage()).into(imageViewCategory);
                editTextCategoryName.setText(category.getName());
                //Here buttonCreate is button edit not create
                buttonCreate.setText("Edit");
                downloadUri = category.getImage();
                buttonCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postImageCategoryInFireStorage();
                    }
                });
            }
        });
    }

    //Crop image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                imageUri = result.getUri();
                imageViewCategory.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
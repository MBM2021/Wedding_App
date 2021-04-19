package com.moomen.graduationproject.ui.fragment.categories;

import android.Manifest;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.FragmentDressesBinding;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class DressesFragment extends Fragment {

    private static final int MAX_LENGTH = 100;
    private FragmentDressesBinding dressesBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private boolean isEmpty = false;
    private String dressesStoreName;
    private String ownerName;
    private String phone;
    private String location;
    private String details;
    private String city = "";
    private String dressUid;
    private String userImage;
    private String userName;
    private String date;
    private String serviceId;
    private String userId;
    private Uri imageUri = null;
    private String imageName;
    private Bitmap compressor;
    private String downloadUri;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        dressesBinding = FragmentDressesBinding.inflate(inflater, container, false);
        return dressesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerCreate(dressesBinding.spinnerCity);
        dressesBinding.imageViewDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImage();
            }
        });
        dressesBinding.buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createHallService();
            }
        });
    }

    private void cropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setMinCropResultSize(512,512)
                        .setAspectRatio(4, 4)
                        .start(getContext(), DressesFragment.this);
            }
        }
    }

    private void checkEditText(String stringValue, EditText editText, String tagName) {
        if (stringValue.isEmpty()) {
            editText.setError("The " + tagName + " is required!");
            editText.requestFocus();
            isEmpty = true;
        }
    }

    private void createHallService() {
        isEmpty = false;
        dressesStoreName = dressesBinding.editTextDressesStoreName.getText().toString().trim();
        ownerName = dressesBinding.editTextOwnerName.getText().toString().trim();
        phone = dressesBinding.editTextPhone.getText().toString().trim();
        location = dressesBinding.editTextLocation.getText().toString().trim();
        details = dressesBinding.editTextDetail.getText().toString().trim();

        checkEditText(dressesStoreName, dressesBinding.editTextDressesStoreName, "Store name");
        checkEditText(ownerName, dressesBinding.editTextOwnerName, "Owner name");
        checkEditText(phone, dressesBinding.editTextPhone, "Phone number");
        checkEditText(location, dressesBinding.editTextLocation, "Location");
        checkEditText(details, dressesBinding.editTextDetail, "Detail");

        if (!isEmpty) {
            postDressesImageOnFireBase();
        }
    }

    private void compressAndNameImage() {
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

    }

    private void postDressesImageOnFireBase() {
        if (imageUri != null) {
            compressAndNameImage();
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            compressor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
            byte[] thumpData = byteArrayInputStream.toByteArray();
            StorageReference filePath = storageReference.child("Dress_Image/").child(imageName);
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

                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult().toString();
                                postDressOnFirebase();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });
        } else {
            Toast.makeText(getContext(), "The Dress image is required!", Toast.LENGTH_LONG).show();
        }
    }

    private void postDressOnFirebase() {
        Service service = new Service(downloadUri, city, dressesStoreName, ownerName, phone, location, details, false, new ArrayList<>(), "Dresses", date);
        firebaseFirestore.collection("Services").add(service).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                serviceId = task.getResult().getId();
                firebaseFirestore.collection("Dresses").add(service).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        dressUid = task.getResult().getId();
                        Toast.makeText(getContext(), "Service created successfully!", Toast.LENGTH_LONG).show();
                        getCurrentUserInfo();
                    }
                });
            }
        });
    }

    private void getCurrentUserInfo() {
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                userImage = user.getUserImage();
                userName = user.getName();
                createNotification();
            }
        });
    }

    private void createNotification() {
        Notification notification = new Notification(userImage, userName, "Add new Dresses Service", dressesStoreName, date, serviceId, dressUid, userId, "service", false, false);
        firebaseFirestore.collection("Notifications").add(notification);
    }

    private void spinnerCreate(Spinner spinner) {
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.city, R.layout.support_simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                city = "Gaza";
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                imageUri = result.getUri();
                dressesBinding.imageViewDresses.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
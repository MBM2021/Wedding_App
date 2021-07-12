package com.moomen.graduationproject.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 100;
    private ImageView imageViewEditImage;
    private ImageView userImage;
    private EditText editTextName;
    private EditText editTextEmailName;
    private EditText editTextPhoneName;
    private EditText editTextDateOfBirth;
    private EditText editTextLocationName;
    private Button submitEdit;
    private ImageView back;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private Bitmap compressor;
    private String downloadUri = "";
    private String imageName;
    private Uri userImageUri = null;
    private String name;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String location;
    private ImageView backImage;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imageViewEditImage = findViewById(R.id.imageView_edit_user_image_id);
        userImage = findViewById(R.id.imageView_user_image_id);
        editTextName = findViewById(R.id.edit_text_first_name_id);
        editTextEmailName = findViewById(R.id.edit_text_email_id);
        editTextPhoneName = findViewById(R.id.edit_text_phone_name_id);
        editTextDateOfBirth = findViewById(R.id.edit_text_date_id);
        editTextLocationName = findViewById(R.id.edit_text_location_id);
        backImage = findViewById(R.id.imageView_back);
        submitEdit = findViewById(R.id.button_submit_edit_profile_id);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        getUserInformation(userID);
        backButton();

    }

    private void backButton() {
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserInformation(String userID) {
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                editTextName.setText(user.getName());
                editTextEmailName.setText(user.getEmail());
                editTextPhoneName.setText(user.getPhone());
                editTextDateOfBirth.setText(user.getDateOfBirth());
                editTextLocationName.setText(user.getAddress());
                downloadUri = user.getUserImage();
                Picasso.get().load(user.getUserImage()).into(userImage);
            }
        });
    }

    public void imageEditImageOnClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setMinCropResultSize(512,512)
                        .setAspectRatio(2, 2)
                        .start(EditProfileActivity.this);

            }
        }
    }

    public void submitButtonOnClick(View view) {
        if (userImageUri != null) {////If edit user Image
            progressBar.setVisibility(View.VISIBLE);
            imageName = random() + ".jpg";
            name = editTextName.getText().toString().trim();
            email = editTextEmailName.getText().toString().trim();
            phone = editTextPhoneName.getText().toString().trim();
            dateOfBirth = editTextDateOfBirth.getText().toString().trim();
            location = editTextLocationName.getText().toString().trim();
            storageImageAndEditProfile();
        } else if (!downloadUri.isEmpty()) {//If no edit user Image
            progressBar.setVisibility(View.VISIBLE);
            name = editTextName.getText().toString().trim();
            email = editTextEmailName.getText().toString().trim();
            phone = editTextPhoneName.getText().toString().trim();
            dateOfBirth = editTextDateOfBirth.getText().toString().trim();
            location = editTextLocationName.getText().toString().trim();
            editUserProfile();
        }
        finish();
    }

    private void storageImageAndEditProfile() {
        File imageFile = new File(userImageUri.getPath());
        try {
            compressor = new Compressor(getApplicationContext())
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
        StorageReference filePath = storageReference.child("users_image").child(imageName);
        UploadTask uploadTask = filePath.putBytes(thumpData);
        //UploadTask uploadTask = filePath.putFile(userImageUri);
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
                            //Edit user Info
                            documentReference = firebaseFirestore.collection("Users").document(userID);
                            documentReference.update("name", name, "phone", phone, "dateOfBirth", dateOfBirth, "address", location, "userImage", downloadUri);
                            Toast.makeText(getApplicationContext(), "Modified", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void editUserProfile() {
        documentReference = firebaseFirestore.collection("Users").document(userID);
        documentReference.update("name", name, "phone", phone, "dateOfBirth", dateOfBirth, "address", location);
        Toast.makeText(getApplicationContext(), "Modified", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    //Crop image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userImageUri = result.getUri();
                userImage.setImageURI(userImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void backOnClick(View view) {
        finish();
    }
}
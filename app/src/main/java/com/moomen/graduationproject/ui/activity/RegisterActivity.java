package com.moomen.graduationproject.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.moomen.graduationproject.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private CheckBox checkBoxType;

    private String userName;
    private String userEmail;
    private String userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextText_user_name);
        editTextEmail = findViewById(R.id.editText_user_email);
        editTextPassword = findViewById(R.id.editText_user_password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        checkBoxType = findViewById(R.id.checkBox_type);
    }

    public void signInButtonOnClick(View view) {
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

    public void registerButtonOnClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        userName = editTextName.getText().toString().trim();
        userEmail = editTextEmail.getText().toString().trim();
        userPassword = editTextPassword.getText().toString().trim();
        checkEditText(userName, editTextName, "Name");
        checkEditText(userEmail, editTextEmail, "Email");
        checkEditText(userPassword, editTextPassword, "Password");
        if (isValid) {
            //registerNewUserByEmailAndPassword();
            Toast.makeText(getApplicationContext(), "isValid" + isValid, Toast.LENGTH_SHORT).show();
        }

    }

   /* private void registerNewUserByEmailAndPassword() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addNewUserOnDbFirebase();
                }
                *//* else {
                    Toast.makeText(getContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                }*//*
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewUserOnDbFirebase() {
        User user = new User(firstName, lastName, email, dateOfBirth, address, phone, gender, userType,dateOfCreate,image,status,aboutCompany);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // save on cluode
        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid());
        documentReference.set(user);
        verifyEmail();
        //save on reayltime database
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    verifyEmail();
                    //Toast.makeText(getContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void verifyEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.sendEmailVerification();
        Toast.makeText(getContext(), "Registered Successfully!\nCheck your email to verify your account!", Toast.LENGTH_LONG).show();
    }
*/

    private boolean isValid = true;

    private void checkEditText(String stringValue, EditText editTextName, String tagName) {
        isValid = true;
        if (stringValue.isEmpty()) {
            editTextName.setError("The " + tagName + " is required!");
            editTextName.requestFocus();
            isValid = false;
            progressBar.setVisibility(View.GONE);
        } else {
            if (tagName.equals("Email")) {
                if (!Patterns.EMAIL_ADDRESS.matcher(stringValue).matches()) {
                    editTextName.setError("Please provide valid email!");
                    editTextName.requestFocus();
                    isValid = false;
                    progressBar.setVisibility(View.GONE);
                }
            } else if (tagName.equals("Password")) {
                if (stringValue.length() < 8) {
                    editTextName.setError("Min password length should be 8 characters!");
                    editTextName.requestFocus();
                    isValid = false;
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }
}
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private CheckBox checkBoxType;

    private String userName;
    private String userEmail;
    private String userPassword;
    private String userType = "user";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void checkBoxTypeOnClick(View view) {
        if (checkBoxType.isChecked()) {
            userType = "company";
        } else {
            userType = "user";
        }
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
            registerNewUserByEmailAndPassword();
            Toast.makeText(getApplicationContext(), "isValid" + isValid, Toast.LENGTH_SHORT).show();
        }

    }

    private void registerNewUserByEmailAndPassword() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addNewUserOnDbFirebase();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewUserOnDbFirebase() {
        firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User(userName, userEmail, "", "", "", "", userType, "", "", true, "");
        // save on cloudFireStore
        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid());
        documentReference.set(user);
        verifyEmail();
    }

    private void verifyEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.sendEmailVerification();
        Toast.makeText(getApplicationContext(), "Registered Successfully!\nCheck your email to verify your account!", Toast.LENGTH_LONG).show();
    }

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
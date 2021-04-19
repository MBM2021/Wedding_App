package com.moomen.graduationproject.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.utils.PreferenceUtils;

public class SignInActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private ProgressBar progressBarReset;

    private String userEmail;
    private String userPassword;
    private String resetEmail;
    private boolean isValid = true;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        editTextEmail = findViewById(R.id.editText_user_email);
        editTextPassword = findViewById(R.id.editText_user_password);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void bottomSheetResetPassword() {
        bottomSheetDialog = new BottomSheetDialog(SignInActivity.this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(SignInActivity.this).inflate(R.layout.bottom_sheet_forgot_password, null);
        Button buttonReset = view.findViewById(R.id.button_reset);
        ImageView imageViewBack = view.findViewById(R.id.imageView_back);
        EditText editTextEmail = view.findViewById(R.id.editText_user_email);
        progressBarReset = view.findViewById(R.id.progressBar);
        progressBarReset.setVisibility(View.GONE);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarReset.setVisibility(View.VISIBLE);
                resetEmail = editTextEmail.getText().toString().trim();
                if (resetEmail.isEmpty()) {
                    editTextEmail.setError("The Email is required to reset password!");
                    editTextEmail.requestFocus();
                    progressBarReset.setVisibility(View.GONE);
                } else {
                    resetPassword(resetEmail);
                }

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

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Check your email to reset your Password!", Toast.LENGTH_SHORT).show();

               /* if (task.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Check your email to reset your Password!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Try again! something is wrong!", Toast.LENGTH_SHORT).show();*/
                progressBarReset.setVisibility(View.GONE);
                bottomSheetDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void registerButtonOnClick(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void forgotPasswordOnClick(View view) {
        bottomSheetResetPassword();
    }

    public void signInButtonOnClick(View view) {
        userEmail = editTextEmail.getText().toString().trim();
        userPassword = editTextPassword.getText().toString().trim();
        checkEditText(userEmail, editTextEmail, "Email");
        checkEditText(userPassword, editTextPassword, "Password");
        if (isValid) {
            signInByEmailAndPassword();

        }
    }

    private void signInByEmailAndPassword() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //check email is verified and save User Login and loin
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (user.isStatus()) {
                                if (firebaseUser.isEmailVerified()) {
                                    //redirect to user profile
                                    PreferenceUtils.saveEmail(userEmail, getApplicationContext());
                                    PreferenceUtils.savePassword(userPassword, getApplicationContext());
                                    //Check type user
                                    checkUserTypeToSignIn(authResult.getUser().getUid());
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    firebaseUser.sendEmailVerification();
                                    Toast.makeText(getApplicationContext(), "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                startActivity(new Intent(getApplicationContext(), UserBlocked.class));
                                //getActivity().finish();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), UserBlocked.class));
                            //getActivity().finish();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startActivity(new Intent(getApplicationContext(), UserBlocked.class));
                        //getActivity().finish();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getApplicationContext(), "Failed to login! please check your password or email", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkUserTypeToSignIn(String uid) {
        DocumentReference df = firebaseFirestore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType = documentSnapshot.getString("userType");
                if (userType != null) {
                    if (userType.equals("user"))
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    else if (userType.equals("company"))
                        startActivity(new Intent(getApplicationContext(), MainActivityCompany.class));
                    else if (userType.equals("admin"))
                        startActivity(new Intent(getApplicationContext(), MainActivityAdmin.class));
                    finish();
                }
            }
        });
    }

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
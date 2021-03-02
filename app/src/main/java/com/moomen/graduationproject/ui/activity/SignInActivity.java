package com.moomen.graduationproject.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.moomen.graduationproject.R;

public class SignInActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    String email;

    private void bottomSheetResetPassword() {
        bottomSheetDialog = new BottomSheetDialog(SignInActivity.this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(SignInActivity.this).inflate(R.layout.bottom_sheet_forgot_password, null);
        Button buttonReset = view.findViewById(R.id.button_reset);
        ImageView imageViewBack = view.findViewById(R.id.imageView_back);
        EditText editTextEmail = view.findViewById(R.id.editText_user_email);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                email = editTextEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    editTextEmail.setError("The Email is required to reset password!");
                    editTextEmail.requestFocus();
                    return;
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

    public void registerButtonOnClick(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void forgotPasswordOnClick(View view) {
        bottomSheetResetPassword();
    }
}
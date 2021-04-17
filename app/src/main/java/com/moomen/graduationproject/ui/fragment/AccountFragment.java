package com.moomen.graduationproject.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.BuildConfig;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.activity.EditProfileActivity;
import com.moomen.graduationproject.ui.activity.FavoriteActivity;
import com.moomen.graduationproject.ui.activity.SignInActivity;
import com.moomen.graduationproject.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AccountFragment extends Fragment {

    private TextView nameTextView, emailTextView;
    private ImageView userImage;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private LinearLayout logoutLinear;
    private LinearLayout editProfileLinear;
    private LinearLayout favoriteLinear;
    private BottomSheetDialog bottomSheetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.tex_view_name_id);
        emailTextView = view.findViewById(R.id.text_view_email_id);
        logoutLinear = view.findViewById(R.id.linear_logout_id);
        favoriteLinear = view.findViewById(R.id.linear_favorite_id);
        LinearLayout aboutUsLinear = view.findViewById(R.id.linear_about_us_id);
        LinearLayout privacyLinear = view.findViewById(R.id.linear_privacy_policy_id);
        LinearLayout shareLinear = view.findViewById(R.id.linear_share_app_id);
        editProfileLinear = view.findViewById(R.id.linear_edit_profile_id);
        userImage = view.findViewById(R.id.imageView_user_id);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        favoriteLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FavoriteActivity.class));
            }
        });

        shareLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
        if (PreferenceUtils.getEmail(getContext()) != null && !PreferenceUtils.getEmail(getContext()).isEmpty()) {
            userID = firebaseUser.getUid();
            getUserInformation(userID);
            editProfile();
            logout();
        }
        linearOnClick(aboutUsLinear, 0);
        linearOnClick(privacyLinear, 1);
    }

    private void linearOnClick(LinearLayout linearLayout, int tag) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBottomSheet(tag);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void intentBottomSheet(int tag) {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_about_us_privecy, null);
        ImageView buttonBack = view.findViewById(R.id.back_btn);
        ImageView buttonIcon = view.findViewById(R.id.imageView_icon);
        TextView textViewContent = view.findViewById(R.id.textView_content);
        TextView textViewTitle = view.findViewById(R.id.textView_title);
        InputStream inputStream;
        switch (tag) {
            case 0:
                inputStream = getResources().openRawResource(R.raw.about);
                readTextAboutAndPrivacy(inputStream, textViewContent);
                break;
            case 1:
                buttonIcon.setImageResource(R.drawable.ic_baseline_info_24);
                textViewTitle.setText(R.string.privacy);
                inputStream = getResources().openRawResource(R.raw.privacy);
                readTextAboutAndPrivacy(inputStream, textViewContent);
                break;
        }
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }


    private void editProfile() {
        editProfileLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
    }

    private void getUserInformation(String userID) {
        DocumentReference df = firebaseFirestore.collection("Users").document(userID);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                String name = user.getName();
                String email = user.getEmail();
                String userImageUrl = user.getUserImage();
                nameTextView.setText(name);
                emailTextView.setText(email);
                Picasso.get().load(userImageUrl).into(userImage);
            }
        });
    }

    private void logout() {
        logoutLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                PreferenceUtils.saveEmail("", getContext());
                PreferenceUtils.savePassword("", getContext());
                //redirect to login activity
                startActivity(new Intent(getContext(), SignInActivity.class));
                getActivity().finish();
            }
        });
    }

    private void shareApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id" + BuildConfig.APPLICATION_ID + "\n\n";
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(intent, "Choose on"));
        } catch (Exception e) {

        }
    }

    private void readTextAboutAndPrivacy(InputStream inputStream, TextView textView) {
        StringBuilder text = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        textView.setText(text.toString());
    }
}
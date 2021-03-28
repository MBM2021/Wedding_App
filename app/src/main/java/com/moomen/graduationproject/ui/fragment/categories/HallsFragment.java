package com.moomen.graduationproject.ui.fragment.categories;

import android.content.Context;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.FragmentHallsBinding;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HallsFragment extends Fragment {

    FragmentHallsBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private boolean isEmpty = false;
    private String hallImage = "https://firebasestorage.googleapis.com/v0/b/weddingapp-be318.appspot.com/o/Ads_Image%2F2.jpg?alt=media&token=610fb28e-8c44-4480-8da4-927678d8f401";
    private String hallName;
    private String ownerName;
    private String phone;
    private String location;
    private String details;
    private String city = "";
    private String hallUid;
    private String userImage;
    private String userName;
    private String date;
    private String serviceId;
    private String userId;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHallsBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerCreate(binding.spinnerCity);
        binding.buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createHallService();
            }
        });
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

    private void createHallService() {
        isEmpty = false;
        hallName = binding.editTextHallName.getText().toString().trim();
        ownerName = binding.editTextOwnerName.getText().toString().trim();
        phone = binding.editTextPhone.getText().toString().trim();
        location = binding.editTextLocation.getText().toString().trim();
        details = binding.editTextDetail.getText().toString().trim();

        checkEditText(hallName, binding.editTextHallName, "Hall name");
        checkEditText(ownerName, binding.editTextOwnerName, "Owner name");
        checkEditText(phone, binding.editTextPhone, "Phone number");
        checkEditText(location, binding.editTextLocation, "Location");
        checkEditText(details, binding.editTextDetail, "Detail");

        if (!isEmpty) {
            Service service = new Service(hallImage, city, hallName, ownerName, phone, location, details, false, new ArrayList<>(), "Halls", date);
            postHallOnFirebase(service);
        }
    }

    private void postHallOnFirebase(Service service) {
        firebaseFirestore.collection("Services").add(service).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                serviceId = task.getResult().getId();
                firebaseFirestore.collection("Halls").add(service).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        hallUid = task.getResult().getId();
                        makeToast(getContext(), "Service created successfully!");
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
        Notification notification = new Notification(userImage, userName, "Add new Hall Service", hallName, date, serviceId, hallUid, userId, "service", false, false);
        firebaseFirestore.collection("Notifications").add(notification);
    }

    private void checkEditText(String stringValue, EditText editText, String tagName) {
        if (stringValue.isEmpty()) {
            editText.setError("The " + tagName + " is required!");
            editText.requestFocus();
            isEmpty = true;
        }
    }

    private void makeToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}

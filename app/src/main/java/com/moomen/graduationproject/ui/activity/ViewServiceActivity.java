package com.moomen.graduationproject.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.ActivityViewServiceBinding;
import com.moomen.graduationproject.model.Notification;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.fragment.admin.NotificationAdminFragment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

public class ViewServiceActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private String serviceId;
    private String hallId;
    private String userType;
    private String Notification_ID;
    private String city = "";
    private BottomSheetDialog bottomSheetDialog;
    private ActivityViewServiceBinding binding;
    private EditText hallName, OwnerName, PhoneNumber, Location, Details;
    private ImageView imageView_update;
    private Spinner spinner_city;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewServiceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(NotificationAdminFragment.USER_ID)
                && intent.hasExtra(NotificationAdminFragment.SERVICE_ID)
                && intent.hasExtra(NotificationAdminFragment.HALL_ID)) {
            userId = intent.getStringExtra(NotificationAdminFragment.USER_ID);
            serviceId = intent.getStringExtra(NotificationAdminFragment.SERVICE_ID);
            hallId = intent.getStringExtra(NotificationAdminFragment.HALL_ID);
            userType = intent.getStringExtra(NotificationAdminFragment.USER_TYPE);
            Notification_ID = intent.getStringExtra(NotificationAdminFragment.NOTIFICATION_ID);
        }
        if (!(userType == null) && userType.equals("company")) {
            binding.linearLayout2.setVisibility(View.GONE);
            binding.imageViewEditCompany.setVisibility(View.VISIBLE);
        }
        getServiceInfo();
        getUserInfo();
        bottomNavigationOnClickItem();
        binding.imageViewEditCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfoCompany();
            }
        });

    }

    int select = 0;
    private String serviceCategory = "";

    private void getUserInfo() {
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                Picasso.get().load(user.getUserImage()).into(binding.imageViewUserId);
                binding.textViewUserNameId.setText(user.getName());
                binding.textViewUserEmailId.setText(user.getEmail());
                binding.textViewUserPhoneId.setText(user.getPhone());


            }
        });
    }

    private void getServiceInfo() {
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                binding.textViewServiceType.setText(service.getType());
                binding.textViewServiceName.setText(service.getName());
                binding.textViewServiceOwnerName.setText(service.getOwnerName());
                binding.textViewServiceContactPhone.setText(service.getPhone());
                binding.textViewServiceLocation.setText(service.getLocation());
                binding.textViewServiceCity.setText(service.getCity());
                binding.textViewServiceDetails.setText(service.getDetail());
                Picasso.get().load(service.getImage()).into(binding.serviceImage);
                //Service status
                if (service.isStatus())
                    binding.textViewServiceStatus.setText(getString(R.string.accepted));
                else
                    binding.textViewServiceStatus.setText(getString(R.string.rejected));
                binding.textViewServiceCreatedDate.setText(service.getDate());
                serviceCategory = service.getType();
            }
        });
    }

    private String serviceStatus = "";
    private Notification notification;

    private void bottomNavigationOnClickItem() {
        binding.linearLayoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.linearLayoutAcceptService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStatus = getString(R.string.accepted);
                firebaseFirestore.collection("Services").document(serviceId).update("status", true);
                firebaseFirestore.collection(serviceCategory).document(hallId).update("status", true);
                Toast.makeText(getApplicationContext(), serviceStatus, Toast.LENGTH_SHORT).show();
                pushNotification();
                getServiceInfo();
            }
        });
        binding.linearLayoutCancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStatus = getString(R.string.rejected);
                firebaseFirestore.collection("Services").document(serviceId).update("status", false);
                firebaseFirestore.collection(serviceCategory).document(hallId).update("status", false);
                Toast.makeText(getApplicationContext(), serviceStatus, Toast.LENGTH_SHORT).show();
                getServiceInfo();
                pushNotification();
            }
        });
    }

    private void pushNotification() {
        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        if (serviceStatus.equals("Accepted")) {
            notification = new Notification("", "Your service" + serviceStatus, "Congratulations! Users can view your service ", "", date, serviceId, hallId, userId, "service", false, false, false, "company");
            Notification userNotification = new Notification("", "New service", "Users can view your service ", "New service has been added", date, serviceId, hallId, userId, "service", false, false, false, "user");
            firebaseFirestore.collection("Notifications").add(userNotification);
        } else {
            notification = new Notification("", "Your service" + serviceStatus, "Sorry! Users can't view your service ", "", date, serviceId, hallId, userId, "service", false, false, false, "company");
        }
        firebaseFirestore.collection("Notifications").add(notification);
    }

    private void updateInfoCompany() {
        bottomSheetDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.fragment_halls, null);
        hallName = view.findViewById(R.id.editText_hall_name);
        OwnerName = view.findViewById(R.id.editText_owner_name);
        PhoneNumber = view.findViewById(R.id.editText_phone);
        Location = view.findViewById(R.id.editText_location);
        Details = view.findViewById(R.id.editText_detail);
        Button btn_update = view.findViewById(R.id.button_create);
        spinner_city = view.findViewById(R.id.spinner_city);
        imageView_update = view.findViewById(R.id.imageView_hall);
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                hallName.setText(service.getName());
                OwnerName.setText(service.getOwnerName());
                PhoneNumber.setText(service.getPhone());
                Location.setText(service.getLocation());
                Details.setText(service.getDetail());
                btn_update.setText("Update Service");
                Picasso.get().load(service.getImage()).into(imageView_update);
                spinnerCreate(spinner_city, service.getCity());
                city = service.getCity();
                imageView_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });


            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Services").document(serviceId).update("name", hallName.getText().toString(), "ownerName", OwnerName.getText().toString()
                        , "phone", PhoneNumber.getText().toString(), "location", Location.getText().toString(), "detail", Details.getText().toString(), "city", city);

                Toast.makeText(getApplicationContext(), "update Done", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void spinnerCreate(Spinner spinner, String selctedCirty) {
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.city, R.layout.support_simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                city = selctedCirty;
            }
        });
    }


}
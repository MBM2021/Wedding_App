package com.moomen.graduationproject.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.databinding.ActivityViewServiceDetailsBinding;
import com.moomen.graduationproject.model.Favourite;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.ui.fragment.HomeFragment;
import com.moomen.graduationproject.ui.fragment.admin.UsersAdminFragment;
import com.moomen.graduationproject.ui.fragment.user.NotificationUserFragment;
import com.moomen.graduationproject.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ViewServiceDetailsActivity extends AppCompatActivity {

    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String RECEIVER_ID = "RECEIVER_ID";
    public static final String IS_COMPANY = "IS_COMPANY";
    public static final String CATEGORY_TYPE = "CATEGORY_TYPE";
    public static final String COMPANY_ID = "COMPANY_ID";
    private ActivityViewServiceDetailsBinding binding;
    private String serviceId;
    private String receiverId;
    private String serviceTypeId;
    private String categoryType;
    private String companyId;
    private boolean status;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewServiceDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.imageViewMenu.setVisibility(View.GONE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(HomeFragment.SERVICE_ID)
                && intent.hasExtra(HomeFragment.CATEGORY_TYPE)) {
            serviceId = intent.getStringExtra(HomeFragment.SERVICE_ID);
            categoryType = intent.getStringExtra(HomeFragment.CATEGORY_TYPE);
        } else if (intent != null && intent.hasExtra(SignInActivity.SERVICE_ID)) {
            serviceId = intent.getStringExtra(SignInActivity.SERVICE_ID);
            categoryType = "Services";
        } else if (intent != null && intent.hasExtra(UsersAdminFragment.SERVICE_UID)) {
            serviceId = intent.getStringExtra(UsersAdminFragment.SERVICE_UID);
            categoryType = "Services";
            binding.imageViewMenu.setVisibility(View.VISIBLE);
            serviceSettings();
        } else if (intent != null && intent.hasExtra(NotificationUserFragment.SERVICE_ID)) {
            serviceId = intent.getStringExtra(NotificationUserFragment.SERVICE_ID);
            categoryType = "Services";
        }
        getServiceInfo();
        addToFavouriteOnClick();
        bookService();
        chatCompany();
        visitStore();
        backButton();
    }

    private void backButton() {
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void serviceSettings() {
        binding.imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                if (status)
                    popupMenu.inflate(R.menu.popup_menu);
                else
                    popupMenu.inflate(R.menu.popup_menu_tow);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.block_user_item:
                                if (status) {
                                    confirmDialog(item.getTitle().toString(), "Are you sure you want to block this Service", "This Service was blocked now");
                                } else {
                                    confirmDialog(item.getTitle().toString(), "Are you sure you want to unblock this Service", "This Service was unblocked now");
                                }
                                break;
                            case R.id.delete_user_item:
                                confirmDialog(item.getTitle().toString(), "Are you sure you want to remove this Service", "This Service was removed now");
                                break;
                            /*case R.id.chat_user_item:
                                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                                intent.putExtra(RECEIVER_ID, userID);
                                intent.putExtra(IS_COMPANY, "company");
                                startActivity(intent);
                                break;*/
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void confirmDialog(String title, String message, String toast) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewServiceDetailsActivity.this);
        dialogBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(title, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (title.equals("Block")) {
                            firebaseFirestore.collection("Services").document(serviceId).update("status", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (title.equals("Unblock")) {
                            firebaseFirestore.collection("Services").document(serviceId).update("status", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (title.equals("Delete")) {
                            firebaseFirestore.collection("Services").document(serviceId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void getServiceInfo() {
        firebaseFirestore.collection("Services").document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                receiverId = service.getCompanyId();
                companyId = service.getCompanyId();
                status = service.isStatus();
                Picasso.get().load(service.getImage()).into(binding.imageViewServiceImageDetailsActivity);
                binding.textViewServiceNameDetailsActivity.setText(service.getName());
                binding.textViewServiceDetailsDetailsActivity.setText(service.getDetail());
                binding.textViewServicePriceDetailsActivity.setText(service.getPrice() + " $");
            }
        });
    }

    private void addToFavouriteOnClick() {
        binding.imageViewFavouriteIconDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {
                    Favourite favourite = new Favourite(serviceId);
                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    firebaseFirestore.collection("Users").document(userUid)
                            .collection("Favourite")
                            .add(favourite).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(getApplicationContext(), "Add to Favourite List", Toast.LENGTH_SHORT).show();
                            binding.imageViewFavouriteIconDetailsActivity.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                        }
                    });
                } else {
                    showSnackBar();
                }
            }
        });
    }

    private void showSnackBar() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "You must sign in!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Sign in", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.putExtra(SERVICE_ID, serviceId);
                startActivity(intent);
                snackbar.dismiss();
            }
        }).setActionTextColor(getResources().getColor(R.color.purple_700)).show();
    }

    private boolean isLogin() {
        return PreferenceUtils.getEmail(getApplicationContext()) != null && !PreferenceUtils.getEmail(getApplicationContext()).isEmpty();
    }

    private void bookService() {
        binding.buttonBookServiceDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin()) {
                    Intent intent = new Intent(getApplicationContext(), BookingServiceActivity.class);
                    intent.putExtra(SERVICE_ID, serviceId);
                    //intent.putExtra(SERVICE_TYPE_ID,serviceTypeId);
                    //intent.putExtra(CATEGORY_TYPE,categoryType);
                    startActivity(intent);
                } else
                    showSnackBar();
            }
        });
    }

    private void contactCompany() {

    }

    private void chatCompany() {
        binding.buttonChatServiceDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(RECEIVER_ID, receiverId);
                    intent.putExtra(SERVICE_ID, serviceId);
                    intent.putExtra(IS_COMPANY, "company");
                    startActivity(intent);
                } else
                    showSnackBar();
            }
        });
    }

    private void visitStore() {
        binding.imageViewVisitStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OpenUserProfile.class);
                intent.putExtra(COMPANY_ID, companyId);
                startActivity(intent);
            }
        });
    }
}
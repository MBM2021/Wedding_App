package com.moomen.graduationproject.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.MessagesAdapter;
import com.moomen.graduationproject.model.Chat;
import com.moomen.graduationproject.model.Message;
import com.moomen.graduationproject.model.User;
import com.moomen.graduationproject.ui.fragment.company.ChatCompanyFragment;
import com.moomen.graduationproject.ui.fragment.user.ChatUserFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    private ImageView sendMessageButton;
    private EditText messageEditText;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private User user;
    private String userName;
    private String userEmail;
    private String userImage;
    private RecyclerView recyclerView;
    private boolean senderExist = false;
    private MessagesAdapter messagesAdapter;
    private String docID;
    private ArrayList<Message> messageArrayList;
    private String receiverId = "";
    private String serviceId = "";
    private String type = "";
    private TextView titleActivity;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        titleActivity = findViewById(R.id.text_view_user_name_id);
        sendMessageButton = findViewById(R.id.image_view_send_message_id);
        messageEditText = findViewById(R.id.edit_text_message_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        recyclerView = findViewById(R.id.recycler_view_message_id);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ViewServiceDetailsActivity.RECEIVER_ID)
                && intent.hasExtra(ViewServiceDetailsActivity.SERVICE_ID)
                && (intent.hasExtra(ViewServiceDetailsActivity.IS_COMPANY))) {
            receiverId = intent.getStringExtra(ViewServiceDetailsActivity.RECEIVER_ID);
            serviceId = intent.getStringExtra(ViewServiceDetailsActivity.SERVICE_ID);
            type = "company";
            senderIsExist();
        } else if (intent != null && intent.hasExtra(ChatCompanyFragment.IS_SUPPORT_COMPANY)) {
            type = "support";
            titleActivity.setText("Support team");
            receiverId = userID;
            senderIsExistIfSupport();
        } else if (intent != null && (intent.hasExtra(ChatUserFragment.IS_SUPPORT) || intent.hasExtra(OpenUserProfile.IS_COMPANY))) {
            type = "support";
            titleActivity.setText("Support team");
            senderIsExistIfSupport();
        }
        createNewMessage();
    }

    private void createNewMessage() {
        //Change image send message
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!messageEditText.getText().toString().isEmpty())
                    sendMessageButton.setImageResource(R.drawable.ic_baseline_send_blue_24);
                else
                    sendMessageButton.setImageResource(R.drawable.ic_baseline_send_24);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!senderExist) {
                    createRomeChat();
                } else {
                    String messageText = messageEditText.getText().toString();
                    if (!TextUtils.isEmpty(messageText)) {
                        addNewMessage(messageText);
                    }
                }
            }
        });
    }

    private void createRomeChat() {
        firebaseFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                userName = user.getName();
                userImage = user.getUserImage();
                userEmail = user.getEmail();
                String dateRomeChat = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                Chat chat = new Chat(userID, receiverId, userName, userEmail, userImage, dateRomeChat, serviceId, type, new ArrayList<Message>());
                firebaseFirestore.collection("Chat").add(chat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        docID = task.getResult().getId();
                        senderExist = true;
                        String messageText = messageEditText.getText().toString();
                        if (!TextUtils.isEmpty(messageText)) {
                            addNewMessage(messageText);
                        }
                    }
                });
            }
        });
    }

    private void senderIsExistIfSupport() {
        messageArrayList = new ArrayList<>();
        senderExist = false;
        firebaseFirestore.collection("Chat")
                .whereEqualTo("senderID", userID)
                .whereEqualTo("type", "support")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                checkUser(task);
            }
        });
    }

    private void senderIsExist() {
        messageArrayList = new ArrayList<>();
        senderExist = false;
        firebaseFirestore.collection("Chat")
                .whereEqualTo("senderID", userID)
                .whereEqualTo("receiverID", receiverId)
                .whereEqualTo("serviceId", serviceId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                checkUser(task);
            }
        });
    }

    private void checkUser(Task<QuerySnapshot> task) {
        if (!task.getResult().isEmpty()) {
            senderExist = true;
            docID = task.getResult().getDocuments().get(0).getId();
            Chat chat = task.getResult().getDocuments().get(0).toObject(Chat.class);
            messageArrayList = chat.getMessageArrayList();
            if (messageArrayList.isEmpty()) {
                //get Auto message
                getAutoMessage();
            }
        } else {
            //get Auto message
            getAutoMessage();
        }
        fillRecycleAdapter(messageArrayList);
        if (!senderExist) {
            createRomeChat();
        }
    }

    private void getAutoMessage() {
        firebaseFirestore.collection("AutoMessage").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    Message message = task.getResult().getDocuments().get(0).toObject(Message.class);
                    messageArrayList.add(message);
                    fillRecycleAdapter(messageArrayList);
                }
            }
        });
    }

    private void fillRecycleAdapter(ArrayList<Message> messageArrayList) {
        messagesAdapter = new MessagesAdapter(this, userID, messageArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
    }

    private void addNewMessage(String messageText) {
        DocumentReference documentReference = firebaseFirestore.collection("Chat").document(docID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Chat chat = documentSnapshot.toObject(Chat.class);
                assert chat != null;
                ArrayList<Message> messageArrayList = chat.getMessageArrayList();
                String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                Message message = new Message(messageText, userID, date);
                messageArrayList.add(message);
                documentReference.update("messageArrayList", messageArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideKeyboard(ChatActivity.this);
                        messageEditText.setText("");
                        fillRecycleAdapter(messageArrayList);
                    }
                });
            }
        });
    }
}
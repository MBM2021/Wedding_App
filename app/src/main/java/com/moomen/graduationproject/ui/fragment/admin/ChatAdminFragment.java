package com.moomen.graduationproject.ui.fragment.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.ChatAdapter;
import com.moomen.graduationproject.model.Chat;
import com.moomen.graduationproject.model.Message;
import com.moomen.graduationproject.ui.activity.ViewChat;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;

public class ChatAdminFragment extends Fragment {
    public static final String CHAT_ID = "CHAT_ID";

    private View view;

    private EditText editTextAutoMessage;
    private Button buttonAutoMessage;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextAutoMessage = view.findViewById(R.id.editText_automatic_message);
        buttonAutoMessage = view.findViewById(R.id.button_save_auto_message);
        recyclerView = view.findViewById(R.id.recycler_view_chat_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        autoMessage();
        getAllChats();
        getAutoMessage();
    }

    private void getAutoMessage() {
        firebaseFirestore.collection("AutoMessage").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    Message message = task.getResult().getDocuments().get(0).toObject(Message.class);
                    //List<String> message = task.getResult().toObjects(String.class);
                    editTextAutoMessage.setText(message.getMessage());
                }
            }
        });
    }

    private void autoMessage() {
        buttonAutoMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextAutoMessage.getText().toString().trim();
                //HashMap<String, Object> hashMapMessage = new HashMap<>();
                //hashMapMessage.put("message", message);
                String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                Message message = new Message(messageText, firebaseUser.getUid(), "");
                firebaseFirestore.collection("AutoMessage").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            String id = task.getResult().getDocuments().get(0).getId();
                            firebaseFirestore.collection("AutoMessage").document(id).update("message", messageText);
                        } else
                            firebaseFirestore.collection("AutoMessage").add(message);
                    }
                });
            }
        });
    }

    private void getAllChats() {
        Query query = FirebaseFirestore.getInstance().collection("Chat")
                .whereEqualTo("type", "support")
                .orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();
        ChatAdapter chatAdapter = new ChatAdapter(options);
        chatAdapter.setFragmentType("admin");
        chatAdapter.onItemSetOnClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String chatID = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), ViewChat.class);
                intent.putExtra(CHAT_ID, chatID);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setHasFixedSize(true);
        chatAdapter.startListening();
    }
}
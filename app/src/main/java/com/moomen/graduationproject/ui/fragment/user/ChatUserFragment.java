package com.moomen.graduationproject.ui.fragment.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.ChatAdapter;
import com.moomen.graduationproject.model.Chat;
import com.moomen.graduationproject.ui.activity.ChatActivity;
import com.moomen.graduationproject.ui.activity.ViewChat;

import org.jetbrains.annotations.NotNull;

public class ChatUserFragment extends Fragment {
    public static final String IS_SUPPORT = "IS_SUPPORT";
    public static final String CHAT_ID = "CHAT_ID";

    private View view;

    private EditText editTextAutoMessage;
    private Button buttonAutoMessage;
    private ImageView supportChat;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_user, container, false);
        return root;
    }

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
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        supportChat = view.findViewById(R.id.imageView_support_id);
        recyclerView = view.findViewById(R.id.recycler_view_chat_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        getAllChats();
        openSupportChatRoom();
    }

    private void openSupportChatRoom() {
        supportChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(IS_SUPPORT, "support");
                startActivity(intent);
            }
        });
    }

    private void getAllChats() {
        Query query = FirebaseFirestore.getInstance()
                .collection("Chat")
                .whereEqualTo("senderID", userId);
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();
        ChatAdapter chatAdapter = new ChatAdapter(options);
        chatAdapter.setFragmentType("user");
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
package com.moomen.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Chat;
import com.moomen.graduationproject.model.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private String userId;
    private String fragmentType = "";

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    public void setFragmentType(String fragmentType) {
        this.fragmentType = fragmentType;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat model) {
        if (fragmentType.equals("admin")) {
            firebaseFirestore.collection("Users").document(model.getSenderID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    Picasso.get().load(user.getUserImage()).into(holder.senderImage);
                    holder.senderName.setText(user.getName());
                    holder.senderEmail.setText(user.getEmail());
                }
            });
        } else {
            if (model.getType().equals("support")) {
                Picasso.get().load(R.drawable.logo).into(holder.senderImage);
                holder.senderName.setText("Afrah");
                holder.senderEmail.setText("afrah@gmail.com");
            } else {
                currentUserId = model.getSenderID();
                if (userId.equals(currentUserId))
                    currentUserId = model.getReceiverID();
                firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        Picasso.get().load(user.getUserImage()).into(holder.senderImage);
                        holder.senderName.setText(user.getName());
                        holder.senderEmail.setText(user.getEmail());
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    public void onItemSetOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView senderImage;
        TextView senderName;
        TextView senderEmail;
        LinearLayout chatItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.image_view_user_image_sender_id);
            senderName = itemView.findViewById(R.id.text_view_sender_name_id);
            senderEmail = itemView.findViewById(R.id.text_view_sender_email_id);
            chatItem = itemView.findViewById(R.id.sender_item_layout_id);
            chatItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition());
                    }
                }
            });

        }
    }


}

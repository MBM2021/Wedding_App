package com.moomen.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.User;
import com.squareup.picasso.Picasso;

public class UsersAdapter extends FirestoreRecyclerAdapter<User, UsersAdapter.ViewHolder> {

    private OnItemClickListener listener;


    public UsersAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position, @NonNull User model) {
        Picasso.get()
                .load(model.getUserImage())
                .into(holder.userImageView);
        holder.userNameTextView.setText(model.getName());
        holder.userDateCreateTextView.setText(model.getDateCreate());
        holder.userEmailTextView.setText(model.getEmail());
        holder.userLocationTextView.setText(model.getAddress());
        if (model.isStatus()) {
            holder.userStatusTextView.setText("Active");
            holder.userStatusImageView.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);

        } else {
            holder.userStatusTextView.setText("Blocked");
            holder.userStatusImageView.setImageResource(R.drawable.ic_baseline_block_24);
        }
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImageView;
        TextView userNameTextView;
        TextView userDateCreateTextView;
        TextView userEmailTextView;
        TextView userLocationTextView;
        TextView userStatusTextView;
        ImageView userStatusImageView;
        ConstraintLayout constraintLayoutUserItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.imageView_user_users_fragment);
            userNameTextView = itemView.findViewById(R.id.textView_user_name_users_fragment);
            userDateCreateTextView = itemView.findViewById(R.id.textView_date_created_users_fragment);
            userEmailTextView = itemView.findViewById(R.id.textView_user_email_users_fragment);
            userLocationTextView = itemView.findViewById(R.id.textView_location_users_fragment);
            userStatusTextView = itemView.findViewById(R.id.textView_user_status_users_fragment);
            userStatusImageView = itemView.findViewById(R.id.imageView_user_status_users_fragment);
            constraintLayoutUserItem = itemView.findViewById(R.id.constraintLayout_user_item_id);
            constraintLayoutUserItem.setOnClickListener(new View.OnClickListener() {
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

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onItemSetOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}

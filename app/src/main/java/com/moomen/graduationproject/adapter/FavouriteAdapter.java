package com.moomen.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Favourite;
import com.moomen.graduationproject.model.Service;
import com.squareup.picasso.Picasso;

public class FavouriteAdapter extends FirestoreRecyclerAdapter<Favourite, FavouriteAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public FavouriteAdapter(@NonNull FirestoreRecyclerOptions<Favourite> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position, @NonNull Favourite model) {
        String favouriteItemId = getSnapshots().getSnapshot(position).getId();
        firebaseFirestore.collection("Services").document(model.getServiceId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //String favouriteItemId = task.getResult().getId();
                Service service = task.getResult().toObject(Service.class);
                Picasso.get()
                        .load(service.getImage())
                        .into(holder.serviceImage);
                holder.serviceName.setText(service.getName());
                //TODO:Error caused by favourite list
                //holder.servicePrice.setText(service.getPrice() + " $");
                holder.favouriteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("Users")
                                .document(userUid)
                                .collection("Favourite").document(favouriteItemId).delete();
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.favourite_item, parent, false);
        return new FavouriteAdapter.ViewHolder(view);
    }

    public void setContext(Context context) {

    }

    public void onItemSetOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceName, servicePrice;
        ImageView favouriteIcon;
        ConstraintLayout constraintLayoutFavouriteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.imageView_product);
            serviceName = itemView.findViewById(R.id.textView_product_name);
            favouriteIcon = itemView.findViewById(R.id.imageViewFavourite);
            //servicePrice = itemView.findViewById(R.id.textView_product_price);
            constraintLayoutFavouriteItem = itemView.findViewById(R.id.constraintlayout_service_item_layout);
            constraintLayoutFavouriteItem.setOnClickListener(new View.OnClickListener() {
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
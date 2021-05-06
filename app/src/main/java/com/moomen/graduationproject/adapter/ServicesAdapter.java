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
import com.google.firebase.firestore.DocumentSnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Service;
import com.squareup.picasso.Picasso;

public class ServicesAdapter extends FirestoreRecyclerAdapter<Service, ServicesAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public ServicesAdapter(@NonNull FirestoreRecyclerOptions<Service> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ServicesAdapter.ViewHolder holder, int position, @NonNull Service model) {
        Picasso.get()
                .load(model.getImage())
                .into(holder.serviceImage);
        holder.serviceName.setText(model.getName());
        holder.servicePrice.setText(model.getPrice() + " $");
    }

    @NonNull
    @Override
    public ServicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.service_item, parent, false);
        return new ServicesAdapter.ViewHolder(view);
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
        ConstraintLayout constraintLayoutServiceItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.imageView_product);
            serviceName = itemView.findViewById(R.id.textView_product_name);
            servicePrice = itemView.findViewById(R.id.textView_product_price);

            constraintLayoutServiceItem = itemView.findViewById(R.id.constraintlayout_service_item_layout);
            constraintLayoutServiceItem.setOnClickListener(new View.OnClickListener() {
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
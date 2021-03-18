package com.moomen.graduationproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Ads;
import com.moomen.graduationproject.model.Category;
import com.squareup.picasso.Picasso;

public class AdsAdapter extends FirestoreRecyclerAdapter<Ads, AdsAdapter.ViewHolder> {

    private AdsAdapter.OnItemClickListener listener;
    public AdsAdapter(@NonNull FirestoreRecyclerOptions<Ads> options) {
        super(options);
    }

    @NonNull
    @Override
    public AdsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ads_item, parent, false);
        return new AdsAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdsAdapter.ViewHolder holder, int position, @NonNull Ads model) {
        if (!model.getImage().isEmpty()){
            Picasso.get()
                    .load(model.getImage())
                    .into(holder.adsImage);
        }
    }

    public void setContext(Context context) {
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView adsImage,edit,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adsImage = itemView.findViewById(R.id.imageView_ads);
            edit = itemView.findViewById(R.id.imageView_edit);
            delete = itemView.findViewById(R.id.imageView_delete);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition(), edit.getId());
                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition(), delete.getId());
                    }
                }
            });
        }
    }
    public void onItemSetOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

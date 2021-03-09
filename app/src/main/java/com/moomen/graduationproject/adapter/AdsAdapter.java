package com.moomen.graduationproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Ads;
import com.moomen.graduationproject.model.Category;
import com.squareup.picasso.Picasso;

public class AdsAdapter extends FirestoreRecyclerAdapter<Ads, AdsAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
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
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Ads model) {
        Picasso.get()
                .load(model.getImage())
                .into(holder.adsImage);
    }

    public void setContext(Context context) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView adsImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adsImage = itemView.findViewById(R.id.imageView_ads);

            /*constraintLayoutCategoryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition());
                    }
                }
            });
*/
        }
    }
}

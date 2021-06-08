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
import com.google.firebase.firestore.DocumentSnapshot;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.model.Booking;

public class BookingAdapter extends FirestoreRecyclerAdapter<Booking, BookingAdapter.ViewHolder> {

    private BookingAdapter.OnItemClickListener listener;

    public BookingAdapter(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ads_item, parent, false);
        return new BookingAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position, @NonNull Booking model) {

    }

    public void setContext(Context context) {
    }

    public void onItemSetOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage, userImage;
        TextView userName, userMobile, serviceName, ownerName, bookingDate, date, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            /*delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition(), delete.getId());
                    }
                }
            });*/
        }
    }
}

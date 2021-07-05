package com.moomen.graduationproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.moomen.graduationproject.model.Booking;
import com.moomen.graduationproject.model.Service;
import com.squareup.picasso.Picasso;

public class BookingAdapter extends FirestoreRecyclerAdapter<Booking, BookingAdapter.ViewHolder> {

    private BookingAdapter.OnItemClickListener listener;
    private String userId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public BookingAdapter(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.booking_item, parent, false);
        return new BookingAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position, @NonNull Booking model) {
        String serviceId = model.getServiceId();
        if (model.isCancelBooking())
            holder.cancelBooking.setVisibility(View.GONE);
        else
            holder.cancelBooking.setVisibility(View.VISIBLE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Services")
                .document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                Picasso.get().load(service.getImage()).into(holder.serviceImage);
                holder.serviceName.setText(service.getName());
                holder.bookingDate.setText(model.getBookingDate());
                if (model.isCancelBooking())
                    holder.service_status.setText("You was canceled this booking");
                else {
                    if (model.isInReview())
                        holder.service_status.setText("In Review");
                    else {
                        if (model.isStatus())
                            holder.service_status.setText("Accepted");
                        else
                            holder.service_status.setText("Rejected");
                    }
                }
            }
        });
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
        TextView serviceName, service_status, bookingDate;
        ConstraintLayout bookingItem;
        Button cancelBooking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.imageView_service_image_id);
            serviceName = itemView.findViewById(R.id.textView_service_name);
            service_status = itemView.findViewById(R.id.textView_service_status);
            bookingDate = itemView.findViewById(R.id.textView_booking_date_id);
            bookingItem = itemView.findViewById(R.id.constraintLayout_booking_item_id);
            cancelBooking = itemView.findViewById(R.id.button_cancel_id);
            cancelBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        listener.onItemClick(documentSnapshot, getAdapterPosition());
                    }
                }
            });
        }
    }
}

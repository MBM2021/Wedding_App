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
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

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
        userId = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("Services")
                .document(serviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Service service = task.getResult().toObject(Service.class);
                Picasso.get().load(service.getImage()).into(holder.serviceImage);
                holder.serviceName.setText(service.getName());
                if (model.isStatus())
                    holder.service_status.setText("Accepted");
                else {
                    if (model.isInReview())
                        holder.service_status.setText("Your Book In Review");
                    else
                        holder.service_status.setText("Your Book Rejected");
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
        void onItemClick(DocumentSnapshot documentSnapshot, int position, int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceName, service_status;

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

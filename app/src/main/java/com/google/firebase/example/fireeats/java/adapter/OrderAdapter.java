package com.google.firebase.example.fireeats.java.adapter;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.ItemInboxBinding;
import com.google.firebase.example.fireeats.databinding.ItemOrderBinding;
import com.google.firebase.example.fireeats.databinding.ItemShopProductCardBinding;
import com.google.firebase.example.fireeats.java.model.Order;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class OrderAdapter extends FirestoreAdapter<OrderAdapter.ViewHolder> {

    public interface OnOrderSelectedListener {

        void onOrderSelected(DocumentSnapshot restaurant);
    }

    private OnOrderSelectedListener mListener;

    public OrderAdapter(Query query, OnOrderSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemOrderBinding binding;

        public ViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnOrderSelectedListener listener) {

            Order order = snapshot.toObject(Order.class);
            Resources resources = itemView.getResources();

            binding.shippingName.setText(order.getPaymentDetail().getName());
            binding.shippingAddress.setText(order.getPaymentDetail().getAddress1() + '\n' + order.getPaymentDetail().getAddress2());
            binding.shippingEmail.setText(order.getPaymentDetail().getEmail());
            binding.orderStatus1.setVisibility((order.getOrderStatus().equals("PROCESSING")) ? View.VISIBLE : View.GONE);
            binding.orderStatus2.setVisibility((order.getOrderStatus().equals("DONE")) ? View.VISIBLE : View.GONE);
            binding.orderStatus3.setVisibility((order.getOrderStatus().equals("CANCELLED")) ? View.VISIBLE : View.GONE);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            String strDate = formatter.format(order.getCreatedDate());
            binding.createdDate.setText(strDate);

            binding.lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOrderSelected(snapshot);
                    }
                }
            });

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOrderSelected(snapshot);
                    }
                }
            });
        }

    }
}

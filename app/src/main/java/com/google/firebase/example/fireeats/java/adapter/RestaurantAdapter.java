package com.google.firebase.example.fireeats.java.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fireeats.databinding.ItemShopProductCardBinding;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class RestaurantAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder> {

    public interface OnRestaurantSelectedListener {

        void onRestaurantSelected(DocumentSnapshot restaurant);
    }

    private OnRestaurantSelectedListener mListener;

    public RestaurantAdapter(Query query, OnRestaurantSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemShopProductCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemShopProductCardBinding binding;

        public ViewHolder(ItemShopProductCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnRestaurantSelectedListener listener) {

            Product product = snapshot.toObject(Product.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(binding.restaurantItemImage.getContext())
                    .load(product.getPhoto())
                    .into(binding.restaurantItemImage);

            binding.restaurantItemName.setText(product.getName());
            binding.restaurantItemRating.setRating((float) product.getAvgRating());
            binding.lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRestaurantSelected(snapshot);
                    }
                }
            });
//            binding.restaurantItemCity.setText(product.getSubcategory());
//            binding.restaurantItemCategory.setText(product.getCategory());
//            binding.restaurantItemNumRatings.setText(resources.getString(R.string.fmt_num_ratings,
//                    product.getNumRatings()));
            binding.restaurantItemPrice.setText("VND " + product.getPrice());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRestaurantSelected(snapshot);
                    }
                }
            });
        }

    }
}

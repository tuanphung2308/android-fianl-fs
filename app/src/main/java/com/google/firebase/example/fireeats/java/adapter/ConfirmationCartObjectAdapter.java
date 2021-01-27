package com.google.firebase.example.fireeats.java.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.model.CartObject;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.List;

public class ConfirmationCartObjectAdapter extends RecyclerView.Adapter<ConfirmationCartObjectAdapter.ViewHolder> {

    private List<CartObject> cartObjectList;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView productPicture;
        private final TextView quantityText;
        private final TextView priceText;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.cartObjectTitle);
            productPicture = view.findViewById(R.id.cartObjectImageView);
            quantityText = (TextView) view.findViewById(R.id.cartObjectQuanText);
            priceText = (TextView) view.findViewById(R.id.cartObjectPriceText);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getProductPicture() {
            return productPicture;
        }

        public TextView getQuantityText() {
            return quantityText;
        }

        public TextView getPriceText() {
            return priceText;
        }
    }

    public ConfirmationCartObjectAdapter(List<CartObject> cartObjectList2) {
        cartObjectList = cartObjectList2;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_confirmation_cart_object, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        // Load image
        Glide.with(viewHolder.getProductPicture().getContext())
                .load(cartObjectList.get(position).getProduct().getPhoto())
                .into(viewHolder.getProductPicture());

        viewHolder.getTextView().setText(cartObjectList.get(position).getProduct().getName());
        viewHolder.getQuantityText().setText(String.valueOf("Quantity: " + cartObjectList.get(position).getQuantity()));
        viewHolder.getPriceText().setText("VND " + String.valueOf(cartObjectList.get(position).getQuantity() * cartObjectList.get(position).getProduct().getPrice()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cartObjectList.size();
    }
}


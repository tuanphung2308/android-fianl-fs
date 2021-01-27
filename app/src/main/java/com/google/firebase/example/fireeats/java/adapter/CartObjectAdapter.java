package com.google.firebase.example.fireeats.java.adapter;

import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.List;

public class CartObjectAdapter extends RecyclerView.Adapter<CartObjectAdapter.ViewHolder> {

    private List<CartObject> cartObjectList;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView productPicture;
        private final TextView quantityText;
        private final ImageButton quantityUp;
        private final ImageButton quantityDown;
        private final TextView priceText;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.productTitle);
            productPicture = view.findViewById(R.id.productImage);
            quantityText = (TextView) view.findViewById(R.id.productQuantity);
            quantityUp = view.findViewById(R.id.productQuantityUp);
            quantityDown = view.findViewById(R.id.productQuantityDown);
            priceText = (TextView) view.findViewById(R.id.productPrice);
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

        public ImageButton getQuantityUp() {
            return quantityUp;
        }

        public ImageButton getQuantityDown() {
            return quantityDown;
        }

        public TextView getPriceText() {
            return priceText;
        }
    }

    public CartObjectAdapter(List<CartObject> cartObjectList2) {
        cartObjectList = cartObjectList2;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_cart_object, viewGroup, false);

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
        viewHolder.getQuantityText().setText(String.valueOf(cartObjectList.get(position).getQuantity()));
        viewHolder.getPriceText().setText("VND " + String.valueOf(cartObjectList.get(position).getQuantity() * cartObjectList.get(position).getProduct().getPrice()));

        viewHolder.getQuantityUp().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DocumentReference sfDocRef = mFirestore.collection("carts").document(user.getUid());
                try {
                    int quantity = Integer.parseInt((String) viewHolder.getQuantityText().getText()) + 1;
                    viewHolder.getPriceText().setText("VND " + String.valueOf(quantity * cartObjectList.get(position).getProduct().getPrice()));
                    viewHolder.getQuantityText().setText(String.valueOf(quantity));
                    CartObject cartObject = cartObjectList.get(position);
                    mFirestore.runTransaction(new Transaction.Function<Cart>() {
                        @Override
                        public Cart apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            Cart currentCart = snapshot.toObject(Cart.class);
                            List<CartObject> cartObjectsCurrent = currentCart.getCartObjectList();
                            Boolean foundProduct = false;
                            for (CartObject co : cartObjectsCurrent) {
                                if (co.getProduct().getName().equals(cartObject.getProduct().getName())) {
                                    foundProduct = true;
                                    co.setQuantity(quantity);
                                }
                            }
                            if (!foundProduct) {
                                cartObjectsCurrent.add(cartObject);
                            }
                            transaction.update(sfDocRef, "cartObjectList", cartObjectsCurrent);
                            return currentCart;
                        }
                    })
                            .addOnSuccessListener(new OnSuccessListener<Cart>() {
                                @Override
                                public void onSuccess(Cart result) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    return;
                } catch (NumberFormatException e) {
                    return;
                }
            }
        });
        viewHolder.getQuantityDown().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final DocumentReference sfDocRef = mFirestore.collection("carts").document(user.getUid());
                    int quantity = Integer.parseInt((String) viewHolder.getQuantityText().getText());
                    CartObject cartObject = cartObjectList.get(position);

                    if (quantity >= 1) {
                        quantity = quantity - 1;
                        if (quantity == 0) {
                            cartObjectList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartObjectList.size());
                        } else {
                            viewHolder.getQuantityText().setText(String.valueOf(quantity));
                            cartObjectList.get(position).setQuantity(quantity);
                            viewHolder.getPriceText().setText("VND " + String.valueOf(cartObjectList.get(position).getQuantity() * cartObjectList.get(position).getProduct().getPrice()));
                        }

                        mFirestore.runTransaction(
                                new Transaction.Function<Cart>() {
                                    @Override
                                    public Cart apply(Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot snapshot = transaction.get(sfDocRef);
                                        Cart currentCart = snapshot.toObject(Cart.class);
                                        transaction.update(sfDocRef, "cartObjectList", cartObjectList);
                                        return currentCart;
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Cart>() {
                                    @Override
                                    public void onSuccess(Cart result) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                    return;
                } catch (NumberFormatException e) {
                    return;
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cartObjectList.size();
    }


}


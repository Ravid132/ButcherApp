package com.example.butcher.View;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.butcher.Interfaces.ProductClickListener;
import com.example.butcher.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productName, productPrice, productQuantity,productTotalPrice;
    private ProductClickListener productClickListener;

    public ImageView productImage2;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        productName = itemView.findViewById(R.id.cart_item_name);
        productQuantity = itemView.findViewById(R.id.cart_item_quantity);
        productPrice = itemView.findViewById(R.id.cart_item_price);
        productTotalPrice = itemView.findViewById(R.id.cart_total_price);


        productImage2 = (ImageView) itemView.findViewById(R.id.ProductImage2);
    }


    public void onClick(View view) {
    productClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setProductClickListener(ProductClickListener productClickListener) {
        this.productClickListener = productClickListener;
    }
}
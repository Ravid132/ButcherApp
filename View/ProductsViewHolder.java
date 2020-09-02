package com.example.butcher.View;

import android.service.autofill.TextValueSanitizer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.butcher.Interfaces.ProductClickListener;
import com.example.butcher.R;

public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView productName,productDesc,productPrice;
    public ImageView productImage;
    public ProductClickListener productListener;
    public ProductsViewHolder(@NonNull View itemView) {
        super(itemView);

        productName = (TextView) itemView.findViewById(R.id.ProductName);
        productDesc = (TextView) itemView.findViewById(R.id.ProductDesc);
        productPrice = (TextView) itemView.findViewById(R.id.ProductPrice);
        productImage = (ImageView) itemView.findViewById(R.id.ProductImage);
    }

    public void AddProductClickListener(ProductClickListener listener)
    {
        this.productListener = listener;
    }
    @Override
    public void onClick(View v) {
        productListener.onClick(v,getAdapterPosition(),false);
    }
}

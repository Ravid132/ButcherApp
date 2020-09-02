package com.example.butcher.View;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.butcher.Interfaces.CategoryClickListener;
import com.example.butcher.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView categoryName,categoryDesc;
    public ImageView categoryImage;
    public CategoryClickListener categoryListener;

    public CategoryViewHolder(View itemView)
    {
        super(itemView);
        categoryName = (TextView) itemView.findViewById(R.id.CategoryName);
        categoryDesc = (TextView) itemView.findViewById(R.id.CategoryDesc);
        categoryImage = (ImageView) itemView.findViewById(R.id.CategoryImage);

    }
    public void AddCategoryClickListener(CategoryClickListener listener)
    {
        this.categoryListener = listener;
    }
    @Override
    public void onClick(View v) {
        categoryListener.onClick(v,getAdapterPosition(),false);
    }
}

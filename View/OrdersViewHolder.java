package com.example.butcher.View;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.butcher.R;

public class OrdersViewHolder extends RecyclerView.ViewHolder
{
    public TextView CustomerName, CustomerPhone,CustomerPrice;
    public Button orderDetailsButton;

    public OrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        CustomerName = itemView.findViewById(R.id.order_User_name);
        CustomerPhone = itemView.findViewById(R.id.order_User_Phone);
        CustomerPrice = itemView.findViewById(R.id.order_User_price);

        orderDetailsButton = itemView.findViewById(R.id.order_Details_Button);


    }
}

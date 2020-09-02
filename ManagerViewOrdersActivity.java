package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.butcher.View.OrdersViewHolder;
import com.example.butcher.DBClasses.Orders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManagerViewOrdersActivity extends AppCompatActivity {

    private RecyclerView orders;
    private DatabaseReference ordersRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_view_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orders = findViewById(R.id.customers_Orders);
        recyclerView = findViewById(R.id.customers_Orders);
        recyclerView.setHasFixedSize(true);
        linearLayout = new LinearLayoutManager(this);
        orders.setLayoutManager(linearLayout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Orders> ordersOptions = new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(ordersRef, Orders.class).build();

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> ordersAdapter = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(ordersOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder ordersViewHolder, final int i, @NonNull final Orders orders) {
                ordersViewHolder.CustomerName.setText("Name: " + orders.getName());
                ordersViewHolder.CustomerPhone.setText("Phone: " + orders.getPhone());
                ordersViewHolder.CustomerPrice.setText("Price: " + orders.getPrice());

                ordersViewHolder.orderDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String PhoneID = getRef(i).getKey();
                        Intent intent = new Intent(ManagerViewOrdersActivity.this,ManagerCustomersOrderActivity.class);
                        intent.putExtra("phoneID",PhoneID);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customers_orders_layout,parent,false);
                 OrdersViewHolder ordersHolder = new OrdersViewHolder(view);
                 return ordersHolder;
            }
        };
        orders.setAdapter(ordersAdapter);
        ordersAdapter.startListening();
    }
}

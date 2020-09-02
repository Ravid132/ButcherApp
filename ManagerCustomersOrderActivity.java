package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.butcher.View.CartViewHolder;
import com.example.butcher.DBClasses.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ManagerCustomersOrderActivity extends AppCompatActivity {

    private RecyclerView Products;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference dataRef;
    private String UserPhoneID = null;
    private String Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_customers_order);

        Products = findViewById(R.id.customers_Orders_list);
        Products.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        Products.setLayoutManager(layoutManager);
        UserPhoneID = getIntent().getStringExtra("phoneID");
        dataRef = FirebaseDatabase.getInstance().getReference()
                .child("Manager Cart Order").child(UserPhoneID).child("products");



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions
                .Builder<Cart>().setQuery(dataRef, Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                cartViewHolder.productName.setText("Name: " + cart.getName());
                cartViewHolder.productQuantity.setText("Quantity: " + cart.getQuantity());
                cartViewHolder.productPrice.setText("Price: " + cart.getPrice() + "₪");

                DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Product")
                        .child(cart.getProductID());
                Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Image = dataSnapshot.child("image").getValue(String.class);
                        Picasso.get().load(Image).into(cartViewHolder.productImage2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_products_list,parent,false);
                CartViewHolder cartHolder = new CartViewHolder(view);
                return cartHolder;
            }
        };
        Products.setAdapter(adapter);
        adapter.startListening();
    }
}

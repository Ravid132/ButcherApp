package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butcher.View.CartViewHolder;
import com.example.butcher.DBClasses.Cart;
import com.example.butcher.DBClasses.UserOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserCartActivity extends AppCompatActivity {

    private Button orderButton;
    private TextView price,totalPrice;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private float totalPriceNum = 0;
    private double totall = 0;
    private float totalquantity;
    private String Image;
    private int quantityInCart,quantityInStock;
    final HashMap<String, Object> productsHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        totalPrice= (TextView)findViewById(R.id.order_total);
        recyclerView = findViewById(R.id.cart_products);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.slide);
        recyclerView.startAnimation(animation);
        orderButton = (Button)findViewById(R.id.order_button);
        price = (TextView)findViewById(R.id.order_price);

        totalPrice.setText("Total price: " + totalPriceNum + "₪");

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final DatabaseReference orderRef = FirebaseDatabase.getInstance()
                        .getReference().child("Orders").child(UserOptions.OnlineUser.getPhone());

                HashMap<String, Object> orderHashMap = new HashMap<>();
                orderHashMap.put("name",UserOptions.OnlineUser.getName());
                orderHashMap.put("phone",UserOptions.OnlineUser.getPhone());
                orderHashMap.put("email",UserOptions.OnlineUser.getEmail());
                orderHashMap.put("price",String.valueOf(totalPriceNum));
//                updatequantity();


                orderRef.updateChildren(orderHashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            new MyAsyncTask().execute();
                            getTotalPrice();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users Cart").child(UserOptions.OnlineUser.getPhone())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(UserCartActivity.this,"Order completed successfully!",Toast.LENGTH_SHORT);
                                    Intent intent = new Intent(UserCartActivity.this, CategoryActivity.class);
                                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private class MyAsyncTask extends AsyncTask<Void,Void,Void>
    {
        protected Void doInBackground(Void... params)
        {

            updatequantity();
//            try {
//                Thread.sleep(5000);
//            }
//            catch(InterruptedException e)
//            {}
            return null;
        }
    }

    private void updatequantity()
    {
        final DatabaseReference ItemsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users Cart").child(UserOptions.OnlineUser.getPhone()).child("products");

        ItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float sum = 0;
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    HashMap<String, Object> map = (HashMap<String, Object>) dss.getValue();
                    Object quantity = map.get("quantity");

                    Object ID = map.get("productID");
                    String productID = String.valueOf(ID);

                    final float qValue = Float.parseFloat(String.valueOf(quantity));
                    final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                            .child("Product").child(productID);

                    productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int num = Integer.valueOf(String.valueOf(dataSnapshot.child("quantity").getValue()));
                            quantityInCart = num - (int) qValue;
                            productsHashMap.put("quantity", String.valueOf(quantityInCart));
                            productRef.updateChildren(productsHashMap);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getTotalPrice()
    {
        final DatabaseReference ItemsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users Cart").child(UserOptions.OnlineUser.getPhone()).child("products");
        ItemsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float sum = 0;
                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) dss.getValue();
                            Object quantity = map.get("quantity");
                            Object price = map.get("price");

                            Object ID = map.get("productID");
                            String productID = String.valueOf(ID);

                            final float qValue = Float.parseFloat(String.valueOf(quantity));
                            float pValue = Float.parseFloat(String.valueOf(price));
                            sum = (pValue * qValue) + sum;

                        }

                        totalPriceNum = sum;
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        totalPrice.setText(String.valueOf(totalPriceNum));
    }

    private void checkIfHasProducts()
    {
        final DatabaseReference ItemsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users Cart").child(UserOptions.OnlineUser.getPhone());
        ItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    orderButton.setVisibility(View.VISIBLE);
                }
                else{
                    orderButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference();

        checkIfHasProducts();
        final FirebaseRecyclerOptions<Cart> cartOptions = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartItemsRef.child("Users Cart")
        .child(UserOptions.OnlineUser.getPhone()).child("products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> cartAdapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(cartOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, @NonNull final Cart cart)
            {
                cartViewHolder.productName.setText("Name: " + cart.getName());
                cartViewHolder.productQuantity.setText("Quantity: " + cart.getQuantity());
                cartViewHolder.productPrice.setText("Price: " + cart.getPrice() + "₪");
                float total = Float.parseFloat(cart.getPrice()) * Float.parseFloat(cart.getQuantity());

                cartViewHolder.productTotalPrice.setText("Total Price: "+ total + "₪");

                totalPriceNum = totalPriceNum + total;
                totalPrice.setText("Total price: " + totalPriceNum + "₪");
                getTotalPrice();
                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       CharSequence chars[] = new CharSequence[]
                               {
                                       "Edit",
                                       "Remove"
                               };

                        AlertDialog.Builder builder = new AlertDialog.Builder(UserCartActivity.this);
                        builder.setTitle("Remove/Edit Items:");
                        builder.setItems(chars, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which == 0)
                                {
                                    Intent intent = new Intent(UserCartActivity.this,ProductActivity.class);
                                    intent.putExtra("productID",cart.getProductID());
                                    startActivity(intent);
                                }
                                else if(which == 1)
                                {
                                    cartItemsRef.child("Users Cart").child(UserOptions.OnlineUser.getPhone())
                                            .child("products").child(cart.getProductID()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(UserCartActivity.this,"The product was removed successfully!",Toast.LENGTH_SHORT);
                                                        checkIfHasProducts();
                                                        getTotalPrice();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });

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


        recyclerView.setAdapter(cartAdapter);
        cartAdapter.startListening();
    }
}

package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.butcher.DBClasses.Product;
import com.example.butcher.DBClasses.UserOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    private TextView productDesc, productName, productPrice, productQuantity;
    private ImageView product_Image;
    private ElegantNumberButton elegantNumberButton;
    private String productID = null;
    private Button addToCart;
    private int quantityLeft;

    private int totalQuantity = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productName = (TextView)findViewById(R.id.product_Name);
        productPrice = (TextView)findViewById(R.id.product_Price);
        productDesc = (TextView)findViewById(R.id.product_Desc);
        productQuantity = (TextView)findViewById(R.id.product_Quantity);
        product_Image = (ImageView)findViewById(R.id.product_Image);
        elegantNumberButton = (ElegantNumberButton)findViewById(R.id.elegant_number_button);
        productID = getIntent().getStringExtra("productID");
        addToCart = (Button)findViewById(R.id.add_product_to_cart);

        getProduct(productID);

        elegantNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(ProductActivity.this,R.anim.anim);
                addToCart.startAnimation((animation));
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkIfOutOfStock()) {
                    addProductToCart();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference()
                .child("Product").child(productID).child("quantity");

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalQuantity = Integer.parseInt(dataSnapshot.getValue(String.class));
                productQuantity.setText("Quantity in stock: " + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkIfOutOfStock()
    {
        boolean isInStock = true;

        if(Integer.parseInt(elegantNumberButton.getNumber()) > totalQuantity)
        {
            isInStock = false;
            Toast.makeText(ProductActivity.this,"Sorry! we dont have this amount in our stocks!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProductActivity.this,HomePageActivity.class);
            finish();
            startActivity(intent);
        }
        return isInStock;
    }

    private void addProductToCart()
    {
//        quantityLeft = totalQuantity - Integer.valueOf(elegantNumberButton.getNumber());
//        final HashMap<String, Object> productsHashMap = new HashMap<>();
//        productsHashMap.put("quantity",String.valueOf(quantityLeft));
//        final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
//                .child("Product");
//
//        productRef.child(productID).updateChildren(productsHashMap);



        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference();
        final HashMap<String, Object> cartHashMap = new HashMap<>();


        cartHashMap.put("productID",productID);
        cartHashMap.put("name",productName.getText().toString());

        String str = productPrice.getText().toString();
        str =str.replaceAll("[^0-9]","");

        cartHashMap.put("price",str);
        cartHashMap.put("quantity",elegantNumberButton.getNumber());

        cartRef.child("Users Cart")
                .child(UserOptions.OnlineUser.getPhone())
                .child("products").child(productID).updateChildren(cartHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ProductActivity.this, "Product was added to cart succuessfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cartRef.child("Manager Cart Order")
                .child(UserOptions.OnlineUser.getPhone())
                .child("products").child(productID).updateChildren(cartHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(ProductActivity.this,CategoryActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void getProduct(String productID)
    {
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Product");

        Ref.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Product product = dataSnapshot.getValue(Product.class);

                    productName.setText("Name: " + product.getName());
                    productDesc.setText("Description: " + product.getDescription());

                    productPrice.setText("Price: " + product.getPrice() + "₪");
                    Picasso.get().load(product.getImage()).into(product_Image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

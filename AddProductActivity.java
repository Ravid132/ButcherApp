package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.butcher.View.ProductsViewHolder;
import com.example.butcher.DBClasses.Product;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AddProductActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ProductRef;

    private DatabaseReference prod;

    private String categoryID = null;

    private Button addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Product");
        recyclerView = findViewById(R.id.recycler_view_manager);
        recyclerView.setHasFixedSize(true);

        addProduct = (Button) findViewById(R.id.button_add_product);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim);
        recyclerView.startAnimation(animation);


        categoryID = getIntent().getStringExtra("CategoryID");
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this,ProductDetailsActivity.class);
                intent.putExtra("categoryID",categoryID);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference Ref = FirebaseDatabase.getInstance().getReference();

        final FirebaseRecyclerOptions<Product> products =
                new FirebaseRecyclerOptions.Builder<Product>().setQuery(ProductRef.orderByChild("menuID").equalTo(categoryID),Product.class).build();

        FirebaseRecyclerAdapter<Product, ProductsViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductsViewHolder>(products) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductsViewHolder productsViewHolder, int i, @NonNull final Product product) {

                productsViewHolder.productName.setText("Name: " + product.getName());
                productsViewHolder.productDesc.setText("Description: " + product.getDescription());
                productsViewHolder.productPrice.setText("Price: " + product.getPrice() + "₪");

                Picasso.get().load(product.getImage()).into(productsViewHolder.productImage);

                productsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence chars[] = new CharSequence[]
                                {
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                        builder.setTitle("Remove Items:");

                        builder.setItems(chars, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0)
                                {
                                    Ref.child("Product").child(product.getProductID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AddProductActivity.this,"The product was removed successfully!",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }

                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout,parent,false);
                ProductsViewHolder holder = new ProductsViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}

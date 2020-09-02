package com.example.butcher;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.butcher.View.ProductsViewHolder;
import com.example.butcher.DBClasses.Product;
import com.example.butcher.DBClasses.UserOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ProductRef;

    private DatabaseReference prod;

    private String categoryID = null;

    FirebaseRecyclerAdapter<Product, ProductsViewHolder> ProductyAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");


        ProductRef = FirebaseDatabase.getInstance().getReference().child("Product");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim);
        recyclerView.startAnimation(animation);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawer
                ,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this,UserCartActivity.class);
                startActivity(intent);
            }
        });


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);
        TextView userName = headView.findViewById(R.id.user_name);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_exit)
                .setDrawerLayout(drawer)
                .build();
        View headerView = navigationView.getHeaderView(0);

        TextView UserName = headerView.findViewById(R.id.user_name);
        UserName.setText("Welcome " + UserOptions.OnlineUser.getName()+ "!");


        categoryID = getIntent().getStringExtra("CategoryID");
    }


    @Override
    protected void onStart() {
        super.onStart();

        new getData().execute();


    }

    private class getData extends AsyncTask<String,Integer,String>
    {
        FirebaseRecyclerAdapter<Product, ProductsViewHolder> adapter;

        @Override
        protected String doInBackground(String... strings) {
            FirebaseRecyclerOptions<Product> products =
                    new FirebaseRecyclerOptions.Builder<Product>().setQuery(ProductRef.orderByChild("menuID").equalTo(categoryID),Product.class).build();


            adapter = new FirebaseRecyclerAdapter<Product, ProductsViewHolder>(products) {
                @Override
                protected void onBindViewHolder(@NonNull final ProductsViewHolder productsViewHolder, int i, @NonNull final Product product) {

                    productsViewHolder.productName.setText("Name: " + product.getName());
                    productsViewHolder.productDesc.setText("Description: " + product.getDescription());
                    productsViewHolder.productPrice.setText("Price: " + product.getPrice() + "₪");

                    Picasso.get().load(product.getImage()).into(productsViewHolder.productImage);

                    productsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(HomePageActivity.this, ProductActivity.class);
                            intent.putExtra("productID", product.getProductID());
                            startActivity(intent);
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

            return null;
        }

        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.nav_cart)
        {
            Intent intent = new Intent(HomePageActivity.this,UserCartActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_home)
        {
            Intent intent = new Intent(HomePageActivity.this,HomePageActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_exit)
        {
            Intent intent = new Intent(HomePageActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

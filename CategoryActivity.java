package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.butcher.View.CategoryViewHolder;
import com.example.butcher.DBClasses.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference CategoryRef;

    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle toggle;

    private MediaPlayer mplayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        Toolbar toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_category);
        toggle = new ActionBarDrawerToggle(this,drawer
                ,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);



        //

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                onNavigationItemSelected(item);
//                return true;
//            }
//        });


        //
        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        recyclerView = findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(true);

        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this,UserCartActivity.class);
                startActivity(intent);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim);
        recyclerView.startAnimation(animation);

        NavigationView navigationView = findViewById(R.id.nav_view_category);
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);
        TextView userName = headView.findViewById(R.id.user_name);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_exit)
                .setDrawerLayout(drawer)
                .build();
        View headerView = navigationView.getHeaderView(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerOptions<Category> category =
                new FirebaseRecyclerOptions.Builder<Category>().setQuery(CategoryRef,Category.class).build();

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(category) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryViewHolder categoryViewHolder, int i, @NonNull final Category category) {
                categoryViewHolder.categoryName.setText("Name: " + category.getName());
                categoryViewHolder.categoryDesc.setText("Description: " + category.getDescription());
                Picasso.get().load(category.getImage()).into(categoryViewHolder.categoryImage);

                categoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CategoryActivity.this, HomePageActivity.class);
                        intent.putExtra("CategoryID",category.getCategoryID());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout,parent,false);
                CategoryViewHolder holder = new CategoryViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
            Intent intent = new Intent(CategoryActivity.this,UserCartActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_home)
        {
        }

        else if(id == R.id.nav_exit)
        {
            Intent intent = new Intent(CategoryActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_category);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

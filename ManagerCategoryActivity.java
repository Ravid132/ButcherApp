package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.butcher.View.CategoryViewHolder;
import com.example.butcher.DBClasses.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ManagerCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference CategoryRef;

    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_category);

        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        recyclerView = findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim);
        recyclerView.startAnimation(animation);

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
                        Intent intent = new Intent(ManagerCategoryActivity.this, AddProductActivity.class);
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


}



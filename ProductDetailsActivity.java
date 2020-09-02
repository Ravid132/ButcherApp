package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;

public class ProductDetailsActivity extends AppCompatActivity {

    private String categoryID;

    private String Name,Desc,Quantity,Price;
    private ImageView newProductImage;
    private static final int image = 1;
    private static final int image2 = 100;
    private Uri imageUri;
    private EditText newProductName,newProductDesc,newProductQuantity,newProductPrice;
    private Button AddNewProduct;
    private StorageReference storageRef;
    private DatabaseReference dataRef;
    private String productKey,getImageKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        dataRef = FirebaseDatabase.getInstance().getReference().child("Product");
        storageRef = FirebaseStorage.getInstance().getReference().child("images");
        categoryID = getIntent().getExtras().get("categoryID").toString();

        newProductImage = (ImageView)findViewById(R.id.new_product_image_add);
        newProductName = (EditText)findViewById(R.id.new_product_name_add);
        newProductDesc = (EditText)findViewById(R.id.new_product_desc_add);
        newProductQuantity = (EditText)findViewById(R.id.new_product_quantity_add);
        newProductPrice = (EditText)findViewById(R.id.new_product_price_add);

        AddNewProduct = (Button) findViewById(R.id.new_product_add);

        newProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImageFromGallery();
            }
        });

        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProduct();
            }
        });
    }

    private void AddProduct()
    {
        Name = newProductName.getText().toString();
        Price = newProductPrice.getText().toString();
        Quantity = newProductQuantity.getText().toString();
        Desc = newProductDesc.getText().toString();

        if(TextUtils.isEmpty(Name))
        {
            Toast.makeText(this,"You did not enter a name!",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,"You did not enter the price!",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(Desc))
        {
            Toast.makeText(this,"You did not enter a description!",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(Quantity))
        {
            Toast.makeText(this,"You did not enter the quantity!",Toast.LENGTH_SHORT);
        }
        else if(imageUri == null)
        {
            Toast.makeText(this,"You did not choose an image!",Toast.LENGTH_SHORT);
        }
        else
        {
            StoreInFirebase();
        }
    }

    private void StoreInFirebase()
    {
        final int random = new Random().nextInt(1000);

        productKey = String.valueOf(random);
        final StorageReference path = storageRef.child(imageUri.getLastPathSegment() + ".jpg");
        final UploadTask uploadTask = path.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProductDetailsActivity.this,"image uploaded!",Toast.LENGTH_SHORT).show();

                Task<Uri> imageUri = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        getImageKey = path.getDownloadUrl().toString();
                        return path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        getImageKey = task.getResult().toString();
                        AddToDataBase();
                    }
                });
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.toString();

                Toast.makeText(ProductDetailsActivity.this,error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AddToDataBase()
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("productID",productKey);
        map.put("name",Name);
        map.put("price",Price);
        map.put("quantity",Quantity);
        map.put("description",Desc);
        map.put("image",getImageKey);
        map.put("menuID",categoryID);

        dataRef.child(productKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(ProductDetailsActivity.this,ManagerCategoryActivity.class);
                    startActivity(intent);
                    Toast.makeText(ProductDetailsActivity.this,"your product was added successfully!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void takeImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == image && resultCode == RESULT_OK && data !=null)
        {
            imageUri = data.getData();
            newProductImage.setImageURI(imageUri);
        }
    }
}

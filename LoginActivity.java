package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butcher.DBClasses.UserOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignup = (TextView) findViewById(R.id.textViewSignup);
        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View view)
    {
        if(view == buttonSignIn)
            userSignIn();
        if(view == textViewSignup)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    private void userSignIn()
    {
        String name = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Please enter a phone number",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT).show();
            if(password.length()<8)
                Toast.makeText(this,"Please enter a password longer than 8 characters",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            progressDialog.setMessage("Logging in...");
            progressDialog.show();
            progressDialog.dismiss();
            Login(name,password);
        }
    }

    private void Login(final String phone,final String password)
    {
        final DatabaseReference DataRef;
        DataRef = FirebaseDatabase.getInstance().getReference();

        DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("users").child(phone).exists())
                        {
                            Users UserData = dataSnapshot.child("users").child(phone).getValue(Users.class);
                            if (UserData.getPassword().equals(password)) {
                                Toast.makeText(LoginActivity.this, "You're now logging in...", Toast.LENGTH_SHORT).show();

                                UserOptions.OnlineUser = UserData;
//                                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                                startActivity(intent);
                            }
                        }
                else if(dataSnapshot.child("Manager").child(phone).exists())
                {
                    Users ManagerData = dataSnapshot.child("Manager").child(phone).getValue(Users.class);
                    if (ManagerData.getPassword().equals(password)) {
                        Toast.makeText(LoginActivity.this, "Manager now logging in...", Toast.LENGTH_SHORT).show();

                        UserOptions.OnlineUser = ManagerData;
                        Intent intent = new Intent(LoginActivity.this, ManagerCategoryActivity.class);
                        startActivity(intent);
                    }
                }
                    else {
                        Toast.makeText(LoginActivity.this, "The user does not exist.", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

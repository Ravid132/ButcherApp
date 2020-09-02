package com.example.butcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextPhone;
    private TextView textViewSignin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        }
        if (view == textViewSignin) {
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            if (password.length() < 8)
                Toast.makeText(this, "Please enter a password longer than 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            ValidateRegisteration(phone,email,name,password);

        }



       /* firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    //  startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Registeration unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void ValidateRegisteration(final String phone,final String email,final String name, final String password)
    {
        final DatabaseReference DataRef;

        DataRef = FirebaseDatabase.getInstance().getReference();

        DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("users").child(phone).exists())//Phone already in use
                {
                    if(dataSnapshot.child("users").child(phone).child(email).exists())//Email already in use
                    {
                        Toast.makeText(MainActivity.this,"This email already in use!",Toast.LENGTH_SHORT).show();

                    }
                    else
                        {
                        Toast.makeText(MainActivity.this, "This phone already in use!", Toast.LENGTH_SHORT).show();
                    }
                }
                else////creating account
                {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.setMessage("Registering...");
                            progressDialog.show();
                            HashMap<String, Object> userHashMap = new HashMap<>();
                            userHashMap.put("email", email);
                            userHashMap.put("name", name);
                            userHashMap.put("password", password);
                            userHashMap.put("phone", phone);
                            DataRef.child("users").child(phone).updateChildren(userHashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                progressDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Your account was created successfully!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(MainActivity.this, "An error has occured! try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            Toast.makeText(MainActivity.this,"TEST!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

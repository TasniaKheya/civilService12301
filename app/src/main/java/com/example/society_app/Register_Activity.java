package com.example.society_app;

import static com.example.society_app.R.id.emailEt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register_Activity extends AppCompatActivity {

    EditText mEmailEt,mPasswordEt;
    Button mRegiBtn;
    ProgressDialog progressDialog;
    TextView mHaveAccount;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mHaveAccount = findViewById(R.id.have_accountTv);
        mPasswordEt = findViewById(R.id.passwordEt);
        mEmailEt = findViewById(emailEt);
        mRegiBtn=findViewById(R.id.register12);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create New Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mRegiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }
                else if(password.length()<6)
                {
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);

                }
                else
                {
                    registerUser(email,password);

                }
            }
        });

        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register_Activity.this,Login_Activity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void registerUser(String email, String password) {

        progressDialog.show();
       mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful())
               {
                   progressDialog.dismiss();
                   FirebaseUser user = mAuth.getCurrentUser();
                   String email = user.getEmail();
                   String uid = user.getUid();

                   HashMap<Object,String>hashMap = new HashMap<>();

                   hashMap.put("email",email);
                   hashMap.put("uid",uid);
                   hashMap.put("name","");
                   hashMap.put("onlineStatus","online");
                   hashMap.put("typingTo","noOne");
                   hashMap.put("phone","");
                   hashMap.put("image","");
                   hashMap.put("cover","");

                   FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                   DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
                   databaseReference.child(uid).setValue(hashMap);




                   Toast.makeText(Register_Activity.this,"Registered..."+user.getEmail(),Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(Register_Activity.this,profileActivity.class);
                   startActivity(intent);
                   finish();

               }
               else
               {
                   progressDialog.dismiss();
                   Toast.makeText(Register_Activity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();

               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               progressDialog.dismiss();
               Toast.makeText(Register_Activity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
           }
       });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
package com.example.society_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        firebaseAuth  = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_vieww);
        bottomNavigationView.setOnItemSelectedListener(selectedListener);
       // profile = findViewById(R.id.profileTv);

        actionBar.setTitle("Home");
        HomeFragment homeFragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,homeFragment1,"");
        ft1.commit();
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId())
                    {
                        case R.id.nav_home:
                            actionBar.setTitle("Home");
                            HomeFragment homeFragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,homeFragment1,"");
                            ft1.commit();
                            return  true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            ProfileFragment profileFragment= new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,profileFragment,"");
                            ft2.commit();
                            return  true;
                        case R.id.nav_users:
                            actionBar.setTitle("Users");
                            UsersFragment usersFragment = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,usersFragment,"");
                            ft3.commit();
                            return  true;
                        case R.id.nav_chat:
                            actionBar.setTitle("Chats");
                            ChatFragment fragment4 = new ChatFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,fragment4,"");
                            ft4.commit();
                            return  true;
                    }
                    return false;
                }
            };

    private  void checkUserStatus()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            //profile.setText(user.getEmail());
        }
        else
        {
            Intent intent = new Intent(profileActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        checkUserStatus();
        super.onStop();
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

        {
            firebaseAuth.signOut();
   */
}
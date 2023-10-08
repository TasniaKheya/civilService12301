package com.example.society_app;

import static android.app.Activity.RESULT_OK;


import android.Manifest;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.society_app.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;


public class profileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    String mUID;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        firebaseAuth  = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.nav_vieww);
        bottomNavigationView.setOnItemSelectedListener(selectedListener);
       // profile = findViewById(R.id.profileTv);

        actionBar.setTitle("Home");
        HomeFragment homeFragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,homeFragment1,"");
        ft1.commit();
        checkUserStatus();


     // updateToken(FirebaseInstallations.getInstance().getToken(true).toString());


        }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
      //  ref.child(mUID).setValue(mToken);
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId())
                    {
                        case R.id.nav_home:
                           showMoreOptions2();
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
                            actionBar.setTitle("Notice");
                           ChatListFragment chatListFragment = new ChatListFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,chatListFragment,"");
                            ft4.commit();
                            return  true;
                        case R.id.nav_more:
                            showMoreOptions();
                            return  true;


                    }
                    return false;
                }
            };
    private void showMoreOptions2() {
        PopupMenu popupMenu = new PopupMenu(this,bottomNavigationView, Gravity.END);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Road");
        popupMenu.getMenu().add(Menu.NONE,1,0,"Fire");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id =  item.getItemId();
                if(id == 0)
                {
                    actionBar.setTitle("Road");
                    HomeFragment homeFragment1 = new HomeFragment();
                    FragmentTransaction ft10 = getSupportFragmentManager().beginTransaction();
                    ft10.replace(R.id.content,homeFragment1,"");
                    ft10.commit();
                }
                else if(id==1)
                {
                    actionBar.setTitle("Fire");
                    HomeFragment2 homeFragment2 = new HomeFragment2();
                    FragmentTransaction ft11 = getSupportFragmentManager().beginTransaction();
                    ft11.replace(R.id.content,homeFragment2,"");
                    ft11.commit();
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this,bottomNavigationView, Gravity.END);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Notifications");
        popupMenu.getMenu().add(Menu.NONE,1,0,"Group Chats");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id =  item.getItemId();
                if(id == 0)
                {
                    actionBar.setTitle("Notifications");
                    FragmentTransaction ft6= getSupportFragmentManager().beginTransaction();
                    ft6.commit();
                }
                else if(id==1)
                {
                    actionBar.setTitle("Group Chats");
                    GroupChatFragment groupChatFragment = new GroupChatFragment();
                    FragmentTransaction ft7= getSupportFragmentManager().beginTransaction();
                    ft7.replace(R.id.content,groupChatFragment,"");
                    ft7.commit();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private  void checkUserStatus()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            //profile.setText(user.getEmail());
            mUID = user.getUid();
            SharedPreferences sharedPreferences  = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
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
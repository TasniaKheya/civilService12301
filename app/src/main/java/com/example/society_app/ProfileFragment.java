package com.example.society_app;

import static android.app.Activity.RESULT_OK;


import android.Manifest;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.society_app.Adapters.AdapterPosts;
import com.example.society_app.models.ModelPost;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView imageView,coverTv;
    TextView phoneTv,emailTv,nameTv;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_Imgs/";
    DatabaseReference databaseReference;
    RecyclerView postsRecyclerView;
    FloatingActionButton fab;
    ProgressDialog pd;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int IMAGE_PIC_GALLERY_CODE = 300;
    private static final int IMAGE_PIC_CAMERA_CODE  = 400;

    String cameraPermission[];
    String storagePermission[];

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    Uri image_uri;
    String profileOrCoverPhoto;
    public ProfileFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        cameraPermission = new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        imageView = view.findViewById(R.id.avatar);
        nameTv = view.findViewById(R.id.nameTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        coverTv = view.findViewById(R.id.coverTv);
        fab=view.findViewById(R.id.fab);
        emailTv = view.findViewById(R.id.emailTv);
        fab=view.findViewById(R.id.fab);
        postsRecyclerView=view.findViewById(R.id.recyclerview_posts);

        if(firebaseUser != null)
        {
            pd = new ProgressDialog(getActivity());
            Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        String name = "" + ds.child("name").getValue();
                        String email = "" + ds.child("email").getValue();
                        String phone= "" + ds.child("phone").getValue();
                        String image= "" + ds.child("image").getValue();
                        String cover= "" + ds.child("cover").getValue();

                        nameTv.setText(name);
                        emailTv.setText(email);
                        phoneTv.setText(phone);

                        try {
                            Picasso.get().load(image).into(imageView);
                        }
                        catch (Exception e)
                        {
                            Picasso.get().load(R.drawable.ic_face_white).into(imageView);
                        }

                        try {
                            Picasso.get().load(cover).into(coverTv);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditProfileDialog();
                }
            });

            postList=new ArrayList<>();
            loadMyPosts();
            checkUserStatus();



        }
        return view;

    }
    private void loadMyPosts() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(layoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = user.getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        //Query query=ref.orderByChild("uid").equalTo(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              postList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(myUid!=null)
                    {
                        if(ds.child("uid").getValue().toString().equals(myUid))
                        {
                            ModelPost myPosts=ds.getValue(ModelPost.class);
                            postList.add(myPosts);
                            adapterPosts =new AdapterPosts(getActivity(),postList);
                            postsRecyclerView.setAdapter(adapterPosts);
                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchMyPosts(String searchQuery) {
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        Query query=ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               postList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelPost myPosts=ds.getValue(ModelPost.class);
                    if(myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            myPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(myPosts);
                    }

                    adapterPosts =new AdapterPosts(getActivity(),postList);
                    postsRecyclerView.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result;

    }
    private void requestStoragePermission()
    {
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;

    }
    private void requestCameraPermission()
    {
       requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }


    private void showEditProfileDialog() {

        String options[] = {"Edit Profile Picture","Edit Cover Photo","Edit Name","Edit Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0)
                {
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                }
                else if(which == 1)
                {
                    pd.setMessage("Updating Cover Photo");
                    profileOrCoverPhoto = "cover";
                    showImagePicDialog();
                }
                else if(which == 2)
                {
                    pd.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");
                }
                else if(which == 3)
                {
                    pd.setMessage("Updating Phone");
                    showNamePhoneUpdateDialog("phone");
                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(String keyName) {
     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     builder.setTitle("Update"+ keyName);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+keyName);
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value  = editText.getText().toString().trim();

                if(!TextUtils.isEmpty(value))
                {
                    pd.show();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put(keyName,value);

                    databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Updated...",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    if(keyName.equals("Name")){
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                        Query query=ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds:snapshot.getChildren()){
                                    String child= ds.getKey();
                                    snapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Please enter "+keyName,Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void showImagePicDialog() {

        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0)
                {
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickFromCamera();
                    }
                }
                else if(which == 1)
                {
                    if(!checkStoragePermission())
                    {
                        requestStoragePermission();

                    }
                    else
                    {
                        pickFromGallery();
                    }
                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


              switch (requestCode)
              {
                  case STORAGE_REQUEST_CODE:
                  {
                      if(grantResults.length>0)
                      {
                          boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                          boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                          if(cameraAccepted && writeStorageAccepted)
                          {
                              pickFromCamera();
                          }
                          else if(writeStorageAccepted)
                          {
                              pickFromGallery();
                          }
                          else
                          {
                              Toast.makeText(getActivity(),"Please Enable Camera & Storage Permission",Toast.LENGTH_SHORT).show();

                          }
                      }

                  }
                  break;
                  default:
                  {

                  }
              }



      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(resultCode == RESULT_OK)
       {
           if(requestCode == IMAGE_PIC_GALLERY_CODE)
           {
               image_uri = data.getData();
               uploadProfileCoverPhoto(image_uri);
           }
           if(requestCode == IMAGE_PIC_CAMERA_CODE)
           {
               uploadProfileCoverPhoto(image_uri);
           }
       }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        pd.show();
        String filePathAndName  =  storagePath + ""+profileOrCoverPhoto+"_"+firebaseUser.getUid();
      //  StorageReference storageReference2nd = storageReference.child(FirebaseAuth.getInstance().getUid()).child(filePathAndName);
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> UriTask = taskSnapshot.getStorage().getDownloadUrl();

             while (!UriTask.isSuccessful());
                //{
                Uri downloadUri = UriTask.getResult();
                    if(UriTask.isSuccessful())
                    {


                        HashMap<String,Object> results = new HashMap<>();

                        results.put(profileOrCoverPhoto,downloadUri.toString());
                        databaseReference.child(firebaseUser.getUid()).updateChildren(results).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(),"Image Updated...",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(),"Error Updating Image..",Toast.LENGTH_SHORT).show();

                                    }
                                });
                        if(profileOrCoverPhoto.equals("image")){
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                            Query query=ref.orderByChild("uid").equalTo(uid);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds:snapshot.getChildren()){
                                        String child= ds.getKey();
                                        snapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    else
                    {
                        pd.dismiss();
                        Toast.makeText(getActivity(),"Some Error Ocurred",Toast.LENGTH_SHORT).show();
                    }
                }
           // }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });



    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PIC_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PIC_GALLERY_CODE);
    }
    private  void checkUserStatus()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            //profile.setText(user.getEmail());
        }
        else
        {
            Intent intent = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
            getActivity().finish();

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem item= menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    searchMyPosts(query);
                }
                else {

                    loadMyPosts();
                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)){
                    searchMyPosts(newText);
                }
                else {
                    loadMyPosts();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
        {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if(id == R.id.action_add_post)
        {
            startActivity(new Intent(getActivity(),AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
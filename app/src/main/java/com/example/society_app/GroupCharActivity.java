package com.example.society_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.society_app.Adapters.AdapterGroupChatList;
import com.example.society_app.models.ModelGroupChatList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupCharActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String groupId;
    private Toolbar toolbar;
    private ImageView groupIconView;
    private TextView groupTitleTv;
    private EditText messageEt;
    private ImageButton attachBtn,sendBtn;
    private RecyclerView chatRv;
    private ArrayList<ModelGroupChatList>groupChatsArrayList;
    private AdapterGroupChatList adapterGroupChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_char);

        toolbar = findViewById(R.id.toolbar);
        groupIconView = findViewById(R.id.groupIconIv);
        groupTitleTv = findViewById(R.id.groupTitleTv);
        attachBtn = findViewById(R.id.attachBtn);
        messageEt= findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        chatRv = findViewById(R.id.chatRv);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
       firebaseAuth  = FirebaseAuth.getInstance();
       loadGroupInfo();
       loadGroupMessage();

       sendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String message = messageEt.getText().toString().trim();
               if(TextUtils.isEmpty(message))
               {
                   Toast.makeText(GroupCharActivity.this,"Can't send empty message",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   sendMessage(message);
               }
           }
       });
    }

    private void loadGroupMessage() {
        groupChatsArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!groupChatsArrayList.isEmpty())
                {
                    groupChatsArrayList.clear();
                }

                for(DataSnapshot ds : snapshot.getChildren())
                {
                    ModelGroupChatList model = ds.getValue(ModelGroupChatList.class);
                    groupChatsArrayList.add(model);
                }
                adapterGroupChat =  new AdapterGroupChatList(GroupCharActivity.this,groupChatsArrayList);
                chatRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        String timestamp =  ""+System.currentTimeMillis();
        HashMap<String,Object>hashMap  = new HashMap<>();
        hashMap.put("sender",""+firebaseAuth.getUid());
        hashMap.put("message",""+message);
        hashMap.put("timeStamp",""+timestamp);
        hashMap.put("type",""+ "text");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupId).child("Messages").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                messageEt.setText("");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupCharActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String groupTitle =  ""+ds.child("groupTitle").getValue();
                    String groupDescription =  ""+ds.child("groupDescription").getValue();
                    String groupIcon  = ""+ds.child("groupIcon").getValue();
                    String timeStamp = ""+ds.child("timeStamp").getValue();
                    String createdBy = ""+ds.child("createdBy").getValue();

                    groupTitleTv.setText(groupTitle);
                    try {
                        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group).into(groupIconView);
                    }
                    catch(Exception e)
                    {
                        groupIconView.setImageResource(R.drawable.ic_group);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
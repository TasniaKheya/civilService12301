package com.example.society_app.Adapters;


import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.society_app.R;
import com.example.society_app.models.ModelGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChatC extends RecyclerView.Adapter<AdapterGroupChatC.HolderGroupChat>{
    private static  final  int MSG_TYPE_LEFT =0;
    private static  final  int MSG_TYPE_RIGHT =1 ;

    private Context context;
    private ArrayList<ModelGroup> modelGroupChatsArrayList;

    private FirebaseAuth firebaseAuth;
    public AdapterGroupChatC(Context context, ArrayList<ModelGroup> modelGroupChatsArrayList) {
        this.context = context;
        this.modelGroupChatsArrayList = modelGroupChatsArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right,parent,false);
            return new HolderGroupChat(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,parent,false);
            return new HolderGroupChat(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        ModelGroup modelGroupChats = modelGroupChatsArrayList.get(position);
        String message = modelGroupChats.getMessage();
        String sender = modelGroupChats.getSender();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime  = DateFormat.format("dd/MM/yyyy  hh:mm aa" ,cal).toString();
        String timeStamp = modelGroupChats.getTimeStamp();
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        setUserName(modelGroupChats,holder);
    }

    private void setUserName(ModelGroup modelGroupChats, HolderGroupChat holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(modelGroupChats.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            String name = ""+ds.child("name").getValue();
                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelGroupChatsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(modelGroupChatsArrayList.get(position).getSender().equals(firebaseAuth.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

    class HolderGroupChat extends RecyclerView.ViewHolder{

        private TextView nameTv,messageTv,timeTv;
        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }



}

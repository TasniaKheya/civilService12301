package com.example.society_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.society_app.GroupCharActivity;
import com.example.society_app.R;
import com.example.society_app.models.ModelGroupChatList;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class AdapterGroupChatList extends RecyclerView.Adapter<AdapterGroupChatList.HolderGroupChat>{

   private Context context;
    private ArrayList<ModelGroupChatList> groupChats;

    public AdapterGroupChatList(Context context, ArrayList<ModelGroupChatList> groupChats) {
        this.context = context;
        this.groupChats = groupChats;
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchats_list,parent,false);

        return new HolderGroupChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {

        ModelGroupChatList modelGroupChats = groupChats.get(position);
        String groupId  = modelGroupChats.getGroupId();
        String groupIcon = modelGroupChats.getGroupIcon();
        String groupTitle = modelGroupChats.getGroupTitle();

        holder.groupTitleTv.setText(groupTitle);

        try
        {
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group).into(holder.groupIconIv);

        }
        catch (Exception e)
        {
            holder.groupIconIv.setImageResource(R.drawable.ic_group);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context, GroupCharActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupChats.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder{

        private ImageView groupIconIv;
        private TextView groupTitleTv,nameTv,messageTv,timeTv;
        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
            groupIconIv = itemView.findViewById(R.id.groupIconIv);
            groupTitleTv = itemView.findViewById(R.id.groupTitleTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);


        }
    }
}

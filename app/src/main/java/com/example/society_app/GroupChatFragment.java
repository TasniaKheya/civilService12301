package com.example.society_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.society_app.Adapters.AdapterGroupChatList;
import com.example.society_app.models.ModelGroupChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupChatFragment extends Fragment {

    private RecyclerView groupRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelGroupChatList> groupChats;
    private AdapterGroupChatList adapterGroupChat;

    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);
        groupRv = view.findViewById(R.id.groupRv);
        firebaseAuth = FirebaseAuth.getInstance();
        loadGroupChatList();
        return view;
    }

    private void loadGroupChatList() {
        groupChats = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChats.size();
                for (DataSnapshot ds : snapshot.getChildren()) {

                   if (ds.child("Participants").child(firebaseAuth.getUid()).exists()) {
                       ModelGroupChatList model = ds.getValue(ModelGroupChatList.class);
                        groupChats.add(model);

                    }
                }
                adapterGroupChat = new AdapterGroupChatList(getActivity(), groupChats);
                groupRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SearchGroupChatList(String query) {
        groupChats = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChats.size();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists()) {
                        if (ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())) {
                            ModelGroupChatList model = ds.getValue(ModelGroupChatList.class);
                            groupChats.add(model);
                        }

                    }
                }
                adapterGroupChat = new AdapterGroupChatList(getActivity(), groupChats);
                groupRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        @Override
        public void onCreate (@Nullable Bundle savedInstanceState){
            setHasOptionsMenu(true);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
            inflater.inflate(R.menu.menu_main, menu);


            MenuItem item = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (!TextUtils.isEmpty(s.trim())) {
                        SearchGroupChatList(s);
                    } else {
                        loadGroupChatList();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (!TextUtils.isEmpty(s.trim())) {
                        SearchGroupChatList(s);
                    } else {
                       loadGroupChatList();
                    }
                    return false;
                }
            });

            super.onCreateOptionsMenu(menu, inflater);
        }


        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){
            int id = item.getItemId();
            if (id == R.id.action_logout) {
                firebaseAuth.signOut();
                checkUserStatus();
            } else if (id == R.id.action_create_group) {
                startActivity(new Intent(getActivity(), GroupCreateActivity.class));
            }
            return super.onOptionsItemSelected(item);
        }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
    }

}

}
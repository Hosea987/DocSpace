package com.example.mountaineer.DocSpace;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_messages, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);

        mConvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    MainActivity.bottomNav.setVisibility(View.GONE);

//                    Toast.makeText(getContext(), "Scroll Down", Toast.LENGTH_SHORT).show();
                }else{

                    MainActivity.bottomNav.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "Scroll Up", Toast.LENGTH_SHORT).show();


                }
            }
        });
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);


        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Conv>()
                .setQuery(conversationQuery, Conv.class)
                .build();

        FirebaseRecyclerAdapter<Conv, MessagesFragment.ConvViewHolder> adapter = new FirebaseRecyclerAdapter<Conv, MessagesFragment.ConvViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final MessagesFragment.ConvViewHolder holder, final int i, @NonNull Conv model) {


                final String list_user_id = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String messageConv = dataSnapshot.child("message").getValue().toString();
                        final String user_id = dataSnapshot.child("from").getValue().toString();
                        holder.message.setText(messageConv);

                        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("name").getValue().toString();

                                final String user_image = dataSnapshot.child("thumb_image").getValue().toString();

                                holder.userName.setText(userName);
                                Glide.with(getActivity().getApplicationContext())
                                        .load(user_image)
                                        .into(holder.user_image);

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("user_id", list_user_id);
                                        chatIntent.putExtra("name", userName);
                                        startActivity(chatIntent);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public MessagesFragment.ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                MessagesFragment.ConvViewHolder viewHolder = new MessagesFragment.ConvViewHolder(view);

                return viewHolder;

            }
        };
        mConvList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        TextView user_id, desc, userName, message;
        ImageView Image_url;
        CircularImageView user_image;

        public ConvViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.name_text);
            message = itemView.findViewById(R.id.status_text);


            user_image = itemView.findViewById(R.id.profile_image);

        }
    }

}

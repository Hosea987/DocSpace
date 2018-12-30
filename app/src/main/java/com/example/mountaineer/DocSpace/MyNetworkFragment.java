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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyNetworkFragment extends Fragment {


    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mFriendsList;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendsDatabase;

    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


//    private FirebaseRecyclerAdapter<Users, SearchDoctor.FriendsViewHolder> mFriendsRVAdapter;


    public MyNetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_my_network, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.my_network_result_list);

        mFriendsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));



        // Inflate the layout for this fragment
        return mMainView;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabase, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull Friends model) {

                String userIds = getRef(position).getKey();
                mUserDatabase.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            String image = dataSnapshot.child("thumb_image").getValue().toString();
                            String userSpecialty = dataSnapshot.child("specialty").getValue().toString();
                            String userName = dataSnapshot.child("name").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userSpecialty.setText(userSpecialty);

//                            Glide.with(getActivity().getApplicationContext())
//                                    .load(image)
//                                    .into(holder.image);

                            Glide.with(getActivity().getApplicationContext())
                                    .load(image).apply(new RequestOptions().placeholder(R.drawable.default_user).fitCenter())
                                    .into(holder.image);

                            final String user_id = getRef(position).getKey();
                            final String user_name = getRef(position).getKey();




                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                            final String url = model.getUrl();
                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra("user_id", user_id);
                                            intent.putExtra("user_name", user_name);
                                            startActivity(intent);
                                        }
                                    });
                                }



                        }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                FriendsViewHolder viewHolder = new FriendsViewHolder(view);

                return viewHolder;

            }
        };

        mFriendsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userSpecialty;
        ImageView image;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.name_text);
            userSpecialty = itemView.findViewById(R.id.status_text);
            image = itemView.findViewById(R.id.profile_image);

        }
    }
}

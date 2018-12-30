package com.example.mountaineer.DocSpace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private CircularImageView mProfileImage;
    private TextView mProfileName, mProfileStatus;
    private Button mProfileSendReqBtn, mDeclineBtn;
    private Toolbar profileToolbar;
    private TextView ProfilePoints;
    private TextView ProfileFriends;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference mUsersDatabase;

    private DatabaseReference mFriends;

    private ProgressDialog mProgressDialog;
    private RecyclerView mProfilePosts;
    private DatabaseReference mCasesDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference LikesRef;
    private DatabaseReference CommentsRef;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;


    private String mCurrent_user_id;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    Boolean LikeCheker = false;

    int countFriends;

    private FirebaseRecyclerAdapter<CasePost, Profile.CommentsViewHolder> mCommentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        firebaseAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mFriends = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                countFriends = (int) dataSnapshot.child(user_id).getChildrenCount();
                ProfileFriends.setText(Integer.toString(countFriends));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profileToolbar = findViewById(R.id.profile_app_bar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profileToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        mCasesDatabase = FirebaseDatabase.getInstance().getReference().child("Cases");
        mCasesDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        mCurrent_user_id = firebaseAuth.getCurrentUser().getUid();


        mProfileImage = (CircularImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_specialty);
//        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.decline_req_btn);
        mProfilePosts = (RecyclerView) findViewById(R.id.profilePost_list);

        ProfileFriends = (TextView) findViewById(R.id.profileFriends);


        ProfilePoints = (TextView) findViewById(R.id.profilePoints);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mProfilePosts.setLayoutManager(mLayoutManager);

        ViewCompat.setNestedScrollingEnabled(mProfilePosts, false);


        mCurrent_state = "not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String specialty = dataSnapshot.child("specialty").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(specialty);

                Picasso.with(Profile.this).load(image).placeholder(R.drawable.image_placeholder).into(mProfileImage);

//                Glide.with(getApplicationContext())
//                        .load(image)
//                        .into(mProfileImage);

                if (mCurrent_user.getUid().equals(user_id)) {

                    mDeclineBtn.setEnabled(false);
                    mDeclineBtn.setVisibility(View.GONE);

                    mProfileSendReqBtn.setEnabled(false);
                    mProfileSendReqBtn.setVisibility(View.GONE);

                }


                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);


                            } else if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.GONE);
                                mDeclineBtn.setEnabled(false);

                            }

                            mProgressDialog.dismiss();


                        } else {


                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mDeclineBtn.setVisibility(View.GONE);
                                        mDeclineBtn.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                // --------------- NOT FRIENDS STATE ------------

                if (mCurrent_state.equals("not_friends")) {


                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Toast.makeText(Profile.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                            }

                            mProfileSendReqBtn.setEnabled(true);


                        }
                    });

                }


                // - -------------- CANCEL REQUEST STATE ------------

                if (mCurrent_state.equals("req_sent")) {

                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);


                                }
                            });

                        }
                    });

                }


                // ------------ REQ RECEIVED STATE ----------

                if (mCurrent_state.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);


                    friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "friends";
                                mProfileSendReqBtn.setText("Unfriend this Person");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }


                // ------------ UNFRIENDS ---------

                if (mCurrent_state.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();


                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }


            }
        });

        FirebaseRecyclerOptions commentsOption = new FirebaseRecyclerOptions.Builder<CasePost>().setQuery(mCasesDatabase, CasePost.class).build();

        mCommentsAdapter = new FirebaseRecyclerAdapter<CasePost, Profile.CommentsViewHolder>(commentsOption) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder holder, final int position, @NonNull CasePost model) {

                final String postId = getRef(position).getKey();
                mCasesDatabase.child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        final String image_url = dataSnapshot.child("image_url").getValue().toString();
                        final String desc = dataSnapshot.child("desc").getValue().toString();
                        final String title = dataSnapshot.child("title").getValue().toString();
                        final String time = dataSnapshot.child("timestamp").getValue().toString();
                        final String user_id = dataSnapshot.child("user_id").getValue().toString();

                        holder.desc.setText(desc);
                        holder.title.setText(title);

                        GetTimeAgo getTimeAgo = new GetTimeAgo();

                        long lastTime = Long.parseLong(time);

                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                        holder.lastSeenTime.setText(lastSeenTime);

                        Glide.with(getApplicationContext())
                                .load(image_url)
                                .into(holder.Image_url);
                        final String postId = getRef(position).getKey();

                        holder.like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)

                            {
                                LikeCheker = true;

                                LikesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (LikeCheker.equals(true)) {

                                            if (dataSnapshot.child(postId).hasChild(mCurrent_user_id)) {
                                                LikesRef.child(postId).child(mCurrent_user_id).removeValue();
                                                LikeCheker = false;

                                            } else

                                            {
                                                LikesRef.child(postId).child(mCurrent_user_id).setValue(true);
                                                LikeCheker = false;

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        });


                        mUserDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String name = dataSnapshot.child("name").getValue().toString();
                                final String specialty = dataSnapshot.child("specialty").getValue().toString();
                                final String user_case_image = dataSnapshot.child("image").getValue().toString();

                                holder.name.setText(name);
                                holder.specialty.setText(specialty);
                                Glide.with(getApplicationContext())
                                        .load(user_case_image)
                                        .into(holder.user_case_image);

                                holder.setLikeButtonStatus(postId);
                                holder.setCommentNumber(postId);


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent commentsIntent = new Intent(getApplicationContext(), comments_activity.class);
                                        commentsIntent.putExtra("postId", postId);
                                        commentsIntent.putExtra("user_id", user_id);
                                        commentsIntent.putExtra("name", name);
                                        commentsIntent.putExtra("specialty", specialty);
//                                        commentsIntent.putExtra("timestamp", time);
                                        commentsIntent.putExtra("user_case_image", user_case_image);
                                        commentsIntent.putExtra("image_url", image_url);
                                        commentsIntent.putExtra("timestamp", time);
                                        commentsIntent.putExtra("specialty", specialty);
                                        commentsIntent.putExtra("desc", desc);
                                        commentsIntent.putExtra("title", title);
                                        startActivity(commentsIntent);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }


            @NonNull
            @Override
            public Profile.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.case_list_item, parent, false);

                return new Profile.CommentsViewHolder(view);
            }
        };
        mProfilePosts.setAdapter(mCommentsAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        mCommentsAdapter.startListening();

    }

    @Override
    public void onStop() {

        super.onStop();
        mCommentsAdapter.stopListening();
    }


    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView user_id, desc, title, name, specialty, lastSeenTime, likeNumber, CommentNumber;
        ImageView like;
        ImageView Image_url;
        CircularImageView user_case_image;

        int countLikes;
        int countComments;
        String mCurrentUserID;
        DatabaseReference LikesRef;
        DatabaseReference CommentsRef;


        public CommentsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.blog_user_name);
            specialty = itemView.findViewById(R.id.blog_user_specialty);
            desc = itemView.findViewById(R.id.blog_desc);
            title = itemView.findViewById(R.id.blog_title);
            Image_url = itemView.findViewById(R.id.blog_image);
            lastSeenTime = itemView.findViewById(R.id.blog_time);
            like = itemView.findViewById(R.id.caseLike);
            likeNumber = itemView.findViewById(R.id.displayLikesNumber);
            CommentNumber = itemView.findViewById(R.id.displayCommentNumber);
            user_case_image = itemView.findViewById(R.id.blog_user_image);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            CommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");

            mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        }

        public void setLikeButtonStatus(final String postid) {

            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postid).hasChild(mCurrentUserID))

                    {

                        countLikes = (int) dataSnapshot.child(postid).getChildrenCount();

                        like.setImageResource(R.drawable.ic_whatshot_lit_24dp);
                        likeNumber.setText(Integer.toString(countLikes) + (" Likes"));


                    } else {

                        countLikes = (int) dataSnapshot.child(postid).getChildrenCount();

                        like.setImageResource(R.drawable.ic_whatshot_black_24dp);
                        likeNumber.setText(Integer.toString(countLikes) + (" Likes"));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setCommentNumber(final String postid) {

            CommentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    countComments = (int) dataSnapshot.child(postid).getChildrenCount();

                    CommentNumber.setText(Integer.toString(countComments) + (" Comments"));


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }


}



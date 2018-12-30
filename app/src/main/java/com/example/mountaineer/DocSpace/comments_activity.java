package com.example.mountaineer.DocSpace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;

public class comments_activity extends AppCompatActivity {

    private Toolbar commentToolbar;

    private CircularImageView caseUserImage;
    private TextView caseUserName;
    private TextView caseUserSpecialty;
    private TextView caseDesc;
    private TextView caseTitle;
    private TextView timestamp;
    private DatabaseReference CommentsLikesRef;
    private TextView CommentsCount;




    private LinearLayoutManager mLayoutManager;

    private ImageView caseImage;

    private EditText comment_field;
    private Button comment_post_btn;


    private RecyclerView comment_list;

    private DatabaseReference mCasesDatabase;
    private DatabaseReference mCommentsDatabase;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth firebaseAuth;


    private String current_user_id;

    private ProgressDialog mcommentProgress;

    private String mCurrent_user_id;

    Boolean CommentsLikeCheker = false;

    private FirebaseRecyclerAdapter<Comments, comments_activity.CommentsViewHolder> mCommentsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_activity);

        commentToolbar = findViewById(R.id.comment_app_bar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commentToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        final String postId = getIntent().getStringExtra("postId");
//        final String name = getIntent().getStringExtra("name");
//        final String specialty = getIntent().getStringExtra("specialty");
//        final String image_url = getIntent().getStringExtra("image_url");
        final String time = getIntent().getStringExtra("timestamp");
//        final String desc = getIntent().getStringExtra("desc");
//        final String title = getIntent().getStringExtra("title");
//        final String user_case_image = getIntent().getStringExtra("user_case_image");

        firebaseAuth = FirebaseAuth.getInstance();
        try {

            mCurrent_user_id = firebaseAuth.getCurrentUser().getUid();


        } catch (Exception e) {

            Log.d("Crash error", e.getMessage());

        }


        commentToolbar = findViewById(R.id.comment_app_bar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        timestamp = (TextView) findViewById(R.id.case_time);

        GetTimeAgo getTimeAgo = new GetTimeAgo();

        long lastTime = Long.parseLong(time);

        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

        timestamp.setText(lastSeenTime);

        CommentsLikesRef = FirebaseDatabase.getInstance().getReference().child("Comment_Likes");

//        mCommentsDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//
//                int countComments;
//
//                countComments= (int) dataSnapshot.child(postId).getChildrenCount();
//
//                CommentsCount.setText(Integer.toString(countComments) + ( " Comments"));
//
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        mcommentProgress = new ProgressDialog(this);
//
        firebaseAuth = FirebaseAuth.getInstance();

        mCommentsDatabase = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).push();
        mCasesDatabase = FirebaseDatabase.getInstance().getReference().child("Cases").child(postId);
        mCasesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image_url = dataSnapshot.child("image_url").getValue().toString();
                final String desc = dataSnapshot.child("desc").getValue().toString();
                final String title = dataSnapshot.child("title").getValue().toString();
                final String time = dataSnapshot.child("timestamp").getValue().toString();
                final String user_id = dataSnapshot.child("user_id").getValue().toString();


                CommentsCount = (TextView) findViewById(R.id.commentsPageCommentsCount);
                caseDesc = (TextView) findViewById(R.id.case_desc);
                caseDesc.setText(desc);

                caseTitle = (TextView) findViewById(R.id.case_title);
                caseTitle.setText(title);

                caseImage = (ImageView) findViewById(R.id.case_image);
                Glide.with(getApplicationContext())
                        .load(image_url).apply(new RequestOptions().placeholder(R.drawable.default_user).fitCenter())
                        .into(caseImage);

                caseImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                                Intent replyIntent = new Intent(getApplicationContext(), caseImageViewer.class);
                                replyIntent.putExtra("postId", postId);
                                startActivity(replyIntent);
                    }
                });


                mUserDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String specialty = dataSnapshot.child("specialty").getValue().toString();
                        final String user_case_image = dataSnapshot.child("image").getValue().toString();


                        caseUserName = (TextView) findViewById(R.id.case_user_name);
                        caseUserName.setText(name);


                        caseUserSpecialty = (TextView) findViewById(R.id.case_user_specialty);
                        caseUserSpecialty.setText(specialty);

                        caseUserImage = (CircularImageView) findViewById(R.id.case_user_image);
                        Glide.with(getApplicationContext())
                                .load(user_case_image).apply(new RequestOptions().placeholder(R.drawable.default_user).fitCenter())
                                .into(caseUserImage);




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
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
//
//
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        Query commentsQuery = commentsRef.orderByKey();
//
//

        current_user_id = firebaseAuth.getCurrentUser().getUid();
//        final String user_id = getIntent().getStringExtra("user_id");
//
        comment_field = (EditText) findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);


//        comment_list.setHasFixedSize(true);
//        comment_list.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        comment_list.setLayoutManager(mLayoutManager);

        ViewCompat.setNestedScrollingEnabled(comment_list, false);

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = comment_field.getText().toString();
//                String timestamp = String.valueOf(ServerValue.TIMESTAMP).toString();

                if (!TextUtils.isEmpty(message)) {

                    post_comment(message);


                }

            }
        });

        FirebaseRecyclerOptions commentsOption = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(commentsQuery, Comments.class).build();

        mCommentsAdapter = new FirebaseRecyclerAdapter<Comments, comments_activity.CommentsViewHolder>(commentsOption) {
            @Override
            protected void onBindViewHolder(@NonNull final comments_activity.CommentsViewHolder holder, int position, @NonNull Comments model) {

                holder.commentMessage.setText(model.getMessage());
                final String user_id = model.getUser_id().toString();
//                holder.setUser_id(model.getUser_id());

                GetTimeAgo getTimeAgo = new GetTimeAgo();

                long lastTime = Long.parseLong(model.getTimestamp());

                String  commentTime= getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                holder.commentTime.setText(commentTime);

                final String postId = getRef(position).getKey();

                holder.commentLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)

                    {
                        CommentsLikeCheker = true;

                        CommentsLikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (CommentsLikeCheker.equals(true)) {

                                    if (dataSnapshot.child(postId).hasChild(mCurrent_user_id)) {
                                        CommentsLikesRef.child(postId).child(mCurrent_user_id).removeValue();
                                        CommentsLikeCheker = false;

                                    } else

                                    {
                                        CommentsLikesRef.child(postId).child(mCurrent_user_id).setValue(true);
                                        CommentsLikeCheker = false;

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
                                .into(holder.user_comment_image);

                        holder.setLikeButtonStatus(postId);
//                        holder.setCommentNumber(postId);

//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                Intent replyIntent = new Intent(getApplicationContext(), Reply.class);
//                                replyIntent.putExtra("postId", postId);
//                                startActivity(replyIntent);
//                            }
//                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public comments_activity.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list, parent, false);

                return new comments_activity.CommentsViewHolder(view);
            }
        };
        comment_list.setAdapter(mCommentsAdapter);

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

        TextView user_id, commentMessage, name, specialty, lastSeenTime, CommentlikeNumber, ReplyNumber, commentTime;
        ImageView commentLike;
        CircularImageView user_comment_image;

        DatabaseReference CommentsLikesRef;
        String mCurrentUserID;
        int countLikes;
        int countComments;


        public CommentsViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.comment_username);
            commentTime = itemView.findViewById(R.id.comment_time);
            specialty = itemView.findViewById(R.id.comment_userSpecialty);
            commentMessage = itemView.findViewById(R.id.comment_message);
            user_comment_image = itemView.findViewById(R.id.comment_image);
            commentLike = itemView.findViewById(R.id.commentLike);
            CommentlikeNumber = itemView.findViewById(R.id.displaycommentsLikesNumber);
            ReplyNumber = itemView.findViewById(R.id.displayReplyNumber);
            CommentsLikesRef = FirebaseDatabase.getInstance().getReference().child("Comment_likes");
            mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        }

        public void setLikeButtonStatus(final String postid) {

            CommentsLikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postid).hasChild(mCurrentUserID))

                    {

                        countLikes = (int) dataSnapshot.child(postid).getChildrenCount();

                        commentLike.setImageResource(R.drawable.ic_whatshot_lit_24dp);
                        CommentlikeNumber.setText(Integer.toString(countLikes) + (" Likes"));


                    } else {

                        countLikes = (int) dataSnapshot.child(postid).getChildrenCount();

                        commentLike.setImageResource(R.drawable.ic_whatshot_black_24dp);
                        CommentlikeNumber.setText(Integer.toString(countLikes) + (" Likes"));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

//        public void setCommentNumber(final String postid) {
//
//            CommentsLikesRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                    countComments = (int) dataSnapshot.child(postid).getChildrenCount();
//
//                    ReplyNumber.setText(Integer.toString(countComments) + (" Replies"));
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//        }
    }


    private void post_comment(final String message) {

        final String postId = getIntent().getStringExtra("postId");


        mCommentsDatabase = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).push();

        String current_user_id = firebaseAuth.getCurrentUser().getUid();

        Map<String, String> commentMap = new HashMap();
        commentMap.put("message", message);
        commentMap.put("type", "text");
        commentMap.put("user_id", current_user_id);
        commentMap.put("timestamp",  String.valueOf(System.currentTimeMillis()));

        mCommentsDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(comments_activity.this, "Your comment has been posted", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(comments_activity.this, "Oops,your comment was not succesfully posted,try again", Toast.LENGTH_SHORT).show();

                }

                comment_field.setText("");
            }
        });

//        } else {
//
//            Toast.makeText(comments_activity.this, "Try again", Toast.LENGTH_SHORT).show();
//        }


    }
}











package com.example.mountaineer.DocSpace;

import android.support.annotation.NonNull;
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

public class Reply extends AppCompatActivity {

    private RecyclerView reply_list;
    private Toolbar replyToolbar;
    private EditText reply_field;
    private Button reply_post_btn;

    private DatabaseReference mRepliesDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth firebaseAuth;
    private String mCurrent_user_id;
    private DatabaseReference RepliesLikesRef;


    Boolean CommentsLikeCheker = false;

    private FirebaseRecyclerAdapter<Comments, Reply.CommentsViewHolder> mCommentsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        replyToolbar = findViewById(R.id.reply_app_bar);
        setSupportActionBar(replyToolbar);
        getSupportActionBar().setTitle("Replies");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        replyToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        final String postId = getIntent().getStringExtra("postId");

        firebaseAuth = FirebaseAuth.getInstance();
        try {

            mCurrent_user_id = firebaseAuth.getCurrentUser().getUid();


        } catch (Exception e) {

            Log.d("Crash error", e.getMessage());

        }

        mRepliesDatabase = FirebaseDatabase.getInstance().getReference().child("Comments_reply").child(postId).push();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        RepliesLikesRef = FirebaseDatabase.getInstance().getReference().child("Reply_Likes");

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments_reply").child(postId);
        Query commentsQuery = commentsRef.orderByKey();


        reply_field = (EditText) findViewById(R.id.reply_field);
        reply_post_btn = (Button) findViewById(R.id.reply_post_btn);

        reply_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = reply_field.getText().toString();
//                String timestamp = String.valueOf(ServerValue.TIMESTAMP).toString();

                if (!TextUtils.isEmpty(message)) {

                    post_comment(message);


                }

            }
        });

        reply_list = (RecyclerView) findViewById(R.id.reply_list);
        reply_list.setHasFixedSize(true);
        reply_list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions commentsOption = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(commentsQuery, Comments.class).build();

        mCommentsAdapter = new FirebaseRecyclerAdapter<Comments, Reply.CommentsViewHolder>(commentsOption) {
            @Override
            protected void onBindViewHolder(@NonNull final Reply.CommentsViewHolder holder, int position, @NonNull Comments model) {

                holder.commentMessage.setText(model.getMessage());
                final String user_id = model.getUser_id().toString();
//                holder.setUser_id(model.getUser_id());

                final String postId = getRef(position).getKey();

                holder.commentLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)

                    {
                        CommentsLikeCheker = true;

                        RepliesLikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (CommentsLikeCheker.equals(true)) {

                                    if (dataSnapshot.child(postId).hasChild(mCurrent_user_id)) {
                                        RepliesLikesRef.child(postId).child(mCurrent_user_id).removeValue();
                                        CommentsLikeCheker = false;

                                    } else

                                    {
                                        RepliesLikesRef.child(postId).child(mCurrent_user_id).setValue(true);
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


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public Reply.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list, parent, false);

                return new Reply.CommentsViewHolder(view);
            }
        };
        reply_list.setAdapter(mCommentsAdapter);

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

        TextView user_id, commentMessage, name, specialty, lastSeenTime, CommentlikeNumber, CommentNumber;
        ImageView commentLike;
        CircularImageView user_comment_image;

        DatabaseReference RepliesLikesRef;
        String mCurrentUserID;
        int countLikes;


        public CommentsViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.comment_username);
            specialty = itemView.findViewById(R.id.comment_userSpecialty);
            commentMessage = itemView.findViewById(R.id.comment_message);
            user_comment_image = itemView.findViewById(R.id.comment_image);
            commentLike = itemView.findViewById(R.id.commentLike);
            CommentlikeNumber = itemView.findViewById(R.id.displaycommentsLikesNumber);

            RepliesLikesRef = FirebaseDatabase.getInstance().getReference().child("Reply_likes");
            mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        }

        public void setLikeButtonStatus(final String postid) {

            RepliesLikesRef.addValueEventListener(new ValueEventListener() {
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
    }


    private void post_comment(final String message) {

        final String postId = getIntent().getStringExtra("postId");


        mRepliesDatabase = FirebaseDatabase.getInstance().getReference().child("Comments_reply").child(postId).push();

        String current_user_id = firebaseAuth.getCurrentUser().getUid();

        Map<String, String> commentMap = new HashMap();
        commentMap.put("message", message);
        commentMap.put("type", "text");
        commentMap.put("user_id", current_user_id);

        mRepliesDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(Reply.this, "Your comment has been posted", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(Reply.this, "Oops,your comment was not succesfully posted,try again", Toast.LENGTH_SHORT).show();

                }

                reply_field.setText("");
            }
        });

//        } else {
//
//            Toast.makeText(comments_activity.this, "Try again", Toast.LENGTH_SHORT).show();
//        }


    }
}





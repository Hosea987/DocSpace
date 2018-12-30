package com.example.mountaineer.DocSpace;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CasesFragment extends Fragment {

    //    private FloatingActionMenu menuCase;
//    private FloatingActionButton questionFab;
    private FloatingActionButton caseFab;
    private View mMainCasesView;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView mCaseList;
    private List<CasePost> case_list;

    Context mContext;
    private DatabaseReference mCasesDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference LikesRef;
    private DatabaseReference CommentsRef;
    private FirebaseAuth firebaseAuth;

    private String mCurrent_user_id;

    Boolean LikeCheker = false;


//    public  CasesFragment newInstance (Context context) {
//        CasesFragment casesFragment = new CasesFragment();
//        this.mContext = context;
//
//        return casesFragment;
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainCasesView = inflater.inflate(R.layout.fragment_cases, container, false);
        // Inflate the layout for this fragment


        firebaseAuth = FirebaseAuth.getInstance();

        caseFab = (FloatingActionButton) mMainCasesView.findViewById(R.id.case_item);

        try {

            mCurrent_user_id = firebaseAuth.getCurrentUser().getUid();


        } catch (Exception e) {

            Log.d("Crash error", e.getMessage());

        }


        mCaseList = (RecyclerView) mMainCasesView.findViewById(R.id.case_list_view);
        mCaseList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    MainActivity.bottomNav.setVisibility(View.GONE);
                    caseFab.setVisibility(View.GONE);
//                    Toast.makeText(getContext(), "Scroll Down", Toast.LENGTH_SHORT).show();
                } else {

                    MainActivity.bottomNav.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "Scroll Up", Toast.LENGTH_SHORT).show();
                    caseFab.setVisibility(View.VISIBLE);

                }
            }
        });

//        menuCase = (FloatingActionMenu) mMainCasesView.findViewById(R.id.cases_menu);
//        questionFab = (FloatingActionButton) mMainCasesView.findViewById(R.id.question_item);

        mCasesDatabase = FirebaseDatabase.getInstance().getReference().child("Cases");
        mCasesDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");

        caseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newCaseIntent = new Intent(getContext(), NewCase.class);
                startActivity(newCaseIntent);

            }
        });


//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
//        mCaseList.setHasFixedSize(true);
//        mCaseList.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
// Set the layout manager to your recyclerview
        mCaseList.setLayoutManager(mLayoutManager);

//        mCaseList.setNestedScrollingEnabled(false);


        return mMainCasesView;
    }

    private FragmentTransaction getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

//
//        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("Cases");
//        Query postsQuery = personsRef.orderByChild("timestamp").limitToLast(20);
        mCasesDatabase.orderByChild("timestamp").limitToLast(20);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<CasePost>()
                .setQuery(mCasesDatabase, CasePost.class)
                .build();

        FirebaseRecyclerAdapter<CasePost, CasesFragment.CasesViewHolder> adapter = new FirebaseRecyclerAdapter<CasePost, CasesFragment.CasesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CasesFragment.CasesViewHolder holder, final int position, @NonNull CasePost model) {


                final String user_id = getRef(position).getKey();

                final String postId = getRef(position).getKey();
                mCasesDatabase.child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild("image_thumb")) {
                        final String image_url = dataSnapshot.child("image_url").getValue().toString();
                        final String desc = dataSnapshot.child("desc").getValue().toString();
                        final String title = dataSnapshot.child("title").getValue().toString();
                        final String time = dataSnapshot.child("timestamp").getValue().toString();
                        final String user_id = dataSnapshot.child("user_id").getValue().toString();


//                        holder.user_id.setText(user_id);
                        holder.desc.setText(desc);
                        holder.title.setText(title);


                        GetTimeAgo getTimeAgo = new GetTimeAgo();

                        long lastTime = Long.parseLong(time);

                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getActivity().getApplicationContext());

                        holder.lastSeenTime.setText(lastSeenTime);
                        holder.user_case_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getContext(), Profile.class);
                                intent.putExtra("user_id", user_id);
                                startActivity(intent);
                            }
                        });

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


//                        Picasso.with(Context).load(image_url).placeholder(R.drawable.default_user).into(holder.Image_url);
                        Glide.with(getActivity().getApplicationContext())
                                .load(image_url)
                                .into(holder.Image_url);


                        final String postId = getRef(position).getKey();


                        mUserDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String name = dataSnapshot.child("name").getValue().toString();
                                final String specialty = dataSnapshot.child("specialty").getValue().toString();
                                final String user_case_image = dataSnapshot.child("image").getValue().toString();

                                holder.name.setText(name);
                                holder.specialty.setText(specialty);
                                Glide.with(getActivity().getApplicationContext())
                                        .load(user_case_image).apply(new RequestOptions().placeholder(R.drawable.default_user).fitCenter())
                                        .into(holder.user_case_image);

                                holder.setLikeButtonStatus(postId);
                                holder.setCommentNumber(postId);

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent commentsIntent = new Intent(getContext(), comments_activity.class);
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


//

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });


            }

            @NonNull
            @Override
            public CasesFragment.CasesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.case_list_item, parent, false);

                CasesFragment.CasesViewHolder viewHolder = new CasesFragment.CasesViewHolder(view);

                return viewHolder;

            }
        };
        mCaseList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class CasesViewHolder extends RecyclerView.ViewHolder {

        TextView user_id, desc, title, name, specialty, lastSeenTime, likeNumber, CommentNumber;
        ImageView like;
        ImageView Image_url;

        int countLikes;
        int countComments;
        String mCurrentUserID;
        DatabaseReference LikesRef;
        DatabaseReference CommentsRef;
        FirebaseAuth firebaseAuth;
        CircularImageView user_case_image;

        public CasesViewHolder(View itemView) {
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
            firebaseAuth = FirebaseAuth.getInstance();

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


    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0);
    }


}




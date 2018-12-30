package com.example.mountaineer.DocSpace;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private FloatingActionMenu menuCase;
    private FloatingActionButton questionFab;
    private FloatingActionButton caseFab;
    private View mMainCasesView;
//    private LinearLayoutManager mLayoutManager;

    private RecyclerView mArticlesList;
    private List<CasePost> case_list;

    private DatabaseReference mArticlesDatabase;
    private DatabaseReference mUserDatabase;
//    private DatabaseReference LikesRef;
    private DatabaseReference CommentsRef;
    private FirebaseAuth firebaseAuth;

    private String user_id;

//    Boolean LikeCheker = false;


//    public FeedFragment() {
//
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainCasesView = inflater.inflate(R.layout.fragment_feed, container, false);
        // Inflate the layout for this fragment


        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getUid();

        mArticlesList = (RecyclerView) mMainCasesView.findViewById(R.id.feed_list);

        mArticlesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    MainActivity.bottomNav.setVisibility(View.GONE);
//                    caseFab.setVisibility(View.GONE);
//                    Toast.makeText(getContext(), "Scroll Down", Toast.LENGTH_SHORT).show();
                }else{

                    MainActivity.bottomNav.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "Scroll Up", Toast.LENGTH_SHORT).show();
//                    caseFab.setVisibility(View.VISIBLE);

                }
            }
        });

//        menuCase = (FloatingActionMenu) mMainCasesView.findViewById(R.id.cases_menu);
//        questionFab = (FloatingActionButton) mMainCasesView.findViewById(R.id.question_item);
//        caseFab = (FloatingActionButton) mMainCasesView.findViewById(R.id.case_item);

        mArticlesDatabase = FirebaseDatabase.getInstance().getReference().child("flamelink").child("environments").child("production").child("content").child("articles").child("en-US");
        mArticlesDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

//        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
//        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");

//        caseFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent newCaseIntent = new Intent(getContext(), NewCase.class);
//                startActivity(newCaseIntent);
//
//            }
//        });

//
//        mArticlesList.setHasFixedSize(true);
//        mArticlesList.setLayoutManager(new LinearLayoutManager(getContext()));
//        mArticlesList.setNestedScrollingEnabled(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
// Set the layout manager to your recyclerview
        mArticlesList.setLayoutManager(mLayoutManager);


        return mMainCasesView;
    }

    private FragmentTransaction getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ArticlePost>().setQuery(mArticlesDatabase, ArticlePost.class).build();


        FirebaseRecyclerAdapter<ArticlePost, FeedFragment.ArticlesViewHolder> adapter = new FirebaseRecyclerAdapter<ArticlePost, FeedFragment.ArticlesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FeedFragment.ArticlesViewHolder holder, final int position, @NonNull ArticlePost model) {




                final String articleId = getRef(position).getKey();
                mArticlesDatabase.child(articleId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild("image_thumb")) {
                        final String image_url = dataSnapshot.child("articleImageUrl").getValue().toString();
                        final String author = dataSnapshot.child("author").getValue().toString();
                        final String content = dataSnapshot.child("content").getValue().toString();
                        final String title = dataSnapshot.child("title").getValue().toString();


//                        holder.user_id.setText(user_id);
                        holder.author.setText(author);
//
                        holder.title.setText(title);
                        holder.contentSneek.setText(content);


                        Glide.with(getActivity().getApplicationContext())
                                .load(image_url)
                                .into(holder.ArticleImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent articlesIntent = new Intent(getContext(), ArticleActivity.class);
                                articlesIntent.putExtra("articleId", articleId);
                                articlesIntent.putExtra("ArticleImage", image_url);
                                articlesIntent.putExtra("author", author);
                                articlesIntent.putExtra("content", content);
                                articlesIntent.putExtra("title", title);
                                startActivity(articlesIntent);
                            }
                        });


//



                        final String postId = getRef(position).getKey();




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


            }

            @NonNull
            @Override
            public FeedFragment.ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_single_layout, parent, false);

                FeedFragment.ArticlesViewHolder viewHolder = new FeedFragment.ArticlesViewHolder(view);

                return viewHolder;

            }
        };
        mArticlesList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ArticlesViewHolder extends RecyclerView.ViewHolder {

        TextView author, content, name, title, contentSneek;
        ImageView ArticleImage;
//        ImageView Image_url;

//        int countLikes;
//        int countComments;
//        String currentUserId;
//        DatabaseReference LikesRef;
//        DatabaseReference CommentsRef;
//        CircularImageView user_case_image;

        public ArticlesViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.newsname);
            ArticleImage = itemView.findViewById(R.id.ArticleImage);
            name = itemView.findViewById(R.id.blog_desc);
            title = itemView.findViewById(R.id.ArticleTitle);
            contentSneek = itemView.findViewById(R.id.ArticleContentSneek);


//            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
//            CommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");
//
//            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

//        public void setLikeButtonStatus(final String postid)
//        {
//
//            LikesRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot)
//                {
//
//                    if (dataSnapshot.child(postid).hasChild(currentUserId))
//
//                    {
//
//                        countLikes = (int) dataSnapshot.child(postid).getChildrenCount();
//
//                        like.setImageResource(R.drawable.ic_whatshot_lit_24dp);
//                        likeNumber.setText(Integer.toString(countLikes) + ( " Likes"));
//
//
//                    }else{
//
//                        countLikes = (int) dataSnapshot.child(postid).getChildrenCount();
//
//                        like.setImageResource(R.drawable.ic_whatshot_black_24dp);
//                        likeNumber.setText(Integer.toString(countLikes) + (" Likes"));
//                    }
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


//        public void setCommentNumber(final String postid)
//        {
//
//            CommentsRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot)
//                {
//
//
//
//                    countComments= (int) dataSnapshot.child(postid).getChildrenCount();
//
//                    CommentNumber.setText(Integer.toString(countComments) + ( " Comments"));
//
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

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0);
    }


}








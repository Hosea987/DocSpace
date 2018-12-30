package com.example.mountaineer.DocSpace;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;

public class SearchDoctor extends AppCompatActivity {

//    private EditText mSearchField;
//    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;

    private LinearLayoutManager mLayoutManager;


    private FirebaseRecyclerAdapter<Users, SearchDoctor.NewsViewHolder> mPeopleRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");


        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Query personsQuery = personsRef.orderByKey();


//        mSearchField = (EditText) findViewById(R.id.search_field);
//        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.hasFixedSize();
        mResultList.setLayoutManager(new LinearLayoutManager(this));



//
//        mSearchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String searchText = mSearchField.getText().toString();
//
//                firebaseUserSearch(searchText);
//
//            }
//        });



//        private void firebaseUserSearch(String searchText) {
//
//            Toast.makeText(SearchDoctor.this, "Searching", Toast.LENGTH_LONG).show();
//
//            Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Users>().setQuery(personsQuery, Users.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<Users, SearchDoctor.NewsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(SearchDoctor.NewsViewHolder holder, int position, Users model) {

                holder.setTitle(model.getName());
                holder.setDesc(model.getSpecialty());
                holder.setImage(getBaseContext(), model.getImage());

                final String user_id = getRef(position).getKey();

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            final String url = model.getUrl();
                            Intent intent = new Intent(getApplicationContext(), Profile.class);
                            intent.putExtra("user_id", user_id);
                            startActivity(intent);
                        }
                    });

            }

            @NonNull
            @Override
            public SearchDoctor.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                return new SearchDoctor.NewsViewHolder(view);
            }
        };

        mResultList.setAdapter(mPeopleRVAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPeopleRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPeopleRVAdapter.stopListening();


    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public NewsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.name_text);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.status_text);
            post_desc.setText(desc);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = (CircularImageView) mView.findViewById(R.id.profile_image);
            Glide.with(ctx).load(image).into(post_image);
        }
    }
}


package com.example.mountaineer.DocSpace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

public class caseImageViewer extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;

    private float mScaleFactor = 1.0f;

    private ImageView mImageView;

    private DatabaseReference mCasesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_image_viewer);


        final String postId = getIntent().getStringExtra("postId");

        mImageView = (ImageView) findViewById(R.id.imageView);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        mCasesDatabase = FirebaseDatabase.getInstance().getReference().child("Cases").child(postId);
        mCasesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image_url = dataSnapshot.child("image_url").getValue().toString();
                final String desc = dataSnapshot.child("desc").getValue().toString();
                final String title = dataSnapshot.child("title").getValue().toString();
                final String time = dataSnapshot.child("timestamp").getValue().toString();
                final String user_id = dataSnapshot.child("user_id").getValue().toString();


                Glide.with(getApplicationContext())
                        .load(image_url)
                        .into(mImageView);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public boolean onTouchEvent(MotionEvent motionEvent) {

        mScaleGestureDetector.onTouchEvent(motionEvent);

        return true;

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            mScaleFactor *= scaleGestureDetector.getScaleFactor();

            mScaleFactor = Math.max(0.1f,

                    Math.min(mScaleFactor, 10.0f));

            mImageView.setScaleX(mScaleFactor);

            mImageView.setScaleY(mScaleFactor);

            return true;

        }
    }
}

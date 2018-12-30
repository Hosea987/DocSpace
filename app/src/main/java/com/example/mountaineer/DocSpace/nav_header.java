package com.example.mountaineer.DocSpace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class nav_header extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;

    private CircleImageView mNavImage;
    private TextView mNavName;
    private TextView mNavSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header);

        mNavImage = (CircleImageView) findViewById(R.id.navProfPic);
        mNavName = (TextView) findViewById(R.id.userNavName);
        mNavSpecialty = (TextView) findViewById(R.id.userNavSpecialty);


        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String specialty = dataSnapshot.child("specialty").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mNavName.setText(name);
                mNavSpecialty.setText(specialty);

                if(!image.equals("default")) {

                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                    Picasso.with(nav_header.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_user).into(mNavImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(nav_header.this).load(image).placeholder(R.drawable.default_user).into(mNavImage);

                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

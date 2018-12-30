package com.example.mountaineer.DocSpace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewCase extends AppCompatActivity {

    private Toolbar newPostToolbar;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    //Android Layout



    private ImageView newPostImage;
    private EditText newPostDesc;
    private EditText newPostTitle;
    private Button newPostBtn;

    private Uri postImageUri = null;

    private ProgressBar newPostProgress;

    private StorageReference mImageStorage;
    private DatabaseReference mCasesDatabase;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    private long countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_case);

        FirebaseApp.initializeApp(this);



        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCasesDatabase = FirebaseDatabase.getInstance().getReference().child("Cases").push();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Case");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        newPostToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostTitle = findViewById(R.id.new_post_title);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserDatabase.keepSynced(true);

//

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewCase.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String.valueOf(System.currentTimeMillis());

                mCasesDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists())

                        {

                            countPosts = dataSnapshot.getChildrenCount();

                        }
                        else

                            {

                                countPosts = 0 ;

                            }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final String desc = newPostDesc.getText().toString();
                final String title = newPostTitle.getText().toString();



                if(!TextUtils.isEmpty(desc) || (!TextUtils.isEmpty(title))){

                    newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = null;
                    try {

                        if(postImageUri!=null) {

                            newImageFile = new File(postImageUri.getPath());
                            compressedImageFile = new Compressor(NewCase.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] imageData = new byte[0];

                    if(postImageUri!=null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        imageData = baos.toByteArray();
                    }

                    // PHOTO UPLOAD

//
                    UploadTask filePath = mImageStorage.child("post_images").child(randomName + ".jpg").putBytes(imageData);
                    filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if(task.isSuccessful()){



                                File newThumbFile ;
                                try {

                                    if(postImageUri!=null) {
                                        newThumbFile = new File(postImageUri.getPath());

                                        compressedImageFile = new Compressor(NewCase.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                byte[] thumbData = new byte[0];

                                if(postImageUri!=null) {

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    thumbData = baos.toByteArray();
                                }



                                UploadTask uploadTask = mImageStorage.child("post_images/thumbs")
                                        .child(randomName + ".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        HashMap postMap = new HashMap<>();
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("image_thumb", downloadthumbUri);
                                        postMap.put("desc", desc);
                                        postMap.put("title", title);
                                        postMap.put("user_id", current_user_id);
                                        postMap.put("counter", countPosts);
                                        postMap.put("priority", "1");
                                        postMap.put("timestamp",  String.valueOf(System.currentTimeMillis()));


//


                                        mCasesDatabase.setValue(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){


                                                    Intent mainIntent = new Intent(NewCase.this, MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();

                                                }

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Error handling

                                    }
                                });


                            } else {

                                newPostProgress.setVisibility(View.INVISIBLE);

                            }

                        }
                    });


                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}

package com.example.mountaineer.DocSpace;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SpecialtyActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mSpecialty;
    private Button mSavebtn;
    private ProgressDialog mProgress;


    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialty);
        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

//        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Account Status");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value = getIntent().getStringExtra("status_value");

        mSpecialty = (TextInputLayout) findViewById(R.id.specialty_input);
        mSavebtn = (Button) findViewById(R.id.status_save_btn);

        mSpecialty.getEditText().setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Progress
                mProgress = new ProgressDialog(SpecialtyActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                String specialty = mSpecialty.getEditText().getText().toString();

                mStatusDatabase.child("specialty").setValue(specialty).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            mProgress.dismiss();

                        } else {

                            Toast.makeText(getApplicationContext(), "There was an error in saving your specialty.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });



    }
}

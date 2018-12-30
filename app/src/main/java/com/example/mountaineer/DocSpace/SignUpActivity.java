package com.example.mountaineer.DocSpace;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mProfession, mSpecialty, mOthers;
    private EditText mPassword;
    private Button mCreateBtn;
    private TextView mLogin;
    AlertDialog professionDialog;

    //ProgressDialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        // Android Fields

        mDisplayName = (EditText) findViewById(R.id.et_full_name);
        mEmail = (EditText) findViewById(R.id.et_email_address);
        mProfession = (EditText) findViewById(R.id.profession);
        mSpecialty =  findViewById(R.id.specialty);
        mOthers =  findViewById(R.id.other_profession);
        mPassword = (EditText) findViewById(R.id.et_password);
        mCreateBtn = (Button) findViewById(R.id.btn_signup);
        mLogin = (TextView) findViewById(R.id.loginText);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(reg_intent);


            }
        });

        mProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

//        mOthers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showOthers();
//            }
//        });


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String profession = mProfession.getText().toString();
                String others = mOthers.getText().toString();
                String specialty = mSpecialty.getText().toString();
                String password = mPassword.getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(profession) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(others) || !TextUtils.isEmpty(specialty)) {

                    mRegProgress.setTitle("Registering");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, others,specialty, profession, password);

                }


            }
        });


    }

    private void register_user(final String display_name, String email, final String profession, final String other, final String specialty,String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("license", profession);
                    userMap.put("specialty", specialty);
                    userMap.put("others", other);
                    userMap.put("board_number", "");
                    userMap.put("country", "");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                sendEmailVerificationMessage();
                                mRegProgress.dismiss();

//                                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
//                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(mainIntent);
//                                finish();

                            }

                        }
                    });


                } else {

                    mRegProgress.hide();
                    Toast.makeText(SignUpActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void showAlert() {

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);

        final CharSequence[] professions = {"Doctor", "Resident", "Medical student", "Clinical officer","Nurse", "Non medical"};

        myBuilder.setTitle("Profession").setItems(professions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(SignUpActivity.this, professions[which].toString(), Toast.LENGTH_SHORT).show();

                mProfession.setText(professions[which].toString());

//                if (professions[which].toString().equals("Doctor")|| professions[which].toString().equals("Resident")){
//
//                    Toast.makeText(SignUpActivity.this, professions[which].toString(), Toast.LENGTH_SHORT).show();
//                    mSpecialty.setVisibility(View.VISIBLE);
//                    mOthers.setVisibility(View.GONE);
//
//
//                }else{
//                    mSpecialty.setVisibility(View.GONE);
//                    mOthers.setVisibility(View.GONE);
//
//
//                }

//                if (professions[which].toString().equals("Other Healthcare profession")){
//
//                    Toast.makeText(SignUpActivity.this, professions[which].toString(), Toast.LENGTH_SHORT).show();
//                    mOthers.setVisibility(View.VISIBLE);
//
//
//                }

            }
        });

        professionDialog = myBuilder.create();
        professionDialog.show();
    }

    private void showOthers() {

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);

        final CharSequence[] professions = {"Clinical officer", "Nurse", "Nutritionist"};

        myBuilder.setTitle("Other Profession").setItems(professions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(SignUpActivity.this, professions[which].toString(), Toast.LENGTH_SHORT).show();

                mOthers.setText(professions[which].toString());



            }
        });

        professionDialog = myBuilder.create();
        professionDialog.show();
    }

    private void sendEmailVerificationMessage() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)

        {

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)

                {
                    if (task.isSuccessful())

                    {
                        Toast.makeText(SignUpActivity.this, "Registration successfull!Please chek your email to verify your account", Toast.LENGTH_SHORT).show();
                        sendUserToLogin();
                        mAuth.signOut();
                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(SignUpActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();

                    }

                }
            });
        }
    }

    private void sendUserToLogin()

    {
        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

}
package com.example.mountaineer.DocSpace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private ActionBar actionBar;
    public static BottomNavigationView bottomNav;

    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileName;
    private TextView NavSpecialty;

    private NavigationView navigationView;

    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("Cases");
        FirebaseMessaging.getInstance().subscribeToTopic("Alerts");
        FirebaseMessaging.getInstance().subscribeToTopic("Articles");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        mToolbar = (Toolbar) findViewById(R.id.appBarLayout);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        navigationView = (NavigationView)findViewById(R.id.nav_view);

        View navView = navigationView.inflateHeaderView(R.layout.nav_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.navProfPic);
        NavProfileName = (TextView) navView.findViewById(R.id.userNavName);
        NavSpecialty = (TextView) navView.findViewById(R.id.userNavSpecialty);



        mUserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)

            {

                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String specialty = dataSnapshot.child("specialty").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    NavProfileName.setText(name);
                    NavSpecialty.setText(specialty);
                    Glide.with(getApplicationContext())
                            .load(image)
                            .into(NavProfileImage);



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNav);

//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNav.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());
//        BottomNavigationViewHelper.enableShiftingMode(false);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    new CasesFragment()).commit();
        }


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_orange_24dp);


        //Auth

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_profile:
                Intent settingsIntent = new Intent(MainActivity.this, MyProfile.class);
                startActivity(settingsIntent);
                break;

            case R.id.nav_search:
                Intent searchIntent = new Intent(MainActivity.this, SearchDoctor.class);
                startActivity(searchIntent);
                break;
            case R.id.nav_saved:
                Intent savedIntent = new Intent(MainActivity.this, SavedArticles.class);
                startActivity(savedIntent);
                break;

            case R.id.nav_Share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "Check it out. Your message goes here";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Grow the community"));
                break;

            case R.id.nav_about:
                Intent AboutIntent = new Intent(MainActivity.this, About.class);
                startActivity(AboutIntent);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;


        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.action_item2:
                            selectedFragment = new CasesFragment();
                            break;
                        case R.id.action_item1:
                            selectedFragment = new FeedFragment();
                            break;
                        case R.id.action_item3:
                            selectedFragment = new MyNetworkFragment();
                            break;

                        case R.id.action_item4:
                            selectedFragment = new AlertsFragment();
                            break;

                        case R.id.action_item5:
                            selectedFragment = new MessagesFragment();
                            break;


                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                            selectedFragment).commit();


                    return true;


                }
            };

    boolean isNavigationHide = false;


    public void onStart() {

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();
        }

        super.onStart();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser != null) {
//
//            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
//
//        }
//


//    }


    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }


}

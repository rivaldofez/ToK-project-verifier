package com.example.setripverifier.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.setripverifier.R;
import com.example.setripverifier.fragment.CheckFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ActionBar actionBar;
    ChipNavigationBar chipNavigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionBar = getSupportActionBar();
        firebaseAuth = FirebaseAuth.getInstance();


        //home fragment transaction default on start
        actionBar.setTitle("Check Verifier");
        CheckFragment fragment2 = new CheckFragment();
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.content, fragment2, "");
        ft2.commit();

        chipNavigationBar = findViewById(R.id.nav_view);
        chipNavigationBar.setItemSelected(R.id.nav_check,true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.nav_check:
                        //check fragment
                        actionBar.setTitle("Check-In/Out");
                        CheckFragment fragment3 = new CheckFragment();
                        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, fragment3, "");
                        ft3.commit();
                        break;

//                case R.id.nav_profile:
//                    //profile fragment
//                    actionBar.setTitle(R.string.profil);
//                    ProfileFragment fragment4 = new ProfileFragment();
//                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
//                    ft4.replace(R.id.content, fragment4, "");
//                    ft4.commit();
//                    break;
                }
            }
        });

    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this, Login.class));
            finish();
        }
    }
}
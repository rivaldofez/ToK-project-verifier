package com.example.setripverifier.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.setripverifier.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spark.submitbutton.SubmitButton;

import java.util.HashMap;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhonenbr;
    private EditText etPassword;
    private EditText etAddress;
    private TextView tvNotifPassword;
    private Button register;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String Latitude ="0";
    private String Longitude ="0";

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_username_register);
        etEmail = findViewById(R.id.et_email_register);
        etPhonenbr = findViewById(R.id.et_phonenbr_register);
        etPassword = findViewById(R.id.et_password_register);
        etAddress = findViewById(R.id.et_address);
        register = findViewById(R.id.btn_register);
        tvNotifPassword = findViewById(R.id.tvPasswordNotification);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.title_register);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            Log.d("Test", Latitude);
            Log.d("Test", Longitude);
        }else{
            ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

    }

    public void register(View view) {
        AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.et_username_register,
                "[a-zA-Z\\s]+", R.string.invalid_name);
        awesomeValidation.addValidation(this, R.id.et_email_register,
                Patterns.EMAIL_ADDRESS,R.string.invalid_email);
        awesomeValidation.addValidation(this, R.id.et_phonenbr_register,
                ".{11,}", R.string.invalid_phonenbr);

        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();




        if(awesomeValidation.validate() && password.length() >= 6 && address.length() >=6 ) {
            showLoader(true);

            final String username = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            final String phoneNbr = etPhonenbr.getText().toString().trim();
            final String addresses = etAddress.getText().toString().trim();



            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                String email = user.getEmail();
                                String uid = user.getUid();


                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("username", username);
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("phone_nbr", phoneNbr);
                                hashMap.put("image", "");
                                hashMap.put("latitude",Latitude.toString());
                                hashMap.put("longitude",Longitude.toString());
                                hashMap.put("address", addresses);
                                hashMap.put("level", "verifier");

                                databaseReference = FirebaseDatabase.getInstance().getReference("Verifier");
                                databaseReference.child(uid).setValue(hashMap);

                                showLoader(false);
                                Toast.makeText(Register.this, R.string.berhasil, Toast.LENGTH_LONG).show();

                                startActivity(new Intent(Register.this, Login.class));
                                finish();

                            } else {
                                showLoader(false);
                                Toast.makeText(Register.this, R.string.trouble, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else if(password.length() < 6) {
            tvNotifPassword.setVisibility(View.VISIBLE);
            if(address.length() < 6){
                etAddress.setError("Alamat tidak valid");
            }
            showLoader(false);
        } else if(password.length() >=6){
            tvNotifPassword.setVisibility(View.GONE);
            showLoader(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void showLoader(boolean b) {
        if(b) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    private void getCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Double Lat,Long;
                if(location!=null){
                    Lat = location.getLatitude();
                    Long = location.getLongitude();
                }else{
                    Lat = 0.0;
                    Long = 0.0;
                }
                Latitude = Lat.toString();
                Longitude = Long.toString();
            }
        });
    }

}
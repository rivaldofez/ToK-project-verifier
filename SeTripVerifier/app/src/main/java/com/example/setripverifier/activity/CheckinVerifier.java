package com.example.setripverifier.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.setripverifier.R;
import com.example.setripverifier.model.TripModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class CheckinVerifier extends AppCompatActivity {

    private static final String TAG = "Test";
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    ToggleButton toggleState;
    String uid;
    String nameLokasi ;
    String latitude, longitude;

    FirebaseDatabase root;
    DatabaseReference reference;
    FirebaseUser verifier;
    FirebaseAuth firebaseAuth;

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_verifier);

        firebaseAuth = FirebaseAuth.getInstance();
        verifier = firebaseAuth.getCurrentUser();

        root = FirebaseDatabase.getInstance();
        DatabaseReference path = root.getReference("Verifier");
        Query query = path.orderByChild("email").equalTo(verifier.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    nameLokasi = "" + ds.child("username").getValue();
                    latitude = "" +ds.child("latitude").getValue();
                    longitude = ""+ds.child("longitude").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        scannerView = findViewById(R.id.checkinScanner);
        toggleState = findViewById(R.id.toggleState);

        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uid = result.getText();

                        if(toggleState.isChecked()){
                            checkIn("Reject",nameLokasi);
                        }else{
                            checkIn( "Allow", nameLokasi);
                        }
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(CheckinVerifier.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void checkIn(final String verifikasi, final String lokasi){
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("Trip");

        root = FirebaseDatabase.getInstance();
        DatabaseReference path = root.getReference().child("Trip").child(uid);

        Query query = path.orderByChild("status").equalTo("Checkin");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Toast.makeText(CheckinVerifier.this, "Anda belum checkout pada lokasi sebelumnya", Toast.LENGTH_SHORT).show();
                }else{
                    String inTime = String.valueOf(System.currentTimeMillis());
                    TripModel tripModel = new TripModel(inTime, "Not Yet", lokasi, "Checkin", uid,verifikasi,latitude,longitude);
                    reference.child(uid).child(inTime).setValue(tripModel);
                    Toast.makeText(CheckinVerifier.this, "Berhasil Checkin", Toast.LENGTH_SHORT).show();

                    root = FirebaseDatabase.getInstance();
                    DatabaseReference path = root.getReference().child("Trip").child(uid);
                    Query query = path.orderByChild("status").equalTo("Checkin");
                    query.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            };
        };

        query.addValueEventListener(valueEventListener);
    }
}
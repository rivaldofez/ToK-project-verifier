package com.example.setripverifier.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.setripverifier.R;
import com.example.setripverifier.model.TripModel;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

public class CheckoutVerifier extends AppCompatActivity {

    private static final String TAG = "Test";
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    String uid;

    FirebaseDatabase root;
    DatabaseReference reference;

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_verifier);

        scannerView = findViewById(R.id.checkoutScanner);

        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uid = result.getText();
                        checkout();
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
                Toast.makeText(CheckoutVerifier.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void checkout(){
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("Trip");

        root = FirebaseDatabase.getInstance();
        DatabaseReference path = root.getReference().child("Trip").child(uid);

        Query query = path.orderByChild("status").equalTo("Checkin");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                if (dataSnapshot.exists()){
                    String idChild="";

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        idChild = snapshot.child("inTime").getValue().toString();
                    }

//                    Log.d(TAG,"Nilai Idchild" + idChild);
                    String outTime = String.valueOf(System.currentTimeMillis());
                    HashMap hashMap = new HashMap();
                    hashMap.put("status","Checkout");
                    hashMap.put("outTime",outTime);
                    root = FirebaseDatabase.getInstance();
                    root.getReference().child("Trip").child(uid).child(idChild)
                            .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(CheckoutVerifier.this, "Berhasil Checkout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    root = FirebaseDatabase.getInstance();
                    DatabaseReference path = root.getReference().child("Trip").child(uid);
                    Query query = path.orderByChild("status").equalTo("Checkin");
                    query.removeEventListener(valueEventListener);

                }else{
                    Toast.makeText(CheckoutVerifier.this, "Ada belum checkin pada lokasi ini", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            };
        };

        query.addValueEventListener(valueEventListener);

    }
}
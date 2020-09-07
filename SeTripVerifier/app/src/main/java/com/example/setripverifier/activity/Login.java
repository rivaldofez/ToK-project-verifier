package com.example.setripverifier.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.setripverifier.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spark.submitbutton.SubmitButton;

public class Login extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private TextView tvNotifPassword;
    private SubmitButton login;
    private ProgressBar progressBar;

    AwesomeValidation awesomeValidation;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference database;
    DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if(firebaseUser != null){
            startActivity (new Intent(this, CheckinVerifier.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);
        tvNotifPassword = findViewById(R.id.tvPasswordNotification);
        progressBar = findViewById(R.id.progressBar);



    }

    public void login(View view) {
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        databaseReference = database.child("User");

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.et_email_login,
                Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        String password = etPassword.getText().toString().trim();


        if(awesomeValidation.validate() && password.length() >= 6) {
            showLoader(true);

            final String email = etEmail.getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                showLoader(false);
                                Toast.makeText(Login.this, R.string.trouble1, Toast.LENGTH_SHORT).show();
                            } else {
                                showLoader(false);
                                Toast.makeText(Login.this, R.string.welcome, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, CheckinVerifier.class));
                                finish();
                            }
                        }
                    });
        } else if(password.length() <6) {
            tvNotifPassword.setVisibility(View.VISIBLE);
        } else if(password.length() >=6) {
            tvNotifPassword.setVisibility(View.GONE);
        }
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, CheckinVerifier.class));
    }

    public void register(View view) {
        startActivity(new Intent(this, Register.class));
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.konfirmasi_keluar_aplikasi);
        builder.setIcon(R.drawable.ic_baseline_exit_to_app_24);
        builder.setMessage(R.string.apakah_anda_yakin);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.ya, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
                finish();
            }
        });

        builder.setNegativeButton(R.string.tidak, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showLoader(boolean b) {
        if(b) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
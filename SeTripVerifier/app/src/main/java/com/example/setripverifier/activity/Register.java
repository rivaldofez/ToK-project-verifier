package com.example.setripverifier.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhonenbr;
    private EditText etPassword;
    private TextView tvNotifPassword;
    private SubmitButton register;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_username_register);
        etEmail = findViewById(R.id.et_email_register);
        etPhonenbr = findViewById(R.id.et_phonenbr_register);
        etPassword = findViewById(R.id.et_password_register);
        register = findViewById(R.id.btn_register);
        tvNotifPassword = findViewById(R.id.tvPasswordNotification);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.title_register);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

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



        if(awesomeValidation.validate() && password.length() >= 6) {
            showLoader(true);

            final String username = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            final String phoneNbr = etPhonenbr.getText().toString().trim();



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
                                hashMap.put("level","verifier");

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
}
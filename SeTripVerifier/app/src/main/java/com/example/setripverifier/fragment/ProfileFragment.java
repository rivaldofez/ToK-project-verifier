package com.example.setripverifier.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//import com.example.setripverifier.DetectorActivity;
import com.example.setripverifier.R;
import com.example.setripverifier.activity.Login;
//import com.example.setripverifier.activity.MapsLocationActivity;
import com.example.setripverifier.activity.NoteReminderActivity;
import com.example.setripverifier.activity.SettingProfileActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    String uid;
    //for checking profile picture
    private String profile;

    //firebase
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Dialog dialog;
    Button btndismiss, btnConfNameNumber;
    EditText etChangeName, etChengeNumber;
    TextView tvKeteranganubahNamaNomor;

    //storage
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    //path where image will be created
    private String storagePath = "user_profile_imgs/";

    private ProgressBar progressBar;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String[] cameraPermission;
    String[] storagePermission;

    //uri of picked image
    Uri image_uri;

    private ImageView backgroundIv;
    private ImageView avatarIv;
    private TextView selamatTv;
    private TextView nameTv;
    private TextView emailTv;
    private TextView phoneNumber;
    private TextView detail;
    private TextView noteTv;
    private TextView smartCamera;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Verifier");
        storageReference = FirebaseStorage.getInstance().getReference();

        //init array of permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //TextView viewTv = view.findViewById(R.id.viewTv);
        selamatTv = view.findViewById(R.id.selamatTv);
        ImageView changeProfilePicture = view.findViewById(R.id.changePictureIv);
        backgroundIv = view.findViewById(R.id.backgroundIv);
        nameTv = view.findViewById(R.id.usernameTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneNumber = view.findViewById(R.id.phonenbrTv);
        noteTv = view.findViewById(R.id.noteTv);
        smartCamera = view.findViewById(R.id.viewCamera);
        progressBar = view.findViewById(R.id.progressBar);
        detail = view.findViewById(R.id.selamatBekerjaTv);
        avatarIv = view.findViewById(R.id.avatarIv);
        ImageView changeName = view.findViewById(R.id.changeName);
        ImageView changeNumber = view.findViewById(R.id.changeNumber);

        checkUserStatus();

        loadDataUser();


//        viewTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewAllTourismLocation();
//            }
//        });

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });

        noteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteTv();
            }
        });

        greetings();

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNamePhoneUpdateDialog("username");
            }
        });

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNamePhoneUpdateDialog("phone_nbr");
            }
        });

        return view;
    }

    private void showNamePhoneUpdateDialog(final String key) {
        dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(R.layout.dialog_ubah_nama_nomor);
        btnConfNameNumber = dialog.findViewById(R.id.btn_ubah);
        btndismiss = dialog.findViewById(R.id.btn_dismiss_ubah);
        etChangeName = dialog.findViewById(R.id.et_ubahnama);
        etChengeNumber = dialog.findViewById(R.id.et_ubahnomor);
        tvKeteranganubahNamaNomor = dialog.findViewById(R.id.tv_keterangan_ubahnamanomor);

        String tv = tvKeteranganubahNamaNomor.getText().toString().trim();

        if(key.equals("username")){
            etChengeNumber.setVisibility(View.GONE);
            tvKeteranganubahNamaNomor.setText(R.string.nama_lengkap_baru);
        }else{
            etChangeName.setVisibility(View.GONE);
            tvKeteranganubahNamaNomor.setText(R.string.nomor_handphone_baru);
        }

        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfNameNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etChangeName.getText().toString().trim();
                String number = etChengeNumber.getText().toString().trim();

                if(!name.isEmpty() || !number.isEmpty()) {
                    showLoader(true);
                    String value;

                    if(name.isEmpty()){
                        value = number;
                    }else {
                        value = name;
                    }

                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showLoader(false);
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), R.string.data_diperbarui, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showLoader(false);
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Mohon masukkan " + key + " dengan benar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void showNoteTv() {
        startActivity(new Intent(getActivity(), NoteReminderActivity.class));
    }

    private void loadDataUser() {
        showLoader(true);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getActivity() == null) {
                    return;
                }

                for (DataSnapshot ds: snapshot.getChildren()) {
                    //get data
                    String name = ""+ds.child("username").getValue();
                    String email = ""+ds.child("email").getValue();
                    String phone_nbr = ""+ds.child("phone_nbr").getValue();
                    String image = ""+ds.child("image").getValue();

                    //setData
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneNumber.setText(phone_nbr);


                    Glide.with(ProfileFragment.this).load(image)
                            .error(R.drawable.avatar_blank)
                            .placeholder(R.drawable.avatar_blank)
                            .into(avatarIv);

                    showLoader(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeProfilePic() {
        profile = "image";
        String[] options = {"Kamera", "Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pilih_mode);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0) {
                    //camera clicked
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    } else  {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //galeri clicked
                    if(!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }

            }
        });
        builder.create().show();
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pict");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri = Objects.requireNonNull(getActivity()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galeryIntent = new Intent(Intent.ACTION_PICK);
        galeryIntent.setType("image/*");
        startActivityForResult(galeryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case  CAMERA_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), R.string.mohon_setujui_kamera, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), R.string.mohon_setujui_galeri, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {

            if(requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery, get uri image
                assert data != null;
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }

            if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        // show proggress bar
        showLoader(true);

        //path and name of image ti be stored in firebase storage
        String filePathAndName = storagePath + ""+ profile + "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());
                        final Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and uri is received
                        if(uriTask.isSuccessful()) {
                            //image uploaded
                            //add / update uri in user's database
                            HashMap<String, Object> result = new HashMap<>();
                            result.put(profile, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showLoader(false);
                                            Toast.makeText(getActivity(), R.string.gambar_diperbarui, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showLoader(false);
                                    Toast.makeText(getActivity(), R.string.gambar_tidak, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            showLoader(false);
                            Toast.makeText(getActivity(), R.string.gangguan, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLoader(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void greetings() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay > 4 && timeOfDay < 12) {
            backgroundIv.setBackgroundResource(R.drawable.header_morning);
            selamatTv.setText(R.string.selamat_pagi);
        } else if(timeOfDay >= 12 && timeOfDay < 18)  {
            backgroundIv.setBackgroundResource(R.drawable.header_morning);
            selamatTv.setText(R.string.selamat_siang);
        } else if (timeOfDay > 18 || timeOfDay <4){
            backgroundIv.setBackgroundResource(R.drawable.header_night);
            selamatTv.setTextColor(Color.parseColor("#FFFFFF"));
            detail.setTextColor(Color.parseColor("#FFFFFF"));
            selamatTv.setText(R.string.selamat_malam);
        }
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            //user is signed in
            uid = user.getUid();
        } else {
            startActivity(new Intent(getActivity(), Login.class));
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);

        //hide add group icon
        menu.findItem(R.id.option_language).setVisible(false);
        menu.findItem(R.id.option_add).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.option_setting) {
            startActivity(new Intent(getActivity(), SettingProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoader(boolean b) {
        if(b) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
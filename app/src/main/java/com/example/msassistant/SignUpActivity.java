package com.example.msassistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msassistant.Model.RegistrationModel;
import com.example.msassistant.UIClass.FlashBar;
import com.example.msassistant.Util.Validator;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST=1;

    Uri mImageUri;
    ImageView profileImage;

    DatabaseReference mDatabaseReference;
    StorageReference mStorageReference;
    FirebaseAuth mAuth;
    StorageTask mStorageTask;

    private ProgressDialog progressDialog;

    Animation shake;

    EditText txtFirstName;
    EditText txtLastName;
    EditText txtPhoneNumber;
    EditText txtEmailAddress;
    EditText txtPassword;
    EditText txtConfirmPassword;

    Vibrator vibrator;
    FlashBar flashBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("userDetails");
        mStorageReference = FirebaseStorage.getInstance().getReference("userDetails");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setCanceledOnTouchOutside(false);

        profileImage = findViewById(R.id.uploaded_image);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        Button btn_next = findViewById(R.id.btn_next);
        TextView txtSignIn = findViewById(R.id.txtSignIn);

        shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake_edit_text);

        flashBar = new FlashBar(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(v);
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;

                if (Validator.checkEmpty(txtFirstName.getText().toString().trim())) {
                    txtFirstName.startAnimation(shake);
                    txtFirstName.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtFirstName.setHint("Please enter your first name!");
                    flag += 1;
                }else{
                    txtFirstName.clearAnimation();
                }

                if (Validator.checkEmpty(txtLastName.getText().toString().trim())) {
                    txtLastName.startAnimation(shake);
                    txtLastName.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtLastName.setHint("Please enter your last name!");
                    flag += 1;
                }else{
                    txtLastName.clearAnimation();
                }

                if (Validator.checkEmpty(txtPhoneNumber.getText().toString().trim())) {
                    txtPhoneNumber.startAnimation(shake);
                    txtPhoneNumber.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtPhoneNumber.setHint("Please enter your phone number!");
                }else{
                    if (!Validator.textLength(txtPhoneNumber.getText().toString().trim(), 10)) {
                        txtPhoneNumber.startAnimation(shake);
                        txtPhoneNumber.setText("");
                        txtPhoneNumber.setHintTextColor(getResources().getColor(R.color.flashDanger));
                        txtPhoneNumber.setHint("Valid phone number must have 10 digits only!");
                        flag += 1;
                    }else{
                        txtPhoneNumber.clearAnimation();
                    }
                }

                if (Validator.checkEmpty(txtEmailAddress.getText().toString().trim())) {
                    txtEmailAddress.startAnimation(shake);
                    txtEmailAddress.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtEmailAddress.setHint("Please enter your e-Mail address!");
                    flag += 1;
                }else{
                    if (!Validator.validateEmail(txtEmailAddress.getText().toString().trim())) {
                        txtEmailAddress.startAnimation(shake);
                        txtEmailAddress.setText("");
                        txtEmailAddress.setHintTextColor(getResources().getColor(R.color.flashDanger));
                        txtEmailAddress.setHint("Please enter a valid e-Mail address!");
                        flag += 1;
                    }else{
                        txtEmailAddress.clearAnimation();
                    }
                }

                if (Validator.checkEmpty(txtPassword.getText().toString().trim())) {
                    txtPassword.startAnimation(shake);
                    txtPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtPassword.setHint("Please enter your password!");
                    flag += 1;
                }else{
                    if (!Validator.textLength(txtPassword.getText().toString().trim(), 6)) {
                        txtPassword.startAnimation(shake);
                        txtPassword.setText("");
                        txtPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                        txtPassword.setHint("Password must have 6 digits only!");
                        flag += 1;
                    }else{
                        if (!Validator.checkTwoSame(txtConfirmPassword.getText().toString().trim(), txtPassword.getText().toString().trim())) {

                            txtPassword.setText("");
                            txtConfirmPassword.setText("");

                            txtPassword.startAnimation(shake);
                            txtPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                            txtPassword.setHint("Password and confirm password is not matching!");

                            txtConfirmPassword.startAnimation(shake);
                            txtConfirmPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                            txtConfirmPassword.setHint("Password and confirm password is not matching!");
                            flag += 1;
                        }else{
                            txtPassword.clearAnimation();
                            txtConfirmPassword.clearAnimation();
                        }
                    }
                }

                if (Validator.checkEmpty(txtConfirmPassword.getText().toString().trim())) {
                    txtConfirmPassword.startAnimation(shake);
                    txtConfirmPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtConfirmPassword.setHint("Please enter your confirm password!");
                    flag += 1;
                }else{
                    if (!Validator.textLength(txtConfirmPassword.getText().toString().trim(), 6)) {
                        txtConfirmPassword.startAnimation(shake);
                        txtConfirmPassword.setText("");
                        txtConfirmPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                        txtConfirmPassword.setHint("Password must have 6 digits only!");
                        flag += 1;
                    }else{
                        if (!Validator.checkTwoSame(txtConfirmPassword.getText().toString().trim(), txtPassword.getText().toString().trim())) {

                            txtPassword.setText("");
                            txtConfirmPassword.setText("");

                            txtPassword.startAnimation(shake);
                            txtPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                            txtPassword.setHint("Password and confirm password is not matching!");

                            txtConfirmPassword.startAnimation(shake);
                            txtConfirmPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                            txtConfirmPassword.setHint("Password and confirm password is not matching!");

                            flag += 1;
                        }else{
                            txtPassword.clearAnimation();
                            txtConfirmPassword.clearAnimation();
                        }
                    }
                }

                if(flag == 0){
                    progressDialog.show();
                    uploadFile();

                } else {
                    vibrator.vibrate(10);
                }
            }
        });

    }

    public void openFileChooser(View view) {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(profileImage);
        }
    }

    public void uploadFile(){
        if(mImageUri !=null){
            final StorageReference fileReference = mStorageReference.child(txtEmailAddress.getText().toString().trim());

            profileImage.setDrawingCacheEnabled(true);
            profileImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = fileReference.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(SignUpActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("ImageUpload","Image uploaded!");
                }
            });

            uploadTask = fileReference.putFile(mImageUri);
            mStorageTask = fileReference.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        createProfile(downloadUri);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "fail to set download link", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            flashBar.alertDanger("Please select a profile picture!");
            vibrator.vibrate(10);
            profileImage.setAnimation(shake);
        }

    }

    public void createProfile(Uri uri) {
        final Uri url = uri;
        mAuth.createUserWithEmailAndPassword(txtEmailAddress.getText().toString().trim(), txtPassword.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    updateProfile(url, user);

                } else {
                    progressDialog.dismiss();
                    flashBar.alertDanger(task.getException().getLocalizedMessage());
                }
            }
        });

    }

    public void updateProfile(Uri uri, final FirebaseUser user){
        final Uri URL = uri;
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(txtFirstName.getText().toString().trim()+" "+txtLastName.getText().toString().trim())
                .setPhotoUri(URL)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveUserData(URL.toString(), user.getUid());
                }
            }
        });
    }

    public  void saveUserData(String url, String uid){

        RegistrationModel userReg = new RegistrationModel(
                txtFirstName.getText().toString().trim(),
                txtLastName.getText().toString().trim(),
                txtEmailAddress.getText().toString().trim(),
                url,
                Integer.parseInt(txtPhoneNumber.getText().toString().trim()),
                uid
        );

        mDatabaseReference.child(uid).setValue(userReg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    flashBar.alertSuccess("Your account created successfully!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        }
                    },2500);
                }else {
                    progressDialog.dismiss();
                    flashBar.alertWarning("Operation failed!");
                }
            }
        });
    }
}
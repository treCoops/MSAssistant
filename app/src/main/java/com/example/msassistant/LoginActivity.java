package com.example.msassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.msassistant.UIClass.FlashBar;
import com.example.msassistant.Util.Validator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";
    private FirebaseAuth mAuth;

    EditText txtEmailAddress;
    EditText txtPassword;

    Animation shake;
    Vibrator vibrator;
    FlashBar flashBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.signInButton);
        TextView signUp = findViewById(R.id.signUp);
        Button btn_local_login = findViewById(R.id.btn_local_login);
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("Login with Google");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setCanceledOnTouchOutside(false);

        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        txtPassword = findViewById(R.id.txtPassword);

        shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake_edit_text);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        flashBar = new FlashBar(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btn_local_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;

                txtEmailAddress.clearAnimation();
                txtPassword.clearAnimation();

                if (Validator.checkEmpty(txtEmailAddress.getText().toString().trim())) {
                    txtEmailAddress.startAnimation(shake);
                    txtEmailAddress.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtEmailAddress.setHint("Please enter your e-Mail address!");
                    flag += 1;
                }else{
                    if (!Validator.validateEmail(txtEmailAddress.getText().toString().trim())) {
                        txtEmailAddress.setText("");
                        txtEmailAddress.startAnimation(shake);
                        txtEmailAddress.setHintTextColor(getResources().getColor(R.color.flashDanger));
                        txtEmailAddress.setHint("Please enter a valid e-Mail address!");
                        flag += 1;
                    }
                }

                if (Validator.checkEmpty(txtPassword.getText().toString().trim())) {
                    txtPassword.startAnimation(shake);
                    txtPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                    txtPassword.setHint("Please enter your password!");
                    flag += 1;
                }else {
                    if (!Validator.textLength(txtPassword.getText().toString().trim(), 6)) {
                        txtPassword.startAnimation(shake);
                        txtPassword.setText("");
                        txtPassword.setHintTextColor(getResources().getColor(R.color.flashDanger));
                        txtPassword.setHint("Password must have 6 digits only!");
                        flag += 1;
                    }
                }

                if (flag == 0){
                    signInWithEmailAndPassword();
                }else{
                    vibrator.vibrate(10);
                }
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        saveUserData(currentUser);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            saveUserData(mAuth.getCurrentUser());
                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            flashBar.alertWarning("Authentication failed!");
                            saveUserData(null);
                        }
                    }
                });
    }

    public void saveUserData(FirebaseUser firebaseUser){

        if(firebaseUser !=null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    public void signInWithEmailAndPassword() {
        mAuth.signInWithEmailAndPassword(txtEmailAddress.getText().toString().trim(), txtPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            saveUserData(user);


                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            flashBar.alertWarning("Authentication failed!");
                            saveUserData(null);
                        }
                    }
                });
    }
}
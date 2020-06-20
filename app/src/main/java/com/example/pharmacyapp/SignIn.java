package com.example.pharmacyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class SignIn extends AppCompatActivity implements TextWatcher {

    private static final int RC_SIGN_IN = 123;
    EditText password, email;
    TextView signUp;
    ConstraintLayout signInWithGmail, singInWithPhone;
    Button signIn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setViews();

        password.addTextChangedListener(this);
        email.addTextChangedListener(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent creatacount = new Intent(SignIn.this, SignUp.class);
                startActivity(creatacount);
            }
        });


        signInWithGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String spassword , semail ;
                semail= email.getText().toString().trim() ;
                spassword = password.getText().toString();
                verefications();

                if (verefications())
                {
                    singin(semail,spassword);
                }

            }
        });
    }

    private void setViews() {
        password = findViewById(R.id.sign_in_password);
        email = findViewById(R.id.sign_in_email);
        signUp = findViewById(R.id.sign_up);
        signInWithGmail = findViewById(R.id.sig_in_google);
        singInWithPhone = findViewById(R.id.sign_in_phone);
        signIn = findViewById(R.id.sign_in_button);
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        //thab tzid them hna zid
                        .setAvailableProviders(
                                Arrays.asList( //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.google_plus)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        signIn.setEnabled(!password.getText().toString().trim().isEmpty()
                && !email.getText().toString().trim().isEmpty());

    }

    public void singin(String email1, String password1) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //updatedata();
                    if (userisverified())
                    {
                        Intent lunchmainactfromsignin = new Intent(SignIn.this, HomeActivity.class);
                        lunchmainactfromsignin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(lunchmainactfromsignin);
                    }else
                    {
                        Toast.makeText(SignIn.this, "verify your email", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignIn.this, task.getException().getMessage().toLowerCase(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean verefications() {
        boolean result = true;
        if (password.getText().toString().trim().length() < 8) {
            password.setError("password is to short");
            password.requestFocus();
            result = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.setError("this is not an email address");
            // cette ligne a verifier manich 3aref wech tdir jusqu'a present
            email.requestFocus();
            result = false;
        }
        return result ;

    }

    private boolean userisverified()
    {
        return firebaseAuth.getCurrentUser().isEmailVerified();
    }

}

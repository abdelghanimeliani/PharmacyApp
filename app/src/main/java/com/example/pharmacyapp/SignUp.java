package com.example.pharmacyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pharmacyapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity implements TextWatcher {

   private EditText name , password , email ,verifyPassword ;
   private Button signup ;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore ;
    DocumentReference userdata;
    CollectionReference userscollection;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setViews();

        password.addTextChangedListener(this);
        name.addTextChangedListener(this);
        verifyPassword.addTextChangedListener(this);
        email.addTextChangedListener(this);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                verefications();

                if (verefications())
                {
                    String semail = email.getText().toString().trim();
                    String spasword = password.getText().toString().trim();


                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(semail, spasword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {

                                // updatedata();
                                String sname = name.getText().toString().trim();
                                User newuser = new User(sname);
                                firebaseFirestore= FirebaseFirestore.getInstance();
                                userscollection = firebaseFirestore.collection("useres");
                                userdata = userscollection.document(firebaseAuth.getCurrentUser().getUid());
                                userdata.set(newuser);

                                // pour lancer l'aure act
                              sentUserVerification();
                            }
                            else {
                                Toast.makeText(SignUp.this,task.getException().getMessage()+".", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
    }

    private void setViews()
    {
        name= findViewById(R.id.sign_up_name);
        password=findViewById(R.id.sign_up_password);
        verifyPassword=findViewById(R.id.sign_up_verify_password);
        email=findViewById(R.id.sign_up_email);
        signup=findViewById(R.id.sign_up_button);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        signup.setEnabled(!password.getText().toString().trim().isEmpty()
                && !email.getText().toString().trim().isEmpty()
                && !name.getText().toString().trim().isEmpty()
                && !verifyPassword.getText().toString().trim().isEmpty());
    }

    public boolean verefications()
    {
        boolean result ;
        result=true;
        if (password.getText().toString().length() < 8) {
            password.setError("password is to short");
            password.requestFocus();

            result=false ;
        }

        if (!password.getText().toString().equals(verifyPassword.getText().toString()))
        {
            password.setError("passwords are not equals");
            verifyPassword.setError("passwords ae not equals");
            password.requestFocus();
            verifyPassword.requestFocus();
            result=false ;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
        {
            email.setError("this is not an email address");
            // cette ligne a verifier manich 3aref wech tdir jusqu'a present
            email.requestFocus();
            result=false;
        }
        if (name.getText().toString().isEmpty())
        {
            name.setError("password is to short");
            name.requestFocus();
            result=false;
        }
        return result;
    }

    private void sentUserVerification()
    {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                        Intent returntosingin = new Intent(SignUp.this, SignIn.class);
                    returntosingin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(returntosingin);

                }else {
                    Toast.makeText(SignUp.this,task.getException().getMessage()+".", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText, passEditText;
    private Button logInButton, resendButton;
    private TextView textView, verification;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        logInButton = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView6);
        emailEditText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passEditText = (EditText) findViewById(R.id.editTextTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        verification = (TextView) findViewById(R.id.verMessage);
        resendButton = (Button) findViewById(R.id.resendButton);

        textView.setOnClickListener(this);
        logInButton.setOnClickListener(this);

        //getSupportActionBar().hide();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.textView6){
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.button) userLogIn();
    }

    private void userLogIn() {

        String email = emailEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Enter an email address");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passEditText.setError("Enter a password");
            passEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passEditText.setError("Password length should be 6 or higher");
            passEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    verification.setVisibility(View.VISIBLE);
                                    resendButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, "Please Verify your email first",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else
                            Toast.makeText(LoginActivity.this, "LogIn Unsuccessful. " +
                                    "Recheck your email and password", Toast.LENGTH_LONG).show();
                    }
                });

    }


    public void resendEmail(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(LoginActivity.this,"Verification email has been sent", Toast.LENGTH_LONG).show();
                    verification.setVisibility(View.GONE);
                    resendButton.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this,e.getMessage() + "\nTry Again!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
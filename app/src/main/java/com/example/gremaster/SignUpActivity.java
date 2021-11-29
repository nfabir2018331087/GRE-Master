package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText, passEditText, nameText;
    private RadioGroup radioGroup;
    private RadioButton selButton;
    private Button signUpButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        emailEditText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passEditText = (EditText) findViewById(R.id.editTextTextPassword);
        nameText = (EditText) findViewById(R.id.editTextTextPersonName);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
       // selButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        signUpButton = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) userRegister();
    }

    private void userRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();
        String name = nameText.getText().toString().trim();
      //  String expert = selButton.getText().toString().trim();

        if(name.isEmpty()) {
            nameText.setError("Enter your name");
            nameText.requestFocus();
            return;
        }
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
        /*if (radioGroup.getCheckedRadioButtonId()==0) {
            Toast.makeText(this, "Select any option", Toast.LENGTH_SHORT).show();
            return;
        }*/
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,"Account created successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "You already have an account", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent1);
                            }
                            else Toast.makeText(SignUpActivity.this,
                                    "Error: "+task.getException().getMessage()+" Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
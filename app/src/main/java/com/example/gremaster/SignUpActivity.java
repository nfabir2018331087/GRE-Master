package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static boolean x;
    private EditText emailEditText, passEditText, nameText, userNameText;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;
    private Button signUpButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

        //getSupportActionBar().hide();

        emailEditText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passEditText = (EditText) findViewById(R.id.editTextTextPassword);
        nameText = (EditText) findViewById(R.id.editTextTextPersonName);
        userNameText = (EditText) findViewById(R.id.editTextUser);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        signUpButton = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            userRegister();
            //saveData();
        }
    }


    private void userRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();
        String name = nameText.getText().toString().trim();
        String username = userNameText.getText().toString().trim();
        String whiteSpaces = "\\A\\w{4,20}\\z";
      //  String expert = selButton.getText().toString().trim();

        if(name.isEmpty()) {
            nameText.setError("Enter your name");
            nameText.requestFocus();
            return;
        }
        if(username.isEmpty()){
            userNameText.setError("Set up a username");
            userNameText.requestFocus();
            return;
        }
        if(username.length()<3 || username.length()>15){
            userNameText.setError("Username length should be between 3 to 15");
            userNameText.requestFocus();
            return;
        }
        if(!username.matches(whiteSpaces)){
            userNameText.setError("Username shouldn't have whitespaces");
            userNameText.requestFocus();
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
        if (!radioButton1.isChecked() && !radioButton2.isChecked()) {
            Toast.makeText(this, "One field is empty! Select any option", Toast.LENGTH_SHORT).show();
            radioGroup.requestFocus();
            return;
        }
        /*if(checkValue()){
            System.out.println("hello there!");
            return;
        }*/
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignUpActivity.this, "Account created successfully" +
                                                "\nVerification email has been sent", Toast.LENGTH_LONG).show();
                                        saveData();
                                        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this,e.getMessage() + "\nTry Again!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "You already have an account", Toast.LENGTH_LONG).show();
                                Intent intent1 = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent1);
                            }
                            else Toast.makeText(SignUpActivity.this,
                                    "Error: "+task.getException().getMessage()+" Try Again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void saveData(){
        String name = nameText.getText().toString().trim();
        String username = userNameText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();
        String expert = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString().trim();

        String userId = mAuth.getCurrentUser().getUid();

        StoreData storeData = new StoreData(name,username,email,password,expert);

        reference.child(userId).setValue(storeData);
    }


}
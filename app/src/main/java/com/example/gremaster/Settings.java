package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//changing name and password and deleting account
public class Settings extends AppCompatActivity {

    EditText newName, currentName, newPass, currentPass;
    Button saveNameBtn, savePassBtn, delBtn;
    ProgressDialog loadingBar;
    String currentUserID, currentUserName, currentUserPassword;

    FirebaseAuth mAuth;
    DatabaseReference reference;
    //StoreData storeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");

        loadingBar = new ProgressDialog(this);
        newName = findViewById(R.id.newName);
        currentName = findViewById(R.id.currentName);
        newPass = findViewById(R.id.newPassword);
        currentPass = findViewById(R.id.currentPassword);
        saveNameBtn = findViewById(R.id.saveName);
        savePassBtn = findViewById(R.id.savePass);
        delBtn = findViewById(R.id.deleteAC);

        //setting on click listener on the delete button
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogue while delete account is clicked
                AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your " +
                        "account from the app and you won't able to access the app!");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference deUsers = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
                        deUsers.removeValue();
                        if(currentUser!=null) {
                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //after deletion of the account going back to login page
                                        Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();;
            }
        });

        //getting user value
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentUserName = snapshot.child(currentUserID).child("name").getValue(String.class);
                    currentUserPassword = snapshot.child(currentUserID).child("password").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //when save changes is clicked
    public void saveName(View view) {
        loadingBar.setMessage("Updating.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        String cName = currentName.getText().toString();
        String nName = newName.getText().toString();

        if(cName.contains(currentUserName)&&!cName.isEmpty()){
            if(!TextUtils.isEmpty(nName)){
                loadingBar.dismiss();
                //changing the name
                reference.child(currentUserID).child("name").setValue(nName);
                Toast.makeText(getApplicationContext(),"Name Changed Successfully",Toast.LENGTH_SHORT).show();
                currentName.setText("");
                newName.setText("");

            }else{
                loadingBar.dismiss();
                newName.setError("Please Enter a new Name");
                currentName.requestFocus();
            }

        }else{
            loadingBar.dismiss();
            currentName.setError("Please Enter Your Name Correctly");
            currentName.requestFocus();
        }


    }

    //when save changes clicked after the password part
    public void savePassword(View view) {
        loadingBar.setMessage("Updating.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        String cPass = currentPass.getText().toString();
        String nPass = newPass.getText().toString();

        if(cPass.contains(currentUserPassword)&&!cPass.isEmpty()){
            if(!nPass.isEmpty()){
                if(nPass.length()>=6) {
                    loadingBar.dismiss();
                    reference.child(currentUserID).child("password").setValue(nPass);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider
                            .getCredential("user@example.com", "password1234");

                    // Prompt the user to re-provide their sign-in credentials
                    if(user!=null) {
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(nPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Password Change Unsuccessful", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                        }
                                    }
                                });
                    }
                    currentPass.setText("");
                    newPass.setText("");
                }else{
                    loadingBar.dismiss();
                    newName.setError("Password Length should be 6 or higher");
                    currentName.requestFocus();
                }
            }else{
                loadingBar.dismiss();
                newPass.setError("Please Enter a new Password");
                newPass.requestFocus();
            }

        }else{
            loadingBar.dismiss();
            currentPass.setError("Please Enter Your Password Correctly");
            currentPass.requestFocus();
        }

    }


}
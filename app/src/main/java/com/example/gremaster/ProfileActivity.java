package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    StorageReference storageRef;
    DatabaseReference reference;
    FirebaseUser currentUser;
    String currentUid;
    ImageView pImage;
    TextView pName,pUsername,pEmail,pPassword,pStatus;
    final static int GALLERY_PICK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pImage = findViewById(R.id.profileImage);
        pName = findViewById(R.id.profileName);
        pUsername = findViewById(R.id.profileUserName);
        pEmail = findViewById(R.id.profileEmail);
        pPassword = findViewById(R.id.profilePass);
        pStatus = findViewById(R.id.greStatus);

        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
             currentUid = currentUser.getUid();
        }
        reference = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseStorage.getInstance().getReference("profile images");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(currentUid!=null) {
                        String name = snapshot.child(currentUid).child("name").getValue(String.class);
                        String expert = snapshot.child(currentUid).child("expert").getValue(String.class);
                        String username = snapshot.child(currentUid).child("username").getValue(String.class);
                        String email = snapshot.child(currentUid).child("email").getValue(String.class);
                        String password = snapshot.child(currentUid).child("password").getValue(String.class);
                        String image = snapshot.child(currentUid).child("profileimage").getValue(String.class);

                        if(expert.contains("Yes")) pStatus.setText("Expert");
                        else pStatus.setText("Not Expert");
                        pName.setText(name);
                        pUsername.setText(username);
                        pEmail.setText(email);
                        pPassword.setText(password);
                        Picasso.get().load(image).placeholder(R.drawable.default2).into(pImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            StorageReference filePath = storageRef.child(currentUid + "." + getFileExtension(imageUri));

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child(currentUid).child("profileimage").setValue(uri.toString());
                        }
                    });
                }
            });

        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
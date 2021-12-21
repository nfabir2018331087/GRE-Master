package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Instrumentation;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final static int GALLERY_PICK = 1;
    String userId, email;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView navName, navStatus;
    ImageView navImage;

    FirebaseAuth mAuth;
    DatabaseReference reference;
    StorageReference storageRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseMessaging.getInstance().subscribeToTopic("Vocabulary");

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile images");
        user = mAuth.getCurrentUser();
        if(user!=null) userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");


        loadData();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);
        View navHeader = getLayoutInflater().inflate(R.layout.nav_header,navigationView);
        navName = (TextView) navHeader.findViewById(R.id.textView9);
        navStatus = (TextView) navHeader.findViewById(R.id.textView10);
        navImage =(ImageView) navHeader.findViewById(R.id.navImage);

        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().hide();

        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            StorageReference filePath = storageRef.child(userId + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        if(userId!=null){
                            String url = "https://firebasestorage.googleapis.com/v0/b/gre-master-18666.appspot.com/o/profile%20images%2FHwzmde6MemNbRNeHmYjIVOmOVvr2.jpg?alt=media&token=14dad47d-043b-4c74-a7e2-8e13ee2f85fc";
                            reference.child(userId).child("profileimage").setValue(url);
                        }
                    }
                }
            });

        }
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void loadData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(userId!=null) {
                        String name = snapshot.child(userId).child("name").getValue(String.class);
                        String expert = snapshot.child(userId).child("expert").getValue(String.class);
                        String image = snapshot.child(userId).child("profileimage").getValue(String.class);

                        navName.setText(name);
                        navStatus.setText("GRE Expert: " + expert);
                        Picasso.get().load(image).placeholder(R.drawable.default2).into(navImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void quizButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,VocabularyQuiz.class);
        startActivity(intent);
    }
}
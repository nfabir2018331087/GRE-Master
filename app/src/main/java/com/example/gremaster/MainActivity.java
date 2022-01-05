package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    boolean isVerified;
    final static int GALLERY_PICK = 1;
    static int vMarks, mMarks;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_teal, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.teal_700));
        }

        //Vocabulary notification send
        FirebaseMessaging.getInstance().subscribeToTopic("Vocabulary")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to daily vocabulary";
                        if (!task.isSuccessful()) {
                            msg = "Subscribing to daily vocabulary failed";
                        }
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile images");
        user = mAuth.getCurrentUser();
        if(user!=null) {
            userId = user.getUid();
            isVerified = user.isEmailVerified();
        }
        reference = FirebaseDatabase.getInstance().getReference("users");


        loadData();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);
        View navHeader = getLayoutInflater().inflate(R.layout.nav_header,navigationView);
        navName = (TextView) navHeader.findViewById(R.id.textView9);
        navStatus = (TextView) navHeader.findViewById(R.id.textView10);
        navImage =(ImageView) navHeader.findViewById(R.id.navImage);

        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().hide();

        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) isVerified = user.isEmailVerified();
        if(user==null || !isVerified)
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
                        if(expert!=null) {
                            if (expert.contains("Yes")) navStatus.setText("Expert");
                            else navStatus.setText("Learner");
                        }
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
        }
        if(item.getItemId()==R.id.profile){
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.home){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId()==R.id.share){
            onInviteClicked();
        }
        if(item.getItemId()==R.id.settings){
            Intent intent = new Intent(getApplicationContext(),Settings.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    private void onInviteClicked(){
        Intent intent=new AppInviteInvitation.IntentBuilder("GRE Master Invite")
                .setMessage("Prepare for GRE with GRE Master")
                .setDeepLink(Uri.parse("http://google.com"))
                .setCallToActionText("Invitation")
                .build();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==RESULT_OK && data!=null){
                String[] ids= AppInviteInvitation.getInvitationIds(resultCode,data);
                for(String id:ids)
                {
                    Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void goToQuiz1(View view) {
        Intent intent = new Intent(MainActivity.this,VocabularyQuiz.class);
        startActivity(intent);
    }

    public void goToQuiz2(View view) {
        Intent intent = new Intent(MainActivity.this,MathQuiz.class);
        startActivity(intent);
    }

    public void goToForum(View view) {
        Intent intent = new Intent(getApplicationContext(),Forum.class);
        startActivity(intent);
    }

    public void openLeaderboard(View view) {
        Intent intent = new Intent(getApplicationContext(), Leaderboard.class);
        startActivity(intent);
    }

    public void goToResource(View view) {
        Intent intent = new Intent(getApplicationContext(), ResourceOne.class);
        startActivity(intent);
    }

}
package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

//forum class where all the questions are shown
public class Forum extends AppCompatActivity implements View.OnClickListener {

    public static Activity activity;
    FloatingActionButton addButton;
    RecyclerView questionsList;
    MyAdapter adapter;
    String currentUserID;

    FirebaseAuth mAuth;
    DatabaseReference reference, allQuestionRef, userQuestionRef, likesRef, likeListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        activity = this;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();

        //Firebase data references
        allQuestionRef= FirebaseDatabase.getInstance().getReference("all questions");
        userQuestionRef = FirebaseDatabase.getInstance().getReference("user questions").child(currentUserID);
        likesRef = FirebaseDatabase.getInstance().getReference("all likes");
        likeListRef = FirebaseDatabase.getInstance().getReference("all likes list").child(currentUserID);
        reference = FirebaseDatabase.getInstance().getReference("users");

        //Initializing recycler view
        questionsList = findViewById(R.id.questionsList);
        questionsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        questionsList.setLayoutManager(linearLayoutManager);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(this);

        FirebaseRecyclerOptions<Questions> options =
                new FirebaseRecyclerOptions.Builder<Questions>().setQuery(allQuestionRef,Questions.class).build();

        //setting adapter class in recycler view and passing recycler option
        adapter = new MyAdapter(options);
        questionsList.setAdapter(adapter);

    }

    //Starting Recycler view adapter on start
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    //Stopping Recycler view adapter on stop
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //going to add button page when floating button clicked
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addButton){
            Intent intent = new Intent(getApplicationContext(),AddQuestion.class);
            startActivity(intent);
        }
    }

    //going back to home page on back pressing
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
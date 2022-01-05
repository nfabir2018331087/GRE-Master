package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//Adding and showing answers in forum
public class AnswersActivity extends AppCompatActivity {

    RecyclerView answersList;
    MyAnsAdapter myAnsAdapter;
    EditText answer;
    ImageButton ansBtn;
    FirebaseAuth mAuth;
    String currentUserID, userAnswer, key;
    DatabaseReference reference, allAnsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        //getting the key of the question which I am answering
        String postKey = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();

        //Firebase references for adding answers
        allAnsRef= FirebaseDatabase.getInstance().getReference("all questions").child(postKey).child("answers");
        reference = FirebaseDatabase.getInstance().getReference("users");

        //setting up recycler view in a descending order
        answersList = findViewById(R.id.answersList);
        answersList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        answersList.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Answers> options =
                new FirebaseRecyclerOptions.Builder<Answers>().setQuery(allAnsRef,Answers.class).build();

        //setting adapter class in anslist and passing recycler option
        myAnsAdapter = new MyAnsAdapter(options);
        answersList.setAdapter(myAnsAdapter);

        answer = findViewById(R.id.answersInput);
        userAnswer = answer.getText().toString();
        ansBtn = findViewById(R.id.addAns);


        ansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAnswer = answer.getText().toString();
                if(TextUtils.isEmpty(userAnswer)){
                    Toast.makeText(getApplicationContext(),"Please write your answer.",Toast.LENGTH_LONG).show();
                    return;
                }
                //Dialogue box for adding answers when ansBtn is clicked
                new AlertDialog.Builder(AnswersActivity.this)
                        .setMessage("Are you sure you want to post this Answer?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addNewAnswer();
                                answer.setText("");
                                Toast.makeText(getApplicationContext(),"Answered successfully",Toast.LENGTH_SHORT).show();
                                View view = getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });
    }

    private void addNewAnswer() {
        key = currentUserID + Calendar.getInstance().getTime();

        //this is for getting and setting data in given database location
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(currentUserID!=null) {
                        //Fetching data from users
                        String name = dataSnapshot.child(currentUserID).child("name").getValue(String.class);
                        String expert = dataSnapshot.child(currentUserID).child("expert").getValue(String.class);
                        String userDP = dataSnapshot.child(currentUserID).child("profileimage").getValue(String.class);
                        //getting time and date of the answer
                        Calendar calForDate = Calendar.getInstance();
                        SimpleDateFormat currentDate = new SimpleDateFormat("dd.MM.yy", Locale.US);
                        final String saveCurrentDate = currentDate.format(calForDate.getTime());

                        Calendar calForTime = Calendar.getInstance();
                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm", Locale.US);
                        final String saveCurrentTime = currentTime.format(calForTime.getTime());

                        Answers answers = new Answers(name, userDP, userAnswer, saveCurrentTime, saveCurrentDate, expert);

                        answers.setKey(key);

                        //Storing data through model class
                        allAnsRef.child(key).setValue(answers);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Recycler view adapter starting
        myAnsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //recycler view adapter stopping
        myAnsAdapter.stopListening();
    }

    //going back to forum on back pressing
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),Forum.class);
        startActivity(intent);
        finish();
    }
}
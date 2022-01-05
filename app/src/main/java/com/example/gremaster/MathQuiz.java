package com.example.gremaster;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MathQuiz extends AppCompatActivity {

    private ScrollView scroll;
    private RadioGroup radioGroupOne;
    private RadioGroup radioGroupTwo;
    private RadioGroup radioGroupThree;
    private RadioGroup radioGroupFour;
    private RadioGroup radioGroupFive;
    private RadioButton question1;
    private RadioButton question2;
    private RadioButton question3;
    private RadioButton question4;
    private RadioButton question5;
    DatabaseReference reference, quizRef;
    FirebaseAuth mAuth;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_quiz);

        scroll = (ScrollView) findViewById(R.id.Scroll);

        question1 = (RadioButton) findViewById(R.id.rb_CorrectAnswerOne);
        question2 = (RadioButton) findViewById(R.id.rb_CorrectAnswerTwo);
        question3 = (RadioButton) findViewById(R.id.rb_CorrectAnswerThree);
        question4 = (RadioButton) findViewById(R.id.rb_CorrectAnswerFour);
        question5 = (RadioButton) findViewById(R.id.rb_CorrectAnswerFive);

        radioGroupOne = (RadioGroup) findViewById(R.id.radioGroupOne);
        radioGroupTwo = (RadioGroup) findViewById(R.id.radioGroupTwo);
        radioGroupThree = (RadioGroup) findViewById(R.id.radioGroupThree);
        radioGroupFour = (RadioGroup) findViewById(R.id.radioGroupFour);
        radioGroupFive = (RadioGroup) findViewById(R.id.radioGroupFive);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");
        quizRef = FirebaseDatabase.getInstance().getReference("quiz marks");

    }

    public void SubmitResponse(View v) {

        MainActivity.mMarks = 0;

        if (question1.isChecked()) {
            MainActivity.mMarks++;
        }

        if (question2.isChecked()) {
            MainActivity.mMarks++;
        }

        if (question3.isChecked()) {
            MainActivity.mMarks++;
        }

        if (question4.isChecked()) {
            MainActivity.mMarks++;
        }

        if (question5.isChecked()) {
            MainActivity.mMarks++;
        }



        if (MainActivity.mMarks == 5) {
            Toast.makeText(this, "Congrats, All Answers are Correct  \n Thanks for attempting this Quiz ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Correct Answers: " + MainActivity.mMarks + "/5", Toast.LENGTH_LONG).show();
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(currentUserID!=null) {
                        //Fetching data from users
                        String name = dataSnapshot.child(currentUserID).child("name").getValue(String.class);
                        String userDP = dataSnapshot.child(currentUserID).child("profileimage").getValue(String.class);

                        QuizMarks quizMarks = new QuizMarks(name, userDP, (MainActivity.vMarks+MainActivity.mMarks));

                        quizRef.child(currentUserID).setValue(quizMarks);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ResetQuiz();

    }

    public void ResetQuiz() {

        radioGroupOne.clearCheck();
        radioGroupTwo.clearCheck();
        radioGroupThree.clearCheck();
        radioGroupFour.clearCheck();
        radioGroupFive.clearCheck();


        scroll.fullScroll(ScrollView.FOCUS_UP);
    }
}
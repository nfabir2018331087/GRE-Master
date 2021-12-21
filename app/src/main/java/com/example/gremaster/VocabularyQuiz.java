package com.example.gremaster;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VocabularyQuiz extends AppCompatActivity {

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
    private int correctAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_quiz);

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

    }

    public void SubmitResponse(View v) {

        String wrongAnswers = "Check this question and try again :-\n";
        if (question1.isChecked()) {
            correctAnswers++;
        } else {
            wrongAnswers = wrongAnswers + "Question - 1\n";
        }
        if (question2.isChecked()) {
            correctAnswers++;
        } else {
            wrongAnswers = wrongAnswers + "Question - 2\n";
        }
        if (question3.isChecked()) {
            correctAnswers++;
        } else {
            wrongAnswers = wrongAnswers + "Question - 3\n";
        }

        if (question4.isChecked()) {
            correctAnswers++;
        } else {
            wrongAnswers = wrongAnswers + "Question - 4\n";
        }


        if (question5.isChecked()) {
            correctAnswers++;
        } else {
            wrongAnswers = wrongAnswers + "Question - 5\n";
        }



        if (correctAnswers == 5) {
            Toast.makeText(this, "Congrats, All Answers are Correct  \n Thanks for attempting this Quiz ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Correct Answers: " + correctAnswers + " /5\n" + wrongAnswers, Toast.LENGTH_LONG).show();
        }

        ResetQuiz(findViewById(R.id.all));

    }

    public void ResetQuiz(View v) {

        correctAnswers = 0;


        radioGroupOne.clearCheck();
        radioGroupTwo.clearCheck();
        radioGroupThree.clearCheck();
        radioGroupFour.clearCheck();
        radioGroupFive.clearCheck();


        scroll.fullScroll(ScrollView.FOCUS_UP);
    }
}
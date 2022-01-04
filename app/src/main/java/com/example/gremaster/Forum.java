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

        //DisplayAllQuestion();

        FirebaseRecyclerOptions<Questions> options =
                new FirebaseRecyclerOptions.Builder<Questions>().setQuery(allQuestionRef,Questions.class).build();

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

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.addButton){
            Intent intent = new Intent(getApplicationContext(),AddQuestion.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    /* public static class QuestionViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageButton likeButton, commentButton;
        TextView noOfLikes, user, userq, ptime, pdate;
        ImageView image;
        int countLikes;
        String userId;
        DatabaseReference likeRef;
        FirebaseAuth mAuth;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            likeButton = (ImageButton) view.findViewById(R.id.like_button);
            commentButton = (ImageButton) view.findViewById(R.id.comment_button);
            noOfLikes = (TextView) view.findViewById(R.id.no_likes);

            user = (TextView) view.findViewById(R.id.userName);
            image = (ImageView) view.findViewById(R.id.userImage);
            userq = (TextView) view.findViewById(R.id.userQuestion);
            ptime = (TextView) view.findViewById(R.id.postTime);
            pdate = (TextView) view.findViewById(R.id.postDate);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser!=null) userId=mAuth.getCurrentUser().getUid();

            likeRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("questions").child("likes");
        }

        public void setLikeButtonStatus(final String PostKey){
            likeRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(userId)){
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);

                        noOfLikes.setText((Integer.toString(countLikes)+(" Likes")));
                    }
                    else {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);

                        noOfLikes.setText((Integer.toString(countLikes)+(" Likes")));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

       /* public void setUsername(String username){
            TextView u = (TextView) view.findViewById(R.id.userName);
            u.setText(username);
        }
        public void setProfileImage(String profileImage) {
            ImageView image = (ImageView) view.findViewById(R.id.userImage);
            Picasso.get().load(profileImage).into(image);
        }
        public void setQuestion(String question) {
            TextView userq = (TextView) view.findViewById(R.id.userQuestion);
            userq.setText(question);
        }
        public void setTime(String time){
            TextView ptime = (TextView) view.findViewById(R.id.postTime);
            ptime.setText(time);
        }
        public void setDate(String date){
            TextView pdate = (TextView) view.findViewById(R.id.postDate);
            pdate.setText(date);
        }
    }*/
}
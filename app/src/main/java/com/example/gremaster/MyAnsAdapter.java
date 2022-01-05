package com.example.gremaster;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

//adapter for showing data on recycler view of answers
public class MyAnsAdapter extends FirebaseRecyclerAdapter<Answers, MyAnsAdapter.AnswerViewHolder> {
    boolean likeChecker = false;
    DatabaseReference likesRef;
    FirebaseAuth mAuth;
    String currentUserID;

    //recycler view adapter for showing data on the answers page
    public MyAnsAdapter(@NonNull FirebaseRecyclerOptions<Answers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyAnsAdapter.AnswerViewHolder holder, int position, @NonNull Answers model) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference("all answer likes");
        final String  rKey = getRef(position).getKey();

        if(model.getStatus()!=null) {
            String status = model.getStatus();
            if (status.contains("Yes")) holder.expertBtn.setVisibility(View.VISIBLE);
        }

        holder.username.setText(model.getName());
        holder.answer.setText(model.getAnswer());
        holder.time.setText(model.getTime());
        holder.date.setText(model.getDate());
        Picasso.get().load(model.getProfileImage()).into(holder.userImage);

        holder.setLikeButtonStatus(rKey);

        //setting up the like button functionality
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeChecker = true;
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(likeChecker){
                            if (dataSnapshot.child(rKey).hasChild(currentUserID)) {
                                likesRef.child(rKey).child(currentUserID).removeValue();
                            } else {
                                likesRef.child(rKey).child(currentUserID).setValue(true);
                            }
                            likeChecker = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //answer view holder class
    @NonNull
    @Override
    public MyAnsAdapter.AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_answer,parent,false);
        return new MyAnsAdapter.AnswerViewHolder(view);
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder{
        ImageButton likeButton, expertBtn;
        TextView username, answer, time, date, noOfLikes;
        ImageView userImage;
        int countLikes;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.ansUserName);
            userImage = (ImageView) itemView.findViewById(R.id.ansUserImage);
            answer = (TextView) itemView.findViewById(R.id.userAnswer);
            time = (TextView) itemView.findViewById(R.id.ansTime);
            date = (TextView) itemView.findViewById(R.id.ansDate);
            likeButton = (ImageButton) itemView.findViewById(R.id.ansLikeButton);
            noOfLikes = (TextView) itemView.findViewById(R.id.ansLikes);
            expertBtn = (ImageButton) itemView.findViewById(R.id.expertId);
        }

        //setting up like button status
        public void setLikeButtonStatus(String postKey) {
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(postKey).hasChild(currentUserID)){
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);

                        if(countLikes<=1) noOfLikes.setText((Integer.toString(countLikes)+" Like"));
                        else noOfLikes.setText((Integer.toString(countLikes)+(" Likes")));                    }
                    else {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);

                        if(countLikes<=1) noOfLikes.setText((Integer.toString(countLikes)+" Like"));
                        else noOfLikes.setText((Integer.toString(countLikes)+(" Likes")));                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}

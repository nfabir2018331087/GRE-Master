package com.example.gremaster;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.gremaster.Forum;

public class MyAdapter extends FirebaseRecyclerAdapter<Questions, MyAdapter.QuestionViewHolder> {

    boolean likeChecker = false;
    DatabaseReference likesRef, likeListRef;
    FirebaseAuth mAuth;
    String currentUserID;
    Forum forum;


    public MyAdapter(@NonNull FirebaseRecyclerOptions<Questions> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull QuestionViewHolder holder, int position, @NonNull Questions model) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference("all question likes");
        //likeListRef = FirebaseDatabase.getInstance().getReference("all likes list").child(currentUserID);

        final String  postKey = getRef(position).getKey();

        if(model.getStatus()!=null) {
            String status = model.getStatus();
            if (status.contains("Yes")) holder.expertButton.setVisibility(View.VISIBLE);
        }

        holder.username.setText(model.getUsername());
        holder.question.setText(model.getQuestion());
        holder.time.setText(model.getTime());
        holder.date.setText(model.getDate());
        Picasso.get().load(model.getProfileImage()).into(holder.userImage);

        holder.setLikeButtonStatus(postKey);

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeChecker = true;
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(likeChecker){
                            if(dataSnapshot.child(postKey).hasChild(currentUserID))
                            {
                                likesRef.child(postKey).child(currentUserID).removeValue();
                            }
                            else {
                                likesRef.child(postKey).child(currentUserID).setValue(true);
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

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forum.activity,AnswersActivity.class);
                intent.putExtra("PostKey", postKey);
                Bundle bundle = new Bundle();
                startActivity(Forum.activity,intent,bundle);
            }
        });
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_question,parent,false);
        return new QuestionViewHolder(view);
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder{
        ImageButton likeButton, commentButton, expertButton;
        TextView username, question, time, date, noOfLikes;
        ImageView userImage;
        int countLikes;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.userName);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            question = (TextView) itemView.findViewById(R.id.userQuestion);
            time = (TextView) itemView.findViewById(R.id.postTime);
            date = (TextView) itemView.findViewById(R.id.postDate);
            likeButton = (ImageButton) itemView.findViewById(R.id.like_button);
            commentButton = (ImageButton) itemView.findViewById(R.id.comment_button);
            noOfLikes = (TextView) itemView.findViewById(R.id.no_likes);
            expertButton = (ImageButton) itemView.findViewById(R.id.expertId);
        }

        public void setLikeButtonStatus(String postKey) {
            likesRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(postKey).hasChild(currentUserID)){
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);

                        if(countLikes<=1) noOfLikes.setText((Integer.toString(countLikes)+" Like"));
                        else noOfLikes.setText((Integer.toString(countLikes)+(" Likes")));
                    }
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

package com.example.gremaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Leaderboard extends AppCompatActivity {

    RecyclerView userList;
    DatabaseReference reference, quizRef;
    FirebaseAuth mAuth;
    String currentUserID;
    FirebaseRecyclerAdapter<QuizMarks, UserViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<QuizMarks> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) currentUserID=mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");
        quizRef = FirebaseDatabase.getInstance().getReference("quiz marks");
        Query query = quizRef.orderByChild("marks");



        userList = findViewById(R.id.userList);
        userList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userList.setLayoutManager(linearLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<QuizMarks>().setQuery(query,QuizMarks.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<QuizMarks, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull QuizMarks model) {
                String marks = Integer.toString(model.getMarks());

                holder.name.setText(model.getName());
                holder.marks.setText(marks);
                Picasso.get().load(model.getImage()).into(holder.image);

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_leaderboard,parent,false);
                return new UserViewHolder(view);
            }
        };

        userList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, marks;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.quizUserImage);
            name = itemView.findViewById(R.id.quizUserName);
            marks = itemView.findViewById(R.id.quizMarks);
        }
    }
}

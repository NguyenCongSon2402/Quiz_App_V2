package by.nguyencongson.quiz_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.model.Question;

public class StartActivity extends AppCompatActivity {
    Button btnPlay;

    FirebaseDatabase database;
    DatabaseReference questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        database = FirebaseDatabase.getInstance();
        questions = database.getReference("Questions");
        loadQuestion(Common.CategoryId);
        btnPlay=(Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, PlayingActivity.class));
                finish();
            }
        });
    }

    private void loadQuestion(String CategoryId) {
        // Fist, clear List if have old question
        if (Common.questionList.size() > 0)
            Common.questionList.clear();
        questions.orderByChild("CategoryId").equalTo(CategoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapchot : dataSnapshot.getChildren()) {
                    Question ques = postSnapchot.getValue(Question.class);
                    Common.questionList.add(ques);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        // Random list
        Collections.shuffle(Common.questionList);
    }
}

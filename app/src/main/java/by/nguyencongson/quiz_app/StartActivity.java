package by.nguyencongson.quiz_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

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
    private SharedPreferences sharedPreferences;
    private RelativeLayout backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        database = FirebaseDatabase.getInstance();
        questions = database.getReference("Questions");
        loadQuestion(Common.CategoryId);
        backgroundView=findViewById(R.id.background_view);
        sharedPreferences = this.getSharedPreferences("THEME", Context.MODE_PRIVATE);
        Common.is_night = sharedPreferences.getBoolean("is_night", false);
        if (Common.is_night == true) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_btn_menu);
            backgroundView.setBackground(drawable);
        }
        else {
            Drawable drawable = getResources().getDrawable(R.drawable.background_banner);
            backgroundView.setBackground(drawable);
        }
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

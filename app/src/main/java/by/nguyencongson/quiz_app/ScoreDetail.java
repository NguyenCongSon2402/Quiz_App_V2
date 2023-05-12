package by.nguyencongson.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.model.Question;
import by.nguyencongson.quiz_app.model.QuestionScore;
import by.nguyencongson.quiz_app.model.Ranking;
import by.nguyencongson.quiz_app.viewholder.RankingViewHolder;
import by.nguyencongson.quiz_app.viewholder.ScoreDetailViewHolder;

public class ScoreDetail extends AppCompatActivity {
    private String viewUser = "";
    private DrawerLayout mDrawerLayout;
    FirebaseDatabase database;
    DatabaseReference question_score;
    RecyclerView scoreList;
    RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private RelativeLayout backgroundView;
    FirebaseRecyclerAdapter<QuestionScore, ScoreDetailViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);
        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");
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
        scoreList = (RecyclerView) findViewById(R.id.scoreList);
        scoreList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        scoreList.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            viewUser = getIntent().getStringExtra("viewUser");
        }
        if (!viewUser.isEmpty()) {
            loadScoreDetail(viewUser);
        }
    }

    private void loadScoreDetail(String viewUser) {
        Query query = question_score.orderByChild("user").equalTo(viewUser);

        FirebaseRecyclerOptions<QuestionScore> options =
                new FirebaseRecyclerOptions.Builder<QuestionScore>()
                        .setQuery(query, QuestionScore.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<QuestionScore, ScoreDetailViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ScoreDetailViewHolder holder, int position, @NonNull QuestionScore model) {
                holder.txt_name.setText(model.getCategoryName());
                holder.txt_score.setText(model.getScore());
            }

            @NonNull
            @Override
            public ScoreDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.score_detail_layout, parent, false);

                return new ScoreDetailViewHolder(view);
            }
        };
        scoreList.setAdapter(adapter);
        adapter.startListening();
    }

}
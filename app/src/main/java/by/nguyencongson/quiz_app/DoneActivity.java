package by.nguyencongson.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.model.QuestionScore;
import by.nguyencongson.quiz_app.model.User;

public class DoneActivity extends AppCompatActivity {
    private Button btnTryAgain;
    DatabaseReference databaseReference;
    private TextView txtResultScore, getTxtResultQuestion;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private RelativeLayout backgroundView;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TESTING", "onStart: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        FirebaseDatabase.getInstance().getReference("Question_Score");

        txtResultScore = (TextView) findViewById(R.id.txtTotalScore);
        getTxtResultQuestion = (TextView) findViewById(R.id.txtTotalQuestion);
        progressBar = (ProgressBar) findViewById(R.id.doneProgressBar);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);
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
        btnTryAgain.setOnClickListener(v -> {
//                startActivity(new Intent(Done.this, HomeNavigationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
//                finish();
            onBackPressed();
        });
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");
            FirebaseDatabase.getInstance().getReference("Question_Score").child(String.format("%s_%s", Common.currentUser.getUserName(),
                    Common.CategoryId)).child("score").get().addOnCompleteListener(task -> {
                        String a = String.valueOf(task.getResult().getValue());
                        if (a == "null" || a.isEmpty()) {
                            a = "0";
                            uploadPoint2Fb(score, Common.currentUser);
                        } else {
                            Log.e("LAG",a);
                            if(Integer.parseInt(a) < score){
                                uploadPoint2Fb(score, Common.currentUser);
                            }
                        }
                    });
            txtResultScore.setText(String.format("SCORE : %d!", score));
            getTxtResultQuestion.setText(String.format("PASSED : %d / %d", correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);
        }
    }

    @Override
    public void onBackPressed() {
        //Chỗ này là chỗ trả result code cho thằng Playing
        //Result OK
        // Result Cancel . blalba
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TESTING", "onDestroy: "+ DoneActivity.class.getName());
    }

    //.substring(0, user.getUserName().indexOf("@"))
    private void uploadPoint2Fb(int score, User user) {
        FirebaseDatabase.getInstance().getReference("Question_Score").child(String.format("%s_%s", user.getUserName(),
                        Common.CategoryId)).
                setValue(new QuestionScore(String.format("%s_%s", user.getUserName(),
                        Common.CategoryId),
                        Common.currentUser.getUserName(),
                        String.valueOf(score),
                        Common.CategoryId,
                        Common.categoryName));
    }

}
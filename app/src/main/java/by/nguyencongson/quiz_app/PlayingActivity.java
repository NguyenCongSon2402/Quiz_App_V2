package by.nguyencongson.quiz_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Time;

import by.nguyencongson.quiz_app.common.Common;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    final static int INTERVAL = 1000;
    final static int TIMEOUT = 30000;//30sec
    private int progressValue = 0;
    private ProgressDialog TempDialog;
    private CountDownTimer mcountDown;
    int index = 0, score = 0, thisQuestion = 0, totalQuestion, correctAnswer;

    private ProgressBar progressBar;
    private ImageView question_image;
    private Button btnA, btnB, btnC, btnD;
    private TextView txtScore, txtQuestionNum, question_text;
    private ObjectAnimator animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        txtScore = (TextView) findViewById(R.id.txtScore);
        txtQuestionNum = (TextView) findViewById(R.id.txtTotalQuestion);
        question_text = (TextView) findViewById(R.id.question_text);
        question_image = (ImageView) findViewById(R.id.question_image);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        btnA = (Button) findViewById(R.id.btnAnswerA);
        btnB = (Button) findViewById(R.id.btnAnswerB);
        btnC = (Button) findViewById(R.id.btnAnswerC);
        btnD = (Button) findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mcountDown.cancel();
        if (index < totalQuestion) {
            Button clickedButton = (Button) v;
            if (clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer())) {
                score += 10;
                correctAnswer++;
                showQuestion(++index); // next
            } else {
                showQuestion(++index);
            }
            txtScore.setText(String.format("%d", score));
        }
    }

    private void showQuestion(int index) {
        if (index < totalQuestion) {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;
            if (Common.questionList.get(index).getIsImageQuestion().equals("true")) {
                Picasso.get()
                        .load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            } else {
                question_text.setText(Common.questionList.get(index).getQuestion());
                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }
            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());
            mcountDown.start(); // Start timer

            animation.start();
        } else {
            Intent intent = new Intent(PlayingActivity.this, DoneActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            intent.putExtras(dataSend);
            //Tạo activity Done với nhu cầu là lắng nghe kết quả trả về với request code là Finish
            startActivityForResult(intent,Global.FINISH);// cái này Global.FINISH là reqeust code ạ


            //Từ Playing có thể gọi đến nhiều activity khác nhau
            //Nên cần phải có request code để phân biệt giữa các lời gọi

        }
    }

    // Khi Done nó bị finish sẽ trả kết quả vào onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Kiểm tra xem đúng request code đã gửi k
        // Đúng thì check theo resultCode
        if (requestCode == Global.FINISH){
            if (resultCode == RESULT_OK){
                onBackPressed();
            }
            if (resultCode == RESULT_CANCELED){
                //do something
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("TESTING", "onBackPressed: "+PlayingActivity.class.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        //Progress bar nos chwa muot
        animation = ObjectAnimator.ofInt(progressBar, "progress", TIMEOUT, 0);
        progressBar.setMax(TIMEOUT);
        animation.setDuration(0); // 3.5 second
        animation.setInterpolator(new LinearInterpolator());
        mcountDown = new CountDownTimer(TIMEOUT, 10) {
            @Override
            public void onTick(long millisUntilFinished) {

//                Log.v("Log_tag", "Tick of Progress" + progressValue + millisUntilFinished);
                progressValue++;
                //Log.d("TAG", "onTick: "+millisUntilFinished);
//                int percent = (int) ((TIMEOUT-millisUntilFinished)/(TIMEOUT/100));
//                Log.d("TAG", "onTick: "+percent);
//                Log.d("Log_tag", "onTick: "+percent);
//                progressBar.setProgress((int) progressValue * 100 / (TIMEOUT / INTERVAL));
                progressBar.setProgress((int) (TIMEOUT-millisUntilFinished));
                //Log.d("TESTING", "onTick: " + progressValue);
            }

            @Override
            public void onFinish() {
                mcountDown.cancel();
                animation.end();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}
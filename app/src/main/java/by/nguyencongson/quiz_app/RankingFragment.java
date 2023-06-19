package by.nguyencongson.quiz_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.interfaces.IItemClickListener;
import by.nguyencongson.quiz_app.interfaces.IRankingCallBack;
import by.nguyencongson.quiz_app.model.Category;
import by.nguyencongson.quiz_app.model.QuestionScore;
import by.nguyencongson.quiz_app.model.Ranking;
import by.nguyencongson.quiz_app.viewholder.CategoryViewHolder;
import by.nguyencongson.quiz_app.viewholder.RankingViewHolder;


public class RankingFragment extends Fragment {


    View myFragment;
    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference questionScore, rankingTbl;
    int sum = 0;
    private SharedPreferences sharedPreferences;
    private RelativeLayout backgroundView;


    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://quizapp-7cf64-default-rtdb.asia-southeast1.firebasedatabase.app/");
        questionScore = database.getReference("Question_Score");
        rankingTbl = database.getReference("Ranking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking, container, false);
        //init View
        backgroundView=myFragment.findViewById(R.id.background_view);
        sharedPreferences = getContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        Common.is_night = sharedPreferences.getBoolean("is_night", false);
        if (Common.is_night) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_btn_menu);
            backgroundView.setBackground(drawable);
        }
        else {
            Drawable drawable = getResources().getDrawable(R.drawable.background_banner);
            backgroundView.setBackground(drawable);
        }
        rankingList = (RecyclerView) myFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);
        // Vì phương thức OrderByChild của Firebase sẽ sắp xếp danh sách theo thứ tự tăng dần
// Vì vậy, cần đảo ngược dữ liệu Recycler
// Bởi LayoutManager
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);
        //Log.e("TAG",Common.currentUser.getUserName());


        // implememt callback
        updateScore(Common.currentUser.getUserName(), ranking -> {
            rankingTbl.child(ranking.getUserName()).setValue(ranking);
            //    showRanking();// sort ranking table and show result
        });

        // set adapter
        Query query = database.getReference("Ranking").orderByChild("score");

        FirebaseRecyclerOptions<Ranking> options =
                new FirebaseRecyclerOptions.Builder<Ranking>()
                        .setQuery(query, Ranking.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RankingViewHolder holder, int position, @NonNull Ranking model) {
                holder.txt_name.setText(model.getUserName());
                holder.txt_score.setText(String.valueOf(model.getScore()));
                holder.setiItemClickListener((view, position1, isLongClick) -> {
                    Intent scoreDetail= new Intent(getActivity(),ScoreDetail.class);
                    scoreDetail.putExtra("viewUser",model.getUserName());
                    startActivity(scoreDetail);
                });

            }

            @NonNull
            @Override
            public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ranking, parent, false);

                return new RankingViewHolder(view);
            }
        };


       // adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        adapter.startListening();

        return myFragment;
    }


    private void updateScore(final String userName, IRankingCallBack<Ranking> callBack) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            QuestionScore ques = dataSnapshot.getValue((QuestionScore.class));
                            sum += Integer.parseInt(ques.getScore());
                        }
                        Ranking ranking = new Ranking(userName, sum);
                        callBack.callBack(ranking);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
package by.nguyencongson.quiz_app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class WrapContentLinearLayoutManager extends StaggeredGridLayoutManager {



    public WrapContentLinearLayoutManager(int i, int vertical) {
        super(i, vertical);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        }catch (IndexOutOfBoundsException e){
            Log.e("TAG","meet a IOOBE in RecycleView");
        }
    }
}

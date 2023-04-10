package by.nguyencongson.quiz_app.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import by.nguyencongson.quiz_app.R;
import by.nguyencongson.quiz_app.interfaces.IItemClickListener;

public class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txt_name, txt_score;
    private IItemClickListener iItemClickListener;

    public RankingViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_name = (TextView) itemView.findViewById((R.id.txt_name));
        txt_score = (TextView) itemView.findViewById((R.id.txt_score));
        itemView.setOnClickListener(this);
    }

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    @Override
    public void onClick(View v) {
        iItemClickListener.onClick(v, getAdapterPosition(), false);
    }
}

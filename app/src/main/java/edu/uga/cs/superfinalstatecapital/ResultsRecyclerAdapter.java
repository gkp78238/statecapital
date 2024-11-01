package edu.uga.cs.superfinalstatecapital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsRecyclerAdapter.QuizResultHolder> {
    private List<QuizResult> quizResults;
    private Context context;

    public ResultsRecyclerAdapter(Context context, List<QuizResult> quizResults) {
        this.context = context;
        this.quizResults = quizResults;
    }

    class QuizResultHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView scoreText;

        public QuizResultHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            scoreText = itemView.findViewById(R.id.scoreText);
        }
    }

    @NonNull
    @Override
    public QuizResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_result_item, parent, false);
        return new QuizResultHolder(view);
    }

    @Override
    public void onBindViewHolder(QuizResultHolder holder, int position) {
        QuizResult result = quizResults.get(position);
        holder.dateText.setText(result.getDate());
        holder.scoreText.setText(String.format("Score: %d/6", result.getScore()));
    }

    @Override
    public int getItemCount() {
        return quizResults.size();
    }
}
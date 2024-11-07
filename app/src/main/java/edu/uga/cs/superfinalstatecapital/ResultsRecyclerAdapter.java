package edu.uga.cs.superfinalstatecapital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * ResultsRecyclerAdapter is a RecyclerView adapter that binds a list of QuizResult objects
 * to views displayed in a RecyclerView. It provides a convenient way to display quiz results
 * with date and score information.
 */
public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsRecyclerAdapter.QuizResultHolder> {

    // List of quiz results to display
    private List<QuizResult> quizResults;

    // Application context
    private Context context;

    /**
     * Constructor for the ResultsRecyclerAdapter.
     *
     * @param context     the application context
     * @param quizResults the list of quiz results to display
     */
    public ResultsRecyclerAdapter(Context context, List<QuizResult> quizResults) {
        this.context = context;
        this.quizResults = quizResults;
    }

    /**
     * ViewHolder class for the RecyclerView. Holds references to the views for each item.
     */
    class QuizResultHolder extends RecyclerView.ViewHolder {

        // TextView to display the date of the quiz
        TextView dateText;

        // TextView to display the score of the quiz
        TextView scoreText;

        /**
         * Constructor for the QuizResultHolder.
         *
         * @param itemView the view of the individual item
         */
        public QuizResultHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            scoreText = itemView.findViewById(R.id.scoreText);
        }
    }

    /**
     * Inflates the layout for an individual item in the RecyclerView.
     *
     * @param parent   the parent view group
     * @param viewType the view type of the new view
     * @return a new QuizResultHolder with the inflated view
     */
    @NonNull
    @Override
    public QuizResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_result_item, parent, false);
        return new QuizResultHolder(view);
    }

    /**
     * Binds data to the views in the ViewHolder for a specific position.
     *
     * @param holder   the ViewHolder for the current item
     * @param position the position of the item in the data set
     */
    @Override
    public void onBindViewHolder(QuizResultHolder holder, int position) {
        QuizResult result = quizResults.get(position);

        // Set the date of the quiz
        holder.dateText.setText(result.getDate());

        // Set the score of the quiz
        holder.scoreText.setText(String.format("Score: %d/6", result.getScore()));
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return the size of the quiz results list
     */
    @Override
    public int getItemCount() {
        return quizResults.size();
    }
}

package edu.uga.cs.superfinalstatecapital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Fragment that displays the results of the current quiz session
 * and provides navigation options to view all results, start new quiz, or return home
 */
public class CurrentQuizResultFragment extends Fragment {
    private int score;          // Current quiz score
    private int totalQuestions; // Total number of questions in quiz

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Extract score and total from fragment arguments
        if (getArguments() != null) {
            score = getArguments().getInt("score");
            totalQuestions = getArguments().getInt("total");
        }

        // Initialize and set up the score display
        TextView scoreTextView = view.findViewById(R.id.currentScoreText);
        scoreTextView.setText(String.format("Your Score: %d/%d", score, totalQuestions));

        // Initialize navigation buttons
        Button viewAllResultsButton = view.findViewById(R.id.viewAllResultsButton);
        Button newQuizButton = view.findViewById(R.id.newQuizButton);
        Button homeButton = view.findViewById(R.id.homeButton);

        // Set up navigation button click handlers
        viewAllResultsButton.setOnClickListener(v -> {
            Fragment resultsFragment = new ResultsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resultsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        newQuizButton.setOnClickListener(v -> {
            Fragment quizFragment = new QuizFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, quizFragment)
                    .commit();
        });

        homeButton.setOnClickListener(v -> {
            Fragment mainFragment = new MainFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .commit();
        });
    }
}
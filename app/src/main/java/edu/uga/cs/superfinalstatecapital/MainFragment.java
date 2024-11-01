package edu.uga.cs.superfinalstatecapital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * MainFragment serves as the splash screen and entry point for the State Capitals Quiz app.
 * Displays quiz purpose, rules, and provides navigation options.
 */
public class MainFragment extends Fragment {

    private QuizData quizData = null;  // Database helper instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize navigation buttons
        Button startQuizButton = view.findViewById(R.id.buttonStartQuiz);
        Button viewResultsButton = view.findViewById(R.id.buttonViewResults);

        // Navigate to quiz screen
        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment quizFragment = new QuizFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, quizFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Navigate to results screen
        viewResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment resultsFragment = new ResultsFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, resultsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Set up database and load initial data if needed
        setupDatabase();
    }

    /**
     * Initializes database and loads CSV data if database is empty
     */
    private void setupDatabase() {
        quizData = new QuizData(getContext());
        quizData.open();

        if (quizData.retrieveAllStates().isEmpty()) {
            CSVReader.readStateData(getContext(), quizData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (quizData != null && !quizData.isDBOpen()) {
            quizData.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (quizData != null) {
            quizData.close();
        }
    }
}
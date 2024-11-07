package edu.uga.cs.superfinalstatecapital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * ResultsFragment is a Fragment that displays a list of past quiz results
 * using a RecyclerView. It also provides navigation buttons to start a new quiz
 * or return to the main menu.
 */
public class ResultsFragment extends Fragment {

    // Database helper for quiz data
    private QuizData quizData;

    // RecyclerView for displaying quiz results
    private RecyclerView recyclerView;

    // Adapter for the RecyclerView
    private ResultsRecyclerAdapter recyclerAdapter;

    /**
     * Inflates the fragment layout.
     *
     * @param inflater           the LayoutInflater object
     * @param container          the parent view group
     * @param savedInstanceState the saved state of the fragment
     * @return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    /**
     * Called immediately after the fragment's view has been created.
     * Sets up the RecyclerView, navigation buttons, and loads quiz results.
     *
     * @param view               the root view of the fragment
     * @param savedInstanceState the saved state of the fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up navigation buttons
        Button newQuizButton = view.findViewById(R.id.newQuizButton);
        Button homeButton = view.findViewById(R.id.homeButton);

        // Navigate to QuizFragment when "New Quiz" button is clicked
        newQuizButton.setOnClickListener(v -> {
            Fragment quizFragment = new QuizFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, quizFragment)
                    .commit();
        });

        // Navigate to MainFragment when "Home" button is clicked
        homeButton.setOnClickListener(v -> {
            Fragment mainFragment = new MainFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .commit();
        });

        // Initialize quiz data and load results
        quizData = new QuizData(getActivity());
        quizData.open();

        // Load quiz results into the RecyclerView
        List<QuizResult> results = quizData.getPastQuizResults();
        recyclerAdapter = new ResultsRecyclerAdapter(getActivity(), results);
        recyclerView.setAdapter(recyclerAdapter);
    }

    /**
     * Ensures the database connection is open when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (quizData != null && !quizData.isDBOpen()) {
            quizData.open();
        }
    }

    /**
     * Closes the database connection when the fragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (quizData != null) {
            quizData.close();
        }
    }
}

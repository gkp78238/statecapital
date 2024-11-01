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

public class ResultsFragment extends Fragment {
    private QuizData quizData;
    private RecyclerView recyclerView;
    private ResultsRecyclerAdapter recyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set up navigation buttons
        Button newQuizButton = view.findViewById(R.id.newQuizButton);
        Button homeButton = view.findViewById(R.id.homeButton);

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

        // Initialize quiz data and load results
        quizData = new QuizData(getActivity());
        quizData.open();

        // Load quiz results
        List<QuizResult> results = quizData.getPastQuizResults();
        recyclerAdapter = new ResultsRecyclerAdapter(getActivity(), results);
        recyclerView.setAdapter(recyclerAdapter);
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
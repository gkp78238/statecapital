package edu.uga.cs.superfinalstatecapital;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment {
    private static final String DEBUG_TAG = "QuizFragment";
    private static final int QUESTIONS_PER_QUIZ = 6;

    // Keys for saving state
    private static final String KEY_QUIZ_STATES = "quizStates";
    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private static final String KEY_CURRENT_SCORE = "currentScore";
    private static final String KEY_QUIZ_ID = "quizId";
    private static final String KEY_CURRENT_CHOICES = "currentChoices";
    private static final String KEY_SELECTED_ANSWER = "selectedAnswer";

    private QuizData quizData;
    private List<State> quizStates;
    private List<String> currentChoices;
    private int currentQuestionIndex = 0;
    private int currentScore = 0;
    private long currentQuizId;

    private TextView questionTextView;
    private RadioGroup choicesRadioGroup;
    private RadioButton choice1;
    private RadioButton choice2;
    private RadioButton choice3;
    private TextView progressTextView;
    private Button nextButton;
    private GestureDetectorCompat gestureDetector;
    private View quizCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        questionTextView = view.findViewById(R.id.questionText);
        choicesRadioGroup = view.findViewById(R.id.choicesRadioGroup);
        choice1 = view.findViewById(R.id.choice1);
        choice2 = view.findViewById(R.id.choice2);
        choice3 = view.findViewById(R.id.choice3);
        progressTextView = view.findViewById(R.id.progressText);
        nextButton = view.findViewById(R.id.nextButton);
        quizCardView = view.findViewById(R.id.quizCard);

        quizData = new QuizData(getContext());
        quizData.open();

        // Set up gesture detection
        gestureDetector = new GestureDetectorCompat(getContext(), new QuizGestureListener());
        setupSwipeListener();

        // Set up next button
        nextButton.setOnClickListener(v -> handleNextQuestion());

        if (savedInstanceState != null) {
            restoreQuizState(savedInstanceState);
        } else {
            initializeQuiz();
        }

        displayCurrentQuestion();
    }

    private void setupSwipeListener() {
        quizCardView.setOnTouchListener(new View.OnTouchListener() {
            float dX = 0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (choicesRadioGroup.getCheckedRadioButtonId() != -1) {
                            float newX = event.getRawX() + dX;
                            if (newX <= v.getX()) {
                                v.setTranslationX(Math.max(newX - v.getX(), -200));
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .translationX(0f)
                                .setDuration(100)
                                .start();
                        break;
                }
                return true;
            }
        });
    }

    private class QuizGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX < 0) {
                            if (choicesRadioGroup.getCheckedRadioButtonId() == -1) {
                                Toast.makeText(getContext(), "Please select an answer before continuing",
                                        Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            handleNextQuestion();
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Error in swipe gesture: " + e.getMessage());
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private void initializeQuiz() {
        List<State> allStates = quizData.retrieveAllStates();
        quizStates = new ArrayList<>();
        Random random = new Random();

        while (quizStates.size() < QUESTIONS_PER_QUIZ) {
            int index = random.nextInt(allStates.size());
            State state = allStates.get(index);
            if (!quizStates.contains(state)) {
                quizStates.add(state);
            }
        }

        currentQuizId = quizData.startNewQuiz();
    }

    private void displayCurrentQuestion() {
        State currentState = quizStates.get(currentQuestionIndex);
        questionTextView.setText(getString(R.string.question_format, currentState.getName()));
        progressTextView.setText(getString(R.string.progress_format,
                currentQuestionIndex + 1, QUESTIONS_PER_QUIZ));

        currentChoices = new ArrayList<>();
        currentChoices.add(currentState.getCapital());
        currentChoices.add(currentState.getCity2());
        currentChoices.add(currentState.getCity3());
        Collections.shuffle(currentChoices);

        choice1.setText(currentChoices.get(0));
        choice2.setText(currentChoices.get(1));
        choice3.setText(currentChoices.get(2));

        choicesRadioGroup.clearCheck();
    }

    private void handleNextQuestion() {
        if (choicesRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        State currentState = quizStates.get(currentQuestionIndex);
        RadioButton selectedButton = getView().findViewById(choicesRadioGroup.getCheckedRadioButtonId());
        String userAnswer = selectedButton.getText().toString();

        quizData.storeQuizQuestion(currentQuizId, currentState.getId(), userAnswer);

        if (userAnswer.equals(currentState.getCapital())) {
            currentScore++;
        }

        currentQuestionIndex++;

        quizData.updateQuizScore(currentQuizId, currentScore, currentQuestionIndex);

        if (currentQuestionIndex < QUESTIONS_PER_QUIZ) {
            displayCurrentQuestion();
        } else {
            completeQuiz();
        }
    }

    private void completeQuiz() {
        quizData.updateQuizScore(currentQuizId, currentScore, QUESTIONS_PER_QUIZ);

        Bundle args = new Bundle();
        args.putInt("score", currentScore);
        args.putInt("total", QUESTIONS_PER_QUIZ);

        Fragment currentResultFragment = new CurrentQuizResultFragment();
        currentResultFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentResultFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_CURRENT_INDEX, currentQuestionIndex);
        outState.putInt(KEY_CURRENT_SCORE, currentScore);
        outState.putLong(KEY_QUIZ_ID, currentQuizId);

        int selectedId = choicesRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedButton = getView().findViewById(selectedId);
            outState.putString(KEY_SELECTED_ANSWER, selectedButton.getText().toString());
        }

        if (currentChoices != null) {
            outState.putStringArrayList(KEY_CURRENT_CHOICES, new ArrayList<>(currentChoices));
        }

        if (quizStates != null) {
            ArrayList<String> stateNames = new ArrayList<>();
            for (State state : quizStates) {
                stateNames.add(state.getName());
            }
            outState.putStringArrayList(KEY_QUIZ_STATES, stateNames);
        }
    }

    private void restoreQuizState(Bundle savedInstanceState) {
        currentQuestionIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX, 0);
        currentScore = savedInstanceState.getInt(KEY_CURRENT_SCORE, 0);
        currentQuizId = savedInstanceState.getLong(KEY_QUIZ_ID, -1);

        ArrayList<String> stateNames = savedInstanceState.getStringArrayList(KEY_QUIZ_STATES);
        if (stateNames != null) {
            quizStates = new ArrayList<>();
            List<State> allStates = quizData.retrieveAllStates();
            for (String stateName : stateNames) {
                for (State state : allStates) {
                    if (state.getName().equals(stateName)) {
                        quizStates.add(state);
                        break;
                    }
                }
            }
        }

        currentChoices = savedInstanceState.getStringArrayList(KEY_CURRENT_CHOICES);

        String selectedAnswer = savedInstanceState.getString(KEY_SELECTED_ANSWER);
        if (selectedAnswer != null) {
            for (int i = 0; i < choicesRadioGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) choicesRadioGroup.getChildAt(i);
                if (radioButton.getText().toString().equals(selectedAnswer)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
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
        if (currentQuizId != -1) {
            quizData.updateQuizScore(currentQuizId, currentScore, currentQuestionIndex);
        }
        if (quizData != null) {
            quizData.close();
        }
    }
}
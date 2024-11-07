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

/**
 * The QuizFragment class implements the quiz functionality for the application.
 * It displays questions, manages user interactions, and tracks the progress of the quiz.
 * The quiz includes gesture-based navigation and saves/restores its state for a seamless user experience.
 */
public class QuizFragment extends Fragment {
    private static final String DEBUG_TAG = "QuizFragment";
    private static final int QUESTIONS_PER_QUIZ = 6;

    // Keys for saving instance state
    private static final String KEY_QUIZ_STATES = "quizStates";
    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private static final String KEY_CURRENT_SCORE = "currentScore";
    private static final String KEY_QUIZ_ID = "quizId";
    private static final String KEY_CURRENT_CHOICES = "currentChoices";
    private static final String KEY_SELECTED_ANSWER = "selectedAnswer";

    // Quiz data and state variables
    private QuizData quizData;
    private List<State> quizStates;
    private List<String> currentChoices;
    private int currentQuestionIndex = 0;
    private int currentScore = 0;
    private long currentQuizId;

    // UI components
    private TextView questionTextView;
    private RadioGroup choicesRadioGroup;
    private RadioButton choice1, choice2, choice3;
    private TextView progressTextView;
    private Button nextButton;
    private GestureDetectorCompat gestureDetector;
    private View quizCardView;

    /**
     * Ensures the fragment retains its state across configuration changes.
     *
     * @param savedInstanceState Bundle containing saved state, if any.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Inflates the layout for the fragment.
     *
     * @param inflater           LayoutInflater for inflating views.
     * @param container          Parent container for the fragment.
     * @param savedInstanceState Saved state, if any.
     * @return The root view for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    /**
     * Initializes the fragment's UI and restores or initializes quiz state.
     *
     * @param view               Root view of the fragment.
     * @param savedInstanceState Bundle containing saved state, if any.
     */
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

        // Show default state until quiz is initialized
        questionTextView.setText(R.string.question_format);
        progressTextView.setText(R.string.progress_format);

        if (savedInstanceState != null) {
            restoreQuizState(savedInstanceState);
            displayCurrentQuestion();
        } else {
            checkForInterruptedQuiz();
        }
    }

    /**
     * Displays a dialog to resume an interrupted quiz or start a new one.
     *
     * @param interruptedQuiz The interrupted quiz to be resumed.
     */
    private void showResumeQuizDialog(QuizData.Quiz interruptedQuiz) {
        Log.d(DEBUG_TAG, "Showing resume dialog for quiz: " + interruptedQuiz.getId());
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Resume Quiz")
                .setMessage("Would you like to resume your previous quiz?")
                .setPositiveButton("Resume", (dialog, which) -> {
                    resumeQuiz(interruptedQuiz);
                    displayCurrentQuestion();
                })
                .setNegativeButton("Start New", (dialog, which) -> {
                    initializeQuiz();
                    displayCurrentQuestion();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Checks for an interrupted quiz and prompts the user to resume it.
     */
    private void checkForInterruptedQuiz() {
        QuizData.Quiz interruptedQuiz = quizData.getQuizInProgress();
        if (interruptedQuiz != null) {
            showResumeQuizDialog(interruptedQuiz);
        }
    }

    /**
     * Resumes a previously interrupted quiz.
     *
     * @param interruptedQuiz The quiz to be resumed.
     */
    private void resumeQuiz(QuizData.Quiz interruptedQuiz) {
        currentQuizId = interruptedQuiz.getId();
        currentScore = interruptedQuiz.getScore();
        currentQuestionIndex = interruptedQuiz.getQuestionsAnswered();

        // Restore quiz states and questions
        quizStates = quizData.getQuizStates(currentQuizId);
        Log.d(DEBUG_TAG, "Retrieved states size: " + (quizStates != null ? quizStates.size() : "null"));
        displayCurrentQuestion();
    }

    /**
     * Sets up a swipe listener for the quiz card to handle gesture navigation.
     */
    private void setupSwipeListener() {
        quizCardView.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().translationX(0f).setDuration(100).start();
                    break;
            }
            return true;
        });
    }

    /**
     * Gesture listener for handling swipe gestures during the quiz.
     */
    private class QuizGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (choicesRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Please select an answer before continuing", Toast.LENGTH_SHORT).show();
                    return false;
                }
                handleNextQuestion();
                return true;
            }
            return false;
        }
    }

    /**
     * Initializes a new quiz by selecting random states.
     */
    private void initializeQuiz() {
        List<State> allStates = quizData.retrieveAllStates();
        quizStates = new ArrayList<>();
        Random random = new Random();

        while (quizStates.size() < QUESTIONS_PER_QUIZ) {
            State state = allStates.get(random.nextInt(allStates.size()));
            if (!quizStates.contains(state)) {
                quizStates.add(state);
            }
        }
        currentQuizId = quizData.startNewQuiz();
    }

    /**
     * Displays the current question and answer choices.
     */
    private void displayCurrentQuestion() {
        // Add null/empty check for quizStates
        if (quizStates == null || quizStates.isEmpty() || currentQuestionIndex >= quizStates.size()) {
            Log.e(DEBUG_TAG, "Invalid quiz state in displayCurrentQuestion, reinitializing");
            initializeQuiz();
            return;
        }

        State currentState = quizStates.get(currentQuestionIndex);
        questionTextView.setText(getString(R.string.question_format, currentState.getName()));
        progressTextView.setText(getString(R.string.progress_format,
                currentQuestionIndex + 1, QUESTIONS_PER_QUIZ));

        currentChoices = new ArrayList<>();
        currentChoices.add(currentState.getCapital());
        currentChoices.add(currentState.getCity2());
        currentChoices.add(currentState.getCity3());
        Collections.shuffle(currentChoices);

        // Ensure there are enough choices before setting text
        if (currentChoices.size() > 0) choice1.setText(currentChoices.get(0));
        if (currentChoices.size() > 1) choice2.setText(currentChoices.get(1));
        if (currentChoices.size() > 2) choice3.setText(currentChoices.get(2));

        choicesRadioGroup.clearCheck();
    }


    /**
     * Handles the action for moving to the next question in the quiz.
     */
    private void handleNextQuestion() {
        if (choicesRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        State currentState = quizStates.get(currentQuestionIndex);
        RadioButton selectedButton = getView().findViewById(choicesRadioGroup.getCheckedRadioButtonId());
        String userAnswer = selectedButton.getText().toString();

        quizData.storeQuizQuestion(currentQuizId, currentState.getId(), userAnswer);
        if (userAnswer.equals(currentState.getCapital())) currentScore++;
        if (++currentQuestionIndex >= QUESTIONS_PER_QUIZ) {
            completeQuiz();
        } else {
            displayCurrentQuestion();
        }
    }

    /**
     * Completes the quiz and navigates to the results fragment.
     */
    private void completeQuiz() {
        quizData.updateQuizScore(currentQuizId, currentScore, QUESTIONS_PER_QUIZ);
        currentQuizId = -1;

        Bundle args = new Bundle();
        args.putInt("score", currentScore);
        args.putInt("total", QUESTIONS_PER_QUIZ);

        Fragment resultFragment = new CurrentQuizResultFragment();
        resultFragment.setArguments(args);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, resultFragment).commit();
    }

    /**
     * Saves the current state of the quiz.
     *
     * @param outState Bundle to save state information.
     */
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

    /**
     * Restores the quiz state from saved instance data.
     *
     * @param savedInstanceState Bundle containing saved state.
     */
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

    /**
     * Opens the quiz data when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "QuizFragment onResume called");
        if (quizData != null && !quizData.isDBOpen()) quizData.open();
        if (currentQuizId == -1) checkForInterruptedQuiz();
    }

    /**
     * Saves the quiz state and closes the quiz data when the fragment pauses.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (quizData != null) quizData.close();
    }
}

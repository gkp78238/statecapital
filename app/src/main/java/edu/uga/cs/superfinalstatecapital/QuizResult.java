package edu.uga.cs.superfinalstatecapital;

/**
 * The QuizResult class represents the result of a single quiz.
 * Each result includes an ID, the date the quiz was taken, and the score achieved.
 */
public class QuizResult {

    // Unique identifier for the quiz result
    private long id;

    // Date the quiz was taken
    private String date;

    // Score achieved in the quiz
    private int score;

    /**
     * Constructs a QuizResult with the specified values.
     *
     * @param id    the unique identifier for the quiz result
     * @param date  the date the quiz was taken
     * @param score the score achieved in the quiz
     */
    public QuizResult(long id, String date, int score) {
        this.id = id;
        this.date = date;
        this.score = score;
    }

    /**
     * Returns the ID of the quiz result.
     *
     * @return the ID of the quiz result
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the date the quiz was taken.
     *
     * @return the date of the quiz
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the score achieved in the quiz.
     *
     * @return the score of the quiz
     */
    public int getScore() {
        return score;
    }
}

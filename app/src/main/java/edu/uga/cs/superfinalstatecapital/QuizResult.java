package edu.uga.cs.superfinalstatecapital;

public class QuizResult {
    private long id;
    private String date;
    private int score;

    public QuizResult(long id, String date, int score) {
        this.id = id;
        this.date = date;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }
}
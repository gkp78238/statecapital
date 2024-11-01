package edu.uga.cs.superfinalstatecapital;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads and processes state data from a CSV file in the assets folder
 */
public class CSVReader {
    private static final String DEBUG_TAG = "CSVReader";

    /**
     * Reads state data from CSV file and stores it in database
     * Format: state name, capital, second city, third city
     */
    public static void readStateData(Context context, QuizData quizData) {
        try {
            // Open and read the CSV file from assets
            InputStream in = context.getAssets().open("state_capitals.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // Skip the header line
            String line = reader.readLine();

            // Process each line of state data
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 4) {
                    State state = new State(
                            fields[0].trim(), // state name
                            fields[1].trim(), // capital
                            fields[2].trim(), // second city
                            fields[3].trim()  // third city
                    );
                    quizData.storeState(state);
                }
            }
            reader.close();
            Log.d(DEBUG_TAG, "Successfully loaded states data from CSV");
        }
        catch (IOException e) {
            Log.e(DEBUG_TAG, "Error reading CSV file: " + e.getMessage());
        }
    }
}
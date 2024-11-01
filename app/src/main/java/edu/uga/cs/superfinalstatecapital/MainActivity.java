package edu.uga.cs.superfinalstatecapital;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Main activity for the State Capitals Quiz app.
 * Serves as the entry point and container for app fragments.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize with main menu fragment only on first creation
        // Prevents duplicate fragments when activity is recreated
        if (savedInstanceState == null) {
            Fragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mainFragment)
                    .commit();
        }
    }
}
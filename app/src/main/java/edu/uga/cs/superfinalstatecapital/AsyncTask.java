package edu.uga.cs.superfinalstatecapital;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

/**
 * AsyncTask handles background operations and updates UI thread when complete.
 * Used for database operations and other long-running tasks.
 */
public abstract class AsyncTask<Param,Result> {

    /**
     * Executes task in background thread and delivers result to UI thread
     */
    private void executeInBackground(Param... params) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Result result = doInBackground(params);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> onPostExecute(result));
        });
    }

    /**
     * Public method to start async operation
     */
    public void execute(Param... arguments) {
        executeInBackground(arguments);
    }

    /**
     * Override this method to perform computation in background thread
     */
    protected abstract Result doInBackground(Param... arguments);

    /**
     * Override this method to handle results in UI thread
     */
    protected abstract void onPostExecute(Result result);
}
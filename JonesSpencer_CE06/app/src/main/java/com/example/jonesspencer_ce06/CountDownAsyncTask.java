// Spencer Jones
// MDV3832-0 - 062024
// CountDownAsyncTask.java

package com.example.jonesspencer_ce06;

import android.os.AsyncTask;

public class CountDownAsyncTask extends AsyncTask<Integer, Integer, Float> {
    static final private long SLEEP_INTERVAL = 1000; // Sleep interval of 1 second
    final private OnFinished mFinishedInterface; // Interface for callback

    // Interface for notifying when task is finished
    public interface OnFinished {
        void onPre(); // Pre-execution callback

        void onProg(Integer... values); // Progress update callback

        void onPost(Float elapsedTime); // Post-execution callback

        void onCan(Float elapsedTime); // Cancellation callback

        void onFinished(); // Finished callback
    }

    // Constructor with public access modifier
    public CountDownAsyncTask(OnFinished finished) {
        mFinishedInterface = finished; // Initialize interface
    }

    // Method called before task starts
    @Override
    protected void onPreExecute() {
        mFinishedInterface.onPre();
    }

    // Background task to count down
    @Override
    protected Float doInBackground(Integer... params) {
        if (params == null || params.length == 0) {
            return 0.0f; // Return 0 if no parameters
        }

        int totalSeconds = params[0]; // Get total seconds
        long startTime = System.currentTimeMillis(); // Get start time

        while (totalSeconds > 0 && !isCancelled()) {
            publishProgress(totalSeconds); // Publish progress
            try {
                Thread.sleep(SLEEP_INTERVAL); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalSeconds--; // Decrement total seconds
        }

        return (System.currentTimeMillis() - startTime) / 1000.0f; // Calculate elapsed time
    }

    // Method to update progress
    @Override
    protected void onProgressUpdate(Integer... values) {
        mFinishedInterface.onProg(values);
    }

    // Method called after task finishes
    @Override
    protected void onPostExecute(Float elapsedTime) {
        mFinishedInterface.onPost(elapsedTime);
        mFinishedInterface.onFinished();
    }

    // Method called if task is cancelled
    @Override
    protected void onCancelled(Float elapsedTime) {
        mFinishedInterface.onCan(elapsedTime);
        mFinishedInterface.onFinished();
    }
}

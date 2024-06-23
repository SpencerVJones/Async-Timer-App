// Spencer Jones
// MDV3832-0 - 062024
// MainActivity.java

package com.example.jonesspencer_ce06;

// Imports
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements CountDownAsyncTask.OnFinished {
    static final String TAG = "CountdownTimer";
    private CountDownAsyncTask countdownTask = null;

    // UI Elements
    private Button buttonStart;
    private Button buttonStop;
    private EditText editMinutes;
    private EditText editSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find UI elements by ID
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        editMinutes = findViewById(R.id.editMinutes);
        editSeconds = findViewById(R.id.editSeconds);

        // Click listener for start button
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        // Click listener for stop button
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        buttonStop.setEnabled(false); // Disable stop button
    }

    // Method to start timer
    private void startTimer() {
        // Get text from EditText fields
        String minutesStr = editMinutes.getText().toString();
        String secondsStr = editSeconds.getText().toString();

        // Parse minutes and seconds
        int minutes = minutesStr.isEmpty() ? 0 : Integer.parseInt(minutesStr);
        int seconds = secondsStr.isEmpty() ? 0 : Integer.parseInt(secondsStr);

        // If time not valid - Show error message
        if (minutes == 0 && seconds == 0) {
            Toast.makeText(this, R.string.invalid_time_message, Toast.LENGTH_SHORT).show();
        } else {
            int totalSeconds = (minutes * 60) + seconds; // Calculate total seconds
            countdownTask = new CountDownAsyncTask(MainActivity.this); // Create new countdown task
            countdownTask.execute(totalSeconds); // Execute task
            buttonStart.setEnabled(false); // Disable start button
            buttonStop.setEnabled(true); // Enable stop button

            // Clear minutes and seconds fields
            editMinutes.setText("");
            editSeconds.setText("");
        }
    }

    // Method to stop timer
    private void stopTimer() {
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.cancel(true); // Cancel countdown task
        }
        buttonStart.setEnabled(true); // Enable start button
        buttonStop.setEnabled(false); // Disable stop button
    }

    // Method called before task starts
    @Override
    public void onPre() {
        // Show message - timer has started
        Toast.makeText(this, R.string.timer_started, Toast.LENGTH_SHORT).show();
    }

    // Method called to update progress
    @Override
    public void onProg(Integer... values) {
        int remainingTime = values[0]; // Get remaining time
        int minutes = remainingTime / 60; // Calculate minutes
        int seconds = remainingTime % 60; // Calculate seconds

        // Update minutes and seconds fields
        editMinutes.setText(String.format("%02d", minutes));
        editSeconds.setText(String.format("%02d", seconds));
    }

    // Method called after task finishes
    @Override
    public void onPost(Float elapsedTime) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(R.string.timer_expired) // Set message for dialog
                .setPositiveButton(android.R.string.ok, null) // Set OK button
                .show(); // Show dialog
        buttonStart.setEnabled(true); // Enable start button
        buttonStop.setEnabled(false); // Disable stop button
    }

    // Method called if task is cancelled
    @Override
    public void onCan(Float elapsedTime) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(getString(R.string.timer_stopped, elapsedTime.intValue())) // Set message for dialog
                .setPositiveButton(android.R.string.ok, null) // Set OK button
                .show(); // Show dialog
        buttonStart.setEnabled(true); // Enable start button
        buttonStop.setEnabled(false); // Disable stop button
    }

    // Method called when task is finished
    @Override
    public void onFinished() {
        buttonStart.setEnabled(true); // Enable start button
        buttonStop.setEnabled(false); // Disable stop button
    }
}
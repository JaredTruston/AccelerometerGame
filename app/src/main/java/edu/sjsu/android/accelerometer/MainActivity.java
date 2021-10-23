package edu.sjsu.android.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;

/*
 MainActivity Class for AccGame
 Creates an instance of WakeLock to keep screen on
  even when app is inactive
 Instantiates the SimulationView and sets it as
  content view for the activity
 Author: Jared Bechthold
*/
public class MainActivity extends AppCompatActivity {
    // Holds package name to pass when instantiating WakeLock
    private static final String TAG = "edu.sjsu.android.accelerometer:MainActivity";
    private PowerManager.WakeLock mWakeLock;    // Holds WakeLock instance
    private SimulationView mSimulationView;     // Holds instance of SimulationView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hides the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Instantiates the WakeLock to keep screen on
        PowerManager mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);

        // Instantiates SimulationView
        mSimulationView = new SimulationView(this);

        // Sets the content view of activity to SimulationView instance
        setContentView(mSimulationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Acquires WakeLock
        mWakeLock.acquire();
        // Starts simulation to register the listener
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release WakeLock
        mWakeLock.release();
        // Stop simulation to unregister the listener
        mSimulationView.stopSimulation();
    }
}
package edu.sjsu.android.accelerometer;

import android.hardware.Sensor;
import android.os.SystemClock;
import android.util.Log;

/*
 Particle Class for AccGame
 Provides implementation that details physics movement
  of the ball in SimulationView
 Used to handle collisions of ball with screen edges in
  SimulationView
 Author: Jared Bechthold
*/

public class Particle {
    private static final float COR = 0.7f;
    public float mPosX;     // X Position of ball
    public float mPosY;     // Y Position of ball
    private float mVelX;    // X Velocity of ball
    private float mVelY;    // Y Velocity of ball

    // Updates position of ball based on given accelerometer axis values
    public void updatePosition(float sx, float sy, float sz, long timeStamp){
        if (timeStamp < 0){
            // If timeStamp is an invalid value, do not calculate new values
            //  of change in time, velocity, or position
        }
        else {
            // calculates change in time
            float dt = (SystemClock.elapsedRealtimeNanos() - timeStamp) / 1000000000.0f;
            // calculates change in velocity
            mVelX += -sx * dt;
            mVelY += -sy * dt;
            // calculates change in position
            mPosX += mVelX * dt;
            mPosY += mVelY * dt;
        }
    }

    // Handles collisions of ball with given bounds
    public void resolveCollisionWithBounds(float mHorizontalBound, float mVerticalBound){
        if (mPosX > mHorizontalBound)
        {
            // Set ball position to Horizontal Bound if ball
            //  collides with bound and reverses velocity
            mPosX = mHorizontalBound;
            mVelX = -mVelX * COR;
        }
        else if (mPosX < -mHorizontalBound)
        {
            // Set ball position to Negative Horizontal Bound if ball
            //  collides with bound and reverses velocity
            mPosX = -mHorizontalBound;
            mVelX = -mVelX * COR;
        }
        if (mPosY > mVerticalBound)
        {
            // Set ball position to Vertical Bound if ball
            //  collides with bound and reverses velocity
            mPosY = mVerticalBound;
            mVelY = -mVelY * COR;
        }
        else if (mPosY < -mVerticalBound)
        {
            // Set ball position to Negative Vertical Bound if ball
            //  collides with bound and reverses velocity
            mPosY = -mVerticalBound;
            mVelY = -mVelY * COR;
        }
    }
}

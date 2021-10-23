package edu.sjsu.android.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.sql.Timestamp;

/*
 SimulationView Class for AccGame
 Outputs a view displaying the ball, basket, and field images.
 Instantiates an accelerometer sensor and provides methods
  for registering and unregistering the sensor listeners
 Author: Jared Bechthold
*/
public class SimulationView extends View implements SensorEventListener {
    SensorManager sensorManager;    // Holds SensorManager instance
    Sensor accelerometer;           // Defines accelerometer using SensorManager
    Display mDisplay;               // Holds Display instance
    Point size = new Point();       // Used to obtain size of view window

    // Fields for the images
    private Bitmap mField;  // Holds image for field
    private Bitmap mBasket; // Holds image for basket
    private Bitmap mBall;   // Holds image for ball

    // Defines size of ball and basket images
    private static final int BALL_SIZE = 128;
    private static final int BASKET_SIZE = 160;

    // Holds values for screen bounds and origin point (at center of screen)
    private float mXOrigin;
    private float mYOrigin;
    private float mHorizontalBound;
    private float mVerticalBound;

    // Holds acceleration values of 3 axes and Timestamp of data
    float x;
    float y;
    float z;
    long timeStamp;

    // Particle instance for tracking current position of the ball
    Particle ballParticle = new Particle();

    // Constructor
    public SimulationView(Context context){
        super(context);

        // Initialize images from drawable
        // Sets values of ball image
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBall = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        // Sets values of basket image
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        // Sets values of field image
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap field = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);


        // Initializes Display field
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mDisplay.getSize(size);
        // Initializes the screen bounds and origin point
        mHorizontalBound = size.x;
        mVerticalBound = size.y;
        mXOrigin = mHorizontalBound/2;
        mYOrigin = mVerticalBound/2;

        // Initializes the field image
        mField = Bitmap.createScaledBitmap(field, (int)mHorizontalBound, (int)mVerticalBound, true);

        // Checks for accelerometer sensor
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // Initializes accelerometer sensor
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        else{
            // Outputs an error message that accelerometer does not exist
            Toast.makeText(context, "Error: No accelerometer available", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Updates images from drawable
        // Sets values of ball image
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBall = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        // Sets values of basket image
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        // Sets values of field image
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap field = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);
        // Updates the field image
        mField = Bitmap.createScaledBitmap(field, (int)mHorizontalBound, (int)mVerticalBound, true);
    }

    // Obtains acceleration values of 3 axes and timeStamp value
    //  from SensorEvent
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Checks if sensor event sensor is of type accelerometer
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            // Initializes axis values of accelerometer based on screen orientation
            if (mDisplay.getRotation() == Surface.ROTATION_0) {
                x = sensorEvent.values[0];
                y = sensorEvent.values[1];
                z = sensorEvent.values[2];
            }
            else if (mDisplay.getRotation() == Surface.ROTATION_90) {
                x = -sensorEvent.values[1];
                y = sensorEvent.values[0];
                z = sensorEvent.values[2];
            }

            // Tracks time when axis values were obtained
            timeStamp = sensorEvent.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Registers Listener
    public void startSimulation(){
        if (accelerometer != null) {
            sensorManager.registerListener(this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Unregisters Listener
    public void stopSimulation(){
        sensorManager.unregisterListener(this);

        // Set timeStamp to an invalid value to make sure
        //  change in time does not account for when listener
        //  is unregistered
        timeStamp = -1;

    }

    // Draws fields of SimulationView to screen
    // Function is repeatedly called to update position
    //  of ball drawing
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draws the field and basket
        canvas.drawBitmap(mField, 0, 0, null);
        canvas.drawBitmap(mBasket, mXOrigin - BASKET_SIZE / 2, (float) (mYOrigin - BASKET_SIZE * 1.25), null);

        // Updates position of ball based on current accelerometer values
        ballParticle.updatePosition(x, y, z, timeStamp);
        // Checks if ball collides with boundaries and bounces ball if it does
        ballParticle.resolveCollisionWithBounds(mHorizontalBound/2 - BALL_SIZE/2, mVerticalBound/2 - BALL_SIZE/2);

        // Draws ball at current position relative to the center of screen
        canvas.drawBitmap(mBall,
                (mXOrigin - BALL_SIZE / 2) + ballParticle.mPosX,
                (mYOrigin - BALL_SIZE / 2) - ballParticle.mPosY, null);

        invalidate();
    }
}

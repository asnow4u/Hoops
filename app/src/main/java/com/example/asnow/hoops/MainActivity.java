package com.example.asnow.hoops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {

    private MainView v;

    /**************************
     *Setup the View
     **************************/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new MainView(this);
        setContentView(v);

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                v.clickListener(motionEvent);
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        v.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        v.resume();
    }

    /********************
     * Setup Layout
     ********************/
    class MainView extends SurfaceView implements Runnable {

        private Thread mainThread;
        SurfaceHolder holder;
        private Canvas canvas;
        private boolean running = true; //Change to false to stop app
        private ball ballArray[];
        private int ballNum;
        float clickX;
        float clickY;
        float clickVelX = 0;
        float clickVelY = 0;
        int clickRadius = 5;
        private VelocityTracker vt = null;
        private Boolean onBall = false;
        long currentTime;
        long previousTime;
        long elaspedTime;
        long leftOverTime;
        int timeStep;

        @SuppressLint("ClickableViewAccessibility")
        public MainView(Context context) {
            super(context);
            holder = getHolder();
            ballNum = 2;
            ballArray = new ball[20];

            for (int i = 0; i < ballNum; i++) {
                ballArray[i] = new ball();
            }

        }

        /*********************
         * Running App
         ********************/
        @Override
        public void run() {
            while (running) {

                if (holder.getSurface().isValid()) {

                    canvas = holder.lockCanvas();

                    //Draw Background
                    canvas.drawColor(Color.WHITE);

                    /*************
                     * Time Steps
                     ************/
                    //Calculate Elapsed Time
                    currentTime = System.currentTimeMillis();
                    elaspedTime = currentTime - previousTime;

                    //Reset PreviousTime
                    previousTime = currentTime;

                    //Divide elaspedTime Into Manageable Chunks Of 16 ms
                    timeStep = (int) ((float) elaspedTime + leftOverTime / 16);

                    //Limit timeStep To Prevent Freezing
                    timeStep = min(timeStep, 5);

                    //Store Left Over Time For Next Frame
                    leftOverTime = elaspedTime - (timeStep * 16);

                    for (int t = 0; t < timeStep; t++) {

                        /***********************
                        * Check Collisions For Each Ball
                        **********************/
                        checkCollision();


                        /**************************
                         * Update Ball Movement
                         ***************************/
                        for (int i = 0; i < ballNum; i++) {
                            ballArray[i].update(timeStep);
                        }
                    }

                    /************************
                    * Draw All Ball Objects Onto Screen
                    ***********************/
                    for (int i = 0; i < ballNum; i++) {
                       ballArray[i].draw(canvas);
                    }

                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        public void pause() {
            running = false;

            try {
                mainThread.join();
            } catch (InterruptedException e) {
                Log.e("ERR", "Joining Thread");
            }
        }

        public void resume() {
            running = true;
            mainThread = new Thread(this);
            mainThread.start();
        }


        /*******************
         * Check For Collisions
         *******************/
        public void checkCollision() {

            for (int i = 0; i < ballNum; i++) {

                //Check For Edge Collision
                //TODO Need to set up last x and y so vel is the same and doesnt increase
                if (ballArray[i].getY() < ballArray[i].getRadius()) {
                    ballArray[i].setY(2 * ballArray[i].getRadius() - ballArray[i].getY());

                } else if (ballArray[i].getX() < ballArray[i].getRadius()) {
                    ballArray[i].setX(2 * ballArray[i].getRadius() - ballArray[i].getX());

                    //} else if (canvas != null) {

                } else if (ballArray[i].getY() > canvas.getHeight() - ballArray[i].getRadius()) {
                        ballArray[i].setY(2 * (canvas.getHeight() - ballArray[i].getRadius()) - ballArray[i].getY());

                } else if (ballArray[i].getX() > canvas.getWidth() - ballArray[i].getRadius()) {
                    ballArray[i].setX(2 * (canvas.getWidth() - ballArray[i].getRadius()) - ballArray[i].getX());
                }

                //}

                //Check For Ball to Ball Collisions
                for (int j = 0; j + i < ballNum; j++) {
                    if (i != j + i) {
                        intercept(ballArray[i].getX(), ballArray[i].getY(), ballArray[i].getRadius(), ballArray[j].getX(), ballArray[j].getY(), ballArray[j].getRadius(), i, j);
                    }
                }
            }
        }


        /**************************
         * View OnClickListener
         * Check For:
         * Down Click - Check If On A Ball
         * Above Click - Reset
         * Movement - Move Ball Or Cursor
         *************************/
        public void clickListener(MotionEvent me) {
            switch (me.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    clickX = me.getX();
                    clickY = me.getY();

                    //Set Velocity Tracker For Touch Input
                    if (vt == null) {
                        vt = VelocityTracker.obtain();
                    } else {
                        vt.clear();
                    }

                    vt.addMovement(me);

                    for (int i = 0; i < ballNum; i++) {
                        //NEED TO CHECK IF CLICKED ON BALL OBJ
                        //return a bool variable from function
                        onBall = ballArray[i].checkOnBall(clickX, clickY);
                    }

                    if (onBall) {
                        //function to update ball cordinates with respect to click cordinates
                        //gravity is negated
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    //let go of held ball
                    onBall = false;

                    //Reset Click Velocity
                    clickVelX = 0;
                    clickVelY = 0;
                    break;

                case MotionEvent.ACTION_MOVE:

                    //Updates Click Position
                    clickX = me.getX();
                    clickY = me.getY();

                    //Update Click Velocity
                    vt.addMovement(me);
                    vt.computeCurrentVelocity(1000);
                    clickVelX = vt.getXVelocity();
                    clickVelY = vt.getYVelocity();

                    //check if ball exist on first click (BOOL)
                    if (onBall) {
                        //function to update ball cordinates with respect to click cordinates
                        //gravity is negated
                    } else {
                        //be able to push balls around
                        //check for collisions (call ball.collision())
                        for (int i = 0; i < ballNum; i++) {
                            intercept(clickX, clickY, clickRadius, ballArray[i].getX(), ballArray[i].getY(), ballArray[i].getRadius(), i, -1);
                        }

                    }
            }
        }

        /************************
         * Check For Collision Among Perams
         * Collision is determined if distance between
         the center of the two circles is shorter then the two radiuses
         * Determine Opposing Force For Each Obj
         ************************/
        public void intercept(float X1, float Y1, int radius1, float X2, float Y2, int radius2, int i, int j) {

            //Get Differences between the X and Y
            float Xdiff = X1 - X2;
            float Ydiff = Y1 - Y2;
            double angle;
            float lengthY;
            float lengthX;
            float lengthErrorX;
            float lengthErrorY;

            //Find sum of radiuses
            int radiusSum = radius1 + radius2;

            //Detect if there is a collision
            if (radiusSum >= sqrt((Xdiff * Xdiff) + (Ydiff * Ydiff))) {

                //Collision with touch event
                if (j < 0) {

                    ballArray[i].setVel(clickVelX, clickVelY, timeStep);

                    //Collision with another ball
                } else {

                    angle = asin(Ydiff / radiusSum);

                    //Find needed x and y
                    lengthY = (float) (radiusSum * sin(angle));
                    lengthX = (float) (radiusSum * cos(angle));
                    lengthErrorX = lengthX - Xdiff;
                    lengthErrorY = lengthY - Ydiff;

                    //Set X and Y Position and adjust last X and Y Position
                    if (X1 > X2) {
                        //Ball 1
                        ballArray[i].setLastX(X1 + lengthErrorX / 2);
                        ballArray[i].setX(ballArray[j].getVelX() + ballArray[i].getLastX());

                        //Ball 2
                        ballArray[j].setLastX(X2 - lengthErrorX / 2);
                        ballArray[j].setX(ballArray[i].getVelX() + ballArray[j].getLastX());

                    } else {
                        //Ball 1
                        ballArray[i].setLastX(X1 - lengthErrorX / 2);
                        ballArray[i].setX(ballArray[j].getVelX() + ballArray[i].getLastX());

                        //Ball 2
                        ballArray[j].setLastX(X2 + lengthErrorX / 2);
                        ballArray[j].setX(ballArray[i].getVelX() + ballArray[j].getLastX());
                    }

                    if (Y1 > Y2) {
                        //Ball 1
                        ballArray[i].setLastY(Y1 + lengthErrorY / 2);
                        ballArray[i].setY(ballArray[j].getVelY() + ballArray[i].getLastY());

                        //Ball 2
                        ballArray[j].setLastY(Y2 - lengthErrorY / 2);
                        ballArray[j].setY(ballArray[i].getVelY() + ballArray[j].getLastY());

                    } else {
                        //Ball 1
                        ballArray[i].setLastY(Y1 - lengthErrorY / 2);
                        ballArray[i].setY(ballArray[j].getVelY() + ballArray[i].getLastY());

                        //Ball 2
                        ballArray[j].setLastY(Y2 + lengthErrorY / 2);
                        ballArray[j].setY(ballArray[i].getVelY() + ballArray[j].getLastY());
                    }

                }
            }


        }
    }
}



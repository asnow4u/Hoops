package com.example.asnow.hoops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.Random;

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
        private Bitmap backboard, net;
        private boolean running = true; //Change to false to stop app

        private ball ballArray[];
        private int ballNum;

        private Hoop hoop;

        float clickX, clickY;
        float clickVelX = 0;
        float clickVelY = 0;
        int clickRadius = 5;
        private VelocityTracker vt = null;

        private Boolean onBall = false;

        long currentTime, previousTime;
        long elaspedTime, leftOverTime;
        int timeStep;
        int randomNum;
        float randomX, randomY;

        @SuppressLint("ClickableViewAccessibility")
        public MainView(Context context) {
            super(context);
            holder = getHolder();

            //Backboard
            backboard = BitmapFactory.decodeResource(getResources(), R.drawable.backboard);
            net = BitmapFactory.decodeResource(getResources(), R.drawable.net);

            hoop = new Hoop(300, 200, backboard, net, false, false);

            ballNum = 1;
            ballArray = new ball[20];

            for (int i = 0; i < ballNum; i++) {

                //Randomly pick x and y
                randomNum = new Random().nextInt(6);
                randomX = pickX(randomNum);
                randomNum = new Random().nextInt(6);
                randomY = pickY(randomNum);

                //Check if space is open
                if (i != 0) { //Not First Ball
                    for (int j = 0; j < i; j++) {
                        while (ballArray[j].checkCollision(randomX, randomY, 100)) { //TODO NOTE: radius is set on a static value (not randomized)

                            //Randomly pick x and y
                            randomNum = new Random().nextInt(6);
                            randomX = pickX(randomNum);
                            randomNum = new Random().nextInt(6);
                            randomY = pickY(randomNum);

                            j=0; //Reset Counter
                        }
                    }
                }

                ballArray[i] = new ball(randomX, randomY, 75);
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

                    //Draw Backboard
                    //TODO backboard needs a net drawn onto it (back part of the net)
                    //TODO update x and y locations for backboard and net

                    hoop.drawBackboard(canvas);


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

                        /**********************
                         Check Collisions For Each Ball
                         **********************/
                        checkCollision();

                        /************************
                         * Update Hoop
                         ***********************/
                        hoop.updateMovement(canvas.getHeight(), canvas.getWidth());


                        /**************************
                         * Update Ball Movement
                         ***************************/
                        for (int i = 0; i < ballNum; i++) {
                            ballArray[i].update(timeStep);

                            Log.d("value", "ball " + i + " velX: " + ballArray[i].getVelX() + " velY: " + ballArray[i].getVelY() + " x: " + ballArray[i].getX() + " y: " + ballArray[i].getY());
                        }
                    }

                    /************************
                    * Draw All Ball Objects Onto Screen
                    ***********************/
                    for (int i = 0; i < ballNum; i++) {
                       ballArray[i].draw(canvas);
                    }

                    //Draw net in front of the ball
                    hoop.drawNet(canvas);

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
         * Checks For Collisions
         *******************/
        public void checkCollision() {

            int radiusSum;
            double angle;
            float Xdiff, Ydiff;
            float lengthX, lengthY;
            float missingX, missingY;
            float velX, velY;


            for (int i = 0; i < ballNum; i++) {

                //Check For Ball to Ball Collisions
                for (int j = i+1; j < ballNum; j++) {

                    //Check For Collision
                    if (ballArray[i].checkCollision(ballArray[j].getX(), ballArray[j].getY(), ballArray[j].getRadius())) {

                        Log.d("value", "Collision");

                        //TODO test to see if we need this (note will need angle later on)
                        //Find the difference in X and Y between the two ball objs
                        Xdiff = ballArray[i].getX() - ballArray[j].getX();
                        Ydiff = ballArray[i].getY() - ballArray[j].getY();

                        //Find the Radius Sum and angle from the differences in X and Y
                        radiusSum = ballArray[i].getRadius() + ballArray[j].getRadius();
                        angle = asin(Ydiff / sqrt((Xdiff * Xdiff) + (Ydiff * Ydiff)));

                        //Use the angle to find the necessary length for X and Y having the radius sum as hypotenuse
                        lengthY = (float) (radiusSum * sin(angle));
                        lengthX = (float) (radiusSum * cos(angle));

                        //Find the needed amount to be added onto current X and Y to equal lengthX and lengthY
                        missingX = lengthX - Xdiff;
                        missingY = lengthY - Ydiff;

                        //Check if ball i is colliding with the bottom edge and ball j is not
                        if (ballArray[i].getY() > canvas.getHeight() - ballArray[i].getRadius() && ballArray[j].getY() < canvas.getHeight() - ballArray[j].getRadius()){

                            //Separate from edge
                            ballArray[i].edgeCollision(canvas.getHeight(), canvas.getWidth(), timeStep);

                            //Check which direction the velocities would need to go
                            if (ballArray[i].getX() > ballArray[j].getX()) {

                                //Add missingX/missingY to ball 2
                                ballArray[j].setX(ballArray[j].getX() + missingX);
                                ballArray[j].setY(ballArray[j].getY() - missingY);

                            } else {

                                //Subtract missingX/missingY to ball 2
                                ballArray[j].setX(ballArray[j].getX() - missingX);
                                ballArray[j].setY(ballArray[j].getY() - missingY);
                            }


                            //Set Vel i
                            ballArray[i].setVelX(ballArray[i].getVelX() + ballArray[j].getVelX(), timeStep);

                            //Set Vel j
                            if (ballArray[j].getVelY() > 0) {
                                //Positive Velocity
                                ballArray[j].setVel((-1) * ballArray[j].getVelX(), (-1) * ballArray[j].getVelY(), timeStep);

                            } else {
                                //Negative Velocity
                                ballArray[j].setVel((-1) * ballArray[j].getVelX(), ballArray[j].getVelY(), timeStep);
                            }

                        //Check if ball j is colliding with the edge and ball i is not
                        } else if (ballArray[j].getY() > canvas.getHeight() - ballArray[j].getRadius() && ballArray[i].getY() < canvas.getHeight() - ballArray[i].getRadius()){

                            //Separate from edge
                            ballArray[j].edgeCollision(canvas.getHeight(), canvas.getWidth(), timeStep);

                            //Check for positive Velocity
                            if(ballArray[i].getVelY() > 0) {

                                //Check which direction the velocities would need to go
                                if (ballArray[j].getX() > ballArray[i].getX()) {

                                    //Add missingX/missingY to ball 2
                                    ballArray[i].setX(ballArray[i].getX() + missingX);
                                    ballArray[i].setY(ballArray[i].getY() - missingY);

                                } else {

                                    //Subtract missingX/missingY to ball 2
                                    ballArray[i].setX(ballArray[i].getX() - missingX);
                                    ballArray[i].setY(ballArray[i].getY() - missingY);
                                }

                                //Set Vel j
                                ballArray[j].setVelX(ballArray[j].getVelX() + ballArray[i].getVelX(), timeStep);

                                //Set Vel i
                                if (ballArray[i].getVelY() > 0) {
                                    //Positive Velocity
                                    ballArray[i].setVel((-1) * ballArray[i].getVelX(), (-1) * ballArray[j].getVelY(), timeStep);

                                } else {
                                    //Negative Velocity
                                    ballArray[i].setVel((-1) * ballArray[i].getVelX(), ballArray[j].getVelY(), timeStep);
                                }
                            }

                        } else {

                            //Set X and Y Position and adjust last X and Y Position
                            if (ballArray[i].getX() > ballArray[j].getX()) {
                                //Ball 1(i)

                                //Seperate balls from collision
                                ballArray[i].setX(ballArray[i].getX() + (missingX / 2));

                                //Grab velocity of first ball before changing it
                                velX = ballArray[i].getVelX();

                                //Change Velocity
                                ballArray[i].setVelX(ballArray[j].getVelX(), timeStep);

                                //ballArray[i].setLastX(ballArray[i].getX() - ballArray[j].getVelX());

                                //Ball 2(j)

                                //Seperate balls from collision
                                ballArray[j].setX(ballArray[j].getX() - (missingX / 2));

                                //Change Velocity
                                ballArray[j].setVelX(velX, timeStep);

                                //ballArray[j].setLastX(ballArray[j].getX() - ballArray[i].getVelX());

                            } else {
                                //Ball 1
                                ballArray[i].setX(ballArray[i].getX() - (missingX / 2)); //Other approch for ballencing gravity

                                velX = ballArray[i].getVelX();

                                ballArray[i].setVelX(ballArray[j].getVelX(), timeStep);

                                //ballArray[i].setLastX(ballArray[i].getX() - ballArray[j].getVelX());

                                //Ball 2
                                ballArray[j].setX(ballArray[j].getX() + (missingX / 2)); //Other approch for ballencing gravity
                                ballArray[j].setVelX(velX, timeStep);

                                //ballArray[j].setLastX(ballArray[j].getX() - ballArray[i].getVelX());
                            }

                            if (ballArray[i].getY() > ballArray[j].getY()) {
                                //Ball 1
                                ballArray[i].setY(ballArray[i].getY() + (missingY / 2));

                                velY = ballArray[i].getVelY();

                                ballArray[i].setVelY(ballArray[j].getVelY(), timeStep);

                                //ballArray[i].setLastY(ballArray[i].getY() - ballArray[j].getVelY());

                                //Ball 2
                                ballArray[j].setY(ballArray[i].getY() - (missingY / 2));
                                ballArray[j].setVelY(velY, timeStep);

                                //ballArray[j].setLastY(ballArray[j].getY() - ballArray[i].getVelY());

                            } else {
                                //Ball 1
                                ballArray[i].setY(ballArray[i].getY() - (missingY / 2));

                                velY = ballArray[i].getVelY();

                                ballArray[i].setVelY(ballArray[j].getVelY(), timeStep);

                                //ballArray[i].setLastY(ballArray[i].getY() - ballArray[j].getVelY());

                                //Ball 2
                                ballArray[j].setY(ballArray[j].getY() + (missingY / 2));
                                ballArray[j].setVelY(velY, timeStep);

                                //ballArray[j].setLastY(ballArray[j].getY() - ballArray[i].getVelY());
                            }
                        }
                    }
                }


                //Check For Edge Collision
                if (ballArray[i].checkEdgeCollision(ballArray[i].getX(), ballArray[i].getY(), ballArray[i].getRadius(), canvas.getHeight(), canvas.getWidth())) {
                    ballArray[i].edgeCollision(canvas.getHeight(), canvas.getWidth(), timeStep);
               }

                //TODO check for hoop collision
                //TODO add dampening effect
                //TODO when the collision happens not by directly hitting but by grazzing, ex: moving to the right, barly touches the top of the collision circle, reverses vel (what?)
                //Checks the two points from the net obj
                if (ballArray[i].checkCollision(hoop.getHoopCollisionLeftX(), hoop.getHoopCollisionLeftY(), hoop.getHoopCollisionRadius())){
                    ballArray[i].setVel((-1) * ballArray[i].getVelX(), (-1) * ballArray[i].getVelY(), timeStep);

                } else if (ballArray[i].checkCollision(hoop.getHoopCollisionRightX(), hoop.getHoopCollisionRightY(), hoop.getHoopCollisionRadius())){
                    ballArray[i].setVel((-1) * ballArray[i].getVelX(), (-1) * ballArray[i].getVelY(), timeStep);

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

                        //Check if clicked on ball obj
                        onBall = ballArray[i].checkOnBall(clickX, clickY);
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

                    //check if ball exist on first click
                    if (onBall) {
                        //watch cord till not on ball obj, then set onball=false

                    } else {
                        //be able to push balls around
                        //check for collisions
                        for (int i = 0; i < ballNum; i++) {
                            //Check For Collision With Touch Event
                            if (ballArray[i].checkCollision(clickX, clickY, clickRadius)) {
                                ballArray[i].setVel(clickVelX, clickVelY, timeStep);
                            }
                        }
                    }
            }
        }

        /******************
         * Randomize Ball X Start
         ******************/
        private float pickX(int num){
            switch(num){
                case 0:
                    return 150;
                case 1:
                    return 300;
                case 2:
                    return 450;
                case 3:
                    return 600;
                case 4:
                    return 750;
                case 5:
                    return 900;
            }

            return 1;
        }

        /******************
         * Randomize Ball Y Start
         ******************/
        private float pickY(int num){
            switch(num){
                case 0:
                    return 900;
                case 1:
                    return 1000;
                case 2:
                    return 1100;
                case 3:
                    return 1200;
                case 4:
                    return 1300;
                case 5:
                    return 1400;
            }

            return 1;
        }
    }
}



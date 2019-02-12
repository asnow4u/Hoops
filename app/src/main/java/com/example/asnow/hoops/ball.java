package com.example.asnow.hoops;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

import static android.graphics.Color.rgb;
import static java.lang.Math.min;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;

public class ball {
    //Coordinates and Movement Variables
    private float x;
    private float y;
    private float lastX;
    private float lastY;
    private float velX;
    private float velY;
    private float accX;
    private float accY;
    private int radius;

    //Paint
    private Paint ballColor = new Paint();
    private Paint ballBorder = new Paint();

    private float gravity = (float)0.098;



    /***************
     * Ball Obj
     * Set values
     **************/

    ball(){

        int randomNum = new Random().nextInt(6);
        //pickRadius(randomNum);
        radius = 100;
        //pickX(randomNum);
        randomNum = new Random().nextInt(6);
        //picky(randomNum);
        lastX = x;
        lastY = y;

        //Paint
        ballBorder.setStyle(Paint.Style.STROKE);
        ballBorder.setColor(Color.BLACK);
        ballBorder.setStrokeWidth(5);
        ballColor.setStyle(Paint.Style.FILL);
        randomNum = new Random().nextInt(6);
        pickColor(randomNum);
    }

    ball(float ballX, float ballY, int ballRadius){
        x = ballX;
        y = ballY;
        lastX = x;
        lastY = y;
        radius = ballRadius;

        //Paint
        ballBorder.setStyle(Paint.Style.STROKE);
        ballBorder.setColor(Color.BLACK);
        ballBorder.setStrokeWidth(5);
        ballColor.setStyle(Paint.Style.FILL);
        int randomNum = new Random().nextInt(6);
        pickColor(randomNum);
    }



    /*************
     * Update Ball Movement
     * Determine Direction and Velocity of ball obj
     *************/

    public void update(int timeStep){


        accY = gravity; //NOTE Could apply mass of ball based on size (F = mg)

        //Velocity
        velX = x - lastX;
        velY = y - lastY;

        //Cap Velocity
        if (velX > 50){
            velX = 50;
        } else if (velX < -50){
            velX = -50;
        }
        if (velY > 50){
            velY = 50;
        } else if (velY < -50){
            velY = -50;
        }

        //Next Point
        float nextX = x + velX + 0.5f * accX * (timeStep * timeStep);
        float nextY = y + velY + 0.5f * accY * (timeStep * timeStep);

        //Update
        lastX = x;
        lastY = y;
        x = nextX;
        y = nextY;
        //accX = 0;
        //accY = 0;
    }



    /*********************
     * Draw Ball
     *********************/

    public void draw(Canvas canvas){
        canvas.drawCircle(x, y, radius, ballBorder);
        canvas.drawCircle(x, y, radius, ballColor);
    }



    /*********************
     * Check If Touch Was On Ball Obj
     *********************/

    public boolean checkOnBall(float clickX, float clickY){

        float xDiff = clickX - x;
        float yDiff = clickY - y;

        //Check if diff is within the radius
        if (sqrt((xDiff * xDiff) + (yDiff * yDiff)) < radius){
            return true;
        } else {
            return false;
        }
    }



    /******************************
     * Check For Ball To Ball Collision
     * Check to see if the combined radius's is greater then the hypothis of the diffrences in x and y for the two colidding balls
     *******************************/

    public boolean checkCollision(float Ball_X, float Ball_Y, int Ball_Radius){

        int radiusSum = radius + Ball_Radius;
        float Xdiff = x - Ball_X;
        float Ydiff = y - Ball_Y;

        if (radiusSum >= sqrt((Xdiff * Xdiff) + (Ydiff * Ydiff))){
            return true;

        } else {
            return false;
        }
    }


    /*************************************
     * Check Collision on edge of screen
     ************************************/
    public boolean checkEdgeCollision(float ballX, float ballY, int ballRadius, int height, int width){

        //Top Edge
        if (ballY < ballRadius) {
            return true;

        //Bottom Edge
        } else if (ballY > height - ballRadius) {
            return true;

        //Left Edge
        } else if (ballX < ballRadius) {
            return true;

        //Right Edge
        } else if (ballX > width - ballRadius) {
            return true;

        } else {
            return false;
        }
    }


    /************************
     * Resolve Edge Collision
     ***********************/

    public void edgeCollision(int height, int width, int timeStep){
        //Top Edge
        if (y < radius){
            y = radius;
            lastY = (y + (velY * 3) / 4);

        //Bottom Edge
        } else if (y > height - radius) {
            y = height - radius;
            lastY = (y + (velY * 3) / 4);
        }

        //Left Edge
        if (x < radius) {
            x = radius;
            setVel(((-1) * velX * 3) / 4, velY, timeStep);

        //Right Edge
        } else if (x > width - radius) {
            x = width - radius;
            setVel(((-1) * velX * 3) / 4, velY, timeStep);
        }
    }




    public void setVel(float colVelX, float colVelY, int timeStep){
        setVelX(colVelX, timeStep);
        setVelY(colVelY, timeStep);
    }

    public void setVelX(float colVelX, int timestep){
        //Cap Velocity
        if (colVelX > 50){
            colVelX = 50;
        } else if (colVelX < -50){
            colVelX = -50;
        }

        lastX = x;
        x = x + colVelX;
    }

    public void setVelY(float colVelY, int timestep){
        //Cap Velocity
        if (colVelY > 50){
            colVelY = 50;
        } else if (colVelY < -50){
            colVelY = -50;
        }

        lastY = y;
        y = y + colVelY + 0.5f * accY * (timestep * timestep);
    }

    public void setX(float newX){
        x = newX;
    }

    public void setY(float newY){
        y = newY;
    }

    public void setLastX(float newX){
        lastX = newX;

    }

    public void setLastY(float newY){
        lastY = newY;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getLastX(){
        return lastX;
    }

    public float getLastY(){
        return lastY;
    }

    public float getVelX(){
        return velX;
    }

    public float getVelY(){
        return velY;
    }

    public int getRadius(){
        return radius;
    }

//    /******************
//     * Randomize Ball X Start
//     ******************/
//    private void pickX(int num){
//        switch(num){
//            case 0:
//                x = 150;
//                break;
//            case 1:
//                x = 300;
//                break;
//            case 2:
//                x = 450;
//                break;
//            case 3:
//                x = 600;
//                break;
//            case 4:
//                x = 750;
//                break;
//            case 5:
//                x = 900;
//                break;
//        }
//    }
//
//    /******************
//     * Randomize Ball Y Start
//     ******************/
//    private void picky(int num){
//        switch(num){
//            case 0:
//                y = 900;
//                break;
//            case 1:
//                y = 1000;
//                break;
//            case 2:
//                y = 1100;
//                break;
//            case 3:
//                y = 1200;
//                break;
//            case 4:
//                y = 1300;
//                break;
//            case 5:
//                y = 1400;
//                break;
//        }
//    }

    /******************
     * Randomize Ball Size
     * MAY NOT NEED
     ******************/
    private void pickRadius(int num){
        switch(num){
            case 0:
                radius = 50;
                break;
            case 1:
                radius = 75;
                break;
            case 2:
                radius = 100;
                break;
            case 3:
                radius = 125;
                break;
            case 4:
                radius = 150;
                break;
            case 5:
                radius = 175;
                break;
            case 6:
                radius = 200;
                break;
        }
    }

    /******************
     * Randomize Ball Color
     ******************/
    private void pickColor(int num){
        switch(num){
            case 0:
                ballColor.setColor(Color.RED);
                break;
            case 1:
                ballColor.setColor(Color.GREEN);
                break;
            case 2:
                ballColor.setColor(Color.BLUE);
                break;
            case 3:
                ballColor.setColor(Color.YELLOW);
                break;
            case 4:
                ballColor.setColor(rgb(255, 165, 0));
                break;
            case 5:
                ballColor.setColor(rgb(178, 58, 238));
        }
    }


}

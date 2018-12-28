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
    private float x;
    private float y;
    private float lastX;
    private float lastY;
    private float velX;
    private float velY;
    private float accX;
    private float accY;
    private int radius;
    private Paint ballColor = new Paint();
    private Paint ballBorder = new Paint();


    private float gravity = (float) 0.098;


    ball(){

        int randomNum = new Random().nextInt(6);
        //pickRadius(randomNum);
        radius = 100;
        pickX(randomNum);
        y = 350;
        lastX = x;
        lastY = y;

        ballBorder.setStyle(Paint.Style.STROKE);
        ballBorder.setColor(Color.BLACK);
        ballBorder.setStrokeWidth(5);
        ballColor.setStyle(Paint.Style.FILL);
        randomNum = new Random().nextInt(6);
        pickColor(randomNum);
    }

    /*************
     * Update Ball Movement
     *************/

    public void update(int timeStep){
        accY += gravity; //NOTE Could apply mass of ball based on size (F = mg)

        //Velocity
        velX = x - lastX;
        velY = y - lastY;


        //Cap Velocity
        if (velX > 100){
            velX = 100;
        } else if (velX < -100){
            velX = -100;
        }
        if (velY > 100){
            velY = 100;
        } else if (velY < -100){
            velY = -100;
        }

        //Next Point
        float nextX = x + velX + 0.5f * accX * (timeStep * timeStep);
        float nextY = y + velY + 0.5f * accY * (timeStep * timeStep);

        //Update
        lastX = x;
        lastY = y;
        x = nextX;
        y = nextY;
        accX = 0;
        accY = 0;
    }


    /*********************
     * Draw Ball
     *********************/
    public void draw(Canvas canvas){
        canvas.drawCircle(x, y, radius, ballBorder);
        canvas.drawCircle(x, y, radius, ballColor);
    }


    /*********************
     * Check If Touch Was On Circle
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

    public void setVel(float collVelX, float collVelY, int timeStep){
        x = x + collVelX;
        y = y + collVelY + accY * (timeStep * timeStep);

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

    /******************
     * Randomize Ball X Start
     ******************/
    private void pickX(int num){
        switch(num){
            case 0:
                x = 150;
                break;
            case 1:
                x = 300;
                break;
            case 2:
                x = 450;
                break;
            case 3:
                x = 600;
                break;
            case 4:
                x = 750;
                break;
            case 5:
                x = 900;
                break;
        }
    }

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

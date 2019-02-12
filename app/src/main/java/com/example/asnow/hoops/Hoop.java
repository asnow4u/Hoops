package com.example.asnow.hoops;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Hoop {
    private Bitmap backboard, net;
    private float backboardX, backboardY;
    private float backboardVelX, backboardVelY;
    private float netDisplacementX; //NOTE: This is used to make the image smoother as it moves across the screen
    private float netDisplacementY;
    private float hoopCollisionLeftX;
    private float hoopCollisionRightX;
    private float hoopCollisionLeftY;
    private float hoopCollisionRightY;
    private int hoopCollisionRadius;

    private boolean horizontalMovement;
    private boolean verticalMovement;


    Hoop(float setBackboardX, float setBackboardY, Bitmap setBitmapBackboard, Bitmap setBitmapNet, boolean hMovement, boolean vMovement){
        backboardX = setBackboardX;
        backboardY = setBackboardY;
        netDisplacementX = (float)0.24;
        netDisplacementY = (float)0.53;
        backboard = setBitmapBackboard;
        net = setBitmapNet;
        horizontalMovement = hMovement;
        verticalMovement = vMovement;
        hoopCollisionRadius = 7;


        if (horizontalMovement){
            backboardVelX = 2;
        }

        if (verticalMovement){
            backboardVelY = 2;
        }
    }

    public void updateMovement(int screenHeight, int screenWidth){

        /********************
        * Update Horizontal Movement
        *********************/
        if (horizontalMovement) {
            backboardX += backboardVelX;

            //Check Right Edge Collision
            if (hoopCollisionRightX + hoopCollisionRadius > screenWidth) {

                //Reverse and get off edge
                backboardVelX *= (-1);
                netDisplacementX = (float) 0.27;

                while (hoopCollisionRightX + hoopCollisionRadius >= screenWidth) {
                    backboardX += backboardVelX;
                    hoopCollisionRightX = backboard.getWidth() * (float) 0.665 + backboardX;
                }

                //Check Left Edge Collision
            } else if (hoopCollisionLeftX - hoopCollisionRadius < 0) {

                //Reverse and get off edge
                backboardVelX *= (-1);
                netDisplacementX = (float) 0.24;

                while (hoopCollisionLeftX - hoopCollisionRadius <= 0) {
                    backboardX += backboardVelX;
                    hoopCollisionLeftX = backboard.getWidth() * (float) 0.34 + backboardX;
                }
            }
        }

        /***********************
        * Update Vertical Movement
        ***********************/
        if (verticalMovement) {
            backboardY += backboardVelY;

            //Check Top Edge Collision
            //if ((backboardY + backboardY * (float)0.19) < 0){ //TODO get this to work and play around with the displacement
            if (backboardY + 85 < 0){

                //Reverse and get off edge
                backboardVelY *= (-1);
                netDisplacementY = (float)0.52;

                while (backboardY + 85 <= 0){
                    backboardY += backboardVelY;
                }

            //Check Bottom Half Of The Screen
            } else if (backboardY > screenHeight/3){

                //Reverse and get off edge
                backboardVelY *= (-1);
                netDisplacementY = (float)0.545;

                while (backboardY >= screenHeight/3){
                    backboardY += backboardVelY;
                }

            }
        }
    }



    public void drawBackboard(Canvas canvas){

        //update collision position
        hoopCollisionLeftX = backboard.getWidth() * (float).34 + backboardX;
        hoopCollisionLeftY = backboard.getHeight() * (float).665 + backboardY;
        hoopCollisionRightX = backboard.getWidth() * (float).665 + backboardX;
        hoopCollisionRightY = backboard.getHeight() * (float).665 + backboardY;

        canvas.drawBitmap(backboard, backboardX, backboardY, null);
    }

    public void drawNet(Canvas canvas){
        canvas.drawBitmap(net, backboard.getWidth() * netDisplacementX + backboardX, backboard.getHeight() * netDisplacementY + backboardY, null);
    }


    public float getHoopCollisionLeftX() {
        return hoopCollisionLeftX;
    }
    public float getHoopCollisionRightX() {
        return hoopCollisionRightX;
    }

    public float getHoopCollisionLeftY() {
        return hoopCollisionLeftY;
    }

    public float getHoopCollisionRightY() {
        return hoopCollisionRightY;
    }

    public int getHoopCollisionRadius() {
        return hoopCollisionRadius;
    }
}

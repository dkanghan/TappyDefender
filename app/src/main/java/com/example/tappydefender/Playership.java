package com.example.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Playership
{
    private Bitmap bitmap;
    private int x,y;
    private int speed = 0;
    private boolean boosting = false;

    private final int GRAVITY = -12;

    private int maxY;
    private int minY;
    private Rect hitBox;
    private int shieldStrength = 2;

    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    public Playership(Context context, int screenX, int screenY) {
    x=50;
    y=50;
    speed = 1;
    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship2);
    hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    minY = 0;
    maxY = screenY - bitmap.getHeight();

    }

    public void setBoosting(){
        boosting = true;
    }
    public void stopBoosting(){
        boosting = false;
    }

    public void update(){
        if(boosting){
            speed += 2;
        }
        else{
            speed -= 5;
        }

        if (speed > MAX_SPEED){
            speed = MAX_SPEED;
        }
        if(speed < MIN_SPEED){
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        if( y < minY){
            y = minY;
        }

        if(y>maxY){
            y = maxY;
        }
        x++;

        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public Rect getHitbox(){
        return hitBox;
    }
    public int getSpeed(){
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }
    public void reduceShieldStrength(){
        shieldStrength --;
    }
}



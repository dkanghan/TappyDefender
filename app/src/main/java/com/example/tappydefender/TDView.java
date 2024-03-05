package com.example.tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable{
    private final int screenX;
    private final int screenY;

    volatile boolean playing;
    Thread gameThread = null;
    private Playership player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private final Paint paint;
    private Canvas canvas;
    private final SurfaceHolder ourHolder;
    private final Context context;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    boolean hitDetected = false;
    private boolean gameEnded = false;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;

        prefs = context.getSharedPreferences("HiScores", Context.MODE_PRIVATE);
// Initialize the editor ready
        editor = prefs.edit();
// Load fastest time from a entry in the file
// labeled "fastestTime"
// if not available highs-core = 1000000
        fastestTime = prefs.getLong("fastestTime", 1000000);
        startGame();

    }

    private void startGame(){

        //Initialize game objects
        player = new Playership(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);
        int numSpecs = 40;
        for (int i = 0; i < numSpecs; i++) {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }
        // Reset time and distance
        distanceRemaining = 10000;// 10 km
        timeTaken = 0;
        // Get start time
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }


    @Override
    public void run() {
       while (playing){
           update();
           draw();
           control();
       }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if(gameEnded){
                    startGame();
                }
                break;
        }
        return true;
    }

    private void update(){

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }
        if(Rect.intersects
                (player.getHitbox(), enemy1.getHitbox())){
            enemy1.setX(-100);
        }
        if(Rect.intersects
                (player.getHitbox(), enemy2.getHitbox())){
            enemy2.setX(-100);
        }
        if(Rect.intersects
                (player.getHitbox(), enemy3.getHitbox())){
            enemy3.setX(-100);
        }
        if(!gameEnded) {
        //subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();
        //How long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        if(distanceRemaining < 0){
        //check for new fastest time
            if(timeTaken < fastestTime) {
                editor.putLong("fastestTime", timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }
        // avoid ugly negative numbers
        // in the HUD
            distanceRemaining = 0;
        // Now end the game
            gameEnded = true;
        }

        if(hitDetected) {
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                gameEnded = true;
                //game over so do something
            }
            hitDetected = false;
        }

    }
    private void draw(){
        if(ourHolder.getSurface().isValid()){
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,
                    0,
                    0,
                    0));

            paint.setColor(Color.argb(255, 255, 255, 255));

//            // Draw Hit boxes
//            canvas.drawRect(player.getHitbox().left,
//                    player.getHitbox().top,
//                    player.getHitbox().right,
//                    player.getHitbox().bottom,
//                    paint);
//            canvas.drawRect(enemy1.getHitbox().left,
//                    enemy1.getHitbox().top,
//                    enemy1.getHitbox().right,
//                    enemy1.getHitbox().bottom,
//                    paint);
//            canvas.drawRect(enemy2.getHitbox().left,
//                    enemy2.getHitbox().top,
//                    enemy2.getHitbox().right,
//                    enemy2.getHitbox().bottom,
//                    paint);
//            canvas.drawRect(enemy3.getHitbox().left,
//                    enemy3.getHitbox().top,
//                    enemy3.getHitbox().right,
//                    enemy3.getHitbox().bottom,
//                    paint);

            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }
                canvas.drawBitmap(
                        player.getBitmap(),
                        player.getX(),
                        player.getY(),
                        paint
                );
                canvas.drawBitmap
                        (enemy1.getBitmap(),
                                enemy1.getX(),
                                enemy1.getY(), paint);
                canvas.drawBitmap
                        (enemy2.getBitmap(),
                                enemy2.getX(),
                                enemy2.getY(), paint);
            canvas.drawBitmap
                    (enemy3.getBitmap(),
                            enemy3.getX(),
                            enemy3.getY(), paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.argb(255, 255, 255, 255));

            if (Rect.intersects(player.getHitbox(), enemy1.getHitbox())) {
                hitDetected = true;
                enemy1.setX(-100);
            }
            if (Rect.intersects(player.getHitbox(), enemy2.getHitbox())) {
                hitDetected = true;
                enemy2.setX(-100);
            }
            if (Rect.intersects(player.getHitbox(), enemy3.getHitbox())) {
                hitDetected = true;
                enemy3.setX(-100);
            }

            if (!gameEnded) {
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + formatTime(fastestTime) + "s", 10, 20, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance:" +
                        distanceRemaining / 1000 +
                        " KM", (float) screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield:" +
                        player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" + player.getSpeed() * 60 +
                        " MPS", ((float) screenX / 3) * 2, screenY - 20, paint);
            }
            else {
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", (float) screenX / 2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Fastest:" +
                        formatTime(fastestTime) + "s", (float) screenX / 2, 160, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) +
                        "s", (float) screenX / 2, 200, paint);
                canvas.drawText("Distance remaining:" +
                        distanceRemaining / 1000 + " KM", (float) screenX / 2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", (float) screenX / 2, 350, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }


    }

    private String formatTime(long time) {
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = String.valueOf(thousandths);
        if (thousandths < 100) {
            strThousandths = "0" + thousandths;
        }
        if (thousandths < 10) {
            strThousandths = "0" + strThousandths;
        }
        String stringTime = seconds + "." + strThousandths;
        return stringTime;
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException ignored) {

        }
    }

    public void pause() {
        playing = false;
        try{
            gameThread.join();

        } catch (InterruptedException ignored){

        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }



}

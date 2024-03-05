package com.example.tappydefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Here we set our UI layout as the view
        setContentView(R.layout.activity_main);

        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);


        final Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
        final TextView textFastestTime = (TextView)findViewById(R.id.textHighScore);
        long fastestTime = prefs.getLong("fastestTime", 1000000);
        textFastestTime.setText("Fastest Time:" + fastestTime);
        // Listen for clicks
        buttonPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this,GameActivity.class);
        startActivity(i);
        finish();
    }
}
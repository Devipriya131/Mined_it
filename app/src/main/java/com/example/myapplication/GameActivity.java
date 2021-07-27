package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    public gameView gameview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.gameactivity);

        gameview = new gameView(this);
        setContentView(gameview);
        // setContentView(R.layout.gameactivity);
    }
}
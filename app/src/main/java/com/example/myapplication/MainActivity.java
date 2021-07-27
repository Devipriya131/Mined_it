package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickPlay();
    }

    public void clickPlay()
    {
        Button btnPlay = (Button) findViewById(R.id.btn);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCanva = new Intent(MainActivity.this, GameActivity.class);
                //intentCanva.putExtra("level", level);
                //intentCanva.putExtra("mode", mode);
                startActivity(intentCanva);
            }
        });
    }
}













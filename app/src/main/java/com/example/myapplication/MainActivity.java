package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import  android.view.ViewGroup.LayoutParams;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    Integer level, diff;
    Button btnPlay;
    Button btnSetting;
    Boolean bSetting=false;
    RadioGroup rg;
    RadioButton selectedBtn;
    RatingBar ratingBar;
    LinearLayout ll;
    PopupWindow popUp;
    boolean click = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setVisible();
        clickPlay();
        showScore();
        popup();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  setVisible();

    }

    public void setVisible()
    {
        ll =(LinearLayout) findViewById(R.id.rglinear);
        ll.setVisibility(View.INVISIBLE);
    }

    //for help
    public void popup()
{
    ConstraintLayout rel = (ConstraintLayout) findViewById(R.id.custlayout) ;
    Button but = (Button) findViewById(R.id.btnhelp);
    but.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Display display = getWindowManager().getDefaultDisplay();

            // Load the resolution into a Point object
            Point size = new Point();

            display.getSize(size);
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View custview = inflater.inflate(R.layout.popup, null);
            popUp = new PopupWindow(custview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            TextView tv = custview.findViewById(R.id.tv);
            tv.setText(Html.fromHtml("<br><br><br><br><u><b>Rules:</b></u><br><br>" +
                    "You will get one point for stepping into every non-mine tile.<br><br>" +
                    "And will be losing the game when stepped into the mine.<br><br>"  +
                    "In level 3, the number of mines in the neighbouring tiles are revealed on the tile you choose, which can be used to track the mines and play the game.<br><br>" +
                   "<u><b>Number of Mines:</b></u><br>" +
                    "<ol>" +
                    "<li>&nbsp;Level 1: 10 mines</li>" +
                    "<li>&nbsp;Level 2: 13 mines</li>" +
                    "<li>&nbsp;Level 3: 3, 6, 9, 12, 15 mines as per difficulty.</li><br>" ));



                    ImageButton btnclose =  custview.findViewById(R.id.btnclose);
                                   btnclose.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           popUp.dismiss();
                                       }
                                   });
                                   popUp.showAtLocation(rel, Gravity.NO_GRAVITY, 10, 10);
                                   popUp.update(50, 50, size.x-100, size.y-100);
                               }});
    }

    //listener for radio button and other buttons in the activity
    public void clickPlay()
    {
        btnPlay = (Button) findViewById(R.id.btn);
        rg = (RadioGroup) findViewById(R.id.rg1);
        btnSetting =(Button) findViewById(R.id.btnsetting);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getLevel();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bSetting=!bSetting;
                ll =(LinearLayout) findViewById(R.id.rglinear);
                if(bSetting)
                {
                     ll.setVisibility(View.VISIBLE);
                     btnSetting.setBackgroundColor(Color.GRAY);
                }
                else
                {
                    ll.setVisibility(View.INVISIBLE);
                    btnSetting.setBackgroundColor(Color.BLUE);
                }


            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLevel();
                Intent intentCanva = new Intent(MainActivity.this, GameActivity.class);
                intentCanva.putExtra("level", level);
                intentCanva.putExtra("diff", diff);
                startActivity(intentCanva);
            }
        });
    }

    //get score from the shared preferences
    private void showScore()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore","0")) ;
        TextView score = (TextView) findViewById(R.id.txtScore);
        score.setText(String.valueOf(highScore));
    }

    //get te level details for passing it to next screen
    public void getLevel()
    {
        rg = (RadioGroup) findViewById(R.id.rg1);
        int selected = rg.getCheckedRadioButtonId();
        selectedBtn = (RadioButton) findViewById(selected);
        String strSelected = selectedBtn.getText().toString();
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.INVISIBLE);
        if(strSelected.equals("  Level 1  "))
        {  level = 1;  }
        else if(strSelected.equals("  Level 2  "))
        {  level = 2;  }
        else
        {
            level = 3;
            ratingBar.setVisibility(View.VISIBLE);
            diff=Integer.valueOf((int) ratingBar.getRating());
        }

    }
}













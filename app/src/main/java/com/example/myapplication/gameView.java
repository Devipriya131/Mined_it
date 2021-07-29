package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.service.quicksettings.Tile;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

import tyrantgit.explosionfield.ExplosionField;

import static android.content.Context.MODE_PRIVATE;

public class gameView extends SurfaceView
{
    public Float xCanvas, yCanvas, xClick, yClick;
    public Integer gridCount = 8, iClick, jClick, flagClickMine = 0, score=0, revealed=0, mine=10;
    public Integer level, flagClick=0, secondsPassed = 0, flagEnd=0;
    public Canvas mcanvas;
    public clsBoard board;
    public Boolean updateView = true;
    RectF rectfTimer = new RectF();
    Paint paint = new Paint();
    Paint paint1 = new Paint();
    Paint paint2 = new Paint();
    String scoreStr;
    private Drawable mCustomImage;
    SurfaceHolder surf;


    public gameView(Context context) {
        super(context);
        mCustomImage = context.getResources().getDrawable(R.drawable.mine);
      //  init(null);

        surf=getHolder();
        surf.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                drawCanvas();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

    }

    private class UpdateViewRunnable implements Runnable {
        public void run()
        {

            drawCanvas();
            if(flagClick==1) {
                secondsPassed++; //when user clicks on the board start the timer
            }
            if(flagEnd>=1)
            {   flagEnd++;   } //flagend is 1 if game ends and increments till 7 and closes the window
             if(updateView) {
                postDelayed(this, 1000);
            }
        }
    }

    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateView = true;
        postDelayed(updateViewRunnable, 1000);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        updateView = false;
        super.onDetachedFromWindow();
    }

    private void init(@Nullable AttributeSet set)
    {
    }

    public void drawCanvas(){
        if (surf.getSurface().isValid()) {
            // Lock the mCanvas ready to draw
            mcanvas = surf.lockCanvas();
            mcanvas.drawColor(Color.WHITE);
            if(board==null)//setup board
            {

                getProperty();
                xCanvas = Float.valueOf(mcanvas.getWidth());
                yCanvas = Float.valueOf(mcanvas.getHeight());
                board = new clsBoard(xCanvas, yCanvas, mine);
                setDefault();

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10f);

                board.setMine(mine); //setup mine
                board.setBoard();
                if(level==3) {
                    board.setNeighbourMine(); //calculate neighbour mine count if level is 3
                }
            }

            else {
                board.setBoard();
            }
            if(flagEnd>=1) {
                endGame();
            }

            setTimer(); //Display timer
            paint2.setColor(Color.BLACK);
            paint2.setTextSize(50);
            //SHow number of mine and score
            paint2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            scoreStr = "#Mine: " + String.valueOf(mine) + "    Score: " + String.valueOf(score);
            mcanvas.drawText(scoreStr, 20, ((board.boardRect.top-25)), paint2);
            surf.unlockCanvasAndPost(mcanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        if(flagEnd==0) {
            xClick = event.getX();
            yClick = event.getY();
            switch (event.getAction()) {
                //if user touches the board, find which tile and do appropriate action
                case MotionEvent.ACTION_UP: {
                    if (board.boardRect.contains(xClick, yClick)) {
                        flagClick = 1;
                        iClick = Integer.valueOf((int) (xClick / board.tileSize));
                        jClick = Integer.valueOf((int) ((yClick - board.boardRect.top) / board.tileSize));

                        //if clicked on mine, vibrate and reveal all other mines
                        if (board.tile[iClick][jClick].isMine) {
                            flagEnd = 1;
                            flagClickMine = 1;
                            flagClick = 0;
                            setVibrate();
                        } else {
                            //if clicked on non-mine tile, make it as revealed
                            if(!board.tile[iClick][jClick].isRevealed) {
                                score += 1;
                                revealed++;
                            }
                            //if all non mine tiles revealed, end the game
                            if (revealed == (board.grid * board.grid) - board.mineCount) {
                                flagEnd = 1;
                                flagClick = 0;
                            }
                        }
                        board.tile[iClick][jClick].isRevealed = true;
                        Log.d("First", "dc before");
                        drawCanvas();
                        Log.d("First", "dc after");
                    }
                    break;
                }
            }
        }
        return true;
    }

    //Class for board
    public class clsBoard
    {
        public Integer grid, mineCount, mineInstant;
        int minePos[];
        ArrayList<Integer> cellNos;
        Float xBoard, yBoard, tileSize;
        clsTile[][] tile, tileFill ;
        RectF boardRect;

        public clsBoard(Float xCanvas, Float yCanvas, Integer mine)
        {
            this.mineCount = mine;
            boardRect = new RectF();
            this.tileSize = xCanvas/8;
            this.grid = gridCount;
            this.xBoard = tileSize*8;
            this.yBoard = tileSize*8;
            boardRect.top = (yCanvas-xCanvas)/2;
            boardRect.left = Float.valueOf(0);
            boardRect.bottom = this.boardRect.top+xCanvas;
            boardRect.right = xCanvas;
            tile = new clsTile[grid][grid];
        }

        @SuppressLint("ResourceAsColor")
        public void setBoard()
        {
            paint.setColor(Color.BLACK);
            paint1.setColor(Color.GRAY);
            for(int i=0; i<grid; i++)
            {
                for(int j=0; j<grid; j++)
                {
                    Float leftTile = board.tile[i][j].xTile;
                    Float topTile = board.tile[i][j].yTile;
                    paint.setStrokeWidth(10);
                    mcanvas.drawRect(leftTile, topTile, leftTile + tileSize, topTile + tileSize, paint);
                    if(!board.tile[i][j].isRevealed) {

                        paint1.setColor(Color.GRAY);
                        mcanvas.drawRect(leftTile + 10, topTile + 10, leftTile + tileSize - 10, topTile + tileSize - 10, paint1);

                        if((flagClickMine==1)&&(board.tile[i][j].isMine))
                        {
                            revealMine(leftTile, topTile);
                        }
                    }
                    else{
                        if(board.tile[i][j].isMine)
                        {
                            revealMine(leftTile, topTile);
                        }
                        else {
                            //if level is 3 show neighbour mine count
                             if (level == 3) {
                                paint2.setColor(R.color.mineClr);
                                paint2.setTextSize(60);
                                mcanvas.drawText(String.valueOf(board.tile[i][j].neighbourMines), (leftTile + 10 +this.tileSize/4), (topTile + 20 + this.tileSize/2), paint2);
                            }
                        }
                    }
                }
            }
            //if(flagClickMine==1) {
              //  endGame("Oops! You stepped on a mine :(\n Better luck next time!");
            //}
        }

        //show mine image
        public void revealMine(Float leftTile, Float topTile)
        {
            Rect rect = new Rect(Integer.valueOf((int) (leftTile + 10)), Integer.valueOf((int) (topTile + 10)), Integer.valueOf((int) (leftTile + tileSize - 10)), Integer.valueOf((int) (topTile + tileSize - 10)));

            mCustomImage = getResources().getDrawable(R.drawable.mine);
            mCustomImage.setBounds(rect);
            mCustomImage.draw(mcanvas);
        }

        //setup mine, hardcoded for level 1, for other level its randomly generated
        //minecount is based on the level and difficulty
        public void setMine(Integer mineCount)
        {
            this.minePos = new int[mineCount];
            this.mineCount = mineCount;
            Integer xMine, yMine;
            if((level==3)||(level==2))
            {
                this.cellNos = new ArrayList<Integer>();
                //this.minePos = new int[this.mineCount];
                for(int i=0; i<(this.grid*this.grid); i++)
                {
                    this.cellNos.add(i);
                }
                Collections.shuffle(this.cellNos);

                for(int i=0; i<this.mineCount; i++)
                {
                    this.minePos[i] = this.cellNos.get(i);
                    yMine = Integer.valueOf(this.minePos[i]/this.grid);
                    xMine = minePos[i]%grid;
                    this.tile[xMine][yMine].isMine = true;
                }

            }

            else {
                minePos = new int[]{1, 3, 4, 54, 23, 9, 11, 10, 45, 33};
                for (int i = 0; i < mineCount; i++) {
                    yMine = Integer.valueOf(this.minePos[i] / this.grid);
                    xMine = minePos[i] % grid;
                    this.tile[xMine][yMine].isMine = true;
                }
            }
        }

        //calculate neighbour mine count
        public void setNeighbourMine()
        {
            Integer jStart=-1, jEnd = -1, isTop=0, isBottom=0;
            for(int a=0; a<this.grid; a++)
            {
                for(int b=0; b<this.grid; b++)
                {
                    isBottom=1;
                    isTop=1;
                    jStart = -1;
                    jEnd = 1;
                    if(a==0){
                        isTop = 0;
                        if(b==0)
                        { jStart=0; }
                        else if (b == 7)
                        { jEnd=0; }
                    }

                    else if(a==7)
                    {
                        isBottom = 0;
                        if(b==0)
                        { jStart=0;  }
                        else if (b == 7)
                        {  jEnd=0; }
                    }
                    else if(b==0)
                    {
                        jStart = 0;
                    }

                    else if(b==7)
                    {
                        jEnd = 0;
                    }

                    if(isTop==1) {
                        for (int j = jStart; j <= jEnd; j++) {
                            if (board.tile[a - 1][b + j].isMine) {
                                this.tile[a][b].neighbourMines++;
                            }
                        }
                    }
                    if(isBottom==1) {
                        for (int j = jStart; j <= jEnd; j++) {
                            if (board.tile[a + 1][b + j].isMine) {
                                this.tile[a][b].neighbourMines++;
                            }
                        }
                    }

                    if((jStart==-1)&&(this.tile[a][b-1].isMine)){
                        this.tile[a][b].neighbourMines++;
                    }

                    if((jEnd==1)&&(this.tile[a][b+1].isMine)){
                        this.tile[a][b].neighbourMines++;
                    }
                }
            }
        }
    }

    //TIle class
    public class clsTile
    {
        Float size, xTile, yTile;
        Boolean isMine, isRevealed;
        Integer neighbourMines;
        public clsTile()
        {
            this.neighbourMines = 0;
            this.xTile = Float.valueOf(0);
            this.yTile = Float.valueOf(0);
            this.size = Float.valueOf(0);
            this.isMine = false;
            this.isRevealed = false;
        }
    }


    public void setDefault()
    {
        //mcanvas.drawColor(Color.WHITE);

        for(int i=0; i<gridCount; i++)
        {
            for(int j=0; j<gridCount; j++)
            {
                 board.tile[i][j]=new clsTile();
                 board.tile[i][j].xTile = i*board.tileSize;
                 board.tile[i][j].yTile = j*board.tileSize + board.boardRect.top;
                 //Log.d("Coord",String.valueOf (board.tile[i][j].xTile) + " , " + String.valueOf (board.tile[i][j].yTile));
            }
        }
    }


    //SHow respective image based on if user lost or won, and explode the image
    private void endGame()
    {
        Rect rect = new Rect(Integer.valueOf((int) (board.boardRect.left+50)), Integer.valueOf((int) (board.boardRect.bottom+50)), Integer.valueOf((int) (xCanvas-50)), Integer.valueOf((int) (yCanvas-100)));
        if((flagClickMine==1)) {
            mCustomImage = getResources().getDrawable(R.drawable.oops);
        }
        else{
            mCustomImage = getResources().getDrawable(R.drawable.congrats);
        }

        mCustomImage.setBounds(rect);
        mCustomImage.draw(mcanvas);

        if(flagEnd>=1){
            //rect = new Rect(Integer.valueOf((int) (board.boardRect.left+50)), Integer.valueOf((int) (board.boardRect.bottom+50)), Integer.valueOf((int) (xCanvas-50)), Integer.valueOf((int) (yCanvas-100)));
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.oops);
            RectF rectF = board.boardRect;
            rectF.round(rect);
            final ExplosionField explosionField = ExplosionField.attach2Window((Activity) getContext());
            explosionField.explode(bm, rect, 200, 7000);
        }
        if(flagEnd==7) {
            storeScore();
            Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
            intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intentCanva);
        }
    }

    //called first time to get the level and difficulty
    public void getProperty()
    {
        Integer diff = ((Activity)getContext()).getIntent().getIntExtra("diff", 1);
        level = ((Activity)getContext()).getIntent().getIntExtra("level", 1);
        if(level==1)
        {  mine = 10;  }
        else if((level==2))
        {  mine=13;    }
        else {
            mine = 3*(diff);
        }
        Log.d("Mine", String.valueOf(mine));
    }

    //stores the highest score in shared preferences to show in main screen
    //This function is called when the balls misses
    private void storeScore() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore", "0"));

        if (score > highScore) {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("highScore", String.valueOf(score));
            myEdit.commit();
        }
    }

    public void setTimer()
    {
            paint2.setColor(Color.BLACK);
            paint2.setTypeface(Typeface.create("Odyssey", Typeface.NORMAL));
            int min = (int) (secondsPassed / 60);
            int sec = (int) ((secondsPassed) % 60);
            paint2.setTextSize(60);
            mcanvas.drawText(String.format ("%02d", min) + ":" + String.format ("%02d", sec), (xCanvas - 2 * board.tileSize+10), board.boardRect.top-25, paint2);
    }

    public void setVibrate() {
        final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        final VibrationEffect vibrationEffect1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect1 = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect1);
        }
    }
}


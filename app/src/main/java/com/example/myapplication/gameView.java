package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.service.quicksettings.Tile;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.Collections;

public class gameView extends SurfaceView
{
    public Float xCanvas, yCanvas, xClick, yClick;
    public Integer gridCount = 8, iClick, jClick, flagClickMine = 0, score=0, revealed=0, mine=8;
    public Canvas mcanvas;
    public clsBoard board;
    Paint paint = new Paint();
    Paint paint1 = new Paint();
    private Drawable mCustomImage;
    SurfaceHolder surf;
    Thread gameThread=null;

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



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
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
            if(board==null)
            {
                xCanvas = Float.valueOf(mcanvas.getWidth());
                yCanvas = Float.valueOf(mcanvas.getHeight());
                board = new clsBoard(xCanvas, yCanvas, mine);
                setDefault();

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10f);
                //mcanvas.drawRect(50,50,100,100, paint);
                board.setBoard();
                board.setMine(8);
            }

            board.setBoard();
            //Log.d("ondraw","testing");


            surf.unlockCanvasAndPost(mcanvas);


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        xClick = event.getX();
        yClick = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (board.boardRect.contains(xClick, yClick))
                {
                    iClick = Integer.valueOf((int) (xClick/board.tileSize));
                    jClick = Integer.valueOf((int) ((yClick-board.boardRect.top)/board.tileSize));
                    board.tile[iClick][jClick].isRevealed = true;

                    if(board.tile[iClick][jClick].isMine) {
                        flagClickMine = 1;
                    }
                    else {
                        score+=1;
                        revealed++;
                        if(revealed==(board.grid*board.grid)-board.mineCount){
                            endGame("You've won!");
                        }
                    }
                    drawCanvas();
                }
                break;
            }
        }
        return true;
    }

    public class clsBoard
    {
        public Integer grid, mineCount, mineInstant;
        int minePos[];
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
                            paint1.setColor(Color.WHITE);
                            mcanvas.drawRect(leftTile + 10, topTile + 10, leftTile + tileSize - 10, topTile + tileSize - 10, paint1);
                        }
                    }
                }
            }
            if(flagClickMine==1) {
                endGame("Oops! You stepped on a mine :(\n Better luck next time!");
            }
        }

        public void revealMine(Float leftTile, Float topTile)
        {
            Rect rect = new Rect(Integer.valueOf((int) (leftTile + 10)), Integer.valueOf((int) (topTile + 10)), Integer.valueOf((int) (leftTile + tileSize - 10)), Integer.valueOf((int) (topTile + tileSize - 10)));
            //RectF rect = new RectF(Math.ceil(leftTile + 10) + 10, Math.ceil(topTile + 10) , Math.floor(topTile + tileSize - 10), Math.floor(topTile + tileSize - 10));
            //Log.d("test", String.valueOf(rect.top) + "," +
              //      String.valueOf(rect.left) + "," +
              //      String.valueOf(minePos[i])
            //);
            mCustomImage = getResources().getDrawable(R.drawable.mine);
            mCustomImage.setBounds(rect);
            mCustomImage.draw(mcanvas);
        }

        public void setMine(Integer mineCount)
        {
            this.mineCount = mineCount;
            minePos = new int[] {1, 3, 4, 54, 23, 9, 11, 10};
            Integer xMine, yMine;
            if(level==3) {
                Collections.shuffle(Collections.singletonList(this.minePos));

                for(int i=0; i<this.mineCount; i++)
                {

                }
            }
            for(int i=0; i<mineCount; i++)
            {
                yMine = Integer.valueOf(this.minePos[i]/this.grid);
                xMine = minePos[i]%grid;
                this.tile[xMine][yMine].isMine = true;
            }
        }
    }

    public class clsTile
    {
        Float size, xTile, yTile;
        Boolean isMine, isRevealed;
        public clsTile()
        {
            this.xTile = Float.valueOf(0);
            this.yTile = Float.valueOf(0);
            this.size = Float.valueOf(0);
            this.isMine = false;
            this.isRevealed = false;
        }
    }

    public void setDefault()
    {
        mcanvas.drawColor(Color.WHITE);

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

    /*public void clickMine()
    {

        Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
        intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intentCanva);
    }*/

    private void endGame(String alertText)
    {
        AlertDialog.Builder alertAnswer = new AlertDialog.Builder((Activity) getContext());

        alertAnswer.setMessage(alertText)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
                        intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intentCanva);
                    }
                });
        AlertDialog createAlert = alertAnswer.create();
        createAlert.show();
    }
}

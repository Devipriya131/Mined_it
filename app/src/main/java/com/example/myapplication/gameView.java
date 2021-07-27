package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.service.quicksettings.Tile;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class gameView extends View
{
    public Float xCanvas, yCanvas, xClick, yClick;
    public Integer gridCount=8, iClick, jClick;
    public Canvas mcanvas;
    public clsBoard board;
    Paint paint = new Paint();
    Paint paint1 = new Paint();
    private Drawable mCustomImage;

    public gameView(Context context) {
        super(context);
        mCustomImage = context.getResources().getDrawable(R.drawable.mine);
        init(null);
    }

    public gameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public gameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public gameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
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
        //setDefault();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mcanvas = canvas;

        if(board==null)
        {
            xCanvas = Float.valueOf(mcanvas.getWidth());
            yCanvas = Float.valueOf(mcanvas.getHeight());
            board = new clsBoard(xCanvas, yCanvas, gridCount);
            setDefault();

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);
            //mcanvas.drawRect(50,50,100,100, paint);
            board.setBoard();
            board.setMine(2);
        }

        board.setBoard();

        //Log.d("ondraw","testing");
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

                    if(board.tile[iClick][jClick].isMine)
                    {
                        clickMine();
                    }
                    else
                    {
                        Float leftTile = board.tile[iClick][jClick].xTile;
                        Float topTile = board.tile[iClick][jClick].yTile;
                        //paint1.setColor(getResources().getColor(R.color.backgnd));
                        Paint paint2 = new Paint();
                        paint2.setColor(Color.RED);
                        Log.d("in event","in event");
                        mcanvas.drawRect(50,50,100,100,paint2);
                        mcanvas.drawRect(leftTile+10, topTile+10, leftTile+board.tileSize-10, topTile+board.tileSize-10, paint2);
                    }
                    invalidate();
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

        public clsBoard(Float xCanvas, Float yCanvas, Integer tileGrid)
        {
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
                       //paint.setStrokeWidth(0);
                       paint1.setColor(Color.GRAY);
                       mcanvas.drawRect(leftTile + 10, topTile + 10, leftTile + tileSize - 10, topTile + tileSize - 10, paint1);
                   }
                   else{
                       paint1.setColor(Color.WHITE);
                       mcanvas.drawRect(leftTile + 10, topTile + 10, leftTile + tileSize - 10, topTile + tileSize - 10, paint1);
                   }
               }
           }
        }

        public void setMine(Integer mineCount)
        {
            this.mineCount = mineCount;
            //this.minePos[0] = 23;
            //this.minePos[1] = 47;
            minePos = new int[] {1, 3};
            Integer xMine, yMine;
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

    public void clickMine()
    {
        Intent intentCanva = new Intent((Activity) getContext(), MainActivity.class);
        intentCanva.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intentCanva);
    }
}

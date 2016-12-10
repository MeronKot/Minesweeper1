package com.example.dell.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RunnableFuture;

import static java.lang.Math.abs;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements GridButtonListener {

    private GridButton [][] buttons;
    private int [][] bombsCoord;
    private int count = 0;
    private Timer timer;
    private TextView timerView;
    private int bombs;
    private int numberOfClickedButtons;
    private int size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Bundle b = getIntent().getExtras();
        size = b.getInt("size");
        bombs = b.getInt("bomb");
        buttons = new GridButton[size][size];
        bombsCoord = new int [2][bombs];
        numberOfClickedButtons = 0;

        LinearLayout rowsLayout = (LinearLayout)findViewById(R.id.rowsLayout);
        rowsLayout.addView(drawButtonsGrid(buttons,size));

        timerView = (TextView)findViewById(R.id.textView2);
        timer = new Timer ();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerView.setText("Timer: " + count);
                        count++;
                    }
                });
            }
        },1000,1000);

        putBombs(buttons,bombsCoord,bombs,size);
        checkNeighbours(buttons,bombsCoord,size);
        calculateNeighbours();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private LinearLayout drawButtonsGrid(GridButton[][] buttons, int size) {
        LinearLayout rowsLayout = new LinearLayout(this);
        rowsLayout.setOrientation(LinearLayout.VERTICAL);
        rowsLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        int param = getScreenWidth();
        for (int col = 0; col < size; col++) {
            LinearLayout colLayout = new LinearLayout(this);
            colLayout.setOrientation(LinearLayout.HORIZONTAL);
            colLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int row = 0; row < size; row++) {
                buttons[col][row] = new GridButton(this);
                buttons[col][row].setX_Y(col, row);
                buttons[col][row].setLayoutParams(new ViewGroup.LayoutParams(param/(size + 1) ,param/(size + 1)));
                buttons[col][row].setListener(this);
                colLayout.addView(buttons[col][row]);
            }
            rowsLayout.addView(colLayout);
        }
        return rowsLayout;
    }
    private void putBombs(GridButton[][] buttons, int[][] bombsCoord, int numOfBombs, int size) {
        int colRand,rowRand;
        Random random = new Random();
        for (int i = 0; i < numOfBombs; i++)
        {
            colRand = abs(random.nextInt()) % size;
            rowRand = abs(random.nextInt()) % size;
            if(notExist(colRand,rowRand,bombsCoord))
            {
                bombsCoord[0][i] = colRand;
                bombsCoord[1][i] = rowRand;
            }else i--;
            buttons[colRand][rowRand].setBomb(true);
        }
    }

    private boolean notExist(int colRand, int rowRand, int[][] bombsCoord) {
        for(int i = 0 ; i < bombsCoord[0].length ; i++)
        {
            if((bombsCoord[0][i] == colRand) && (bombsCoord[1][i] == rowRand))
                return false;
        }
        return true;
    }

    private void checkNeighbours(GridButton[][] buttons, int[][] bombsCoord, int size) {
        for(int i = 0 ; i <= size - 1; i++)
        {
            for(int j = 0 ; j <= size - 1 ; j++)
            {
                buttons[i][j].setNearBombs(calculateNearBombs(i,j,size));
            }
        }
    }

    private int calculateNearBombs(int i, int j, int size) {
        int numOfBombs = 0;
        int startRow = 0, endRow = 0, startCol = 0, endCol = 0;

        if(i == 0)
            startRow = i;
        else
            startRow = i - 1;
        if(j == 0)
            startCol = j;
        else
            startCol = j - 1;

        if(i == size - 1)
            endRow = size - 1;
        else
            endRow = i + 1;
        if(j == size - 1)
            endCol = size - 1;
        else
            endCol = j+1;


        if(!buttons[i][j].isBombed()){
            for(int x = startRow ; x <= endRow ; x++)
                for(int y = startCol ; y <= endCol ; y++)
                {
                    if(buttons[x][y].isBombed())
                        numOfBombs++;
                }
        }
        return numOfBombs;
    }
    @Override
   public void click(GridButton button) {
        if(button.isBombed() && !button.isFlaged())
        {
            button.setBackgroundResource(R.drawable.bomb);
            button.setClickable(false);
            button.setLongClickable(false);
            gameOver();
        }
        else if (button.isFlaged()) {
            button.setClickable(false);
        }else if (button.getNearBombs() != 0) {
            {

                button.setClickable(false);
                button.setLongClickable(false);
                button.setBackgroundResource(R.drawable.button);
                button.setTextColor(Color.WHITE);
                button.setText(""+button.getNearBombs());
            }
            button.setNew(false);
            numberOfClickedButtons++;
            if(numberOfClickedButtons == (size * size) - bombs)
                winner();
        }else{
            openSpaces(button);
        }
    }
    @Override
    public boolean longClick(GridButton button) {
        if(button.isFlaged())
        {
            button.setBackground(button.getOriginal());
            button.setFlaged(false);
            button.setClickable(true);
        }
        else
        {
            button.setBackgroundResource(R.drawable.israel);
            button.setFlaged(true);
            button.setClickable(false);
        }
        return true;
    }

    public GridButton[][] getButtons()
    {
        return buttons;
    }

    public int [][] getBombsCoord(){
        return bombsCoord;
    }

    public int getSize(){
        return size;
    }

    public void calculateNeighbours()
    {
        for(int x = 0 ; x < size ; x++)
            for(int y = 0 ; y < size ; y++)
                for(int i = buttons[x][y].get_X() - 1; i <= buttons[x][y].get_X() + 1 ; i++ )
                    for(int j = buttons[x][y].get_Y() - 1 ; j <= buttons[x][y].get_Y() + 1 ; j++)
                        if(i >= 0 && i < size  && j >= 0 && j < size && !buttons[i][j].isBombed())
                            buttons[x][y].setNeighbor(buttons[i][j]);
    }

    public boolean openSpaces(GridButton button) {
        numberOfClickedButtons++;
        button.setNew(false);
        button.setClickable(false);
        button.setLongClickable(false);
        button.setBackgroundResource(R.drawable.button);
        if(button.getNearBombs() != 0)
            button.setText(""+button.getNearBombs());
        if(!button.isBombed()){
            if(button.getNearBombs() == 0)
            {
                ArrayList<GridButton> neighbors = button.getNeighbors();
                for(int i = 0 ; i < neighbors.size() ; i++)
                    if(neighbors.get(i).isNew() && !neighbors.get(i).isFlaged())
                        openSpaces(neighbors.get(i));
            }
        }
        return button.isBombed();
    }

    public void winner(){
        timer.cancel();
        Intent intent = new Intent(MainActivity.this,WinActivity.class);
        intent.putExtra("time",count-1);
        boolean breakRecord = saveTheScore(count-1);
        intent.putExtra("breakRecord",breakRecord);
        MainActivity.this.startActivity(intent);
    }

    public boolean saveTheScore(int time) {
        boolean breakRecord = false;
        SharedPreferences scores = getSharedPreferences("scores",MODE_PRIVATE);
        SharedPreferences.Editor scoresEditor = scores.edit();
        if(size == 10 && bombs == 5){
            int begginerScore = scores.getInt("beginnerScore",-1);
            if(begginerScore == 0)
                scoresEditor.putInt("beginnerScore",time);
            else if (time < begginerScore)
            {
                scoresEditor.putInt("beginnerScore",time);
                breakRecord = true;
            }
            scoresEditor.commit();
        }else if (size == 10 && bombs == 10){
            int mediumScore = scores.getInt("mediumScore",-1);
            if(mediumScore == 0)
                scoresEditor.putInt("mediumScore",time);
            else if (time < mediumScore)
            {
                scoresEditor.putInt("mediumScore",time);
                breakRecord = true;
            }
            scoresEditor.commit();
        }else if(size == 5 && bombs == 10){
            int expertScore = scores.getInt("expertScore",-1);
            if(expertScore == 0)
                scoresEditor.putInt("expertScore",time);
            else if (time < expertScore)
            {
                scoresEditor.putInt("expertScore",time);
                breakRecord = true;
            }
            scoresEditor.commit();
        }
        return breakRecord;
    }

    private void gameOver() {
        Intent intent = new Intent(MainActivity.this,LossActivity.class);
        MainActivity.this.startActivity(intent);
    }
}

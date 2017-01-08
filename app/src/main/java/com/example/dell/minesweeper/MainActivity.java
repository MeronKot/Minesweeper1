package com.example.dell.minesweeper;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GridButtonListener {

    private Button button;
    private TextView textView;
    private GridButton[][] buttons;
    private LinearLayout rowsLayout;
    private GridLayout gridLayout;
    private int[][] bombsCoord;
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
        bombsCoord = new int[2][bombs];
        numberOfClickedButtons = 0;


        gridLayout = (GridLayout)findViewById(R.id.gridLayout);
        initGrid(gridLayout,buttons,size);
        //rowsLayout = (LinearLayout) findViewById(R.id.rowsLayout);
        //rowsLayout.addView(drawButtonsGrid(buttons, size));

        timerView = (TextView) findViewById(R.id.textView2);
        timer = new Timer();
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
        }, 1000, 1000);

        putBombs(buttons, bombsCoord, bombs, size);
        calculateNeighbours();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void initGrid(GridLayout gridLayout, GridButton[][] buttons, int size) {
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        gridLayout.setColumnCount(this.size);
        gridLayout.setRowCount(this.size);

        int paramWid = getScreenWidth();
        int paramHei = getScreenHeight();
        for(int i = 0 ; i < size ; i++){
            for(int j = 0 ; j < size ; j++){
                buttons[i][j] = new GridButton(this);
                buttons[i][j].setX_Y(i, j);
                buttons[i][j].setLayoutParams(new ViewGroup.LayoutParams
                        (paramWid/(size + 1), paramHei/(size + 2)));
                //buttons[i][j].setBackgroundResource(R.drawable.unbutton);
                buttons[i][j].setListener(this);
                gridLayout.addView(buttons[i][j]);
            }
        }
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
                buttons[col][row].setLayoutParams(new ViewGroup.LayoutParams(param / (size + 1), param / (size + 1)));
                buttons[col][row].setListener(this);
                colLayout.addView(buttons[col][row]);
            }
            rowsLayout.addView(colLayout);
        }
        return rowsLayout;
    }

    private void putBombs(GridButton[][] buttons, int[][] bombsCoord, int numOfBombs, int size) {
        int colRand, rowRand;
        boolean available = true;
        Random random = new Random();
        for (int i = 0; i < numOfBombs; i++) {
            do {
                colRand = random.nextInt(size - 1);
                rowRand = random.nextInt(size - 1);
                available = buttons[colRand][rowRand].isBombed();
            } while (available);
            buttons[colRand][rowRand].setBomb(true);
        }
    }

    @Override
    public void click(GridButton button) {
        if (button.isBombed() && !button.isFlaged()) {
            button.setBackgroundResource(R.drawable.bomb);
            button.setClickable(false);
            button.setLongClickable(false);
            gameOver();
        } else if (button.isFlaged()) {
            button.setClickable(false);
        } else if (button.getNearBombs() != 0) {
            button.setClickable(false);
            button.setLongClickable(false);
            button.setBackgroundResource(R.drawable.button);
            button.setText("" + button.getNearBombs());
            button.setNew(false);
            numberOfClickedButtons++;
            if (numberOfClickedButtons == (size * size) - bombs)
                winner();
        } else {
            openSpaces(button);
        }
    }

    @Override
    public boolean longClick(GridButton button) {
        if (button.isFlaged()) {
            button.setBackground(button.getOriginal());
            button.setFlaged(false);
            button.setClickable(true);
        } else {
            button.setBackgroundResource(R.drawable.israel);
            button.setFlaged(true);
            button.setClickable(false);
        }
        return true;
    }

    public GridButton[][] getButtons() {
        return buttons;
    }

    public int[][] getBombsCoord() {
        return bombsCoord;
    }

    public int getSize() {
        return size;
    }

    public void calculateNeighbours() {
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                for (int i = buttons[x][y].get_X() - 1; i <= buttons[x][y].get_X() + 1; i++)
                    for (int j = buttons[x][y].get_Y() - 1; j <= buttons[x][y].get_Y() + 1; j++)
                        if (i >= 0 && i < size && j >= 0 && j < size)
                            buttons[x][y].setNeighbor(buttons[i][j]);
    }

    public boolean openSpaces(GridButton button) {
        button.setNew(false);
        button.setClickable(false);
        button.setLongClickable(false);
        button.setBackgroundResource(R.drawable.button);

        if (!button.isBombed()) {
            numberOfClickedButtons++;
            if (button.getNearBombs() != 0)
                button.setText("" + button.getNearBombs());
            else {
                ArrayList<GridButton> neighbors = button.getNeighbors();
                for (int i = 0; i < neighbors.size(); i++)
                    if (neighbors.get(i).isNew())
                        openSpaces(neighbors.get(i));
            }
        }

        return button.isBombed();
    }

    public void winner() {
        timer.cancel();
        Intent intent = new Intent(MainActivity.this, WinActivity.class);
        intent.putExtra("time", count - 1);
        boolean breakRecord = saveTheScore(count - 1);
        intent.putExtra("breakRecord", breakRecord);
        MainActivity.this.startActivity(intent);
    }

    public boolean saveTheScore(int time) {
        boolean breakRecord = false;
        SharedPreferences scores = getSharedPreferences("scores", MODE_PRIVATE);
        SharedPreferences.Editor scoresEditor = scores.edit();
        if (size == 10 && bombs == 5) {
            int begginerScore = scores.getInt("beginnerScore", -1);
            if (begginerScore == 0)
                scoresEditor.putInt("beginnerScore", time);
            else if (time < begginerScore) {
                scoresEditor.putInt("beginnerScore", time);
                breakRecord = true;
            }
            scoresEditor.commit();
        } else if (size == 10 && bombs == 10) {
            int mediumScore = scores.getInt("mediumScore", -1);
            if (mediumScore == 0)
                scoresEditor.putInt("mediumScore", time);
            else if (time < mediumScore) {
                scoresEditor.putInt("mediumScore", time);
                breakRecord = true;
            }
            scoresEditor.commit();
        } else if (size == 5 && bombs == 10) {
            int expertScore = scores.getInt("expertScore", -1);
            if (expertScore == 0)
                scoresEditor.putInt("expertScore", time);
            else if (time < expertScore) {
                scoresEditor.putInt("expertScore", time);
                breakRecord = true;
            }
            scoresEditor.commit();
        }
        return breakRecord;
    }

    private void gameOver() {
        for(int i = 0 ; i < size ; i++){
            for(int j = 0 ; j < size ; j++){
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(buttons[i][j],"y",50f);

            }
        }


        Animation anim = AnimationUtils.loadAnimation(this,R.anim.slide_in_bottom);
        LayoutAnimationController controller = new LayoutAnimationController(anim);
        controller.setDelay(0.3f);
        gridLayout.setLayoutAnimation(controller);
        gridLayout.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(MainActivity.this,LossActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        controller.start();
    }
}

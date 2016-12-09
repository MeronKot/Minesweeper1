package com.example.dell.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    private static final String TAG = "Menu";

    private RadioGroup group;
    private Button newGame;
    private int[] scoresOfLevels;
    TextView firstRowRight;
    TextView secondRowRight;
    TextView thirdRowRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        firstRowRight = (TextView)findViewById(R.id.firstRowRight);
        secondRowRight = (TextView)findViewById(R.id.secondRowRight);
        thirdRowRight = (TextView)findViewById(R.id.thirdRowRight);
        loadLevelsScores();
        firstRowRight.setText("" + scoresOfLevels[0]);
        secondRowRight.setText("" + scoresOfLevels[1]);
        thirdRowRight.setText("" + scoresOfLevels[2]);
        group = (RadioGroup)findViewById(R.id.radioGroup);
        group.check(R.id.beginner);
        newGame = (Button) findViewById(R.id.newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, MainActivity.class);
                RadioButton checkedRadioButton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                int choice = -1;
                int bombs = 0;
                if (checkedRadioButton.getId() == R.id.beginner) {
                    choice = 10;
                    bombs = 5;
                } else if (checkedRadioButton.getId() == R.id.medium) {
                    choice = 10;
                    bombs = 10;

                } else if (checkedRadioButton.getId() == R.id.expert) {
                    choice = 5;
                    bombs = 10;
                }

                intent.putExtra("size", choice);
                intent.putExtra("bomb", bombs);
                Menu.this.startActivity(intent);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveLevelsScores();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadLevelsScores();
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

   private void saveLevelsScores() {
        SharedPreferences scores = getSharedPreferences("scores",MODE_PRIVATE);
        SharedPreferences.Editor scoresEditor = scores.edit();
        scoresEditor.putInt("beginnerScore",scoresOfLevels[0]);
        scoresEditor.putInt("mediumScore",scoresOfLevels[1]);
        scoresEditor.putInt("expertScore",scoresOfLevels[2]);
        scoresEditor.commit();
    }

    private void loadLevelsScores() {
        scoresOfLevels = new int [3];
        SharedPreferences scores = getSharedPreferences("scores",MODE_PRIVATE);
        scoresOfLevels[0] = scores.getInt("beginnerScore",0);
        scoresOfLevels[1] = scores.getInt("mediumScore",0);
        scoresOfLevels[2] = scores.getInt("expertScore",0);
        Log.d(TAG,"load successfully");
    }
}


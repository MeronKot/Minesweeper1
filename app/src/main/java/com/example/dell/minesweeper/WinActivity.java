package com.example.dell.minesweeper;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class WinActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        Bundle b = getIntent().getExtras();
        int time = b.getInt("time");
        int size = b.getInt("size");
        int bombs = b.getInt("bombs");
        textView = (TextView)findViewById(R.id.winTime);
        textView.setText(""+time);
        saveTheScore(time,size,bombs);
    }

    private void saveTheScore(int time, int size, int bombs) {
        SharedPreferences scores = getSharedPreferences("scores",MODE_PRIVATE);
        SharedPreferences.Editor scoresEditor = scores.edit();
        if(size == 10 && bombs == 5){
            int begginerScore = scores.getInt("beginnerScore",-1);
            if (time < begginerScore && begginerScore != -1)
                scoresEditor.putInt("beginnerScore",time);
            scoresEditor.commit();
        }else if (size == 10 && bombs == 10){
            int mediumScore = scores.getInt("mediumScore",-1);
            if (time < mediumScore && mediumScore != -1)
                scoresEditor.putInt("mediumScore",time);
            scoresEditor.commit();
        }else if(size == 5 && bombs == 10){
            int expertScore = scores.getInt("expertScore",-1);
            if (time < expertScore && expertScore != -1)
                scoresEditor.putInt("expertScore",time);
            scoresEditor.commit();
        }
    }
}

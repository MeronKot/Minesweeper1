package com.example.dell.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WinActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Bundle b = getIntent().getExtras();
        int time = b.getInt("time");
        boolean breakRecord = b.getBoolean("breakRecord");
        textView = (TextView)findViewById(R.id.winTime);
        textView.setText("Score: "+time);
        if(breakRecord)
            Toast.makeText(getApplicationContext(),"You broke the record",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this.getApplication(),Menu.class));
    }
}

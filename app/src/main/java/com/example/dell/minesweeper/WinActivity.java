package com.example.dell.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WinActivity extends AppCompatActivity {

    private TextView textView;
    private TextView nameTextView;
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Bundle b = getIntent().getExtras();
        int time = b.getInt("time");
        boolean breakRecord = b.getBoolean("breakRecord");
        nameTextView =(TextView)findViewById(R.id.nameTextView);
        nameEditText =(EditText) findViewById(R.id.nameInput) ;
        textView = (TextView)findViewById(R.id.winTime);
        textView.setText("Score: "+time);
        if(breakRecord) {
            Toast.makeText(getApplicationContext(), "You broke the record", Toast.LENGTH_SHORT).show();
            nameTextView.setVisibility(View.VISIBLE);
            nameEditText.setVisibility(View.VISIBLE);
            nameEditText.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this.getApplication(),Menu.class));
    }
}

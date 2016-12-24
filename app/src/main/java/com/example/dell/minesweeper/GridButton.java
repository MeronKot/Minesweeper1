package com.example.dell.minesweeper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.sip.SipAudioCall;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by DELL on 24/11/2016.
 */
public class GridButton extends Button implements View.OnClickListener, View.OnLongClickListener{

    private int x;
    private int y;
    private boolean isNew;
    private boolean bombed;
    private boolean flaged;
    private int nearBombs;
    private ArrayList<GridButton> neighbors;
    private Drawable original;
    private GridButtonListener listener;

    public GridButton(Context context) {
        super(context);
        setNew(true);
        setBomb(false);
        setFlaged(false);
        setNearBombs(0);
        this.neighbors = new ArrayList<GridButton>();
        setOriginal(this.getBackground());
        setOnClickListener(this);
        setOnLongClickListener(this);
        setTextColor(Color.WHITE);
        setTextSize(15);
    }
    @Override
    public void onClick(View v) {
        listener.click(this);
    }

    public boolean onLongClick(View v) {
        return listener.longClick(this);
    }

    public void setX_Y(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int get_X(){
        return x;
    }

    public int get_Y(){
        return y;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setBomb(boolean bombed)
    {
        this.bombed = bombed;
    }

    public boolean isBombed()
    {
        return bombed;
    }

    public void setFlaged(boolean flaged){
        this.flaged = flaged;
    }

    public boolean isFlaged(){
        return flaged;
    }

    public void setNearBombs(int num)
    {
        this.nearBombs = num;
    }

    public int getNearBombs(){
        return nearBombs;
    }

    public void setNeighbor(GridButton button)
    {
        this.neighbors.add(button);
        if(button.isBombed())
            this.nearBombs++;
    }

    public ArrayList<GridButton> getNeighbors()
    {
        return neighbors;
    }

    public void setOriginal(Drawable original){
        this.original = original;
    }

    public Drawable getOriginal(){
        return original;
    }

    public void setListener(GridButtonListener listener){
        this.listener = listener;
    }
}

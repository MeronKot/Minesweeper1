package com.example.dell.minesweeper;


import android.app.Fragment;
import android.os.Bundle;
import android.os.health.PackageHealthStats;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TableFragment extends android.support.v4.app.Fragment {

    private final int MAX_SCORES = 10,MAX_FIELDS = 4;
    private TableLayout tableLayout;
    private TextView [][] textViews;

    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_table, container, false);

        // add 10 table rows with 4 fields each: name,score,level,pos
        createTheTable(v);

        //here we need to load the names,scores,levels and positions from the SharedPreferences.

        return v;
    }

    private void createTheTable(View v) {
        textViews = new TextView[MAX_SCORES][MAX_FIELDS];
        tableLayout = (TableLayout) v.findViewById(R.id.table);
        for(int i = 0 ; i < MAX_SCORES ; i++){
            TableRow tableRow = new TableRow(getActivity());
            for(int j = 0 ; j < MAX_FIELDS ; j++){
                textViews[i][j] = new TextView(getActivity());
                textViews[i][j].setText("" + i + " " + j);//in order to see the textViews!
                tableRow.addView(textViews[i][j]);
            }
            tableLayout.addView(tableRow);
        }
    }

}

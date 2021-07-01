package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class showplayersdata extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showplayersdata);

        getSupportActionBar().setTitle("Top Players");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayList<String> toDisplay = new ArrayList<>();
        listView = findViewById(R.id.listView);
        this.adapter = new ArrayAdapter<String>(this,R.layout.show, toDisplay);
        listView.setAdapter(adapter);

        UsersService.getUsersData(data -> {
          ArrayList<Pair<Long, String >> hm = new ArrayList<>();
            for (Object user : data.values()) {
                HashMap<String, Object> userData = (HashMap<String, Object>)user;
                Long val = (Long) userData.get("score");
               String userName = (String) userData.get("username");
               hm.add(new Pair<Long, String>(val, userName));
            }

            hm.sort(new Comparator<Pair<Long, String >>() {
                @Override
                public int compare(Pair<Long, String > lhs, Pair<Long, String > rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return rhs.first.compareTo(lhs.first);
                }
            });

            for (int i = 0; i < 10 && i < hm.size(); i++) {
                Pair<Long, String> current = hm.get(i);
                toDisplay.add(current.second + " : " + current.first);
            }

            adapter.notifyDataSetChanged();
        });
    }
}
package com.ryms.mathagoras.Class;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageButton;

import com.ryms.mathagoras.Dashb.MyAdapter;
import com.ryms.mathagoras.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;

public class ClassRoom extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    CalenderAdapter calenderAdapter;
    ImageButton discussion;
    AlertDialog.Builder builder;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        sp = getSharedPreferences("SETTING", 0);
        builder = new AlertDialog.Builder(this);

        final ArrayList<ClassModel> modelArrayList = new ArrayList<>();

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        calenderAdapter = new CalenderAdapter(modelArrayList);
        recyclerView.setAdapter(calenderAdapter);

        for (int i = 0; i < 30; i++) {
            ClassModel model = new ClassModel();
            int daysToIncrement = 0;
            Calendar c =Calendar.getInstance();
            c.add(Calendar.DATE, daysToIncrement+i);
            Date d = c.getTime();
            String day = (String) DateFormat.format("EEEE", d);
            String date = (String) DateFormat.format("dd", d);
            String month = (String) DateFormat.format("MMM", d);
            model.date = date;
            model.month = month;
            model.day = day;
            //model.time = ;
            model.setImage(R.drawable.shadowfight);
            modelArrayList.add(model);
        }
    }
}



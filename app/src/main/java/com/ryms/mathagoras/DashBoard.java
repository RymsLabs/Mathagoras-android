package com.ryms.mathagoras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class DashBoard extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new MyAdapter(this, getMyList());
        recyclerView.setAdapter(myAdapter);

    }

    private ArrayList<Model> getMyList(){

        ArrayList<Model> models = new ArrayList<>();

        Model model = new Model();
        model.setImage(R.drawable.tile);
        models.add(model);

        model = new Model();
        model.setImage(R.drawable.tile);
        models.add(model);

        model = new Model();
        model.setImage(R.drawable.tile);
        models.add(model);

        return models;
    }
}
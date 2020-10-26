package com.ryms.mathagoras.Dashb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.ryms.mathagoras.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashBoard extends AppCompatActivity {

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        ArrayList<Model> modelArrayList = new ArrayList<>();
        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        myAdapter = new MyAdapter(modelArrayList);
        recyclerView.setAdapter(myAdapter);

        JSONObject jsonObject = new JSONObject();
        JSONArray courses;
        {
            try {
                courses = jsonObject.getJSONArray("enrolled");
                for (int i = 0; i < courses.length(); i++) {
                    Model model = new Model();
                    JSONObject temp = courses.getJSONObject(i);
                    model.cname = temp.getString("name");
                    //model.tname = temp.getString("name");
                    model.description = temp.getString("description");
                    model.setImage(R.drawable.shadowfight);
                    modelArrayList.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
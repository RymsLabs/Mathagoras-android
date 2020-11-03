package com.ryms.mathagoras.Dashb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ryms.mathagoras.ChoiceLoginSignup.LoginActivity;
import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.concurrent.TaskRunner;

public class DashBoard extends AppCompatActivity {

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        final ArrayList<Model> modelArrayList = new ArrayList<>();
        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        myAdapter = new MyAdapter(modelArrayList);
        recyclerView.setAdapter(myAdapter);


        OkHttpClient client = new OkHttpClient();

        // TODO: SAVE TOKEN FFS
        String plainAuth = "e19cse262" + ":" + "Scooby$1";
        String base64 = null;

        try {
            byte[] data = plainAuth.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.NO_WRAP);

        } catch (java.io.UnsupportedEncodingException err) {
            System.out.println("Error while converting plainAuthText: ");
            err.printStackTrace();
        }


        if (base64 == null) {
            // Hopefully will never be called
            throw new Error("Unexpectedly found base64 null during login");
        }

        // Creating a request obj to request to a url
        Request request = new Request.Builder()
                .header("Authorization", ("Basic "+base64))
                .url(Config.GET_COURSES)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if(e instanceof UnknownHostException) {
                    System.out.println("Please check your Internet connection.");
                } else {
                    System.out.println("Error executing login HTTP req.: ");
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(response.code() != 200) {
                    final JSONObject finalJsonObject = jsonObject;
                    DashBoard.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(DashBoard.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                JSONArray courses;
                try {
                    Log.d("JSON", jsonObject.toString());
                    courses = jsonObject.getJSONArray("enrolled");
                    JSONObject temp;
                    for (int i = 0; i < courses.length(); i++) {
                        temp = courses.getJSONObject(i);
                        Model model = new Model();
                        model.cname = temp.getString("name");
                        model.tname = temp.getString("teacher_name");
                        model.description = temp.getString("description");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    DashBoard.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void addCourse(View view) {

    }
}
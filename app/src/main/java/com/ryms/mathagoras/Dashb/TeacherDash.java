package com.ryms.mathagoras.Dashb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherDash extends AppCompatActivity {

    MyAdapter myAdapter;
    ImageButton add;
    AlertDialog.Builder builder;
    SharedPreferences sp;
    TextView teachId, teachName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dash);

        add = (ImageButton) findViewById(R.id.add);
        builder = new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        final View promptsView = li.inflate(R.layout.alert_dialog, null);
        builder.setView(promptsView);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText joinCode = (EditText) promptsView.findViewById(R.id.joinCode);
                builder
                        .setCancelable(false)
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(),"clicked",
                                        Toast.LENGTH_SHORT).show();
                                JSONObject jsonBody = new JSONObject();
                                try {
                                    JSONObject courseId = jsonBody.put("courseId", joinCode.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Join Code");
                alert.show();
            }
        });

        final ArrayList<Model> modelArrayList = new ArrayList<>();
        RecyclerView recyclerView;

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        myAdapter = new MyAdapter(modelArrayList);
        recyclerView.setAdapter(myAdapter);

        OkHttpClient client = new OkHttpClient();

        sp = getSharedPreferences("SETTING", 0);
        String userID = sp.getString("USERID", "");
        String password= sp.getString("PASSWORD", "");
        String name = sp.getString("NAME", "");

        teachId = findViewById(R.id.teachId);
        teachName = findViewById(R.id.teachName);

        teachId.setText(userID);
        teachName.setText(name);

        String plainAuth = userID + ":" + password;
        String base64 = null;

        byte[] data = plainAuth.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);


        if (base64 == null) {
            // Hopefully will never be called
            throw new Error("Unexpectedly found base64 null during login");
        }

        // Creating a request obj to request to a url
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.GET_COURSES)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (e instanceof UnknownHostException) {
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
                if (response.code() != 200) {
                    final JSONObject finalJsonObject = jsonObject;
                    TeacherDash.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TeacherDash.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                    TeacherDash.this.runOnUiThread(new Runnable() {
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
}


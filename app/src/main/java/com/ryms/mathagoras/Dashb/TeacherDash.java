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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherDash extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    MyAdapter myAdapter;
    ImageButton add;
    AlertDialog.Builder builder;
    SharedPreferences sp;
    TextView teachId, teachName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dash);

        sp = getSharedPreferences("SETTING", 0);
        add = (ImageButton) findViewById(R.id.add);
        builder = new AlertDialog.Builder(this);

        final String userID = sp.getString("USERID", "");
        final String password = sp.getString("PASSWORD", "");
        final String name = sp.getString("NAME", "");

        /** requests courses already enrolled from the server */
        getCourses(userID, password, name);

        final LayoutInflater li = LayoutInflater.from(getApplicationContext());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View promptsView = li.inflate(R.layout.alert_dialog, null);
                builder.setView(promptsView);
                final EditText joinCode = (EditText) promptsView.findViewById(R.id.joinCode);
                builder.setCancelable(false)
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                JoinCourse(userID, password, joinCode.getText().toString(), name);
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
    }

    public void getCourses(String userID, String password, String name) {

        OkHttpClient client = new OkHttpClient();

        final ArrayList<Model> modelArrayList = new ArrayList<>();
        teachId = findViewById(R.id.Id);
        teachName = findViewById(R.id.Name);
        teachId.setText(userID);
        teachName.setText(name);

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        myAdapter = new MyAdapter(modelArrayList);
        recyclerView.setAdapter(myAdapter);

        String plainAuth = userID + ":" + password;
        String base64 = null;

        byte[] data = plainAuth.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);

        if (base64 == null) {
            /** Hopefully will never be called */
            throw new Error("Unexpectedly found base64 null during login");
        }

        /** Creating a request obj to request to a url */
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

    public void JoinCourse(final String userID, final String password, final String joinCode, final String name) {

        OkHttpClient client = new OkHttpClient();

        String plainAuth = userID + ":" + password;
        String base64 = null;

        byte[] data = plainAuth.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);

        if (base64 == null) {
            throw new Error("Unexpectedly found base64 null during login");
        }

        JSONObject reqjson = new JSONObject();
        String requestUrl = Config.ENROLL_COURSES;
        try {
            reqjson.put("courseId", joinCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(String.valueOf(reqjson), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic "+base64))
                .url(requestUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myres = response.body().string();
                Log.d("JOIN_RES", myres);
                TeacherDash.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject joinCourse = new JSONObject(myres);
                            String status = joinCourse.getString("type");
                            if (status.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Joined", Toast.LENGTH_SHORT).show();
                                getCourses(userID, password, name);

                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " +joinCourse.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}


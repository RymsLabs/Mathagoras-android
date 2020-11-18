package com.ryms.mathagoras.Options;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.Dashb.DashBoard;
import com.ryms.mathagoras.Dashb.Model;
import com.ryms.mathagoras.Dashb.MyAdapter;
import com.ryms.mathagoras.Discussion.Discussion;
import com.ryms.mathagoras.Discussion.DissModel;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Options extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final ArrayList<OptionsModel> modelArrayList = new ArrayList<>();

    OptionsAdapter optionsAdapter;
    ImageButton add;
    SharedPreferences sp;
    Button teamsInt;
    String cid;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Bundle bundle = getIntent().getExtras();
        time = bundle.getString("TIME");
        cid = bundle.getString("cid");

        teamsInt = (Button) findViewById(R.id.teamsInt);

        teamsInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.microsoft.teams");
                if (launchIntent != null) {
                        startActivity(launchIntent);
                } else {
                        Toast.makeText(Options.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        });

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.DissView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        optionsAdapter = new OptionsAdapter(modelArrayList);
        recyclerView.setAdapter(optionsAdapter);

        sp = getSharedPreferences("SETTING", 0);
        add = (ImageButton) findViewById(R.id.add);

        final String userID = sp.getString("USERID", "");
        final String password = sp.getString("PASSWORD", "");

        getDiscussions(userID, password);
        getPosts(userID, password);

    }

    public void getDiscussions(String userID, String password) {

        OkHttpClient client = new OkHttpClient();

        String plainAuth = userID + ":" + password;
        String base64 = null;

        byte[] data = plainAuth.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);

        if (base64 == null) {
            /** Hopefully will never be called */
            throw new Error("Unexpectedly found base64 null during login");
        }
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("classDate", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Creating a request obj to request to a url */
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.GET_DISCUSSION+cid)
                .post(body)
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
                    Options.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(Options.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                JSONArray discussions;
                try {
                    Log.d("JSON", jsonObject.toString());
                    discussions = jsonObject.getJSONArray("discussions");
                    JSONObject temp;
                    for (int i = 0; i < discussions.length(); i++) {
                        temp = discussions.getJSONObject(i);
                        OptionsModel model = new OptionsModel();
                        model.discussionId = temp.getString("discussion_id");
                        model.titleDiss = temp.getString("title");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    Options.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            optionsAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getPosts(String userID, String password){

        OkHttpClient client = new OkHttpClient();

        String plainAuth = userID + ":" + password;
        String base64 = null;

        byte[] data = plainAuth.getBytes(StandardCharsets.UTF_8);
        base64 = Base64.encodeToString(data, Base64.NO_WRAP);

        if (base64 == null) {
            /** Hopefully will never be called */
            throw new Error("Unexpectedly found base64 null during login");
        }
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("classDate", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Creating a request obj to request to a url */
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.GET_POST+cid)
                .post(body)
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
                    Options.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(Options.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                JSONArray posts;
                try {
                    Log.d("JSON", jsonObject.toString());
                    posts = jsonObject.getJSONArray("posts");
                    JSONObject temp;

                    for (int i = 0; i < posts.length(); i++) {
                        OptionsModel model = new OptionsModel();
                        temp = posts.getJSONObject(i);
                        model.discussionId = temp.getString("post_id");
                        model.titleDiss = temp.getString("title");
                        model.messagePost = temp.getString("message");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    Options.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            optionsAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
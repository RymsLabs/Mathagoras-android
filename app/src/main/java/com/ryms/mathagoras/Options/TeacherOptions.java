package com.ryms.mathagoras.Options;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class TeacherOptions extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final ArrayList<TeacherOpModel> modelArrayList = new ArrayList<>();

    TeacherOpAdapter teacherOpAdapter;
    SharedPreferences sp;
    Button createButton;
    String time, cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_options);

        Bundle bundle = getIntent().getExtras();
        time = bundle.getString("TIME");
        cid = bundle.getString("cid");

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.createDiss);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        teacherOpAdapter = new TeacherOpAdapter(modelArrayList);
        recyclerView.setAdapter(teacherOpAdapter);

        sp = getSharedPreferences("SETTING", 0);

        createButton = (Button) findViewById(R.id.createButton);

        final String userID = sp.getString("USERID", "");
        final String password = sp.getString("PASSWORD", "");

        final Spinner spinner = findViewById(R.id.create);
        final String[] items = new String[]{"Discussion", "Post"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater li = LayoutInflater.from(getApplicationContext());

        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        final LayoutInflater li2 = LayoutInflater.from(getApplicationContext());

        getDiscussions(userID, password);
        getPosts(userID, password);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View promptsView = li.inflate(R.layout.create_discussion_dialog, null);
                builder.setView(promptsView);
                final EditText dissTitle = (EditText) promptsView.findViewById(R.id.dissTitle);

                View promptsView2 = li2.inflate(R.layout.create_post_dialog, null);
                builder2.setView(promptsView2);
                final EditText postTitle = (EditText) promptsView2.findViewById(R.id.postTitle);
                final EditText postMessage = (EditText) promptsView2.findViewById(R.id.postMessage);

                String selection = spinner.getSelectedItem().toString();
                if (selection.equals("Discussion")) {
                    builder.setCancelable(false)
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    createDiscussion(userID, password, dissTitle.getText().toString());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("Create");
                    alert.show();
                }else{
                    builder2.setCancelable(false)
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    createPost(userID, password, postTitle.getText().toString(), postMessage.getText().toString());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder2.create();
                    alert.setTitle("Create");
                    alert.show();
                }

            }
        });
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
                String body = response.body().string();
                try {
                    jsonObject = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.code() != 200) {
                    final JSONObject finalJsonObject = jsonObject;
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TeacherOptions.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                        TeacherOpModel model = new TeacherOpModel();
                        model.TypeT = "Discussion";
                        model.classId = temp.getString("discussion_id");
                        model.titleCreate = temp.getString("title");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            teacherOpAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void createDiscussion(final String userID, final String password, String dissTitle) {

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
            jsonBody.put("classId", cid);
            jsonBody.put("title", dissTitle);
            jsonBody.put("classDate", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Creating a request obj to request to a url */
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.CREATE_DISCUSSION)
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
                    String body = response.body().string();
                    Log.d("pls work",body);
                    jsonObject = new JSONObject(body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.code() != 200) {
                    final JSONObject finalJsonObject = jsonObject;
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TeacherOptions.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                modelArrayList.clear();
                getResources(userID, password);
            }
        });
    }

    public void createPost(final String userID, final String password, String dissTitle, String postMessage){

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
            jsonBody.put("classId", cid);
            jsonBody.put("title", dissTitle);
            jsonBody.put("message", postMessage);
            jsonBody.put("classDate", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Creating a request obj to request to a url */
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.CREATE_POST)
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
                    String body = response.body().string();
                    jsonObject = new JSONObject(body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.code() != 200) {
                    final JSONObject finalJsonObject = jsonObject;
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TeacherOptions.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                modelArrayList.clear();
                getResources(userID, password);
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
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TeacherOptions.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                        TeacherOpModel model = new TeacherOpModel();
                        temp = posts.getJSONObject(i);
                        model.TypeT = "Post";
                        model.classId = temp.getString("post_id");
                        model.titleCreate = temp.getString("title");
                        model.classDate = temp.getString("message");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            teacherOpAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getResources(final String userID, final String password) {
        modelArrayList.clear();
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
                String body = response.body().string();
                Log.d("disccc", body);
                try {
                    jsonObject = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.code() != 200) {
                    final JSONObject finalJsonObject = jsonObject;
                    TeacherOptions.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(TeacherOptions.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                        TeacherOpModel model = new TeacherOpModel();
                        model.TypeT = "Discussion";
                        model.classId = temp.getString("discussion_id");
                        model.titleCreate = temp.getString("title");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    getPosts(userID, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
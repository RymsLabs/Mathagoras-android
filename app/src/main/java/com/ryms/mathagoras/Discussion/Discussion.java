package com.ryms.mathagoras.Discussion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ryms.mathagoras.Class.ClassModel;
import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.Options.OptionsModel;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Discussion extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final ArrayList<DissModel> modelArrayList = new ArrayList<>();
    DissAdapter dissAdapter;
    AlertDialog.Builder builder;
    SharedPreferences sp;
    String typed, discussionId;
    EditText message;
    ImageButton send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        Bundle bundle = getIntent().getExtras();
        discussionId = bundle.getString("discussionId");

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        dissAdapter = new DissAdapter(modelArrayList);
        recyclerView.setAdapter(dissAdapter);

        sp = getSharedPreferences("SETTING", 0);
        builder = new AlertDialog.Builder(this);

        final String userID = sp.getString("USERID", "");
        final String password = sp.getString("PASSWORD", "");
        final  String userType = sp.getString("USERTYPE", "");

        message = (EditText) findViewById(R.id.message);
        typed = message.getText().toString();

        send = (ImageButton) findViewById(R.id.send);
        getAllMessages(userID, password);

        if(userType.equals("teacher")){
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendTeacherMessage(userID, password);
                    message.getText().clear();
                }
            });
        }
        else {
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendStudentMessage(userID, password);
                    message.getText().clear();
                }
            });
        }
    }

    public void getAllMessages(String userID, String password) {

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

        /** Creating a request obj to request to a url */
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.GET_ALL_MESSAGES+discussionId)
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
                    Discussion.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(Discussion.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                JSONArray messages;
                try {
                    Log.d("JSON", jsonObject.toString());
                    messages = jsonObject.getJSONArray("messages");
                    JSONObject temp;

                    temp = messages.getJSONObject(0);




                    for (int i = 0; i < messages.length(); i++) {
                        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                        Date myDate = myFormat.parse(temp.getString("message_time"));
                        String time = new SimpleDateFormat("H:mm").format(myDate);
                        String date = new SimpleDateFormat("dd-MM-yyyy").format(myDate);
                        temp = messages.getJSONObject(i);
                        DissModel model = new DissModel();
                        model.user = temp.getString("user_id");
                        model.dateDiss = date;
                        model.timeDiss = time;
                        model.userType = temp.getString("user_type");
                        model.mess_age = temp.getString("message");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }
                    Discussion.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendStudentMessage(final String userID, final String password) {

        typed = message.getText().toString();

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
            jsonBody.put("discussionId", discussionId);
            jsonBody.put("message", typed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Creating a request obj to request to a url */
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.SEND_MESSAGE_STUDENT)
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
                    Discussion.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(Discussion.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                getAllMessages(userID, password);
            }
        });
    }

    public void sendTeacherMessage(final String userID, final String password) {

        typed = message.getText().toString();

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
            jsonBody.put("discussionId", discussionId);
            jsonBody.put("message", typed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Creating a request obj to request to a url */
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
                .header("Authorization", ("Basic " + base64))
                .url(Config.SEND_MESSAGE_TEACHER)
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
                    Discussion.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(Discussion.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                getAllMessages(userID, password);
            }
        });
    }
}

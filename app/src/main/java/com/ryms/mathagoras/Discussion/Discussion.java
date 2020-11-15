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

import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;
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
import okhttp3.Response;

public class Discussion extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final ArrayList<DissModel> modelArrayList = new ArrayList<>();
    DissAdapter dissAdapter;
    AlertDialog.Builder builder;
    SharedPreferences sp;
    String typed;
    EditText message;
    ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

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

        message = (EditText) findViewById(R.id.message);
        typed = message.toString();

        send = (ImageButton) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessages(userID, password, typed);
            }
        });
    }

    public void sendMessages(String userID, String password, String typed) {

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
                .url(Config.SEND_MESSAGES)
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
                Log.d("JSON", jsonObject.toString());
                JSONObject jsonBody = new JSONObject();
                DissModel model = new DissModel();

                model.messageSent = message.getText().toString();
                model.setImage(R.drawable.shadowfight);
                modelArrayList.add(model);

                Discussion.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }
}

package com.ryms.mathagoras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signupPressed(View view) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(("{" +
                "\"student_id\": \"e19cse112\"," +
                "\"fname\": \"Isffhaan\"," +
                "\"email\": \"adef@b.com\"," +
                "\"password\": \"Ishaffan\"," +
                "\"dob\": \"2000-08-06\"" +
                "}"), JSON);
        Request request = new Request.Builder()
                .url(Config.STUDENT_SIGNUP)
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
                SignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject signUpResponse = new JSONObject(myres);
                            Log.d("signup", myres);
                            String status = signUpResponse.getString("type");
                            Log.d("signup", status);
                            if (status.equals("success")) {
                                Intent intent = new Intent(SignUpActivity.this, DashBoard.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
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


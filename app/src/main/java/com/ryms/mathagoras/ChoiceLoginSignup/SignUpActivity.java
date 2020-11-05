package com.ryms.mathagoras.ChoiceLoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.Dashb.DashBoard;
import com.ryms.mathagoras.R;

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
    EditText userId, name, emailSign, passSign;
    Switch teacherSwitch;
    boolean isTeacher;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userId = findViewById(R.id.userId);
        name = findViewById(R.id.name);
        emailSign = findViewById(R.id.userid);
        passSign = findViewById(R.id.passSign);
        teacherSwitch = findViewById(R.id.teacherSwitch);

        teacherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isTeacher = isChecked;
            }
        });
    }


    public void signupPressed(View view) {
        JSONObject jsonBody = new JSONObject();
        final String userid = userId.getText().toString();
        final String userPass = passSign.getText().toString();
        final String names = name.getText().toString();
        try {
            if (isTeacher) {
                jsonBody.put("teacher_id", userId.getText().toString());
            } else {
                jsonBody.put("student_id", userId.getText().toString());
            }
            jsonBody.put("fname", name.getText().toString());
            jsonBody.put("email", emailSign.getText().toString());
            jsonBody.put("password", passSign.getText().toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        String requestUrl = isTeacher? Config.TEACHER_SIGNUP:Config.STUDENT_SIGNUP;
        RequestBody body = RequestBody.create(String.valueOf(jsonBody), JSON);
        Request request = new Request.Builder()
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
                SignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject signUpResponse = new JSONObject(myres);
                            Log.d("signup", myres);
                            String status = signUpResponse.getString("type");
                            Log.d("signup", status);
                            if (status.equals("success")) {
                                sp = getSharedPreferences("SETTING", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("USERID", userid);
                                editor.putString("PASSWORD", userPass);
                                editor.putString("NAME", names);
                                editor.commit();
                                sp.edit().putBoolean("logged",true).apply();
                                Intent intent = new Intent(SignUpActivity.this, DashBoard.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + signUpResponse.getString("message"), Toast.LENGTH_LONG).show();
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


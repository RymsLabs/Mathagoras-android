package com.ryms.mathagoras.Class;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ryms.mathagoras.Configurations.Config;
import com.ryms.mathagoras.Dashb.DashBoard;
import com.ryms.mathagoras.Dashb.Model;
import com.ryms.mathagoras.Dashb.MyAdapter;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassRoom extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    CalenderAdapter calenderAdapter;
    ImageButton discussion;
    AlertDialog.Builder builder;
    SharedPreferences sp;
    TextView stuid, stuname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        sp = getSharedPreferences("SETTING", 0);
        builder = new AlertDialog.Builder(this);

        final String userID = sp.getString("USERID", "");
        final String password = sp.getString("PASSWORD", "");

        getClasses(userID, password);
    }

    public void getClasses(String userID, String password) {

        OkHttpClient client = new OkHttpClient();

        final ArrayList<ClassModel> modelArrayList = new ArrayList<>();

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        calenderAdapter = new CalenderAdapter(modelArrayList);
        recyclerView.setAdapter(calenderAdapter);

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
                .url(Config.GET_CLASSES)
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
                    ClassRoom.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(ClassRoom.this, finalJsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                JSONObject classes;
                try {
                    Log.d("JSON", jsonObject.toString());
                    classes = jsonObject.getJSONArray("classes").getJSONObject(0);

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd");
                    String from = classes.getString("from").split("T")[0];
                    String till = classes.getString("till").split("T")[0];

                    String[] inpt1 = from.split("-");
                    String[] inpt2 = till.split("-");

                    LocalDate date1 = LocalDate.of(Integer.parseInt(inpt1[0]), Integer.parseInt(inpt1[1]), Integer.parseInt(inpt1[2]));
                    LocalDate date2 = LocalDate.of(Integer.parseInt(inpt2[0]), Integer.parseInt(inpt2[1]), Integer.parseInt(inpt2[2]));

                    long daysBetween = ChronoUnit.DAYS.between(date1,date2);
                    System.out.println ("Days: " + daysBetween);

                    for (int i = 0; i < daysBetween; i++) {
                        int daysToIncrement = 0;
                        ClassModel model = new ClassModel();
                        String[] d = inpt1;

                        LocalDate temp = date1.plusDays(i);

                        model.date = temp.getDayOfMonth();
                        model.month = temp.getMonth().name();
                        model.day = temp.getDayOfWeek().name();
                        model.time = classes.getString("start_time") + " to " + classes.getString("end_time");
                        model.setImage(R.drawable.shadowfight);
                        modelArrayList.add(model);
                    }

                    ClassRoom.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            calenderAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}



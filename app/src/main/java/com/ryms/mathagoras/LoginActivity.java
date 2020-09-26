package com.ryms.mathagoras;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailSign);
        password = findViewById(R.id.passSign);
    }

    public void loginPressed(View view) {
        Log.d("LOGIN",("Login Pressed"));
        String emailTxt = email.getText().toString();
        String passTxt = password.getText().toString();

        if (emailTxt == null || passTxt == null || emailTxt == "" || passTxt == "") {
            //TODO: Alert user to enter email/pass
            System.out.println("WRONG INPUTS");
            return;
        }


        //TODO: Possibly do a better email validation or use an external library.
        OkHttpClient client = new OkHttpClient();
        String plainAuth = emailTxt + ":" + passTxt;
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
        Log.d("LOGIN",("Creating req with url: "+Config.STUDENT_LOGIN));
        Request request = new Request.Builder()
                .header("Authorization", ("Basic "+base64))
                .url(Config.STUDENT_LOGIN)
                .build();

//        // This is how we make a synchronous network call.
//        // We'll be doing asynchronous network calls though so as not to freeze the UI while we wait for the network result.
//
//        try {
//            Response response = client.newCall(request).execute();
//        } catch (ConnectException e) {
//            // Alert User, Server might be down.
//            System.out.println("Error connecting to Server.");
//            e.printStackTrace();
//        }
//        catch (Exception e) {
//            System.out.println("Error executing login HTTP req.: ");
//            e.printStackTrace();
//        }

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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //TODO: Alert user of login failure and handle error using try catch.
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Incorrect username or password!", Toast.LENGTH_LONG).show();
                        }
                    });
                    throw new IOException("Unexpected code " + response);
                } else {

                    // Request was successful, save username and password in localstoreage (needed for subsequent requests)
                    // And go to Dashboard screen

                    System.out.println("Response received: ");
                    System.out.println(response.body().string());

//                    // IMPORTANT: TO UPDATE UI, USE THE FOLLOWING CODE, UI *MUST* ALWAYS BE UPDATED ON THE *MAIN THREAD*
//                    LoginActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Update UI here.
//                        }
//                    });
                }
            }
        });
    }
}
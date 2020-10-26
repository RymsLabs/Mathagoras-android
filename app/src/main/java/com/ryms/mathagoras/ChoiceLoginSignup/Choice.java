package com.ryms.mathagoras.ChoiceLoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ryms.mathagoras.ChoiceLoginSignup.LoginActivity;
import com.ryms.mathagoras.ChoiceLoginSignup.SignUpActivity;
import com.ryms.mathagoras.R;

public class Choice extends AppCompatActivity {

    Button login, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openLoginActivity();
        }
    });

        signup=(Button)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignActivity();
            }
        });
    }

    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void openSignActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
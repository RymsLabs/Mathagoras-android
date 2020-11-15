package com.ryms.mathagoras.Options;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.ryms.mathagoras.Discussion.Discussion;
import com.ryms.mathagoras.R;

public class Options extends AppCompatActivity {

    Button teamsInt;
    ImageButton discussionInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        teamsInt = (Button) findViewById(R.id.teamsInt);
        discussionInt = (ImageButton) findViewById(R.id.discussionInt);

        teamsInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                if (launchIntent != null) {
                        startActivity(launchIntent);
                } else {
                        Toast.makeText(Options.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        });

        discussionInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Options.this, Discussion.class);
                startActivity(intent);
            }
        });
    }
}
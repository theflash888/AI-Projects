package com.example.devin.mobiledevicesproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOGIN_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, RESULT_LOGIN_ACTIVITY);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOGIN_ACTIVITY) {
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            }
        }
    }
}

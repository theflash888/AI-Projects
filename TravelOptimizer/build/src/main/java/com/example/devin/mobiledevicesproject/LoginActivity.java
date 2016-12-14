package com.example.devin.mobiledevicesproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private static final int RESULT_SIGNUP_ACTIVITY = 1;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }

    protected void login (View view) {
        // attempt to login
        DBHelper dbHelper = new DBHelper(this);

        // get input fields
        EditText editLoginEmail = (EditText) findViewById(R.id.editLoginEmail);
        EditText editLoginPassword = (EditText) findViewById(R.id.editLoginPassword);

        // get input from data fields
        String email = editLoginEmail.getText().toString();
        String password = editLoginPassword.getText().toString();

        RegexHelper rh = new RegexHelper(); // new instance of RegexHelper

        boolean error = false;
        // validate input
        if (!editLoginEmail.getText().toString().matches(rh.email)) {
            editLoginEmail.setError(getString(R.string.errorEmail));
            error = true;
        }

        if (!editLoginPassword.getText().toString().matches(rh.password)) {
            editLoginPassword.setError(getString(R.string.errorPasswordInvalid));
            error = true;
        }

        if (error)
            return;

        User user = dbHelper.login(email, password);
        // could not login, show error
        if (user == null) {
            editLoginPassword.setError(getString(R.string.errorEmailPasswordInvalid));
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void signup (View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, RESULT_SIGNUP_ACTIVITY);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_SIGNUP_ACTIVITY) {
            }
        }
    }
}

package com.example.devin.mobiledevicesproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {
    EditText editUserFirstName;
    EditText editUserLastName;
    EditText editUserEmail;
    EditText editUserPassword;
    EditText editUserPasswordConfirm;
    EditText editUserBirthdateDay;
    EditText editUserBirthdateMonth;
    EditText editUserBirthdateYear;
    EditText editLicenseClass;
    EditText editGender;
    boolean[] errors;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // 0 = email, 1 = password, 2 = passwordConfirm,
        // 3 = birthdateDay, 4 = birthdateMonth, 5 = birthdateYear
        errors = new boolean[8];
        for (int i = 0; i < errors.length; i++) {
            errors[i] = !errors[i];
        }

        final RegexHelper rh = new RegexHelper(); // new instance of RegexHelper

        // get all EditTexts from layout
        editUserFirstName = (EditText) findViewById(R.id.editUserFirstName);
        editUserLastName = (EditText) findViewById(R.id.editUserLastName);
        editUserEmail = (EditText) findViewById(R.id.editUserEmail);
        editUserPassword = (EditText) findViewById(R.id.editUserPassword);
        editUserPasswordConfirm = (EditText) findViewById(R.id.editUserPasswordConfirm);
        editUserBirthdateDay = (EditText) findViewById(R.id.editUserBirthdateDay);
        editUserBirthdateMonth = (EditText) findViewById(R.id.editUserBirthdateMonth);
        editUserBirthdateYear = (EditText) findViewById(R.id.editUserBirthdateYear);
        editLicenseClass = (EditText) findViewById(R.id.lGrade);
        editGender = (EditText) findViewById(R.id.gender);

        // validates email
        editUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editUserEmail.getText().toString().matches(rh.email)) {
                    editUserEmail.setError(getString(R.string.errorEmail));
                    errors[0] = true;
                } else {
                    errors[0] = false;
                }
            }
        });

        // validates initial password
        editUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editUserPassword.getText().toString().matches(rh.password)) {
                    editUserPassword.setError(getString(R.string.errorPasswordInvalid));
                    errors[1] = true;
                } else {
                    errors[1] = false;
                }
            }
        });

        // validates confirmed password
        editUserPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editUserPasswordConfirm.getText().toString().equals(editUserPassword.getText().toString())) {
                    editUserPasswordConfirm.setError(getString(R.string.errorMatchingPassword));
                    errors[2] = true;
                } else {
                    errors[2] = false;
                }
            }
        });

        // validates birth day
        editUserBirthdateDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editUserBirthdateDay.getText().toString().matches(rh.birthdateDay)) {
                    editUserBirthdateDay.setError(getString(R.string.errorBirthdateDay));
                    errors[3] = true;
                } else {
                    errors[3] = false;
                }
            }
        });

        // validates month
        editUserBirthdateMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editUserBirthdateMonth.getText().toString().matches(rh.birthdateMonth)) {
                    editUserBirthdateMonth.setError(getString(R.string.errorBirthdateMonth));
                    errors[4] = true;
                } else {
                    errors[4] = false;
                }
            }
        });

        // validates birth year
        editUserBirthdateYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editUserBirthdateYear.getText().toString().matches(rh.birthdateYear)) {
                    editUserBirthdateYear.setError(getString(R.string.errorBirthdateYear));
                    errors[5] = true;
                } else {
                    errors[5] = false;
                }
            }
        });

        // validates license class
        editLicenseClass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editLicenseClass.getText().toString().matches(rh.licenseClass)) {
                    editLicenseClass.setError(getString(R.string.errorLicenseClass));
                    errors[6] = true;
                } else {
                    errors[6] = false;
                }
            }
        });

        // validates gender
        editGender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editGender.getText().toString().matches(rh.gender)) {
                    editGender.setError(getString(R.string.errorGender));
                    errors[7] = true;
                } else {
                    errors[7] = false;
                }
            }
        });
    }

    protected void cancel (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    protected void confirm (View view) {
        for (boolean error : errors) {
            if (error)
                return;
        }
        // extract data from input fields and construct a User from it
        String firstName = editUserFirstName.getText().toString();
        String lastName = editUserLastName.getText().toString();
        String email = editUserEmail.getText().toString();
        String password = editUserPassword.getText().toString();
        String day = editUserBirthdateDay.getText().toString();
        String month = editUserBirthdateMonth.getText().toString();
        String year = editUserBirthdateYear.getText().toString();
        String license = editLicenseClass.getText().toString();
        String gender = editGender.getText().toString();

        String birthdate = day + "/" + month + "/" + year;

        User user = new User(firstName, lastName, email, password, birthdate, license, gender);
        DBHelper dbHelper = new DBHelper(this); // create new DBHelper

        int result = dbHelper.addUser(user);
        // display proper error message if addUsers was unsuccessful
        if (result == dbHelper.CODE_INVALID_EMAIL) {
            editUserEmail.setError(getString(R.string.errorEmail));
        } else if (result == dbHelper.CODE_EMAIL_TAKEN) {
            editUserEmail.setError(getString(R.string.errorEmailTaken));
        } else if (result == dbHelper.CODE_INVALID_PASSWORD) {
            editUserPassword.setError(getString(R.string.errorPasswordInvalid));
        } else if (result == dbHelper.CODE_INVALID_BIRTHDATE) {
            editUserBirthdateYear.setError(getString(R.string.errorBirthdateYear));
        } else if (result == dbHelper.CODE_SUCCESS) {
            // user added successfully, go to another activity
            Intent intent = new Intent(this, LoginActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}

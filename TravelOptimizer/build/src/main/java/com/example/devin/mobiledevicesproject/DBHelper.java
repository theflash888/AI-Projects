package com.example.devin.mobiledevicesproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

class DBHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "Users";
    private static final String VEHICLE_TABLE_NAME = "Vehicles";
    private static final String DATABASE_NAME = "Users";
    private static int DATABASE_VERSION = 1;

    // database key information
    private static final String KEY_USER_ID = "userID";
    private static final String KEY_USER_FIRSTNAME = "userFirstName";
    private static final String KEY_USER_LASTNAME = "userLastName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PASSWORD = "userPassword";
    private static final String KEY_USER_BIRTHDATE = "userBirthdate";
    private static final String KEY_USER_LICENSE = "userLicense";
    private static final String KEY_USER_GENDER = "userGender";

    //vehicle key information
    private static final String KEY_VEHICLE_CLASS = "vClass";
    private static final String KEY_VEHICLE_EFFICIENCY = "efficiencyMetric";

    final int CODE_SUCCESS = 0;
    final int CODE_INVALID_EMAIL = 1;
    final int CODE_EMAIL_TAKEN = 2;
    final int CODE_INVALID_PASSWORD = 3;
    final int CODE_INVALID_BIRTHDATE = 4;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * Creates a new database table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_USER_ID + " INTEGER PRIMARY KEY, " +
                KEY_USER_FIRSTNAME + " TEXT, " +
                KEY_USER_LASTNAME + " TEXT, " +
                KEY_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                KEY_USER_PASSWORD + " TEXT NOT NULL, " +
                KEY_USER_BIRTHDATE + " TEXT NOT NULL, " +
                KEY_USER_LICENSE + " TEXT NOT NULL, " +
                KEY_USER_GENDER + " TEXT NOT NULL " +
                ")";

        String createVehicleDataBase = "CREATE TABLE " + VEHICLE_TABLE_NAME + " (" +
                KEY_VEHICLE_CLASS + " TEXT PRIMARY KEY, " +
                KEY_VEHICLE_EFFICIENCY + " REAL " +
                ")";

        db.execSQL(createTable);
        db.execSQL(createVehicleDataBase);

        HashMap<String, Float> vehicleEfficiencies = new HashMap<>();
        vehicleEfficiencies.put("Full Size Pickup", 10.69f); // Based on Ford F-150
        vehicleEfficiencies.put("Mid-Size Pickup", 7.60f); // Based on Ford Ranger
        vehicleEfficiencies.put("Full-Size SUV", 13.07f); // Based on Chevrolet Tahoe
        vehicleEfficiencies.put("Smaller SUV/CUV", 13.84f); // Based on GMC Acadia
        vehicleEfficiencies.put("Hatchback", 7.47f); // Based on Kia Rondo
        vehicleEfficiencies.put("Minivan", 11.76f); // Based on Dodge Grand Caravan
        vehicleEfficiencies.put("Mid-size Car", 8.71f); // Based on Honda Accord 4-cyl
        vehicleEfficiencies.put("Compact Car", 7.35f); // Based on Toyota Corolla

        for (HashMap.Entry<String, Float> entry : vehicleEfficiencies.entrySet()) {
            ContentValues vehicleValues = new ContentValues();

            // populate vehicle database
            vehicleValues.put(KEY_VEHICLE_CLASS, entry.getKey());
            vehicleValues.put(KEY_VEHICLE_EFFICIENCY, entry.getValue());

            db.insert(VEHICLE_TABLE_NAME, null, vehicleValues);
        }
    }

    /*
     * Updates the database table if changes are detected
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME + ", " + VEHICLE_TABLE_NAME;
            db.execSQL(dropTable);
            onCreate(db);
            DATABASE_VERSION = newVersion;
        }
    }

    /*
    * Returns a new, unique ID by grabbing the largest ID from the database and incrementing it
    * by 1
    */
    private int getNextID() {
        String maxID = "SELECT MAX(" + KEY_USER_ID + ") FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery(maxID, null);
        int id = 0;
        result.moveToFirst();
        id = result.getInt(0);

        result.close();

        return id + 1;
    }

    float getEfficiency(String vehicleClass) {
        String select = KEY_VEHICLE_EFFICIENCY;
        String from = VEHICLE_TABLE_NAME;
        String where = "vClass=?";
        String[] whereArgs = new String[]{vehicleClass};
        String groupBy = "";
        String groupByArgs = "";
        String orderBy = KEY_VEHICLE_EFFICIENCY;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(from, new String[]{select}, where, whereArgs, groupBy, groupByArgs, orderBy);
        cursor.moveToFirst();
        float eff = cursor.getFloat(0);
        cursor.close();
        db.close();

        return eff;
    }

    /*
     * Takes a user and attempts to add its attributes to the database. If name or email is
     * not unique, returns false. Otherwise, returns true.
     */
    int addUser(User user) {
        // extract data from user
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String password = user.getPassword();
        String birthdate = user.getBirthdate();
        String license = user.getLicense();
        String gender = user.getGender();

        RegexHelper rh = new RegexHelper(); // new instance of RegexHelper

        // perform basic input validation
        if (!email.matches(rh.email))
            return CODE_INVALID_EMAIL;
        else if (!password.matches(rh.password))
            return CODE_INVALID_PASSWORD;
        else if (!birthdate.matches(rh.birthdate))
            return CODE_INVALID_BIRTHDATE;

        // query database for input email
        String queryEmail = "SELECT " + KEY_USER_EMAIL +
                " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery(queryEmail, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            if (results.getString(0).equals(email)) {
                results.close();
                db.close();
                return CODE_EMAIL_TAKEN;
            }
            results.moveToNext();
        }

        results.close();

        ContentValues values = new ContentValues();

        // add all values to a new row
        values.put(KEY_USER_ID, getNextID());
        values.put(KEY_USER_FIRSTNAME, firstName);
        values.put(KEY_USER_LASTNAME, lastName);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_PASSWORD, password);
        values.put(KEY_USER_BIRTHDATE, birthdate);
        values.put(KEY_USER_LICENSE, license);
        values.put(KEY_USER_GENDER, gender);

        db.insert(TABLE_NAME, null, values); // insert to database

        db.close();
        return CODE_SUCCESS;
    }

    User login(String userEmail, String userPassword) {
        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();
        RegexHelper rh = new RegexHelper(); // new instance of RegexHelper

        // check if email and password are a valid format
        if (!userEmail.matches(rh.email) || !userPassword.matches(rh.password))
            return user;

        // perform query for input email and password
        Cursor results = db.query(
                TABLE_NAME,
                new String[]{KEY_USER_ID, KEY_USER_FIRSTNAME, KEY_USER_LASTNAME, KEY_USER_EMAIL, KEY_USER_PASSWORD, KEY_USER_BIRTHDATE, KEY_USER_LICENSE, KEY_USER_GENDER},
                KEY_USER_EMAIL + " = ? AND " + KEY_USER_PASSWORD + " = ?",
                new String[]{userEmail, userPassword},
                null,
                null,
                null);

        if (results.moveToFirst() && results.getCount() > 0) {
            String id = results.getString(0);
            String firstName = results.getString(1);
            String lastName = results.getString(2);
            String email = results.getString(3);
            String password = results.getString(4);
            String birthdate = results.getString(5);
            String license = results.getString(6);
            String gender = results.getString(7);

            Log.d("DBHelper", id + " " + firstName + " " + lastName + " " + email + " " + password + " " + birthdate + " " + license + " " + gender);

            user = new User(firstName, lastName, email, password, birthdate, license, gender);
        } else {
            Log.d("DBHelper", userEmail + " " + userPassword);
        }

        return user;
    }
}

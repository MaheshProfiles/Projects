package com.jeevan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jeevan.GateModel.ProfileMaster;
import com.jeevan.signupmodel.SignupMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 9Jeevan on 19-08-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
//     All Static variables
//     Database Version
    public static final int DATABASE_VERSION = 1;

//     Database Name
    private static final String DATABASE_NAME = "GATE USERS";

//     Contacts table name
    private static final String TABLE_SIGNUPMASTER = "signup";

    /** login table columns */
    private static String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_LOGIN_NAME = "username";
    private static final String KEY_MOBILE_NO = "mobileNo";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_AREA = "area";
    private static final String KEY_OFFERED = "offered";
    private static final String KEY_REQUIRED = "required";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CONFIRM_PASSWORD= "confirm_password";

    /** Tables */
    private static final String CREATE_TABLE_SIGN_UP = "CREATE TABLE " + TABLE_SIGNUPMASTER + "(" + KEY_ID
            + " INTEGER PRIMARY KEY," + KEY_LOGIN_NAME + " TEXT, " + KEY_MOBILE_NO + " TEXT, " + KEY_EMAIL + " TEXT, "
            + KEY_CITY + "TEXT," + KEY_STATE + "TEXT," + KEY_AREA + "TEXT," + KEY_OFFERED + "TEXT," + KEY_REQUIRED + "TEXT,"
            + KEY_PASSWORD + " TEXT, " + KEY_CONFIRM_PASSWORD + "TEXT" + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHandler(Context context) {
        super(context, "DATABASE_NAME", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SIGN_UP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGNUPMASTER);
        onCreate(db);
    }

    public long createProfileUser(ProfileMaster userList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, userList.getUserID());
        values.put(KEY_LOGIN_NAME, userList.getUsername());
        values.put(KEY_MOBILE_NO, userList.getMobile());
        values.put(KEY_EMAIL, userList.getEmail());
        values.put(KEY_CITY, userList.getCity());
        values.put(KEY_STATE, userList.getState());
        values.put(KEY_AREA, userList.getArea());
        values.put(KEY_OFFERED, userList.getOffered());
        values.put(KEY_REQUIRED, userList.getRequired());
        values.put(KEY_PASSWORD, userList.getPassword());
//        values.put(KEY_CONFIRM_PASSWORD, userList.getConfirmpassword());
        values.put(KEY_CREATED_AT, userList.getCreatedAt().toString());

        // insert row
        long login_id = db.replace(TABLE_SIGNUPMASTER, null, values);

        return login_id;
    }

    public List<SignupMaster> getUserList(String username, String Password) {
        List<SignupMaster> userdetail = new ArrayList<SignupMaster>();
        String selectQuery = "SELECT * FROM " + TABLE_SIGNUPMASTER + " WHERE " + KEY_LOGIN_NAME + " = '" + username
                + "' AND " + KEY_PASSWORD + " = '" + Password + "'";
        Log.e("Sql Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SignupMaster userdata = new SignupMaster();
                userdata.setUsername(c.getString((c.getColumnIndex(KEY_LOGIN_NAME))));
                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
                userdetail.add(userdata);
            } while (c.moveToNext());
        }
        return userdetail;
    }

    public String getAllEntry(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SIGNUPMASTER, null, " username=?", new String[] { userName }, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String username = cursor.getString(cursor.getColumnIndex("username"));
        cursor.close();
        return username;
    }

    public long getforgotpasswordCount(String username, String email) {
        long subtotal;

        String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM " + TABLE_SIGNUPMASTER + " WHERE "
                + KEY_LOGIN_NAME + " = '" + username + "' AND " + KEY_EMAIL + " = '" + email + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        subtotal = c.getCount();
        return subtotal;
    }

    public List<SignupMaster> getforgetpassword(String username, String email) {
        List<SignupMaster> userpassword = new ArrayList<SignupMaster>();
        String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM " + TABLE_SIGNUPMASTER + " WHERE "
                + KEY_LOGIN_NAME + " = '" + username + "' AND " + KEY_EMAIL + " = '" + email + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SignupMaster userdata = new SignupMaster();
                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));

                userpassword.add(userdata);
            } while (c.moveToNext());
        }

        return userpassword;
    }

    public List<SignupMaster> getPassword(String Password) {
        List<SignupMaster> userdetail = new ArrayList<SignupMaster>();
        String selectQuery = "SELECT * FROM " + TABLE_SIGNUPMASTER + " WHERE " + KEY_PASSWORD + " = '" + Password + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SignupMaster userdata = new SignupMaster();

                userdata.setUserID(c.getString((c.getColumnIndex(KEY_ID))));
                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));

                userdetail.add(userdata);
            } while (c.moveToNext());
        }

        return userdetail;
    }

    /*
	 * updating a login
	 */
    public long updateLoginUser(SignupMaster userList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, userList.getUserID());
        values.put(KEY_PASSWORD, userList.getPassword());

        // insert row
        long login_id = db.update(TABLE_SIGNUPMASTER, values, KEY_ID + " = ?",
                new String[] { String.valueOf((userList.getUserID())) });

        return login_id;
    }

}

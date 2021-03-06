package com.jeevan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jeevan.GateModel.OTPMaster;
import com.jeevan.GateModel.ProfileMaster;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by 9Jeevan on 30-08-2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="GateProfile.db";
    //profile table of profile in gate db
    public static final String TABLE_PROFILE="Profile";

    // columns for profile table in db
    private static final String KEY_ID = "id";
    private static final String KEY_LOGIN_NAME = "username";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_AREA = "area";
    private static final String KEY_OFFERED = "offered";
    private static final String KEY_REQUIRED = "required";
    private static final String KEY_PASSWORD = "password";

    //common column names
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_MOBILE_NO = "mobileNo";
    private static final String KEY_EMAIL = "email";

    //otp table of otp in gate db
    public static final String TABLE_OTP="otp";

    // columns for otp table in db
    private static final String KEY_OTP  = "otp";


    /** Create Table query for profile*/
    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE " + TABLE_PROFILE + "(" + KEY_ID
            + " INTEGER PRIMARY KEY," + KEY_LOGIN_NAME + " TEXT, " + KEY_MOBILE_NO + " TEXT, " + KEY_EMAIL + " TEXT, "
            + KEY_CITY + " TEXT, " + KEY_STATE + " TEXT, " + KEY_AREA + " TEXT, " + KEY_OFFERED + " TEXT, " + KEY_REQUIRED + " TEXT, "
            + KEY_PASSWORD + " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";

    /** Create Table query for otp*/
    private static final String CREATE_TABLE_OTP = "CREATE TABLE " + TABLE_OTP + "(" + KEY_OTP
            + " INTEGER PRIMARY KEY," + KEY_EMAIL + " TEXT, " + KEY_MOBILE_NO + " TEXT, "
            + KEY_CREATED_AT + " DATETIME" + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_OTP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTP);

        // create new tables
        onCreate(db);
    }

    public long createProfileUser(ProfileMaster userList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("values inserted",userList+"");
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
        values.put(KEY_CREATED_AT, userList.getCreatedAt().toString());
        values.put(KEY_PASSWORD,userList.getPassword());
        db.insert(TABLE_PROFILE, null, values);
        return 1;
    }

    public long createOTP(OTPMaster otplist){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values1 = new ContentValues();
        values1.put(KEY_OTP, otplist.getOtp());
        values1.put(KEY_MOBILE_NO, otplist.getMobile());
        values1.put(KEY_EMAIL, otplist.getEmail());
        values1.put(KEY_CREATED_AT, otplist.getCreatedAt().toString());

        // insert row
        long otp_id = db.insert(TABLE_OTP, null, values1);

        return otp_id;
    }

    public List<ProfileMaster> getuserList(String username){
        List<ProfileMaster> userdetail = new ArrayList<ProfileMaster>();
        String selectQuery = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_LOGIN_NAME + " = ? '" + username + "'";
//                 "' AND " + KEY_PASSWORD + " = ? '" + Password + "'";
        Log.e("Sql Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ProfileMaster userdata = new ProfileMaster();
                userdata.setUsername(c.getString((c.getColumnIndex(KEY_LOGIN_NAME))));
//                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
                userdetail.add(userdata);
            } while (c.moveToNext());
        }
        return userdetail;
    }

    public String getMyOTP(String email,String mobileno){
        String sql2 = "SELECT "+ KEY_OTP +" FROM " + TABLE_OTP + " WHERE " + KEY_EMAIL + " = ? AND "+ KEY_MOBILE_NO + " = ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c2 = db.rawQuery(sql2, new String[]{email, mobileno});
        String rotp = "";
        try{
            c2.moveToFirst();
            rotp = c2.getString(c2.getColumnIndex(KEY_OTP));
            Log.e("ropt",rotp);
        }catch(Exception e){
            e.printStackTrace();
        }
        return rotp;
    }

    public String getValidUser(String email, String mobNo){
        String sql4 = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_EMAIL + " = ? OR "+ KEY_MOBILE_NO + " = ? ";
        Log.e("sql is ",sql4);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c4 = db.rawQuery(sql4, new String[]{email, mobNo});
        email = "";
        mobNo = "";
        try{
            c4.moveToFirst();
            email = c4.getString(c4.getColumnIndex(KEY_EMAIL));
            mobNo = c4.getString(c4.getColumnIndex(KEY_MOBILE_NO));
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public boolean getUserData(String userInput,String password){
        boolean isPresent = false ;
        String sql3 = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_EMAIL + " = ? OR "+ KEY_MOBILE_NO + " = ? OR " + KEY_LOGIN_NAME + "= ?" ;
        Log.e("sql is ",sql3);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c3 = db.rawQuery(sql3, new String[]{userInput, userInput, userInput});
        String user = "";
        String pswd = "";
        String mail = "";
        String mobNo = "";
        try{
            c3.moveToFirst();
            user = c3.getString(c3.getColumnIndex(KEY_LOGIN_NAME));
            pswd = c3.getString(c3.getColumnIndex(KEY_PASSWORD));
            mail = c3.getString(c3.getColumnIndex(KEY_EMAIL));
            mobNo = c3.getString(c3.getColumnIndex(KEY_MOBILE_NO));

            if(pswd.equals(password)){
                isPresent = true ;
            }
            }catch(Exception e){
            e.printStackTrace();
        }
        return isPresent;
    }

    public List<OTPMaster> getOtpList(String otp){
        List<OTPMaster> otpdetail = new ArrayList<OTPMaster>();
        String selectQuery = "SELECT * FROM " + TABLE_OTP + " WHERE " + KEY_OTP + " = '" + otp + "'";

        Log.e("Sql Query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                OTPMaster userdata = new OTPMaster();
                userdata.setOtp(c.getString((c.getColumnIndex(KEY_OTP))));
//                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
                otpdetail.add(userdata);
            } while (c.moveToNext());
        }
        return otpdetail;

    }

    public String getAllEntry(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_PROFILE, null, " username=?", new String[] { userName }, null, null, null);
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

    public String getAllEntry1(String otp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_OTP, null, " otp=?", new String[] { otp }, null, null, null);
        if (cursor.getCount() < 1) // otp Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String otp1 = cursor.getString(cursor.getColumnIndex("otp"));
        cursor.close();
        return otp1;
    }

    public long getforgotpasswordCount(String username, String email) {
        long subtotal;

        String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM " + TABLE_PROFILE + " WHERE "
                + KEY_LOGIN_NAME + " = '" + username + "' AND " + KEY_EMAIL + " = '" + email + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        subtotal = c.getCount();
        return subtotal;
    }

    public List<ProfileMaster> getForgotPassword(String mobileNo, String email) {
        List<ProfileMaster> userPassword = new ArrayList<ProfileMaster>();
        String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM " + TABLE_PROFILE + " WHERE "
                + KEY_MOBILE_NO + " = ? '" + mobileNo + "' AND " + KEY_EMAIL + " = ? '" + email + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ProfileMaster userdata = new ProfileMaster();
                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
                userPassword.add(userdata);
            } while (c.moveToNext());
        }

        return userPassword;
    }

    public List<ProfileMaster> getPassword(String Password) {
        List<ProfileMaster> userdetail = new ArrayList<ProfileMaster>();
        String selectQuery = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_PASSWORD + " = '" + Password + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ProfileMaster userdata = new ProfileMaster();

                userdata.setUserID(c.getString((c.getColumnIndex(KEY_ID))));
//                userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));

                userdetail.add(userdata);
            } while (c.moveToNext());
        }

        return userdetail;
    }

    /*
	 * updating a login
	 */
    public long updateLoginUser(ProfileMaster userList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, userList.getUserID());
//        values.put(KEY_PASSWORD, userList.getPassword());

        // insert row
        long login_id = db.update(TABLE_PROFILE, values, KEY_ID + " = ?",
                new String[] { String.valueOf((userList.getUserID())) });

        return login_id;
    }

    /*
	 * updating a otp
	 */
    public long updateOtpUser(OTPMaster otplist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, otplist.getOtp());
//        values.put(KEY_PASSWORD, userList.getPassword());

        // insert row
        long otp = db.update(TABLE_OTP, values, KEY_OTP + " = ?",
                new String[] { String.valueOf((otplist.getOtp())) });

        return otp;
    }
}


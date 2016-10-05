package com.sysfore.azure.sqlite.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Editable;
import android.util.Log;

import com.sysfore.azure.modal.AzureUtil;
import com.sysfore.azure.model.AddNewRole;
import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.LoginMaster;
import com.sysfore.azure.model.ProcessLog;
import com.sysfore.azure.model.ResourceUsageService;
import com.sysfore.azure.model.ResultPojo;
import com.sysfore.azure.model.Subscription;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String LOG = "DatabaseHelper";
	SQLiteDatabase db;
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "azure";

	// Table Names
	private static final String TABLE_LOGINMASTER = "login";
	// private static final String TABLE_REGISTRATION = "resgister";
	private static final String TABLE_SUBSCRIPTION = "subscription";
	private static final String TABLE_NEWROLE = "newrole";
	private static final String TABLE_RESOURCEUSAGEDETAIL = "resourceusagedetail";
	private static final String TABLE_VMUSAGEDETAIL = "vmusagedetail";

	private static final String TABLE_PRICEDETAILS = "pricedetails";

	private static final String TABLE_MONITORMACHINE = "monitormachine";
	private static final String TABLE_PROCESSLOG = "processlog";

	// Common column names
	private static String KEY_ID = "id";
	private static final String KEY_CREATED_BY = "created_by";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_ACTIVE = "isactive";

	// login Table - column names
	private static final String KEY_LOGINNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_MODE = "offlinemode";
	private static final String KEY_EMAIL = "email";
	// register Table - column names

	// private static final String KEY_EMAIL = "email";
	// private static final String KEY_SECURITYQUESTION = "securityquestion";
	// private static final String KEY_SECURITYANSWER = "securityanswer";

	// subcription Table - column names
	private static final String KEY_SUBCRIPTIONNAME = "subscriptionname";
	private static final String KEY_SUBCRIPTIONID = "subscriptionid";
	private static final String KEY_CERTIFICATEKEY = "certificatekey";

	private static final String KEY_SELECTION = "selection";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_EMPLOYMENTID = "enrollmentid";
	private static final String KEY_USAGESUMMARY = "usagesummary";
	private static final String KEY_ISDEFAULT = "isdefault";

	// newrole Table - column names

	/*
	 * private static final String KEY_ROLENAME = "rolename"; private static
	 * final String KEY_ACCOUNTINFO = "accountinfo"; private static final String
	 * KEY_RESOURCESUMMARY = "resourcesummary"; private static final String
	 * KEY_VMDETAILS = "vmdetails"; private static final String
	 * KEY_VMUSAGEDETAIL = "vmusagedetails"; private static final String
	 * KEY_USAGEDETAIL = "usagedetails";
	 */

	// resource usage Table - column names

	private static final String KEY_MAXSTORAGE = "maxstorage";
	private static final String KEY_MAXHOSTED = "maxhosted";
	private static final String KEY_CURRENTCORECOUNT = "currentcorecount";
	private static final String KEY_CURRENTHOSTEDSERVICES = "currenthostedservices";
	private static final String KEY_CURRENTSTORAGEACCOUNT = "currentstorageaccount";
	private static final String KEY_MAXCORECOUNT = "maxcorecount";

	// vmusage Table - column names

	private static final String KEY_SERVICENAME = "servicename";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_POWERSTATE = "powerstate";
	private static final String KEY_ROLESIZE = "rolesize";
	private static final String KEY_KEYNAME = "key";
	private static final String KEY_COUNT = "count";

	private static final String KEY_MONITORONLINE = "monitoronline";

	// price_detail Table - column names

	private static final String KEY_PRODUCT = "product";
	private static final String KEY_TARIFF = "tariff";
	private static final String KEY_UNIT = "unit";
	private static final String KEY_CONSUMEDQUANTITY = "consumedquantity";
	private static final String KEY_TOTAL = "total";
	private static final String KEY_MONTH = "month";
	private static final String KEY_YEAR = "year";

	// process_log Table - column names

	private static final String KEY_MONTHYEAR = "monthyear";
	private static final String KEY_LASTPROCESS = "lastprocess";

	// Table Create Statements

	// Login table create statement
	private static final String CREATE_TABLE_LOGIN = "CREATE TABLE "
			+ TABLE_LOGINMASTER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LOGINNAME + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_PASSWORD
			+ " TEXT, " + KEY_MODE + " TEXT, " + KEY_ACTIVE + " TEXT, "
			+ KEY_CREATED_AT + " DATETIME" + ")";

	// Register table create statement
	/*
	 * private static final String CREATE_TABLE_REGISTER = "CREATE TABLE " +
	 * TABLE_REGISTRATION + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
	 * KEY_LOGINNAME + " TEXT, "+ KEY_EMAIL + " TEXT, " + KEY_PASSWORD +
	 * " TEXT, " + KEY_SECURITYQUESTION + "TEXT, " +KEY_SECURITYANSWER+
	 * " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";
	 */

	/*
	 * // Subcription table create statement
	 * 
	 * private static final String CREATE_TABLE_NEW_ROLE = "CREATE TABLE " +
	 * TABLE_NEWROLE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ROLENAME +
	 * " TEXT, " + KEY_ACCOUNTINFO + " TEXT, " + KEY_RESOURCESUMMARY+ " TEXT, "
	 * + KEY_VMDETAILS + " TEXT, " + KEY_VMUSAGEDETAIL+ " TEXT, " +
	 * KEY_USAGEDETAIL + " TEXT, " + KEY_ACTIVE+ " TEXT, "+ KEY_CREATED_BY+
	 * " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";
	 */

	// Subcription table create statement
	private static final String CREATE_TABLE_SUBSCRIPTION = "CREATE TABLE "
			+ TABLE_SUBSCRIPTION + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SUBCRIPTIONNAME + " TEXT, " + KEY_SUBCRIPTIONID + " TEXT, "
			+ KEY_CERTIFICATEKEY + " TEXT, " + KEY_PASSWORD + " TEXT, "
			+ KEY_SELECTION + " TEXT, " + KEY_TOKEN + " TEXT, "
			+ KEY_EMPLOYMENTID + " TEXT, " + KEY_USAGESUMMARY + " TEXT, "
			+ KEY_ACTIVE + " TEXT, " + KEY_ISDEFAULT + " TEXT, "
			+ KEY_CREATED_BY + " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";

	// resourceusagedetail table create statement
	private static final String CREATE_TABLE_RESOURCEUSAGEDETAIL = "CREATE TABLE "+ TABLE_RESOURCEUSAGEDETAIL+ "("+ KEY_ID+ " INTEGER PRIMARY KEY,"+ KEY_SUBCRIPTIONID+ " TEXT, "+ KEY_MAXSTORAGE+ " TEXT, "+ KEY_MAXHOSTED+ " TEXT, "+ KEY_CURRENTCORECOUNT+ " TEXT, "+ KEY_CURRENTHOSTEDSERVICES+ " TEXT, "+ KEY_CURRENTSTORAGEACCOUNT+ " TEXT, "+ KEY_MAXCORECOUNT+ " TEXT, "+ KEY_CREATED_BY+ " TEXT, "+ KEY_CREATED_AT + " DATETIME" + ")";

	// vm_usage table create statement
	private static final String CREATE_TABLE_VMUSAGEDETAIL = "CREATE TABLE "
			+ TABLE_VMUSAGEDETAIL + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SUBCRIPTIONID + " TEXT, " + KEY_SERVICENAME + " TEXT, "
			+ KEY_LOCATION + " TEXT, " + KEY_POWERSTATE + " TEXT, "
			+ KEY_ROLESIZE + " TEXT, " + KEY_KEYNAME + " TEXT, " + KEY_COUNT
			+ " TEXT, " + KEY_CREATED_BY + " TEXT, " + KEY_CREATED_AT
			+ " DATETIME" + ")";

	// price_detail table create statement
	private static final String CREATE_TABLE_PRICEDETAILS = "CREATE TABLE "
			+ TABLE_PRICEDETAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SUBCRIPTIONID + " TEXT, " + KEY_PRODUCT + " TEXT, "
			+ KEY_TARIFF + " TEXT, " + KEY_UNIT + " TEXT, "
			+ KEY_CONSUMEDQUANTITY + " TEXT, " + KEY_MONTH + " TEXT, "
			+ KEY_YEAR + " TEXT, " + KEY_TOTAL + " TEXT, " + KEY_CREATED_BY
			+ " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";

	// Monitor machine Table create statement
	private static final String CREATE_TABLE_MONITORMACHINE = "CREATE TABLE "
			+ TABLE_MONITORMACHINE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SUBCRIPTIONID + " TEXT, " + KEY_SERVICENAME + " TEXT, "
			+ KEY_LOCATION + " TEXT, " + KEY_POWERSTATE + " TEXT, "
			+ KEY_ROLESIZE + " TEXT, " + KEY_MONITORONLINE + " TEXT, "
			+ KEY_CREATED_BY + " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";

	// Process Log Table create statement
	private static final String CREATE_TABLE_PROCESSLOG = "CREATE TABLE "
			+ TABLE_PROCESSLOG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SUBCRIPTIONID + " TEXT, " + KEY_MONTHYEAR + " TEXT, "
			+ KEY_LASTPROCESS + " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("Get Tags", "Getting Into super");
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("Get Tags", "Getting Into super: " + context.getPackageName());

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		Log.d("Get Tags", "Getting Into oncreate");
		db.execSQL(CREATE_TABLE_LOGIN);
		// db.execSQL(CREATE_TABLE_REGISTER);
		db.execSQL(CREATE_TABLE_SUBSCRIPTION);
		// db.execSQL(CREATE_TABLE_NEW_ROLE);
		db.execSQL(CREATE_TABLE_RESOURCEUSAGEDETAIL);
		db.execSQL(CREATE_TABLE_VMUSAGEDETAIL);
		db.execSQL(CREATE_TABLE_MONITORMACHINE);
		db.execSQL(CREATE_TABLE_PRICEDETAILS);
		db.execSQL(CREATE_TABLE_PROCESSLOG);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		Log.d("Get Tags", "Getting Into onupdateb4");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGINMASTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWROLE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESOURCEUSAGEDETAIL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VMUSAGEDETAIL);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICEDETAILS);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONITORMACHINE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROCESSLOG);

		Log.d("Get Tags", "Getting Into onupdate");
		// create new tables
		onCreate(db);
	}

	// ------------------------ "Login" table methods ----------------//

	/*
	 * Creating a login
	 */
	public long createLoginUser(LoginMaster userList) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, userList.getLoginId());
		values.put(KEY_LOGINNAME, userList.getLoginName());
		values.put(KEY_PASSWORD, userList.getPassword());
		values.put(KEY_MODE, userList.getOfflinemode());
		values.put(KEY_CREATED_AT, getDateTime());
		values.put(KEY_EMAIL, userList.getEmail());
		// insert row
		long login_id = db.replace(TABLE_LOGINMASTER, null, values);

		return login_id;
	}

	public List<LoginMaster> getUserList(String username, String Password) {
		List<LoginMaster> userdetail = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGINMASTER + " WHERE "+ KEY_LOGINNAME + " = '" + username + "' AND " + KEY_PASSWORD+ " = '" + Password + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();
				userdata.setLoginId(c.getString((c.getColumnIndex(KEY_ID))));
				userdata.setLoginName(c.getString((c.getColumnIndex(KEY_LOGINNAME))));
				userdata.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
				userdata.setOfflinemode((c.getString(c.getColumnIndex(KEY_MODE))));

				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	public String getAllEntry(String userName) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_LOGINMASTER, null, " username=?",
				new String[] { userName }, null, null, null);
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

		String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM "
				+ TABLE_LOGINMASTER + " WHERE " + KEY_LOGINNAME + " = '"
				+ username + "' AND " + KEY_EMAIL + " = '" + email + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		subtotal = c.getCount();
		return subtotal;
	}

	public List<LoginMaster> getforgetpassword(String username, String email) {
		List<LoginMaster> userpassword = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM "
				+ TABLE_LOGINMASTER + " WHERE " + KEY_LOGINNAME + " = '"
				+ username + "' AND " + KEY_EMAIL + " = '" + email + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();
				userdata.setPassword((c.getString(c
						.getColumnIndex(KEY_PASSWORD))));

				userpassword.add(userdata);
			} while (c.moveToNext());
		}

		return userpassword;
	}

	public List<LoginMaster> getPassword(String Password) {
		List<LoginMaster> userdetail = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGINMASTER + " WHERE "
				+ KEY_PASSWORD + " = '" + Password + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();

				userdata.setLoginId(c.getString((c.getColumnIndex(KEY_ID))));
				userdata.setPassword((c.getString(c
						.getColumnIndex(KEY_PASSWORD))));

				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	public List<LoginMaster> getUserDetails() {
		List<LoginMaster> userdetail = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT offlinemode FROM " + TABLE_LOGINMASTER;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();

				/*
				 * userdata.setLoginId(c.getString((c.getColumnIndex(KEY_ID))));
				 * userdata
				 * .setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
				 */
				userdata.setOfflinemode((c.getString(c.getColumnIndex(KEY_MODE))));

				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	/*
	 * updating a login
	 */
	public long updateLoginUser(LoginMaster userList) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("entering inside updatelogin:", "login");
		ContentValues values = new ContentValues();
		values.put(KEY_ID, userList.getLoginId());
		Log.d("KEY_ID:", "" + userList.getLoginId());
		values.put(KEY_PASSWORD, userList.getPassword());
		Log.d("KEY_PASSWORD:", "" + userList.getPassword());
		values.put(KEY_MODE, userList.getOfflinemode());
		Log.d("KEY_MODE:", "" + userList.getOfflinemode());

		// insert row
		long login_id = db.update(TABLE_LOGINMASTER, values, KEY_ID + " = ?",
				new String[] { String.valueOf((userList.getLoginId())) });

		return login_id;
	}

	public long updateLoginUserMode(LoginMaster userList) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("entering inside updatelogin:", "login");
		ContentValues values = new ContentValues();
		values.put(KEY_ID, userList.getLoginId());
		Log.d("KEY_ID:", "" + userList.getLoginId());

		values.put(KEY_MODE, userList.getOfflinemode());
		Log.d("KEY_MODE:", "" + userList.getOfflinemode());

		// insert row
		long login_id = db.update(TABLE_LOGINMASTER, values, KEY_ID + " = ?",
				new String[] { String.valueOf((userList.getLoginId())) });

		return login_id;
	}

	/*
	 * public long createRegisterUser(RegistrationMaster register) {
	 * SQLiteDatabase db = this.getWritableDatabase();
	 * 
	 * ContentValues values = new ContentValues(); values.put(KEY_ID,
	 * register.getRegistrationId()); values.put(KEY_LOGINNAME,
	 * register.getUsername()); values.put(KEY_EMAIL, register.getEmail());
	 * values.put(KEY_PASSWORD, register.getPassword());
	 * values.put(KEY_SECURITYQUESTION, register.getSecurityQuestion());
	 * values.put(KEY_SECURITYANSWER, register.getSecurityAnswer());
	 * values.put(KEY_CREATED_AT, getDateTime());
	 * 
	 * // insert row long login_id = db.replace(TABLE_REGISTRATION, null,
	 * values);
	 * 
	 * return login_id; }
	 */

	/*
	 * Creating a subcription
	 */

	public long createSubcription(Subscription sub) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		ContentValues valuesUpdation = new ContentValues();
		values.put(KEY_ID, sub.get_ID());
		values.put(KEY_SUBCRIPTIONNAME, sub.getSubcriptionName());
		values.put(KEY_SUBCRIPTIONID, AzureUtil.encrypt(sub.getSubcriptionId()));
		values.put(KEY_CERTIFICATEKEY, sub.getCertificateKey());
		values.put(KEY_PASSWORD, AzureUtil.encrypt(sub.getPassword()));
		values.put(KEY_SELECTION, sub.getSelection());
		values.put(KEY_TOKEN,AzureUtil.encrypt(sub.getToken()));
		values.put(KEY_EMPLOYMENTID,AzureUtil.encrypt( sub.getEmplomentID()));
		values.put(KEY_USAGESUMMARY, sub.getUsagesummary());
		values.put(KEY_ISDEFAULT, sub.getIsDefault());
		values.put(KEY_CREATED_BY, sub.getCreatedby());
		values.put(KEY_CREATED_AT, getDateTime());

		values.put(KEY_ISDEFAULT, sub.getIsDefault());
		Log.d("sub.getIsDefault():", "" + sub.getIsDefault());
		valuesUpdation.put(KEY_ISDEFAULT, "0");

		// updating row
		if (sub.getIsDefault() == 1) {
			int updation1 = db.update(TABLE_SUBSCRIPTION, valuesUpdation,
					KEY_ISDEFAULT + "=?", new String[] { Integer.toString(1) });
		}

		// insert row
		long login_id = db.replace(TABLE_SUBSCRIPTION, null, values);

		return login_id;
	}

	public long updateSubscription(Subscription sub) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("entering inside UpdateSubscription:*********************",
				"UpdateSubscription");
		ContentValues values = new ContentValues();

		values.put(KEY_USAGESUMMARY, sub.getUsagesummary());
		Log.d("KEY_USAGESUMMARY:", "" + sub.getUsagesummary());

		// insert row
		long login_id = db.update(TABLE_SUBSCRIPTION, values, KEY_SUBCRIPTIONID
				+ "=? AND " + KEY_CREATED_BY + "=? ",
				new String[] { AzureUtil.encrypt(sub.getSubcriptionId()), sub.getCreatedby() });

		return login_id;
	}

	public int updateSetSubscrition(String login_id) {

		Log.d("update", "updating values");
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		ContentValues valuesUpdation = new ContentValues();

		values.put(KEY_ISDEFAULT, "1");
		valuesUpdation.put(KEY_ISDEFAULT, "0");

		// updating row
		int updation1 = db.update(TABLE_SUBSCRIPTION, valuesUpdation,
				KEY_ISDEFAULT + "=?", new String[] { Integer.toString(1) });

		int updation = db.update(TABLE_SUBSCRIPTION, values, KEY_ID + "=?",
				new String[] { login_id });

		return updation;

	}

	public long getSubscriptionCount(String login_id) {
		long subtotal;
		String selectQuery = "SELECT * FROM " + TABLE_SUBSCRIPTION + " WHERE "
				+ KEY_CREATED_BY + " = '" + login_id + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		subtotal = c.getCount();
		return subtotal;
	}

	public long getPriceDetailcount(String month, String year,
			String subscriptionID) {
		long subtotal;
		String selectQuery = "SELECT * FROM " + TABLE_PRICEDETAILS + " WHERE "
				+ KEY_MONTH + " = '" + month + "' AND " + KEY_YEAR + " = '"
				+ year + "' AND " + KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(subscriptionID)
				+ "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		subtotal = c.getCount();
		return subtotal;
	}

	public List<Subscription> getAllSubcription(String login_id,
			String subscriptionID) {
		List<Subscription> subcription = new ArrayList<Subscription>();
		String selectQuery = "SELECT * FROM " + TABLE_SUBSCRIPTION + " WHERE "
				+ KEY_CREATED_BY + " = '" + login_id + "' AND "
				+ KEY_SUBCRIPTIONID + " = '" +AzureUtil.encrypt(subscriptionID) + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Subscription sub = new Subscription();
				sub.set_ID(c.getString((c.getColumnIndex(KEY_ID))));
				sub.setSubcriptionName(c.getString((c
						.getColumnIndex(KEY_SUBCRIPTIONNAME))));
				sub.setSubcriptionId(AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_SUBCRIPTIONID)))));
				sub.setCertificateKey(c.getString((c
						.getColumnIndex(KEY_CERTIFICATEKEY))));
				sub.setCreatedby(c.getString((c.getColumnIndex(KEY_CREATED_BY))));
				sub.setPassword(AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_PASSWORD)))));

				sub.setSelection(c.getString((c.getColumnIndex(KEY_SELECTION))));
				sub.setToken(AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_TOKEN)))));
				sub.setEmplomentID(AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_EMPLOYMENTID)))));
				sub.setUsagesummary(c.getString((c
						.getColumnIndex(KEY_USAGESUMMARY))));

				subcription.add(sub);
			} while (c.moveToNext());
		}

		return subcription;
	}

	public List<Subscription> getListofSubcription(String login_id) {
		List<Subscription> subcription = new ArrayList<Subscription>();
		String selectQuery = "SELECT * FROM " + TABLE_SUBSCRIPTION + " WHERE "
				+ KEY_CREATED_BY + " = '" + login_id + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Subscription sub = new Subscription();
				sub.set_ID(c.getString((c.getColumnIndex(KEY_ID))));
				sub.setSubcriptionName(c.getString((c
						.getColumnIndex(KEY_SUBCRIPTIONNAME))));
				sub.setSubcriptionId(AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_SUBCRIPTIONID)))));
				sub.setCertificateKey(c.getString((c
						.getColumnIndex(KEY_CERTIFICATEKEY))));
				sub.setCreatedby(c.getString((c.getColumnIndex(KEY_CREATED_BY))));
				sub.setPassword(AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_PASSWORD)))));

				sub.setSelection(c.getString((c.getColumnIndex(KEY_SELECTION))));
				sub.setToken(AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_TOKEN)))));
				sub.setEmplomentID(AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_EMPLOYMENTID)))));
				sub.setUsagesummary(c.getString((c
						.getColumnIndex(KEY_USAGESUMMARY))));

				subcription.add(sub);
			} while (c.moveToNext());
		}

		return subcription;
	}

	// Deleting Subcrition single contact
	public void Delete_Contact(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SUBSCRIPTION, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void clearResourceUsage(String subscriptionID, String login_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("ENTERING TO DELETE************", "resource");

		db.delete(TABLE_RESOURCEUSAGEDETAIL, KEY_SUBCRIPTIONID + "=? AND "
				+ KEY_CREATED_BY + "=? ", new String[] { AzureUtil.encrypt(subscriptionID),
				login_id });

		db.delete(TABLE_VMUSAGEDETAIL, KEY_SUBCRIPTIONID + "=? AND "
				+ KEY_CREATED_BY + "=? ", new String[] { AzureUtil.encrypt(subscriptionID),
				login_id });

	}

	/*
	 * Creating a newrole
	 */
	/*
	 * public long createNewRole(AddNewRole newrole) { SQLiteDatabase db =
	 * this.getWritableDatabase();
	 * 
	 * ContentValues values = new ContentValues();
	 * 
	 * values.put(KEY_ROLENAME, newrole.getRolename());
	 * values.put(KEY_ACCOUNTINFO, newrole.getAccountinfo());
	 * values.put(KEY_RESOURCESUMMARY, newrole.getResourcesummary());
	 * values.put(KEY_VMDETAILS, newrole.getVmdetail());
	 * values.put(KEY_VMUSAGEDETAIL, newrole.getVmusagedetail());
	 * values.put(KEY_USAGEDETAIL, newrole.getUsagedetail());
	 * values.put(KEY_CREATED_BY, newrole.getCreatedby());
	 * values.put(KEY_CREATED_AT, getDateTime());
	 * 
	 * // insert row long login_id = db.replace(TABLE_NEWROLE, null, values);
	 * 
	 * return login_id; }
	 * 
	 * 
	 * updating a newrole
	 * 
	 * 
	 * public long updateNewRole(AddNewRole newrole) { SQLiteDatabase db =
	 * this.getWritableDatabase();
	 * 
	 * ContentValues values = new ContentValues(); values.put(KEY_ID,
	 * newrole.getID()); values.put(KEY_ROLENAME, newrole.getRolename());
	 * values.put(KEY_ACCOUNTINFO, newrole.getAccountinfo());
	 * values.put(KEY_RESOURCESUMMARY, newrole.getResourcesummary());
	 * values.put(KEY_VMDETAILS, newrole.getVmdetail());
	 * values.put(KEY_VMUSAGEDETAIL, newrole.getVmusagedetail());
	 * values.put(KEY_USAGEDETAIL, newrole.getUsagedetail());
	 * values.put(KEY_CREATED_BY, newrole.getCreatedby());
	 * values.put(KEY_CREATED_AT, getDateTime());
	 * 
	 * // insert row long login_id = db.update(TABLE_NEWROLE, values, KEY_ID +
	 * " = ?", new String[] { String.valueOf((newrole.getID())) });
	 * 
	 * return login_id; }
	 * 
	 * public long getRoleCount(String login_id) { long subtotal; String
	 * selectQuery = "SELECT * FROM " + TABLE_NEWROLE + " WHERE " +
	 * KEY_CREATED_BY + " = '" + login_id + "'"; Log.e(LOG, selectQuery);
	 * SQLiteDatabase db = this.getReadableDatabase(); Cursor c =
	 * db.rawQuery(selectQuery, null); subtotal = c.getCount(); return subtotal;
	 * }
	 * 
	 * public List<AddNewRole> getRoleName(String login_id) { List<AddNewRole>
	 * newrole = new ArrayList<AddNewRole>(); String selectQuery =
	 * "SELECT * FROM " + TABLE_NEWROLE + " WHERE " + KEY_CREATED_BY + " = '" +
	 * login_id + "'";
	 * 
	 * Log.e(LOG, selectQuery);
	 * 
	 * SQLiteDatabase db = this.getReadableDatabase(); Cursor c =
	 * db.rawQuery(selectQuery, null); AddNewRole role; role = new AddNewRole();
	 * role.setRolename("Select"); newrole.add(role); // looping through all
	 * rows and adding to list if (c.moveToFirst()) { do { role = new
	 * AddNewRole(); role.setID(c.getString((c.getColumnIndex(KEY_ID))));
	 * role.setAccountinfo(c.getString((c .getColumnIndex(KEY_ACCOUNTINFO))));
	 * role.setResourcesummary(c.getString((c
	 * .getColumnIndex(KEY_RESOURCESUMMARY))));
	 * role.setUsagedetail(c.getString((c .getColumnIndex(KEY_USAGEDETAIL))));
	 * role.setVmdetail(c.getString((c.getColumnIndex(KEY_VMDETAILS))));
	 * role.setVmusagedetail(c.getString((c
	 * .getColumnIndex(KEY_VMUSAGEDETAIL))));
	 * role.setRolename(c.getString((c.getColumnIndex(KEY_ROLENAME))));
	 * 
	 * newrole.add(role); } while (c.moveToNext()); }
	 * 
	 * return newrole; }
	 */

	/*
	 * Creating a resourceusagedetails
	 */

	public long createResourceUsageDetail(ResourceUsageService resourceservice) {
		long login_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d("setMaxStorageAccountsin db  :",
				resourceservice.getMaxStorageAccounts());
		ContentValues values = new ContentValues();

		values.put(KEY_MAXSTORAGE, resourceservice.getMaxStorageAccounts());
		values.put(KEY_MAXHOSTED, resourceservice.getMaxHostedServices());
		values.put(KEY_CURRENTCORECOUNT, resourceservice.getCurrentCoreCount());
		values.put(KEY_CURRENTHOSTEDSERVICES,
				resourceservice.getCurrentHostedServices());
		values.put(KEY_CURRENTSTORAGEACCOUNT,
				resourceservice.getCurrentStorageAccounts());
		values.put(KEY_MAXCORECOUNT, resourceservice.getMaxCoreCount());
		values.put(KEY_SUBCRIPTIONID,AzureUtil.encrypt( resourceservice.getSubcriptionId()));
		values.put(KEY_CREATED_BY, resourceservice.getCreatedby());
		values.put(KEY_CREATED_AT, getDateTime());
		int updation = db.update(TABLE_RESOURCEUSAGEDETAIL, values,
				KEY_SUBCRIPTIONID + "=? AND " + KEY_CREATED_BY + "=? ",
				new String[] { AzureUtil.encrypt(resourceservice.getSubcriptionId()),
						resourceservice.getCreatedby() });
		if (updation == 0) {
			values.put(KEY_CREATED_AT, getDateTime());
			login_id = db.replace(TABLE_RESOURCEUSAGEDETAIL, null, values);
		}

		// insert row
		// long login_id = db.replace(TABLE_RESOURCEUSAGEDETAIL, null, values);

		return login_id;
	}

	public List<ResourceUsageService> getAllResourceUsageDetails(
			String login_id, String subscriptionID) {
		List<ResourceUsageService> resourceservicedetails = new ArrayList<ResourceUsageService>();
		String selectQuery = "SELECT * FROM " + TABLE_RESOURCEUSAGEDETAIL
				+ " WHERE " + KEY_CREATED_BY + " = '" + login_id + "' AND "
				+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(subscriptionID) + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResourceUsageService service = new ResourceUsageService();
				service.setMaxStorageAccounts(c.getString((c
						.getColumnIndex(KEY_MAXSTORAGE))));
				service.setMaxHostedServices(c.getString((c
						.getColumnIndex(KEY_MAXHOSTED))));
				service.setCurrentCoreCount(c.getString((c
						.getColumnIndex(KEY_CURRENTCORECOUNT))));
				service.setCurrentHostedServices(c.getString((c
						.getColumnIndex(KEY_CURRENTHOSTEDSERVICES))));
				service.setCurrentStorageAccounts(c.getString((c
						.getColumnIndex(KEY_CURRENTSTORAGEACCOUNT))));
				service.setMaxCoreCount(c.getString((c
						.getColumnIndex(KEY_MAXCORECOUNT))));
				service.setSubcriptionId(AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_SUBCRIPTIONID)))));

				resourceservicedetails.add(service);
			} while (c.moveToNext());
		}

		return resourceservicedetails;
	}

	public int getResourceCount(String login_id, String subscriptionID) {
		String selectQuery = "SELECT * FROM " + TABLE_RESOURCEUSAGEDETAIL
				+ " WHERE " + KEY_CREATED_BY + " = '" + login_id + "' AND "
				+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(subscriptionID) + "'";
		int countSel = 0;
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor dataCount = db.rawQuery(selectQuery, null);
		countSel = dataCount.getCount();
		dataCount.close();
		return countSel;
	}

	/*
	 * Creating a vmusagedetails
	 */

	public long createVMUsageDetail(HostedService hostedservice) {
		long login_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_SERVICENAME, AzureUtil.encrypt( hostedservice.getServiceName()));
		values.put(KEY_LOCATION,  AzureUtil.encrypt(hostedservice.getLocation()));
		values.put(KEY_POWERSTATE, hostedservice.getPowerState());
		values.put(KEY_ROLESIZE, hostedservice.getRoleSize());
		// values.put(KEY_KEYNAME, hostedservice.getKey());
		// values.put(KEY_COUNT, hostedservice.getCount());
		values.put(KEY_SUBCRIPTIONID, AzureUtil.encrypt(hostedservice.getSubcriptionId()));
		values.put(KEY_CREATED_BY, hostedservice.getCreatedby());
		values.put(KEY_CREATED_AT, getDateTime());

		int updation = db.update(TABLE_VMUSAGEDETAIL, values, KEY_SUBCRIPTIONID
				+ "=? AND " + KEY_SERVICENAME + "=? AND " + KEY_CREATED_BY
				+ "=? ", new String[] { AzureUtil.encrypt(hostedservice.getSubcriptionId()),
				 AzureUtil.encrypt(hostedservice.getServiceName()), hostedservice.getCreatedby() });
		if (updation == 0) {
			values.put(KEY_CREATED_AT, getDateTime());
			login_id = db.replace(TABLE_VMUSAGEDETAIL, null, values);
		}

		return login_id;
	}

	// select servicename,rolesize,powerstate from vmusagedetail where
	// powerstate= 'Started' and created_by='1' and subscriptionid =
	// 'fc7b7fb1-2b20-4fdc-addb-42abd01f1317'

	public List<HostedService> getAllvmUsageUsageDetails(String login_id,
			String SubcriptionID) {

		List<HostedService> hostedservice = new ArrayList<HostedService>();

		String selectQuery = "SELECT * FROM " + TABLE_VMUSAGEDETAIL + " WHERE "
				+ KEY_CREATED_BY + " = '" + login_id + "' AND "
				+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(SubcriptionID) + "' AND "
				+ KEY_POWERSTATE + " = '" + "Started" + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				HostedService service = new HostedService();
				service.setServiceName( AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_SERVICENAME)))));
				service.setRoleSize(c.getString((c.getColumnIndex(KEY_ROLESIZE))));

				service.setPowerState(c.getString((c.getColumnIndex(KEY_POWERSTATE))));
				service.setLocation( AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_LOCATION)))));
				service.setPowerState(c.getString((c.getColumnIndex(KEY_POWERSTATE))));

				hostedservice.add(service);
			} while (c.moveToNext());
		}

		return hostedservice;
	}

	public int getVmusageCount(String login_id, String subscriptionID) {
		String selectQuery = "SELECT * FROM " + TABLE_VMUSAGEDETAIL + " WHERE "	+ KEY_CREATED_BY + " = '" + login_id + "' AND "+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(subscriptionID) + "'";

		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor dataCount = db.rawQuery(selectQuery, null);

		return dataCount.getCount();
	}

	public int getVmusageStartedCount(String login_id, String SubcriptionID) {
		String selectQuery = "SELECT * FROM " + TABLE_VMUSAGEDETAIL + " WHERE "+ KEY_CREATED_BY + " = '" + login_id + "' AND "+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(SubcriptionID) + "' AND "+ KEY_POWERSTATE + " = '" + "Started" + "'";

		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor dataCount = db.rawQuery(selectQuery, null);

		return dataCount.getCount();
	}

	/*
	 * Creating a pricedetails
	 */

	public long createPriceDetail(ResultPojo result) {
		long login_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();

		/*
		 * db.delete(TABLE_PRICEDETAILS,
		 * KEY_SUBCRIPTIONID+"=? AND "+KEY_CREATED_BY
		 * +"=? AND "+KEY_PRODUCT+"=? AND "+KEY_MONTH+"=? AND "+KEY_YEAR+"=? ",
		 * new String[]
		 * {result.getSubcriptionId(),result.getCreatedby(),result.getProduct
		 * (),result.getMonth(),result.getYear()});
		 */

		ContentValues values = new ContentValues();

		values.put(KEY_SUBCRIPTIONID,AzureUtil.encrypt( result.getSubcriptionId()));
		values.put(KEY_PRODUCT, AzureUtil.encrypt( result.getProduct()));
		values.put(KEY_TARIFF, result.getPerUnitCommitted());
		values.put(KEY_UNIT, result.getUnitofMeasure());
		values.put(KEY_CONSUMEDQUANTITY, result.getUnitConsumed());
		values.put(KEY_MONTH, result.getMonth());
		values.put(KEY_YEAR, result.getYear());
		values.put(KEY_TOTAL, result.getBillTotal());
		values.put(KEY_CREATED_BY, result.getCreatedby());
		// values.put(KEY_CREATED_AT, getDateTime());
		int updation = db.update(
				TABLE_PRICEDETAILS,
				values,
				KEY_SUBCRIPTIONID + "=? AND " + KEY_CREATED_BY + "=? AND "
						+ KEY_PRODUCT + "=? AND " + KEY_MONTH + "=? AND "
						+ KEY_YEAR + "=? ",
				new String[] {AzureUtil.encrypt( result.getSubcriptionId()),
						result.getCreatedby(),  AzureUtil.encrypt(result.getProduct()),
						result.getMonth(), result.getYear() });
		if (updation == 0) {
			values.put(KEY_CREATED_AT, getDateTime());
			login_id = db.replace(TABLE_PRICEDETAILS, null, values);
		}
		// long login_id = db.replace(TABLE_PRICEDETAILS, null, values);

		// insert row
		// long login_id = db.replace(TABLE_RESOURCEUSAGEDETAIL, null, values);

		return login_id;
	}

	public List<ResultPojo> getAllPriceDetails(String login_id,
			String SubcriptionID, String month, String year) {
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		String selectQuery = "SELECT * FROM " + TABLE_PRICEDETAILS + " WHERE "
				+ KEY_CREATED_BY + " = '" + login_id + "' AND "
				+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(SubcriptionID) + "' AND "
				+ KEY_MONTH + "='" + month + "' AND " + KEY_YEAR + "='" + year
				+ "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				result.setProduct( AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_PRODUCT)))));
				result.setPerUnitCommitted(c.getString((c
						.getColumnIndex(KEY_TARIFF))));
				result.setUnitofMeasure(c.getString((c.getColumnIndex(KEY_UNIT))));
				result.setUnitConsumed(c.getString((c
						.getColumnIndex(KEY_CONSUMEDQUANTITY))));
				result.setBillTotal(c.getString((c.getColumnIndex(KEY_TOTAL))));
				result.setMonth(c.getString((c.getColumnIndex(KEY_MONTH))));
				result.setYear(c.getString((c.getColumnIndex(KEY_YEAR))));

				resultpojo.add(result);
			} while (c.moveToNext());
		}

		return resultpojo;
	}

	public List<HostedService> getAllmonitormachineDetails(String login_id,
			String SubcriptionID) {

		List<HostedService> MonitorMachine = new ArrayList<HostedService>();
		String selectQuery = "SELECT " + " " + "V" + "." + KEY_SERVICENAME
				+ "," + "V" + "." + KEY_POWERSTATE + "," + "V" + "."
				+ KEY_ROLESIZE + "," + "V" + "." + KEY_LOCATION + ","
				+ "COALESCE" + "(" + "( SELECT " + "M" + "."
				+ KEY_MONITORONLINE + " FROM " + TABLE_MONITORMACHINE + " "
				+ "M" + " WHERE " + "V" + "." + KEY_SERVICENAME + " = " + "M"
				+ "." + KEY_SERVICENAME + ")," + "'N" + "') " + "AS "
				+ KEY_MONITORONLINE + " FROM " + TABLE_VMUSAGEDETAIL + " "
				+ "V" + " WHERE " + "V" + "." + KEY_CREATED_BY + " = '"
				+ login_id + "' AND " + "V" + "." + KEY_SUBCRIPTIONID + " = '"
				+ AzureUtil.encrypt(SubcriptionID) + "' AND " + "V" + "." + KEY_POWERSTATE
				+ " = '" + "Started" + "'";
		Log.e(LOG, selectQuery);
		db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				HostedService service = new HostedService();
				service.setServiceName( AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_SERVICENAME)))));
				service.setPowerState(c.getString((c
						.getColumnIndex(KEY_POWERSTATE))));
				service.setRoleSize(c.getString((c.getColumnIndex(KEY_ROLESIZE))));
				// service.setSubcriptionId(c.getString((c.getColumnIndex(KEY_SUBCRIPTIONID))));
				// service.setCreatedby(c.getString((c.getColumnIndex(KEY_CREATED_BY))));
				service.setMode(c.getString((c
						.getColumnIndex(KEY_MONITORONLINE))));
				service.setLocation( AzureUtil.decrypt(c.getString((c.getColumnIndex(KEY_LOCATION)))));

				MonitorMachine.add(service);
			} while (c.moveToNext());
		}

		return MonitorMachine;
	}

	/*
	 * Creating a Monitor machine details
	 */

	public long createMonitorMachine(HostedService hostedservice) {

		SQLiteDatabase db = this.getWritableDatabase();
		long login_id = 0;
		ContentValues values = new ContentValues();

		values.put(KEY_SERVICENAME,  AzureUtil.encrypt(hostedservice.getServiceName()));
		values.put(KEY_LOCATION,  AzureUtil.encrypt(hostedservice.getLocation()));
		values.put(KEY_POWERSTATE, hostedservice.getPowerState());
		values.put(KEY_ROLESIZE, hostedservice.getRoleSize());
		values.put(KEY_SUBCRIPTIONID, AzureUtil.encrypt(hostedservice.getSubcriptionId()));
		values.put(KEY_CREATED_BY, hostedservice.getCreatedby());
		values.put(KEY_MONITORONLINE, hostedservice.getMode());
		values.put(KEY_CREATED_AT, getDateTime());

		int updation = db.update(TABLE_MONITORMACHINE,values,KEY_SUBCRIPTIONID + "=? AND " + KEY_CREATED_BY + "=? AND "
						+ KEY_SERVICENAME + "=? ",
				new String[] { AzureUtil.encrypt(hostedservice.getSubcriptionId()),
						hostedservice.getCreatedby(),
						 AzureUtil.encrypt(hostedservice.getServiceName()) });

		if (updation == 0) {
			values.put(KEY_CREATED_AT, getDateTime());

			login_id = db.replace(TABLE_MONITORMACHINE, null, values);
		}
		return login_id;
	}

	// **** Monitor Machine Status ****//

	public List<HostedService> getAllDashboardDetails() {
		List<HostedService> MonitorMachine = new ArrayList<HostedService>();
		String selectQuery = "SELECT " + " " + TABLE_VMUSAGEDETAIL + "."
				+ KEY_SERVICENAME + "," + TABLE_VMUSAGEDETAIL + "."
				+ KEY_POWERSTATE + "," + TABLE_MONITORMACHINE + "."
				+ KEY_MONITORONLINE + " FROM " + TABLE_MONITORMACHINE + " "
				+ " LEFT " + " JOIN " + TABLE_VMUSAGEDETAIL + " ON "
				+ TABLE_MONITORMACHINE + "." + KEY_SERVICENAME + " = "
				+ TABLE_VMUSAGEDETAIL + "." + KEY_SERVICENAME + " WHERE "
				+ TABLE_MONITORMACHINE + "." + KEY_MONITORONLINE + " = '" + "Y"
				+ "'";

		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				HostedService service = new HostedService();
				service.setServiceName( AzureUtil.decrypt(c.getString((c
						.getColumnIndex(KEY_SERVICENAME)))));
				service.setPowerState(c.getString((c
						.getColumnIndex(KEY_POWERSTATE))));
				service.setMode(c.getString((c
						.getColumnIndex(KEY_MONITORONLINE))));
				MonitorMachine.add(service);
			} while (c.moveToNext());
		}
		return MonitorMachine;
	}

	/*
	 * Creating a ProcessLog
	 */
	public long createProcessLog(ProcessLog log) {
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d("Enetring in processlog***************  :", "Process log");
		long login_id = 0;
		ContentValues values = new ContentValues();

		values.put(KEY_SUBCRIPTIONID,AzureUtil.encrypt( log.getSubscriptionID()));
		values.put(KEY_MONTHYEAR, log.getMonthyear());
		values.put(KEY_LASTPROCESS, log.getLastprocess());

		int updation = db.update(TABLE_PROCESSLOG, values, KEY_SUBCRIPTIONID
				+ "=? AND " + KEY_MONTHYEAR + "= ?",
				new String[] { AzureUtil.encrypt(log.getSubscriptionID()), log.getMonthyear() });
		if (updation == 0) {

			login_id = db.replace(TABLE_PROCESSLOG, null, values);
		}
		// insert row

		return login_id;
	}

	/*
	 * Retreiving a ProcessLog
	 */
	public String getProcessLog(String subscriptionID, String monthyear) {

		String lastprocess = "";
		String selectQuery = "SELECT * FROM " + TABLE_PROCESSLOG + " WHERE "
				+ KEY_SUBCRIPTIONID + " = '" + AzureUtil.encrypt(subscriptionID) + "' AND "
				+ KEY_MONTHYEAR + " = '" + monthyear + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ProcessLog pLog = new ProcessLog();
				// setSubscriptionID(c.getString((c.getColumnIndex(KEY_SUBCRIPTIONID))));
				// pLog.setMonthyear(c.getString((c.getColumnIndex(KEY_MONTHYEAR))));
				lastprocess = c.getString((c.getColumnIndex(KEY_LASTPROCESS)));

			} while (c.moveToNext());

		}

		return lastprocess;
	}

	/**
	 * get clear monitor machine
	 * */
	public void cleardata() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MONITORMACHINE, null, null);

	}

	/**
	 * get clear price table
	 * */
	/*
	 * public void clearPricedetails() { SQLiteDatabase db =
	 * this.getWritableDatabase(); db.delete(TABLE_PRICEDETAILS, null, null);
	 * 
	 * }
	 */

	public void clearPricedetails(String month, String year, String Token) {
		SQLiteDatabase db = this.getWritableDatabase();
		ResultPojo result = new ResultPojo();
		db.delete(TABLE_PRICEDETAILS, KEY_SUBCRIPTIONID + "=? AND " + KEY_MONTH
				+ "=? AND " + KEY_YEAR + "=? ",
				new String[] { AzureUtil.encrypt(result.getSubcriptionId()), result.getMonth(),
						result.getYear() });

	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

}

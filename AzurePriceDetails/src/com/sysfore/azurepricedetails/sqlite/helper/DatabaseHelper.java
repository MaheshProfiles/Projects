package com.sysfore.azurepricedetails.sqlite.helper;

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
import android.util.Log;

import com.sysfore.azurepricedetails.modal.AzureUtil;
import com.sysfore.azurepricedetails.model.LoginMaster;
import com.sysfore.azurepricedetails.model.ProcessLog;
import com.sysfore.azurepricedetails.model.ResultPojo;
import com.sysfore.azurepricedetails.model.Subscription;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String LOG = "DatabaseHelper";
	SQLiteDatabase db;
	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "azurepricedetails";
	/**
	 * Table Names
	 */
	private static final String TABLE_LOGINMASTER = "login";
	private static final String TABLE_ACCOUNTS = "accounts";
	private static final String TABLE_PROCESSLOG = "processlog";
	private static final String TABLE_PRICEDETAILS = "pricedetails";
	private static final String TABLE_COMPONENT_PRICEDETAILS = "componentpricedetails";
	/**
	 * Common column names
	 */
	private static String KEY_ID = "id";
	private static final String KEY_CREATED_BY = "created_by";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_ACTIVE = "isactive";
	/**
	 * login Table - column names
	 */
	private static final String KEY_LOGINNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_MODE = "offlinemode";
	/**
	 * subcription Table - column names
	 */
	private static final String KEY_ACCOUNTNAME = "accountname";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_EMPLOYMENTID = "enrollmentid";
	private static final String KEY_USAGESUMMARY = "usagesummary";

	/**
	 * price_detail Table - column names
	 */
	private static final String KEY_PRODUCT = "product";
	private static final String KEY_SUBSCRIPTION = "subscription";
	private static final String KEY_SUBSCRIPTION_NAME = "subscriptionname";
	private static final String KEY_TARIFF = "tariff";
	private static final String KEY_UNIT = "unit";
	private static final String KEY_CONSUMEDQUANTITY = "consumedquantity";
	private static final String KEY_TOTAL = "total";
	private static final String KEY_MONTH = "month";
	private static final String KEY_YEAR = "year";
	private static final String KEY_COMPONENT = "component";

	/**
	 * process_log Table - column names
	 */
	private static final String KEY_MONTHYEAR = "monthyear";
	private static final String KEY_LASTPROCESS = "lastprocess";

	/**
	 * Table Create Statements
	 */

	/**
	 * Login table create statement
	 */
	private static final String CREATE_TABLE_LOGIN = "CREATE TABLE "
			+ TABLE_LOGINMASTER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LOGINNAME + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_PASSWORD
			+ " TEXT, " + KEY_MODE + " TEXT, " + KEY_ACTIVE + " TEXT, "
			+ KEY_CREATED_AT + " DATETIME" + ")";
	/**
	 * Subcription table create statement
	 */
	private static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE "
			+ TABLE_ACCOUNTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_ACCOUNTNAME + " TEXT, " + KEY_TOKEN + " TEXT, "
			+ KEY_EMPLOYMENTID + " TEXT, " + KEY_USAGESUMMARY + " TEXT, "
			+ KEY_ACTIVE + " TEXT, " + KEY_CREATED_BY + " TEXT, "
			+ KEY_CREATED_AT + " DATETIME" + ")";
	/**
	 * price_detail table create statement
	 */
	private static final String CREATE_TABLE_PRICEDETAILS = "CREATE TABLE "
			+ TABLE_PRICEDETAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_PRODUCT + " TEXT, " + KEY_SUBSCRIPTION + " TEXT, "
			+ KEY_SUBSCRIPTION_NAME + " TEXT, " + KEY_TARIFF + " TEXT, "
			+ KEY_UNIT + " TEXT, " + KEY_CONSUMEDQUANTITY + " TEXT, "
			+ KEY_MONTH + " TEXT, " + KEY_YEAR + " TEXT, " + KEY_TOTAL
			+ " TEXT, " + KEY_CREATED_BY + " TEXT, " + KEY_CREATED_AT
			+ " DATETIME" + ")";

	/**
	 * price_detail table final create statement
	 */

	private static final String CREATE_TABLE_COMPONENT_PRICEDETAILS = "CREATE TABLE "
			+ TABLE_COMPONENT_PRICEDETAILS
			+ "("
			+ KEY_ID
			+ " INTEGER PRIMARY KEY,"
			+ KEY_PRODUCT
			+ " TEXT, "
			+ KEY_SUBSCRIPTION
			+ " TEXT, "
			+ KEY_SUBSCRIPTION_NAME
			+ " TEXT, "
			+ KEY_COMPONENT
			+ " TEXT, "
			+ KEY_TARIFF
			+ " TEXT, "
			+ KEY_UNIT
			+ " TEXT, "
			+ KEY_CONSUMEDQUANTITY
			+ " TEXT, "
			+ KEY_MONTH
			+ " TEXT, "
			+ KEY_YEAR
			+ " TEXT, "
			+ KEY_TOTAL
			+ " TEXT, "
			+ KEY_CREATED_BY + " TEXT, " + KEY_CREATED_AT + " DATETIME" + ")";

	/**
	 * Process Log Table create statement
	 */

	private static final String CREATE_TABLE_PROCESSLOG = "CREATE TABLE "
			+ TABLE_PROCESSLOG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_TOKEN + " TEXT, " + KEY_MONTHYEAR + " TEXT, "
			+ KEY_LASTPROCESS + " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("Get Tags", "Getting Into super");
		db = this.getWritableDatabase();
		Log.d("Get Tags", "Getting Into super: " + context.getPackageName());

	}

	/**
	 * Creating a Sqlite database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		Log.d("Get Tags", "Getting Into oncreate");
		db.execSQL(CREATE_TABLE_LOGIN);
		db.execSQL(CREATE_TABLE_PROCESSLOG);
		db.execSQL(CREATE_TABLE_ACCOUNTS);
		db.execSQL(CREATE_TABLE_PRICEDETAILS);
		db.execSQL(CREATE_TABLE_COMPONENT_PRICEDETAILS);

	}

	/**
	 * Upgrading the Database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		Log.d("Get Tags", "Getting Into onupdateb4");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGINMASTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROCESSLOG);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICEDETAILS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPONENT_PRICEDETAILS);

		Log.d("Get Tags", "Getting Into onupdate");
		// create new tables
		onCreate(db);
	}

	// ------------------------ "Login" table methods ----------------//

	/**
	 * Creating a login
	 */
	public long createLoginUser(LoginMaster userList) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, userList.getLoginId());
		values.put(KEY_LOGINNAME, userList.getLoginName());
		values.put(KEY_PASSWORD, AzureUtil.encrypt(userList.getPassword()));
		values.put(KEY_EMAIL, AzureUtil.encrypt(userList.getEmail()));
		values.put(KEY_MODE, userList.getOfflinemode());
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row
		long login_id = db.replace(TABLE_LOGINMASTER, null, values);
		return login_id;
	}

	/**
	 * Get the usernames
	 */
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

	/**
	 * Get Password Count
	 */
	public long getforgotpasswordCount(String username, String email) {
		long subtotal;

		String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM "+ TABLE_LOGINMASTER + " WHERE " + KEY_LOGINNAME + " = '"+ username + "' AND " + KEY_EMAIL + " = '"+ AzureUtil.encrypt(email) + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		subtotal = c.getCount();
		return subtotal;
	}

	/**
	 * Validating for Forget password
	 */
	public List<LoginMaster> getpassword(String username, String email) {
		List<LoginMaster> userpassword = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT " + " " + KEY_PASSWORD + " " + " FROM "+ TABLE_LOGINMASTER + " WHERE " + KEY_LOGINNAME + " = '"+ username + "' AND " + KEY_EMAIL + " = '"+ AzureUtil.encrypt(email) + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();
				userdata.setPassword(AzureUtil.decrypt(c.getString(c
						.getColumnIndex(KEY_PASSWORD))));

				userpassword.add(userdata);
			} while (c.moveToNext());
		}

		return userpassword;
	}

	/**
	 * Retereiving User Name and validating pasword and email
	 */
	public List<LoginMaster> getUserList(String username, String Password) {
		List<LoginMaster> userdetail = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGINMASTER + " WHERE "+ KEY_LOGINNAME + " = '" + username + "' AND " + KEY_PASSWORD+ " = '" + AzureUtil.encrypt(Password) + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();
				userdata.setLoginId(c.getString((c.getColumnIndex(KEY_ID))));
				userdata.setLoginName(c.getString((c
						.getColumnIndex(KEY_LOGINNAME))));
				userdata.setPassword(AzureUtil.decrypt(c.getString(c
						.getColumnIndex(KEY_PASSWORD))));
				userdata.setOfflinemode((c.getString(c.getColumnIndex(KEY_MODE))));

				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	/**
	 * Get the password
	 */
	public List<LoginMaster> getPassword(String Password) {
		List<LoginMaster> userdetail = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGINMASTER + " WHERE "+ KEY_PASSWORD + " = '" + AzureUtil.encrypt(Password) + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();

				userdata.setLoginId(c.getString((c.getColumnIndex(KEY_ID))));
				userdata.setPassword(AzureUtil.decrypt(c.getString(c.getColumnIndex(KEY_PASSWORD))));
				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	/**
	 * Getting the offline mode details
	 */
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
				userdata.setOfflinemode((c.getString(c.getColumnIndex(KEY_MODE))));
				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	/**
	 * updating a login Password
	 */
	public long updateLoginUser(LoginMaster userList) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("entering inside updatelogin:", "login");
		ContentValues values = new ContentValues();
		values.put(KEY_ID, userList.getLoginId());
		Log.d("KEY_ID:", "" + userList.getLoginId());
		values.put(KEY_PASSWORD, AzureUtil.encrypt(userList.getPassword()));
		Log.d("KEY_PASSWORD:", "" + AzureUtil.decrypt(userList.getPassword()));

		// insert row
		long login_id = db.update(TABLE_LOGINMASTER, values, KEY_ID + " = ?",new String[] { String.valueOf((userList.getLoginId())) });
		return login_id;
	}

	/**
	 * Updating a login with the password
	 */
	public long updateLoginUserMode(LoginMaster userList) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("entering inside updatelogin:", "login");
		ContentValues values = new ContentValues();
		values.put(KEY_ID, userList.getLoginId());
		Log.d("KEY_ID:", "" + userList.getLoginId());
		values.put(KEY_MODE, userList.getOfflinemode());
		Log.d("KEY_MODE:", "" + userList.getOfflinemode());

		// insert row
		long login_id = db.update(TABLE_LOGINMASTER, values, KEY_ID + " = ?",new String[] { String.valueOf((userList.getLoginId())) });

		return login_id;
	}

	/**
	 * Creating Subscription
	 */
	public long createSubcription(Subscription sub) {
		SQLiteDatabase db = this.getWritableDatabase();
		long login_id = 0;
		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNTNAME, sub.getSubcriptionName());
		values.put(KEY_TOKEN, sub.getToken());
		values.put(KEY_EMPLOYMENTID, sub.getEmplomentID());
		values.put(KEY_CREATED_AT, getDateTime());

		int updation = db.update(TABLE_ACCOUNTS, values,KEY_CREATED_BY + "=? ", new String[] { sub.getCreatedby() });
		// insert row
		if (updation == 0) {
			values.put(KEY_CREATED_BY, sub.getCreatedby());
			login_id = db.replace(TABLE_ACCOUNTS, null, values);
		}
		return login_id;
	}

	/**
	 * Update Subscription
	 */
	public long updateSubscription(Subscription sub) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("entering inside UpdateSubscription:*********************",
				"UpdateSubscription");
		ContentValues values = new ContentValues();
		values.put(KEY_USAGESUMMARY, sub.getUsagesummary());
		Log.d("KEY_USAGESUMMARY:", "" + sub.getUsagesummary());
		// insert row
		long login_id = db.update(TABLE_ACCOUNTS, values, KEY_CREATED_BY+ "=? ", new String[] { sub.getCreatedby() });
		return login_id;
	}

	/**
	 * Get the subscription count
	 */
	public long getSubscriptionCount(String login_id) {
		long subtotal;

		String selectQuery = "SELECT * FROM " + TABLE_ACCOUNTS + " WHERE "+ KEY_CREATED_BY + " = '" + login_id + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		subtotal = c.getCount();
		return subtotal;
	}

	/**
	 * Get all subscription details
	 */
	public List<Subscription> getAllSubcription(String login_id) {
		List<Subscription> subcription = new ArrayList<Subscription>();
		String selectQuery = "SELECT * FROM " + TABLE_ACCOUNTS + " WHERE "+ KEY_CREATED_BY + " = '" + login_id + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Subscription sub = new Subscription();
				sub.set_ID(c.getString((c.getColumnIndex(KEY_ID))));
				sub.setSubcriptionName(c.getString(c.getColumnIndex(KEY_ACCOUNTNAME)));
				sub.setCreatedby(c.getString(c.getColumnIndex(KEY_CREATED_BY)));
				sub.setToken(c.getString(c.getColumnIndex(KEY_TOKEN)));
				sub.setEmplomentID(c.getString(c.getColumnIndex(KEY_EMPLOYMENTID)));
				sub.setUsagesummary(c.getString(c.getColumnIndex(KEY_USAGESUMMARY)));
				subcription.add(sub);
			} while (c.moveToNext());
		}

		return subcription;
	}

	/**
	 * Deleting Subcrition single contact
	 */
	public void Delete_Contact(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ACCOUNTS, KEY_ID + " = ?",new String[] { String.valueOf(id) });
		db.close();
	}

	/**
	 * Creating a pricedetails
	 */

	public long createPriceDetail(ResultPojo result) {
		long login_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PRODUCT, result.getProduct());
		values.put(KEY_SUBSCRIPTION, result.getSubcriptionId());
		values.put(KEY_SUBSCRIPTION_NAME, result.getSubcriptionName());
		values.put(KEY_TARIFF, result.getPerUnitCommitted());
		values.put(KEY_UNIT, result.getUnitofMeasure());
		values.put(KEY_CONSUMEDQUANTITY, result.getUnitConsumed());
		values.put(KEY_MONTH, result.getMonth());
		values.put(KEY_YEAR, result.getYear());
		values.put(KEY_TOTAL, result.getBillTotal());
		values.put(KEY_CREATED_BY, result.getCreatedby());

		int updation = db.update(TABLE_PRICEDETAILS,values,KEY_CREATED_BY + "=? AND " + KEY_PRODUCT + "=? AND "+ KEY_SUBSCRIPTION + "=? AND " + KEY_MONTH + "=? AND "+ KEY_YEAR + "=? ",new String[] { result.getCreatedby(), result.getProduct(),result.getSubcriptionId(), result.getMonth(),	result.getYear() });
		if (updation == 0) {
			values.put(KEY_CREATED_AT, getDateTime());
			login_id = db.replace(TABLE_PRICEDETAILS, null, values);
		}
		return login_id;
	}

	/**
	 * Creating a pricedetails
	 */

	public long createbillingPriceDetail(ResultPojo result) {
		long login_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_PRODUCT, result.getProduct());
		values.put(KEY_SUBSCRIPTION, result.getSubcriptionId());
		values.put(KEY_SUBSCRIPTION_NAME, result.getSubcriptionName());
		values.put(KEY_COMPONENT, result.getComponent());
		values.put(KEY_TARIFF, result.getPerUnitCommitted());
		values.put(KEY_UNIT, result.getUnitofMeasure());
		values.put(KEY_CONSUMEDQUANTITY, result.getUnitConsumed());
		values.put(KEY_MONTH, result.getMonth());
		values.put(KEY_YEAR, result.getYear());
		values.put(KEY_TOTAL, result.getBillTotal());
		values.put(KEY_CREATED_BY, result.getCreatedby());
		int updation = db.update(TABLE_COMPONENT_PRICEDETAILS, values,KEY_CREATED_BY + "=? AND " + KEY_PRODUCT + "=? AND "+ KEY_COMPONENT + " =? AND " + KEY_SUBSCRIPTION+ "=? AND " + KEY_MONTH + "=? AND " + KEY_YEAR + "=? ",new String[] { result.getCreatedby(), result.getProduct(),result.getComponent(), result.getSubcriptionId(),result.getMonth(), result.getYear() });
		if (updation == 0) {
			values.put(KEY_CREATED_AT, getDateTime());
			login_id = db.replace(TABLE_COMPONENT_PRICEDETAILS, null, values);

		}
		return login_id;
	}

	/**
	 * Get all the price details
	 */
	public List<ResultPojo> getAllPriceDetails(String login_id,
			String SubcriptionID, String month, String year) {
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		String selectQuery = "SELECT * FROM " + TABLE_PRICEDETAILS + " WHERE "+ KEY_MONTH + "='" + month + "' AND " + KEY_YEAR + "='" + year+ "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				result.setProduct(c.getString(c.getColumnIndex(KEY_PRODUCT)));
				result.setPerUnitCommitted(c.getString(c.getColumnIndex(KEY_TARIFF)));
				result.setUnitofMeasure(c.getString(c.getColumnIndex(KEY_UNIT)));
				result.setUnitConsumed(c.getString(c.getColumnIndex(KEY_CONSUMEDQUANTITY)));
				result.setBillTotal(c.getString(c.getColumnIndex(KEY_TOTAL)));
				result.setMonth(c.getString(c.getColumnIndex(KEY_MONTH)));
				result.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
				resultpojo.add(result);
			} while (c.moveToNext());
		}

		return resultpojo;
	}

	/**
	 * Get the price details based on the subscriptions
	 */
	public List<ResultPojo> getsubscriptionPriceDetails(String login_id,
			String SubcriptionID, String month, String year) {
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		String selectQuery = "SELECT " + " DISTINCT "+ TABLE_COMPONENT_PRICEDETAILS + "." + KEY_SUBSCRIPTION_NAME+ " FROM " + TABLE_COMPONENT_PRICEDETAILS + " WHERE "+ KEY_MONTH + "='" + month + "' AND " + KEY_YEAR + "='" + year+ "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		ResultPojo defaultn = new ResultPojo();
		defaultn.setSubcriptionName("Select");
		resultpojo.add(defaultn);
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				result.setSubcriptionName(c.getString(c.getColumnIndex(KEY_SUBSCRIPTION_NAME)));
				resultpojo.add(result);
			} while (c.moveToNext());
		}

		return resultpojo;
	}

	/**
	 * Get the price details based on the selected subscription
	 */
	public List<ResultPojo> getAllListPriceDetails(String login_id,String SubcriptionName, String month, String year) {
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		String selectQuery="SELECT component,sum(total) as total,subscription FROM " + TABLE_COMPONENT_PRICEDETAILS+" WHERE "+KEY_SUBSCRIPTION_NAME+" ='" + SubcriptionName +"' AND "+KEY_MONTH+" ='"+month+ "' AND "+KEY_YEAR+" ='"+year+"' GROUP BY "+"component"+"";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				result.setSubcriptionId(c.getString(c.getColumnIndex(KEY_SUBSCRIPTION)));
				result.setComponent(c.getString(c.getColumnIndex(KEY_COMPONENT)));
				result.setBillTotal(c.getString(c.getColumnIndex(KEY_TOTAL)));
				result.setMonth(month);
				result.setYear(year);
				resultpojo.add(result);
			} while (c.moveToNext());
		}

		return resultpojo;
	}

	/**
	 * Creating a ProcessLog
	 */
	public long createProcessLog(ProcessLog log) {
		SQLiteDatabase db = this.getWritableDatabase();

		Log.d("Enetring in processlog***************  :", "Process log");
		long login_id = 0;
		ContentValues values = new ContentValues();
		values.put(KEY_TOKEN, log.getToken());
		values.put(KEY_MONTHYEAR, log.getMonthyear());
		values.put(KEY_LASTPROCESS, log.getLastprocess());
		int updation = db.update(TABLE_PROCESSLOG, values, KEY_TOKEN+ "=? AND " + KEY_MONTHYEAR + "= ?",new String[] { log.getToken(), log.getMonthyear() });
		if (updation == 0) {
			login_id = db.replace(TABLE_PROCESSLOG, null, values);
		}
		// insert row

		return login_id;
	}

	/**
	 * Retreiving a ProcessLog
	 */
	public String getProcessLog(String Token, String monthyear) {

		String lastprocess = "";
		String selectQuery = "SELECT * FROM " + TABLE_PROCESSLOG + " WHERE "+ KEY_TOKEN + " = '" + Token + "' AND " + KEY_MONTHYEAR+ " = '" + monthyear + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				lastprocess = c.getString((c.getColumnIndex(KEY_LASTPROCESS)));
			} while (c.moveToNext());
		}
		return lastprocess;
	}

	/**
	 * Close the database
	 */
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get clear price table
	 * */
	public void clearPricedetails(String month, String year,
			String Subcription, int loginid) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_COMPONENT_PRICEDETAILS, KEY_SUBSCRIPTION + "="+ Subcription + " AND " + KEY_MONTH + "=" + month + " AND "+ KEY_YEAR + "=" + year + " AND " + KEY_CREATED_BY + "="+ loginid, null);

	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Get the price detailsCount
	 */
	public long getPriceDetailcount(String month, String year, String Token) {
		long subtotal;
		String selectQuery = "SELECT * FROM " + TABLE_COMPONENT_PRICEDETAILS+ " WHERE " + KEY_MONTH + " = '" + month + "' AND " + KEY_YEAR+ " = '" + year + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		subtotal = c.getCount();
		return subtotal;
	}

	/**
	 * Get the password for changing the password
	 */
	public List<LoginMaster> getchangePassword(String Password) {
		List<LoginMaster> userdetail = new ArrayList<LoginMaster>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGINMASTER + " WHERE "+ KEY_PASSWORD + " = '" + AzureUtil.encrypt(Password) + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LoginMaster userdata = new LoginMaster();
				userdata.setLoginId(c.getString(c.getColumnIndex(KEY_ID)));
				userdata.setPassword(AzureUtil.decrypt(c.getString(c.getColumnIndex(KEY_PASSWORD))));
				userdetail.add(userdata);
			} while (c.moveToNext());
		}

		return userdetail;
	}

	/**
	 * Get all the price details
	 */
	public List<ResultPojo> getAllComponentPriceDetails(String login_id,String SubcriptionID, String month, String year) {
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		String selectQuery = "SELECT " + " DISTINCT "+ TABLE_COMPONENT_PRICEDETAILS + "." + KEY_COMPONENT + ","+ TABLE_COMPONENT_PRICEDETAILS + "." + KEY_YEAR + ","+ TABLE_COMPONENT_PRICEDETAILS + "." + KEY_TOTAL + ","+ TABLE_COMPONENT_PRICEDETAILS + "." + KEY_MONTH + " FROM "+ TABLE_COMPONENT_PRICEDETAILS + " WHERE " + KEY_MONTH + "='"+ month + "' AND " + KEY_YEAR + "='" + year + "'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				// result.setProduct(c.getString((c.getColumnIndex(KEY_PRODUCT))));
				// result.setPerUnitCommitted(c.getString((c.getColumnIndex(KEY_TARIFF))));
				// result.setUnitofMeasure(c.getString((c.getColumnIndex(KEY_UNIT))));
				// result.setUnitConsumed(c.getString((c.getColumnIndex(KEY_CONSUMEDQUANTITY))));
				result.setComponent(c.getString(c.getColumnIndex(KEY_COMPONENT)));
				result.setBillTotal(c.getString(c.getColumnIndex(KEY_TOTAL)));
				result.setMonth(c.getString(c.getColumnIndex(KEY_MONTH)));
				result.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
				resultpojo.add(result);
			} while (c.moveToNext());
		}
		return resultpojo;
	}
	
	
	
	/**
	 * Get all the price header details
	 */
	public List<ResultPojo> getpriceHeaderDetail(String login_id, String month, String year) {
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		
		
		String selectQuery="SELECT component,sum(total) as total,subscription FROM " + TABLE_COMPONENT_PRICEDETAILS+" WHERE "+KEY_MONTH+" ='" + month + "' AND "+KEY_YEAR+" ='"+year+"' GROUP BY "+"component"+"";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				result.setSubcriptionId(c.getString(c.getColumnIndex(KEY_SUBSCRIPTION)));
				result.setComponent(c.getString((c.getColumnIndex(KEY_COMPONENT))));
				result.setBillTotal(c.getString((c.getColumnIndex(KEY_TOTAL))));
				result.setMonth(month);
				result.setYear(year);
			

				resultpojo.add(result);			
				
				
				
			} while (c.moveToNext());
		}

		return resultpojo;
	}
	
	/**
	 * Get all the price header details
	 */
	public List<ResultPojo> getChildPriceDetails(String month, String year,String component,String SubcriptionID){
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		
		
		String selectQuery="SELECT product,tariff,unit,consumedquantity,total from " + TABLE_COMPONENT_PRICEDETAILS+" WHERE "+KEY_MONTH+" ='" + month + "' AND "+KEY_YEAR+" ='"+year+"' AND "+KEY_COMPONENT+" = '"+component+"' AND "+KEY_SUBSCRIPTION+"='"+SubcriptionID+"'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		ResultPojo result = new ResultPojo();
		resultpojo.add(result);	

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				result = new ResultPojo();
				result.setProduct(c.getString((c.getColumnIndex(KEY_PRODUCT)))); 
				result.setPerUnitCommitted(c.getString((c.getColumnIndex(KEY_TARIFF))));
				result.setUnitofMeasure(c.getString((c.getColumnIndex(KEY_UNIT))));
				result.setUnitConsumed(c.getString((c.getColumnIndex(KEY_CONSUMEDQUANTITY))));
				result.setBillTotal(c.getString((c.getColumnIndex(KEY_TOTAL))));
			
			

				resultpojo.add(result);			
				
				
				
			} while (c.moveToNext());
		}

		return resultpojo;
	}
	
	public ResultPojo getChildPriceObject(String month, String year,String component,String SubcriptionID){
		List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		
		
		String selectQuery="SELECT product,tariff,unit,consumedquantity,total from " + TABLE_COMPONENT_PRICEDETAILS+" WHERE "+KEY_MONTH+" ='" + month + "' AND "+KEY_YEAR+" ='"+year+"' AND "+KEY_COMPONENT+" = '"+component+"' AND "+KEY_SUBSCRIPTION+"='"+SubcriptionID+"'";
		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		ResultPojo result = new ResultPojo();
		resultpojo.add(result);	

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				result = new ResultPojo();
				result.setProduct(c.getString((c.getColumnIndex(KEY_PRODUCT)))); 
				result.setPerUnitCommitted(c.getString((c.getColumnIndex(KEY_TARIFF))));
				result.setUnitofMeasure(c.getString((c.getColumnIndex(KEY_UNIT))));
				result.setUnitConsumed(c.getString((c.getColumnIndex(KEY_CONSUMEDQUANTITY))));
				result.setBillTotal(c.getString((c.getColumnIndex(KEY_TOTAL))));
			
			

				resultpojo.add(result);			
				
				
				
			} while (c.moveToNext());
		}

		return result;
	}
	
	
	
	/*public Map<String, List<ResultPojo>> getChildPriceDetails(String month, String year,String component,String SubcriptionID) {
	//	List<ResultPojo> resultpojo = new ArrayList<ResultPojo>();
		Map<String,List<ResultPojo>> listDataChild = new HashMap<String, List<ResultPojo>>();
		String selectQuery="SELECT component,sum(total) as total,subscription FROM " + TABLE_COMPONENT_PRICEDETAILS+" WHERE "+KEY_MONTH+" ='" + month + "' AND "+KEY_YEAR+" ='"+year+"' GROUP BY "+"component"+"";

		Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ResultPojo result = new ResultPojo();
				
				result.setProduct(c.getString((c.getColumnIndex(KEY_PRODUCT)))); 
				result.setPerUnitCommitted(c.getString((c.getColumnIndex(KEY_TARIFF))));
				result.setUnitofMeasure(c.getString((c.getColumnIndex(KEY_UNIT))));
				result.setUnitConsumed(c.getString((c.getColumnIndex(KEY_CONSUMEDQUANTITY))));
				result.setSubcriptionId(c.getString((c.getColumnIndex(KEY_SUBSCRIPTION))));
				result.setComponent(c.getString((c.getColumnIndex(KEY_COMPONENT))));
				result.setBillTotal(c.getString((c.getColumnIndex(KEY_TOTAL))));
				result.setMonth(month);
				result.setYear(year);

				//resultpojo.add(result);
				
				String selectComponentDetailsQuery="SELECT product,tariff,unit,consumedquantity from " + TABLE_COMPONENT_PRICEDETAILS+" WHERE "+KEY_MONTH+" ='" + month + "' AND "+KEY_YEAR+" ='"+year+"' AND "+KEY_COMPONENT+" = '"+result.getComponent()+"' AND "+KEY_SUBSCRIPTION+"='"+result.getSubcriptionId()+"'";
				List<ResultPojo> resultpojoDetails = new ArrayList<ResultPojo>();
				Log.e(LOG, selectComponentDetailsQuery);
				SQLiteDatabase dbc = this.getReadableDatabase();
				Cursor comp = dbc.rawQuery(selectComponentDetailsQuery, null);
				if (comp.moveToFirst()) {
					do {
						ResultPojo resultDetails = new ResultPojo();
						resultDetails.setProduct(comp.getString((comp.getColumnIndex(KEY_PRODUCT))));
						resultDetails.setPerUnitCommitted(comp.getString((comp.getColumnIndex(KEY_TARIFF))));
						resultDetails.setUnitofMeasure(comp.getString((comp.getColumnIndex(KEY_UNIT))));
						resultDetails.setUnitConsumed(comp.getString((comp.getColumnIndex(KEY_CONSUMEDQUANTITY))));
					

						resultpojoDetails.add(resultDetails);	
						
					}while (comp.moveToNext());
					
				}
				for(ResultPojo c1:resultpojoDetails){
					Log.d("myResult:",c1.toString1());
				}
				listDataChild.put(result.getComponent()+"|"+result.getBillTotal(),resultpojoDetails);
			} while (c.moveToNext());
		}

		return listDataChild;
	}
*/
}

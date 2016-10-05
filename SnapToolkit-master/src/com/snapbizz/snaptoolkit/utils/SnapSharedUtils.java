package com.snapbizz.snaptoolkit.utils;

import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Retailer;
import com.snapbizz.snaptoolkit.domains.SnapMoneyPieData;
import com.snapbizz.snaptoolkit.domains.StockReport;
import com.snapbizz.snaptoolkit.domains.Store;

public class SnapSharedUtils {
	private static final String TAG="[SnapSharedUtils]";
	private static final String WEIGHING_MACHINE_STATUS = "weighing_machine_status";
	private static final String WEIGHING_MACHINE_ADDRESS = "weighing_machine_address";
	private static final String WEIGHING_MACHINE_NAME = "weighing_machine_name";
	private static final String LOOSE_ITEMS_UOM_UPDATED = "loose_items_uom_updated";
	private static final String PRINTER_NAME = "selected_printer_type";
	private static final String PRINTER_SIZE = "selected_printer_size";
	private static final String PRINTER_ADDRESS = "selected_printer_address";
	private static final String STORE_TYPE = "store_type";
	private static final String STORE_IS_DISABLED = "is_disabled";

	public static int getLastVersionCode(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getInt(SnapToolkitConstants.APP_VERSION_CODE, 1);
	}

	public static void storeVersionCode(Context context)
			throws NameNotFoundException {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putInt(SnapToolkitConstants.APP_VERSION_CODE,
						context.getPackageManager().getPackageInfo(
								context.getPackageName(), 0).versionCode)
				.commit();
	}

	public static void storeBackupDbName(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(
						SnapToolkitConstants.DEVICE_DB_NAME,
						new SimpleDateFormat(
								SnapToolkitConstants.BACKUP_DATE_FORMAT)
								.format(Calendar.getInstance().getTime())
								+ SnapToolkitConstants.DB_NAME).commit();
	}

	public static String getDbBackUpName(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.DEVICE_DB_NAME,
				SnapToolkitConstants.DB_NAME);
	}

	public static void storeDeviceLastSyncTime(Date lastSyncDate,
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(
						SnapToolkitConstants.DEVICE_SYNC_TIME,
						new SimpleDateFormat(
								SnapToolkitConstants.STANDARD_DATEFORMAT)
								.format(lastSyncDate)).commit();
	}

	public static Date getDeviceLastSyncTime(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		try {
			return new SimpleDateFormat(
					SnapToolkitConstants.STANDARD_DATEFORMAT).parse(mPrefs
					.getString(SnapToolkitConstants.DEVICE_SYNC_TIME, ""));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void storeDeviceState(DeviceState deviceState, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(SnapToolkitConstants.DEVICE_STATE,
						deviceState.name()).commit();
	}

	public static DeviceState getDeviceState(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return DeviceState.valueOf(mPrefs.getString(
				SnapToolkitConstants.DEVICE_STATE, DeviceState.ENABLED.name()));
	}

	public static void savePrinter(PrinterType printerType, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_TYPE,
						printerType.name()).commit();
	}

	public static PrinterType getSavedPrinterType(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		String printerType = mPrefs.getString(
				SnapToolkitConstants.PRINTER_TYPE, null);
		if (printerType != null)
			return PrinterType.valueOf(printerType);
		else
			return null;
	}

	public static Store getStoreDetails(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		Store store = new Store();
		store.setStoreName(mPrefs.getString(
				SnapToolkitConstants.STORE_NAME_KEY, ""));
		store.setStoreAddress(mPrefs.getString(
				SnapToolkitConstants.STORE_ADDRESS_KEY, ""));
		store.setStorePhoneNumber(mPrefs.getString(
				SnapToolkitConstants.STORE_NUMBER_KEY, ""));
		store.setStoreTinNumber(mPrefs.getString(
				SnapToolkitConstants.STORE_TIN_KEY, ""));
		store.setZipCode(mPrefs.getString(SnapToolkitConstants.STORE_ZIP, ""));
		store.setCity(mPrefs.getString(SnapToolkitConstants.STORE_CITY, ""));
		store.setState(mPrefs.getString(SnapToolkitConstants.STORE_STATE, ""));
		return store;
	}

	public static void setStoreHotfixValue(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putBoolean(SnapToolkitConstants.HOTFIX_KEY, booleanVal)
				.commit();
	}

	public static Boolean getStoreHotfixValue(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.HOTFIX_KEY, false);
	}

	public static String getStorePinCode(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_ZIP, null);
	}

	public static int getLastInvoiceID(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.STORE_FILE,
				Context.MODE_MULTI_PROCESS).getInt(
				SnapToolkitConstants.INVOICE_ID, 0);
	}

	public static void storeLastInvoiceID(Context context, int invoiceID) {
		context.getSharedPreferences(SnapToolkitConstants.STORE_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putInt(SnapToolkitConstants.INVOICE_ID, invoiceID).commit();
	}

	public static String getLastCustomerSuggestionsSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_CUSTOMERSUGGESTIONS_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_CUSTOMERSUGGESTIONS_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_CUSTOMERSUGGESTIONS_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastCustomerSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_CUSTOMER_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_CUSTOMER_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_CUSTOMER_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastProductSkuSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_PRODUCTSKU_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_PRODUCTSKU_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_PRODUCTSKU_KEY,
				SnapToolkitConstants.OLDEST_PRODSYNC_DATE);
	}

	// TODO: Check
	/*public static String getLastProductSkuSyncRemaining(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_PRODUCTSKU_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_PRODUCTSKU_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_PRODUCTSKU_KEY,
				SnapToolkitConstants.OLDEST_RETRIEVAL_TIMESTAMP);
	}*/

	public static String getLastInventorySkuSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_INVENTORY_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_INVENTORY_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_INVENTORY_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastBillItemSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_BILLITEM_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_BILLITEM_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_BILLITEM_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastCustomerPaymentSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_CUSTOMER_PAYMENT_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_CUSTOMER_PAYMENT_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_CUSTOMER_PAYMENT_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastTransactionSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_TRANSACTION_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_TRANSACTION_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_TRANSACTION_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastBatchSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_INVENTORYBATCH_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_INVENTORYBATCH_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_INVENTORYBATCH_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastCompanySyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_COMPANY_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_COMPANY_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_COMPANY_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastBrandSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_BRAND_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_BRAND_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_BRAND_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastDistributorSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_DISTRIBUTOR_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_DISTRIBUTOR_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_DISTRIBUTOR_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastPaymentsSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_PAYMENTS_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_PAYMENTS_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_PAYMENTS_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastOrderSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_ORDERS_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_ORDERS_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_ORDERS_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastOrderDetailsSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_ORDERSDETAILS_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_ORDERSDETAILS_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_ORDERSDETAILS_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastDistributorBrandMapSyncTimestamp(Context context) {
		if(!context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).contains(
				SnapToolkitConstants.SYNC_DISTRIBUTOR_BRAND_MAP_KEY))
			Log.e(TAG, "Missing:"+SnapToolkitConstants.SYNC_DISTRIBUTOR_BRAND_MAP_KEY);
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_DISTRIBUTOR_BRAND_MAP_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastProductRetrievalSyncTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_PRODUCTSKU_RETRIEVAL_KEY, "0");
	}

	public static String getLastBrandRetrievalSyncTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_BRAND_RETRIEVAL_KEY, "0");
	}

	public static String getLastCompanyRetrievalSyncTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_COMPANY_RETRIEVAL_KEY, "0");
	}

	public static String getLastCampaignRetrievalSyncTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_CAMPAIGN_KEY, "0");
	}

	public static String getLastReceiveItemRetrievalSyncTimestamp(
			Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_RECEIVE_ITEMS_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastInvoiceRetrievalSyncTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_INVOICE_ITEMS_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}

	public static String getLastDistributorProductMapTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_DISTRIBUTOR_PRODUCT_MAP_KEY,
				SnapToolkitConstants.OLDEST_SYNC_DATE);
	}
	public static String getLastCategoryTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_CATEGORY_KEY,
				SnapToolkitConstants.OLDEST_CATAGORY_SYNC_DATE);
	}
	
	public static void storeLastCategoryTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_CATEGORY_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_CATEGORY_KEY+":"+status);
	}

	public static void storeLastProductSkuSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_PRODUCTSKU_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_PRODUCTSKU_KEY+":"+status);
	}

	public static void storeLastCustomerSuggestionsSyncTimestamp(
			String timestamp, Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_CUSTOMERSUGGESTIONS_KEY,
						timestamp).commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_CUSTOMERSUGGESTIONS_KEY+":"+status);
	}

	public static void storeLastCustomerSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_CUSTOMER_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_CUSTOMER_KEY+":"+status);
	}

	public static void storeLastCustomerPaymentSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_CUSTOMER_PAYMENT_KEY,
						timestamp).commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_CUSTOMER_PAYMENT_KEY+":"+status);
	}

	public static void storeLastInventorySkuSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_INVENTORY_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_INVENTORY_KEY+":"+status);
	}

	public static void storeLastBillItemSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_BILLITEM_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_BILLITEM_KEY+":"+status);
	}

	public static void storeLastTransactionSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_TRANSACTION_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_TRANSACTION_KEY+":"+status);
	}

	public static void storeLastInventoryBatchSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_INVENTORYBATCH_KEY,
						timestamp).commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_INVENTORYBATCH_KEY+":"+status);
	}

	public static void storeLastCompanySyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_COMPANY_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_COMPANY_KEY+":"+status);
	}

	public static void storeLastCampaignSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_CAMPAIGN_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_CAMPAIGN_KEY+":"+status);
	}

	public static void storeLastBrandSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_BRAND_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_BRAND_KEY+":"+status);
	}

	public static void storeLastDistributorSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_DISTRIBUTOR_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_DISTRIBUTOR_KEY+":"+status);
	}

	public static void storeLastDistributorBranMapSyncTimestamp(
			String timestamp, Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_DISTRIBUTOR_BRAND_MAP_KEY,
						timestamp).commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_DISTRIBUTOR_BRAND_MAP_KEY+":"+status);
	}

	public static void storeLastPaymentSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_PAYMENTS_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_PAYMENTS_KEY+":"+status);
	}

	public static void storeLastOrderSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_ORDERS_KEY, timestamp)
				.commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_ORDERS_KEY+":"+status);
	}

	public static void storeLastOrderDetailsSyncTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_ORDERSDETAILS_KEY,
						timestamp).commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_ORDERSDETAILS_KEY+":"+status);
	}

	public static void storeLastProductRetrievalTimestamp(String timestamp,
			Context context) {
		boolean status = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_PRODUCTSKU_RETRIEVAL_KEY,
						timestamp).commit();
		Log.e(TAG, "Writing"+timestamp+":"+SnapToolkitConstants.SYNC_PRODUCTSKU_RETRIEVAL_KEY+":"+status);
	}

	public static void storeLastBrandRetrievalTimestamp(String timestamp,
			Context context) {
		context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_BRAND_RETRIEVAL_KEY,
						timestamp).commit();
	}

	public static void storeLastCompanyRetrievalTimestamp(String timestamp,
			Context context) {
		context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_COMPANY_RETRIEVAL_KEY,
						timestamp).commit();
	}

	public static void storeLastReceiveItemRetrievalTimestamp(String timestamp,
			Context context) {
		context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_RECEIVE_ITEMS_KEY,
						timestamp).commit();
	}

	public static void storeLastInvoiceRetrievalTimestamp(String timestamp,
			Context context) {
		context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(SnapToolkitConstants.SYNC_INVOICE_ITEMS_KEY,
						timestamp).commit();
	}

	public static void storeLastDistributorProductMapTimestamp(
			String timestamp, Context context) {
		context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,
				Context.MODE_MULTI_PROCESS)
				.edit()
				.putString(
						SnapToolkitConstants.SYNC_DISTRIBUTOR_PRODUCT_MAP_KEY,
						timestamp).commit();
	}
	
	

	public static String getDeviceId(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.DEVICE_ID, "");
	}

	public static void setDeviceId(Context context, String deviceId) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.DEVICE_ID, deviceId);
		editor.commit();
	}

	public static String getStoreAuthKey(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_AUTH_KEY, "");
	}

	public static String getStoreApiKey(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_API_KEY, null);
	}

	public static String getRetailerId(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.RETAILER_ID, "");
	}

	public static String getStoreId(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_ID, "");
	}

	public static void storeRetailer(Context context, Retailer retailer) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.RETAILER_OWNER_NAME,
				retailer.getRetailerName());
		editor.putString(SnapToolkitConstants.STORE_NAME_KEY, retailer
				.getStore().getStoreName());
		editor.putString(SnapToolkitConstants.SALESMAN_NUMBER_KEY,
				retailer.getSalesPersonNumber());
		editor.putString(SnapToolkitConstants.RETAILER_OWNER_NUMBER,
				retailer.getRetailerPhoneNumber());
		editor.putString(SnapToolkitConstants.STORE_NUMBER_KEY, retailer
				.getStore().getStorePhoneNumber());
		editor.putString(SnapToolkitConstants.RETAILER_OWNER_EMAIL,
				retailer.getRetailerEmailId());
		editor.putString(SnapToolkitConstants.STORE_TIN_KEY, retailer
				.getStore().getStoreTinNumber());
		editor.putString(SnapToolkitConstants.STORE_ADDRESS_KEY, retailer
				.getStore().getStoreAddress());
		editor.putString(SnapToolkitConstants.STORE_CITY, retailer.getStore()
				.getCity());
		editor.putString(SnapToolkitConstants.STORE_STATE, retailer.getStore()
				.getState());
		editor.putString(SnapToolkitConstants.STORE_STATE_ID, retailer
				.getStore().getStoreStateId());
		editor.putString(SnapToolkitConstants.STORE_ZIP, retailer.getStore()
				.getZipCode());
		editor.putString(SnapToolkitConstants.RETAILER_ID,
				retailer.getRetailerId());
		editor.putString(SnapToolkitConstants.STORE_AUTH_KEY, retailer
				.getStore().getStoreAuthToken());
		editor.putString(SnapToolkitConstants.STORE_API_KEY, retailer
				.getStore().getStoreApiKey());
		editor.putString(SnapToolkitConstants.STORE_ID, retailer.getStore()
				.getStoreId());
		editor.apply();
	}

	
	public static Retailer getRetailer(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		Retailer retailer = new Retailer(mPrefs.getString(
				SnapToolkitConstants.RETAILER_OWNER_NAME, ""),
				getRetailerPhoneNumber(context), mPrefs.getString(
						SnapToolkitConstants.RETAILER_OWNER_EMAIL, ""),
				mPrefs.getString(SnapToolkitConstants.STORE_NUMBER_KEY, ""),
				getStoreDetails(context));
		return retailer;
	}

	public static String getRetailerPhoneNumber(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.RETAILER_OWNER_NUMBER, "");
	}

	public static String getStoreStateId(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_STATE_ID, null);
	}

	public static void storeBillingAppVersion(Context context,
			String versionNumber) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.BILLING_VERSION_KEY,
				versionNumber);
		editor.commit();
	}

	public static void storeStockAppVersion(Context context,
			String versionNumber) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.STOCK_VERSION_KEY, versionNumber);
		editor.commit();
	}
	public static String getBillingAppVersion(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.BILLING_VERSION_KEY, null);
	}

	public static String getStockAppVersion(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STOCK_VERSION_KEY, null);
	}
	public static void storeLastAvailableBillingUpdateTime(Context context,
			String dateTime) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.BILLING_UPDATE_TIME_KEY, dateTime);
		editor.commit();
	}

	public static void storeLastAvailableStockUpdateTime(Context context,
			String dateTime) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.STOCK_UPDATE_TIME_KEY, dateTime);
		editor.commit();
	}

	public static void storeLastStoredPin(Context context, String storedPin) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.STORE_PIN_KEY, storedPin);
		editor.commit();
	}

	public static String getLastAvailableBillingUpdateTime(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.BILLING_UPDATE_TIME_KEY,
				null);
	}

	public static String getLastAvailableStockUpdateTime(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STOCK_UPDATE_TIME_KEY,
				null);
	}

	public static String getLastStoredPin(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_PIN_KEY, null);
	}
	
	public static String getLastStoredSelection(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.STORE_SELECTION_KEY, "sku_saleprice");
	}
	
	public static void storeLastStoredSelection(Context context, String storedPin) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		Editor editor = mPrefs.edit();
		editor.putString(SnapToolkitConstants.STORE_SELECTION_KEY, storedPin);
		editor.commit();
	}

	public static void setPrintCheckValue(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINT_SAVINGS, booleanVal)
				.commit();
	}

	public static Boolean getPrintCheckValue(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINT_SAVINGS, false);
	}
	
	public static void setExtraLinePrintValue(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINT_EXTRA_LINE, booleanVal)
				.commit();
	}

	public static Boolean getExtraLinePrintValue(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINT_EXTRA_LINE, true);
	}

	public static void setRoundoffCheckValue(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.ROUNDOFF_LOOSEITEMS,
						booleanVal).commit();
	}

	public static Boolean getRoundoffCheckValue(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.ROUNDOFF_LOOSEITEMS,
				false);
	}
	
	public static void setPrintEstimate(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINT_ESTIMATE,
						booleanVal).commit();
	}

	public static Boolean getPrintEstimate(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINT_ESTIMATE,
				false);
	}
	public static void setSettingsNameValue(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putBoolean(SnapToolkitConstants.SAVE_SAVINGS, booleanVal).commit();
	}

	public static Boolean getSettingsNameValue(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.SAVE_SAVINGS, false);
	}
	
	public static void setVisiblitySwitchStatus(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.DISPLAY_VISIBLITY, booleanVal).commit();
	}

	public static boolean getVisiblitySwitchStatus(Context context) {
        if(SnapToolkitConstants.PRODUCTION_BUILD)
            return true;
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.DISPLAY_VISIBLITY, true);
	}
	
	public static void setOfferVisible(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.DISPLAY_OFFER_TAGS, booleanVal).commit();
	}

	public static boolean getOfferVisible(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.DISPLAY_OFFER_TAGS, true);
	}

	public static void setSortingCheckValue(boolean booleanVal, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.SORT_LOOSEITEMS, booleanVal)
				.commit();
	}

	public static Boolean getSortingCheckValue(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.SORT_LOOSEITEMS, true);
	}

	public static List<StockReport> getStockReportList(
			StockReportFilterType stockReportFilterType, Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		Gson gson = new Gson();
		return gson
				.fromJson(
						mPrefs.getString(
								stockReportFilterType.ordinal() == StockReportFilterType.DISTRIBUTOR
										.ordinal() ? SnapToolkitConstants.STOCK_REPORT_DISTRIBUTOR_KEY
										: SnapToolkitConstants.STOCK_REPORT_CATEGORY_KEY,
								""), new TypeToken<List<StockReport>>() {
						}.getType());
	}

	public static void storeStockReportList(List<StockReport> stockReportList,
			StockReportFilterType stockReportFilterType, Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		Gson gson = new Gson();
		mPrefs.edit()
				.putString(
						stockReportFilterType.ordinal() == StockReportFilterType.DISTRIBUTOR
								.ordinal() ? SnapToolkitConstants.STOCK_REPORT_DISTRIBUTOR_KEY
								: SnapToolkitConstants.STOCK_REPORT_CATEGORY_KEY,
						gson.toJson(stockReportList)).apply();
	}
	
	public static void storeSnapMoney(SnapMoneyPieData snapMoneyPieData,
			Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		Gson gson = new Gson();
		mPrefs.edit()
				.putString(SnapToolkitConstants.STOCK_MONEY_KEY,
						gson.toJson(snapMoneyPieData)).apply();
	}

	public static void storeStateInfo(String stateName, String stateId,
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putString(SnapToolkitConstants.STORE_STATE, stateName)
				.apply();
		mPrefs.edit().putString(SnapToolkitConstants.STORE_STATE_ID, stateId)
				.apply();
	}

	public static SnapMoneyPieData getSnapMoney(Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		return new Gson().fromJson(
				mPrefs.getString(SnapToolkitConstants.STOCK_MONEY_KEY, ""),
				SnapMoneyPieData.class);
	}

	public static void storeVisibilityStoreProducts(Context context,
			HashMap<String, String> prodIds) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.VISIBILITY_PROD_FILE,
				Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(
						SnapToolkitConstants.VISIBILITY_STORE_KEY,
						new Gson().toJson(prodIds,
								new TypeToken<HashMap<String, String>>() {
								}.getType())).apply();
	}

	public static void storeVisibilityOfferProducts(Context context,
			HashMap<String, String> prodIds) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.VISIBILITY_PROD_FILE,
				Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(
						SnapToolkitConstants.VISIBILITY_OFFER_KEY,
						new Gson().toJson(prodIds,
								new TypeToken<HashMap<String, String>>() {
								}.getType())).apply();
	}

	public static HashMap<String, String> getVisibilityStoreProductIds(
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.VISIBILITY_PROD_FILE,
				Context.MODE_MULTI_PROCESS);
		return new Gson()
				.fromJson(mPrefs.getString(
						SnapToolkitConstants.VISIBILITY_STORE_KEY, ""),
						new TypeToken<HashMap<String, String>>() {
						}.getType());
	}

	public static HashMap<String, String> getVisibilityOffersProductIds(
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.VISIBILITY_PROD_FILE,
				Context.MODE_MULTI_PROCESS);
		return new Gson()
				.fromJson(mPrefs.getString(
						SnapToolkitConstants.VISIBILITY_OFFER_KEY, ""),
						new TypeToken<HashMap<String, String>>() {
						}.getType());
	}

	public static void storeChartData(
			List<BarGraphDataPoint> barGraphDataPoint, ChartType chartType,
			Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(
						chartType.name(),
						new Gson().toJson(barGraphDataPoint,
								new TypeToken<List<BarGraphDataPoint>>() {
								}.getType())).apply();
	}

	public static List<BarGraphDataPoint> getChartData(ChartType chartType,
			Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		return new Gson().fromJson(mPrefs.getString(chartType.name(), ""),
				new TypeToken<List<BarGraphDataPoint>>() {
				}.getType());
	}

	public static String getHelpVideosLastSyncTimestamp(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
				Context.MODE_MULTI_PROCESS).getString(
				SnapToolkitConstants.SYNC_HELPVIDEO_KEY,
				SnapToolkitConstants.OLDEST_RETRIEVAL_TIMESTAMP);
	}

	public static void storeHelpVideosLastSyncTimestamp(String timestamp,
			Context context) {
		context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putString(SnapToolkitConstants.SYNC_HELPVIDEO_KEY, timestamp)
				.commit();
	}

	public static void storeIncompleteDownloadsVideoList(String videoName,
			Context context) {
		SharedPreferences mPrefs = context
				.getSharedPreferences(SnapToolkitConstants.DASHBOARD_FILE,
						Context.MODE_MULTI_PROCESS);
		// mPrefs.edit().putString().apply();
	}

	public static void setIsVatSelected(Boolean isSelected, Context context) {
		context.getSharedPreferences(SnapToolkitConstants.STORE_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(SnapToolkitConstants.SNAP_VAT_KEY, isSelected)
				.commit();
	}

	public static Boolean getIsVatSelected(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.STORE_FILE,
				Context.MODE_MULTI_PROCESS).getBoolean(
				SnapToolkitConstants.SNAP_VAT_KEY, false);
	}

	public static void storeForceSyncKey(String key, String timestamp,
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.CAMPAIGN_SYNC_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putString(key, timestamp).apply();
	}

	public static String retrieveForceSyncKey(String key, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.CAMPAIGN_SYNC_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(key, "");
	}

	public static void storePrinterStoreName(String name, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_STORE_NAME_KEY, name)
				.apply();
	}

	public static void storePrinterStoreAddress(String address, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_STORE_ADDRESS_KEY,
						address).apply();
	}

	public static void storePrinterStoreNumber(String number, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_STORE_NUMBER_KEY,
						number).apply();
	}

	public static void storePrinterStoreTinNumber(String tinNumber,
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_STORE_TIN_NUMBER_KEY,
						tinNumber).apply();
	}

	public static void storePrinterStoreCity(String city, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_STORE_CITY_KEY, city)
				.apply();
	}

	public static void storePrinterFooter1(String name, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putString(SnapToolkitConstants.PRINTER_FOOTER1_KEY, name)
				.apply();
	}

	public static void storePrinterFooter2(String name, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putString(SnapToolkitConstants.PRINTER_FOOTER2_KEY, name)
				.apply();
	}

	public static void storePrinterFooter3(String name, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit().putString(SnapToolkitConstants.PRINTER_FOOTER3_KEY, name)
				.apply();
	}

	public static void storeProductSkuListUpdate(boolean value, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_SKU_UPDATE, Context.MODE_MULTI_PROCESS);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.UPDATE_PROD_LIST_KEY, value)
				.apply();
	}

	public static String getPrinterStoreName(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_STORE_NAME_KEY,
				SnapSharedUtils.getStoreDetails(context).getStoreName());
	}

	public static String getPrinterStoreNumber(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_STORE_NUMBER_KEY,
				SnapSharedUtils.getRetailerPhoneNumber(context));
	}

	public static String getPrinterStoreAddress(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_STORE_ADDRESS_KEY,
				SnapSharedUtils.getStoreDetails(context).getStoreAddress());
	}

	public static String getPrinterStoreTinNumber(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(
				SnapToolkitConstants.PRINTER_STORE_TIN_NUMBER_KEY,
				SnapSharedUtils.getStoreDetails(context).getStoreTinNumber());
	}

	public static String getPrinterStoreCity(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_STORE_CITY_KEY,
				SnapSharedUtils.getStoreDetails(context).getCity());
	}

	public static String getPrinterFooter1(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_FOOTER1_KEY,
				"Thank You. Come Again!");
	}

	public static String getPrinterFooter2(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_FOOTER2_KEY, "");
	}

	public static String getPrinterFooter3(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_FOOTER3_KEY, "");
	}

	public static boolean isProdSkuListUpdated(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_SKU_UPDATE, Context.MODE_MULTI_PROCESS);
		return mPreferences.getBoolean(SnapToolkitConstants.UPDATE_PROD_LIST_KEY, true);
	}

	public static void storeMhVatUpdated(Context context, boolean value) {
		context.getSharedPreferences(SnapToolkitConstants.DEVICE_SKU_UPDATE, Context.MODE_MULTI_PROCESS)
			   .edit().putBoolean(SnapToolkitConstants.UPDATE_VAT_RATE_KEY, value).commit();
	}
	
	public static boolean isMhVatUpdated(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_SKU_UPDATE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.UPDATE_VAT_RATE_KEY, false);
	}

	public static void setOfferUpdated(Context context, boolean value) {
		context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
				Context.MODE_MULTI_PROCESS).edit()
				.putBoolean(SnapToolkitConstants.UPDATE_OFFER_KEY, value)
				.commit();
	}

	public static boolean isOfferUpdated(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS);
		return mPrefs.getBoolean(SnapToolkitConstants.UPDATE_OFFER_KEY, false);
	}
	  public static void storeCampaignLastSyncTime(Date lastSyncDate, Context context) {
	      SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE, Context.MODE_MULTI_PROCESS);
	      mPrefs.edit().putString(SnapToolkitConstants.DEVICE_CAMP_LAST_SYNC_TIME, new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(lastSyncDate)).commit();
	  } 
	  
	  
	  	public static String getLastInvoiceVersionName(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).getString(
					SnapToolkitConstants.INVOICE_VERSION_NAME,"");
		}

		public static void storeLastInvoiceVersionName(Context context, String name) {
			context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.INVOICE_VERSION_NAME, name).commit();
		}
	  
		public static String getLastBillingVersionName(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).getString(
					SnapToolkitConstants.BILLING_VERSION_NAME,"");
		}

		public static void storeLastBillingVersionName(Context context, String name) {
			context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.BILLING_VERSION_NAME, name).commit();
		}
	  
		public static String getLastVisibilityVersionName(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).getString(
					SnapToolkitConstants.BILLING_VERSION_NAME,"");
		}

		public static void storeLastVisibilityVersionName(Context context, String name) {
			context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.BILLING_VERSION_NAME, name).commit();
		}
		public static String getLastPushoffersVersionName(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).getString(
					SnapToolkitConstants.BILLING_VERSION_NAME,"");
		}

		public static void storeLastPushoffersVersionName(Context context, String name) {
			context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.BILLING_VERSION_NAME, name).commit();
		}
	  
		public static String getLastDashboardVersionName(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).getString(
					SnapToolkitConstants.BILLING_VERSION_NAME,"");
		}

		public static void storeLastDashboardVersionName(Context context, String name) {
			context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.BILLING_VERSION_NAME, name).commit();
		}
		
		public static String getLastTabletDbId(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).getString(
					SnapToolkitConstants.TABLET_DB_VERSION,"");
		}

		public static void storeLastTabletDbId(Context context, String name) {
			context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.TABLET_DB_VERSION, name).commit();
		}
		
		public static long getDownloadingId(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
					Context.MODE_MULTI_PROCESS).getLong(
					SnapToolkitConstants.IMG_DOWNLOADING_ID,0);
		}

		public static void storeDownloadingId(Context context, long downloadingId) {
			context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putLong(SnapToolkitConstants.IMG_DOWNLOADING_ID, downloadingId).commit();
		}
		
		public static Campaigns getCurrentCampaign(Context context) {
			Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
			String json =context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
					Context.MODE_MULTI_PROCESS).getString(SnapToolkitConstants.CURRENT_CAMPAIGN,"");
			return gson.fromJson(json, Campaigns.class);
		}

		public static void storeCurrentCampaign(Context context, Object object) {
			Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
			context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putString(SnapToolkitConstants.CURRENT_CAMPAIGN, gson.toJson(object)).commit();
		}
		
		
		public static void setCampaignSyncStatus(Boolean isSelected, Context context) {
			context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
					Context.MODE_MULTI_PROCESS).edit()
					.putBoolean(SnapToolkitConstants.CAMPAIGN_SYNC_STATUS, isSelected)
					.commit();
		}

		public static boolean getCampaignSyncStatus(Context context) {
			return context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE,
					Context.MODE_MULTI_PROCESS).getBoolean(
					SnapToolkitConstants.CAMPAIGN_SYNC_STATUS, false);
			
		}
			    
	  
	  public static Date getCampaignLastSyncTime(Context context) {
	      SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.CAMPAIGN_SYNC_FILE, Context.MODE_MULTI_PROCESS);
	      try {
	          return new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).parse(mPrefs.getString(SnapToolkitConstants.DEVICE_CAMP_LAST_SYNC_TIME, ""));
	      } catch (ParseException e) {
	          e.printStackTrace();
	          return null;
	      }
	  }
	  
	  public static void setVersionCodeDetails(Context context, String versionDetails){
		  
		  context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE_V2,
					Context.MODE_PRIVATE).edit()
					.putString(SnapToolkitConstants.SYNC_VERSION_CODE_DETAILS, versionDetails)
					.commit();
	  }
	  
	  public static String getVersionCodeDetails(Context context){
		  SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE_V2,
					Context.MODE_PRIVATE);
			return mPrefs.getString(
					SnapToolkitConstants.SYNC_VERSION_CODE_DETAILS,null);
	  }
	  
	  public static void setApkUpgradeDialogLaunchDate(Context context, String date) {
		  context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE_V2,
					Context.MODE_PRIVATE).edit()
					.putString(SnapToolkitConstants.APK_UPGRADE_DIALOG_LAUNCH_DATE, date)
					.commit();
	  }
	  
	  public static String getApkUpgradeDialogLaunchDate(Context context) {
		  SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE_V2,
					Context.MODE_PRIVATE);
			return mPrefs.getString(SnapToolkitConstants.APK_UPGRADE_DIALOG_LAUNCH_DATE,
					SnapToolkitConstants.OLDEST_APK_DIALOG_LAUNCH_DATE);
	  }
	  
	public static void storeHeaderFontSize(String number, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_HEADER_FONT, number)
				.apply();
	}

	public static String getHeaderFontSize(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getString(SnapToolkitConstants.PRINTER_HEADER_FONT, null);
	}

	public static void storeFooter1FontSize(String number, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_FOOTER1_FONT, number)
				.apply();
	}

	public static String getFooter1FontSize(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs
				.getString(SnapToolkitConstants.PRINTER_FOOTER1_FONT, null);
	}

	public static void storeFooter2FontSize(String number, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_FOOTER2_FONT, number)
				.apply();
	}

	public static String getFooter2FontSize(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs
				.getString(SnapToolkitConstants.PRINTER_FOOTER2_FONT, null);
	}

	public static void storeFooter3FontSize(String number, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_FOOTER3_FONT, number)
				.apply();
	}

	public static String getFooter3FontSize(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs
				.getString(SnapToolkitConstants.PRINTER_FOOTER3_FONT, null);
	}

	public static void storeContentFontSize(String number, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.PRINTER_CONTENT_FONT, number)
				.apply();
	}

	public static String getContentFontSize(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs
				.getString(SnapToolkitConstants.PRINTER_CONTENT_FONT, null);
	}

	public static void setAutoConnect(boolean autoConnect, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINTER_AUTO_CONNECT,
						autoConnect).commit();
	}

	public static boolean getAutoConnect(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINTER_AUTO_CONNECT,
				false);
	}

	public static void setPrinterSpacing(boolean bSpacing, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINTER_SPACING, bSpacing)
				.commit();
	}

	public static boolean getPrinterSpacing(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINTER_SPACING, false);
	}

	public static void setSerialNumber(boolean printSerialNumber,
			Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINTER_SERIAL_NUM,
						printSerialNumber).commit();
	}

	public static boolean getSerialNumber(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs
				.getBoolean(SnapToolkitConstants.PRINTER_SERIAL_NUM, false);
	}

	public static void setPrinterVat(boolean printVat, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.PRINTER_VAT, printVat)
				.commit();
	}

	public static boolean getPrinterVAT(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINTER_VAT, false);
	}

	public static void setPrinterMRP(boolean printMrp, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.PRINTER_MRP, printMrp)
				.commit();
	}

	public static boolean getPrinterMRP(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINTER_MRP, false);
	}

	public static void setPrinterSummary(boolean printSummary, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putBoolean(SnapToolkitConstants.PRINTER_SUMMARY, printSummary)
				.commit();
	}

	public static boolean getPrinterSummary(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.PRINTER_SUMMARY, false);
	}
	
	public static void setConnectedPrinterType(String connectedPrinter, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString("connected_printer_type", connectedPrinter)
				.commit();		
	}
	
	public static String getConnectedPrinterType(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getString("connected_printer_type", null);
	}
	
	public static void setConnectedPrinterName(String printerName, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		mPrefs.edit()
				.putString(SnapToolkitConstants.CONNECTED_PRINTER_NAME, printerName)
				.commit();
	}
	
	public static String getConnectedPrinterName(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE);
		return mPrefs.getString(SnapToolkitConstants.CONNECTED_PRINTER_NAME, null);
	}
	
	public static void setCartActiveOnTouch(boolean isActive, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.ACTIVE_CART_ON_TOUCH, isActive).commit();
	}

	public static boolean isCartActiveOnTouch(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
					  .getBoolean(SnapToolkitConstants.ACTIVE_CART_ON_TOUCH, false);
	}
	
	public static void setMultiScanner(boolean isActive, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.ENABLE_MULTI_SCANNER, isActive).commit();
	}

	public static boolean isMultiScanner(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
					  .getBoolean(SnapToolkitConstants.ENABLE_MULTI_SCANNER, false);
	}

	public static void setClient(boolean isClient, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(
				SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE);
		mPrefs.edit().putBoolean(SnapToolkitConstants.MULTIPOS_CLIENT, isClient).commit();
	}

	public static boolean isClient(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
					  .getBoolean(SnapToolkitConstants.MULTIPOS_CLIENT, false);
	}
	public static void storeXtraQuickProductAdded(Context context, boolean isStored) {
		context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
			   .edit().putBoolean(SnapToolkitConstants.XTRA_QUICK_ADD_PRODUCT, isStored).commit();
	}

	public static boolean isXtraQuickProductAdded(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
					  .getBoolean(SnapToolkitConstants.XTRA_QUICK_ADD_PRODUCT, false);
	}

	public static void storeLooseItemsUomUpdated(Context context, boolean isStored) {
		context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
			   .edit().putBoolean(LOOSE_ITEMS_UOM_UPDATED, isStored).commit();
	}

	public static boolean isLooseItemsUomUpdated(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
					  .getBoolean(LOOSE_ITEMS_UOM_UPDATED, false);
	}

	public static void enableWeighingMachine(Context context, boolean bEnabled) {
		context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
		   	   .edit().putBoolean(WEIGHING_MACHINE_STATUS, bEnabled).commit();
	}
	
	public static boolean isWeighingMachine(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
				  	  .getBoolean(WEIGHING_MACHINE_STATUS, false);
	}

	public static String getWeighingMachineAddress(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
			  	  .getString(WEIGHING_MACHINE_ADDRESS, null);
	}
	
	public static String getWeighingMachineName(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE)
			  	  .getString(WEIGHING_MACHINE_NAME, "");
	}
	
	public static void setWeighingMachine(Context context, String name, String address) {
		Editor edit = context.getSharedPreferences(SnapToolkitConstants.BILLING_FILE, Context.MODE_PRIVATE).edit();
		edit.putString(WEIGHING_MACHINE_ADDRESS, address);
		edit.putString(WEIGHING_MACHINE_NAME, name);
		edit.commit();
	}

	public static void setSelectedPrinter(Context context, String name, int size, String address) {
		Editor edit = context.getSharedPreferences(SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE).edit();
		edit.putString(PRINTER_NAME, name);
		edit.putInt(PRINTER_SIZE, size);
		edit.putString(PRINTER_ADDRESS, address);
		edit.commit();
	}
	
	public static String getSelectedPrinterName(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE)
			  	  .getString(PRINTER_NAME, null);
	}
	
	public static int getSelectedPrinterWidth(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE)
			  	  .getInt(PRINTER_SIZE, 2);
	}
	
	public static String getSeletectedPrinterDeviceAddress(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE)
			  	  .getString(PRINTER_ADDRESS, null);
	}
	
	public static boolean isMRPHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_MRP_HEADER, true);
	}

	public static void setMRPHeader(Context context, boolean isMrp) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_MRP_HEADER, isMrp);
		editor.commit();
	}
	
	public static boolean isPurchasePriceHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_PURCHASEPRICE_HEADER, true);
	}

	public static void setPurchasePriceHeader(Context context, boolean ispurchasePrice) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_PURCHASEPRICE_HEADER, ispurchasePrice);
		editor.commit();
	}
	
	public static boolean isQtyHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_QTY_HEADER, true);
	}

	public static void setQtyHeader(Context context, boolean isQty) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_QTY_HEADER, isQty);
		editor.commit();
	}
	
	public static boolean isVatRateHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_VAT_HEADER, true);
	}

	public static void setVatRateHeader(Context context, boolean isVatRate) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_VAT_HEADER, isVatRate);
		editor.commit();
	}
	
	public static boolean isUnitTypeHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_UNIT_HEADER, true);
	}

	public static void setUnitTypeHeader(Context context, boolean isUnitType) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_UNIT_HEADER, isUnitType);
		editor.commit();
	}
	
	public static boolean isCategoryHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_CATEGORY_HEADER, true);
	}

	public static void setCategoryHeader(Context context, boolean isCategory) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_CATEGORY_HEADER, isCategory);
		editor.commit();
	}
	
	public static boolean isSubCategoryHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_SUBCATEGORY_HEADER, true);
	}

	public static void setSubCategoryHeader(Context context, boolean isSubCategory) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_SUBCATEGORY_HEADER, isSubCategory);
		editor.commit();
	}
	
	public static boolean isActionsHeader(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		return mPrefs.getBoolean(SnapToolkitConstants.INVENTORY_ACTIONS_HEADER, true);
	}

	public static void setActionsHeader(Context context, boolean isActions) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.INVENTORY_HEADER, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		editor.putBoolean(SnapToolkitConstants.INVENTORY_ACTIONS_HEADER, isActions);
		editor.commit();
	}

	public static void saveStoreType(Context context, int storeType) {
		context.getSharedPreferences(SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS)
			   .edit().putInt(STORE_TYPE, storeType).commit();
	}

	public static int getStoreType(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS)
					  .getInt(STORE_TYPE, 0);
	}
	
	public static void saveStoreDisabled(Context context, boolean isStoreDisabled) {
		context.getSharedPreferences(SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS)
			   .edit().putBoolean(STORE_IS_DISABLED, isStoreDisabled).commit();
	}

	public static boolean isStoreDisabled(Context context) {
		return context.getSharedPreferences(SnapToolkitConstants.STORE_FILE, Context.MODE_MULTI_PROCESS)
					  .getBoolean(STORE_IS_DISABLED, false);
	}
	
	public static void storeFontSize(Context context, String headerFont, String contentFont, String footer1Font, 
							String footer2Font, String footer3Font) {
		Editor mPrefs = context.getSharedPreferences(SnapToolkitConstants.PRINTER_FILE, Context.MODE_PRIVATE).edit();
		mPrefs.putString(SnapToolkitConstants.PRINTER_HEADER_FONT, headerFont);
		mPrefs.putString(SnapToolkitConstants.PRINTER_CONTENT_FONT, contentFont);
		mPrefs.putString(SnapToolkitConstants.PRINTER_FOOTER1_FONT, footer1Font);
		mPrefs.putString(SnapToolkitConstants.PRINTER_FOOTER2_FONT, footer2Font);
		mPrefs.putString(SnapToolkitConstants.PRINTER_FOOTER3_FONT, footer3Font);
		mPrefs.commit();
	}
	
	public static void saveStoreDetails(Context context, String name, String address, String contactNumber,
			String tinNumber, String city, String footer1, String footer2, String footer3) {
		Editor mPrefs = context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE, Context.MODE_MULTI_PROCESS).edit();
		mPrefs.putString(SnapToolkitConstants.PRINTER_STORE_NAME_KEY, name);
		mPrefs.putString(SnapToolkitConstants.PRINTER_STORE_ADDRESS_KEY, address);
		mPrefs.putString(SnapToolkitConstants.PRINTER_STORE_NUMBER_KEY, contactNumber);
		mPrefs.putString(SnapToolkitConstants.PRINTER_STORE_TIN_NUMBER_KEY, tinNumber);
		mPrefs.putString(SnapToolkitConstants.PRINTER_STORE_CITY_KEY, city);
		mPrefs.putString(SnapToolkitConstants.PRINTER_FOOTER1_KEY, footer1);
		mPrefs.putString(SnapToolkitConstants.PRINTER_FOOTER2_KEY, footer2);
		mPrefs.putString(SnapToolkitConstants.PRINTER_FOOTER4_KEY, city);
		mPrefs.commit();
	}	
}

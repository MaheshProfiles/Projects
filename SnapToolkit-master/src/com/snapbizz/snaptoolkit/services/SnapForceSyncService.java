package com.snapbizz.snaptoolkit.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.sqlcipher.database.SQLiteDatabase;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.snapbizz.snaptoolkit.asynctasks.ClearDeleteRecordsTask;
import com.snapbizz.snaptoolkit.asynctasks.DashboardTask;
import com.snapbizz.snaptoolkit.asynctasks.GetAppVersionTask;
import com.snapbizz.snaptoolkit.asynctasks.GetForceSyncBillItemsTask;
import com.snapbizz.snaptoolkit.asynctasks.GetForceSyncInventoryBatchTask;
import com.snapbizz.snaptoolkit.asynctasks.GetForceSyncInventoryTask;
import com.snapbizz.snaptoolkit.asynctasks.GetForceSyncProductSkuTask;
import com.snapbizz.snaptoolkit.asynctasks.GetForceSyncTransactionsTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSettingsTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncBrandTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncCategoryTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncCompanyTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncCustomerPaymentTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncCustomerTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncDeleteTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncDistriButorBrandMapTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncDistributorTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncInvoiceTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncOrderDetailsTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncOrderTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncPaymentsTask;
import com.snapbizz.snaptoolkit.asynctasks.GetSyncReceiveItemTask;
import com.snapbizz.snaptoolkit.domains.AppVersion;
import com.snapbizz.snaptoolkit.domains.AppVersionSyncRequest;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.BillItemSyncRequest;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.BrandSyncRequest;
import com.snapbizz.snaptoolkit.domains.CategorySyncRequest;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanySyncRequest;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.domains.CustomerPaymentSyncRequest;
import com.snapbizz.snaptoolkit.domains.CustomerSyncRequest;
import com.snapbizz.snaptoolkit.domains.DeleteRecordsRequest;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMapSyncRequest;
import com.snapbizz.snaptoolkit.domains.DistributorSyncRequest;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventoryBatchSyncRequest;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.InventorySyncRequest;
import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.domains.InvoiceSyncRequest;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.domains.OrderDetailsSyncRequest;
import com.snapbizz.snaptoolkit.domains.OrderSyncRequest;
import com.snapbizz.snaptoolkit.domains.PaymentSyncRequest;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ProductSkuSyncRequest;
import com.snapbizz.snaptoolkit.domains.ReceiveItems;
import com.snapbizz.snaptoolkit.domains.ReceiveItemsRequest;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.Settings;
import com.snapbizz.snaptoolkit.domains.SettingsSyncRequest;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.domains.TransactionSyncRequest;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnSyncDateUpdatedListener;
import com.snapbizz.snaptoolkit.utils.DeviceState;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestFormat;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.ResponseCodes;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TableType;

@SuppressLint("SimpleDateFormat")
public class SnapForceSyncService extends IntentService implements
		OnServiceCompleteListener, OnQueryCompleteListener,
		OnSyncDateUpdatedListener {

	private final int GETSYNC_BILLITEMS_TASKCODE = 0;
	private final int GETSYNC_TRANSACTIONS_TASKCODE = 1;
	private final int GETSYNC_CUSTOMERS_TASKCODE = 2;
	private final int GETSYNC_PRODUCTS_TASKCODE = 3;
	private final int GETSYNC_INVENTORY_TASKCODE = 4;
	private final int GETSYNC_INVENTORYBATCH_TASKCODE = 5;
	private final int GETSYNC_COMPANY_TASKCODE = 6;
	private final int GETSYNC_BRAND_TASKCODE = 7;
	private final int GETSYNC_DISTRIBUTOR_TASKCODE = 9;
	private final int GETSYNC_DISTRIBUTOR_BRAND_MAP_TASKCODE = 10;
	private final int GETSYNC_PAYMENTS_TASKCODE = 11;
	private final int GETSYNC_ORDER_TASKCODE = 12;
	private final int GETSYNC_ORDERDETAILS_TASKCODE = 13;
	private final int GETSYNC_DELETE_TASKCODE = 14;
	private final int GETSYNC_RECEIVE_ITEMS_TASKCODE = 18;
	private final int GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE = 20;
	private final int GET_SYNC_INVOICE_TASKCODE = 21;
	private final int GET_SYNC_SETTINGS_TASKCODE = 29;
	private final int GET_SYNC_CATEGORY_TASKCODE=32;
	private final int CLEAR_DELETED_RECORDS_TASKCODE = 16;
	private final int DASHBOARD_TASKCODE = 17;
	private final int APPVERSION_TASKCODE = 30;

	private String customerSyncTimestamp;
	private String companySyncTimestamp;
	private String brandSyncTimestamp;
	private String distributorSyncTimestamp;
	private String distributorBrandMapSyncTimestamp;
	private String paymentsSyncTimestamp;
	private String orderSyncTimestamp;
	private String orderDetailsSyncTimestamp;
	private String invoiceSyncTimestamp;
	private String receivedItemsSyncTimestamp;
	private String customerPaymentSyncTimestamp;
	private String categorySyncTimestamp;

	private String accessToken;
	private String storeId;
	private String deviceId;

	private static boolean isRunning;

	private Context commonContext;
	private Date defaultStartSyncDate;
	private Date defaultEndSyncDate;

	private String transactionSyncStartString;
	private String transactionSyncEndString;
	private String billItemsSyncStartString;
	private String billItemsSyncEndString;
	private String batchSyncStartString;
	private String batchSyncEndString;
	private String prodSyncStartString;
	private String prodSyncEndString;
	private String invSyncStartString;
	private String invSyncEndString;
	private static int runningTasks = 0;

	public SnapForceSyncService() {
		super("");
		// TODO Auto-generated constructor stub
	}
	/** 
	 * retrieve the settings from the Server forcing sync
	 */
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		if (isRunning) {
			stopSelf();
			return;
		}
		try {
			commonContext = SnapCommonUtils.getSnapContext(this);
			transactionSyncStartString = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_TRANSACTION_TIMESTAMP_KEY,
					commonContext);
			billItemsSyncStartString = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_BILLITEM_TIMESTAMP_KEY,
					commonContext);
			batchSyncStartString = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_INV_BATCH_TIMESTAMP_KEY,
					commonContext);
			prodSyncStartString = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PRODSKU_TIMESTAMP_KEY,
					commonContext);
			invSyncStartString = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_INVSKU_TIMESTAMP_KEY,
					commonContext);
			// if(transactionSyncStartString.isEmpty() ||
			// billItemsSyncStartString.isEmpty()){
			defaultStartSyncDate = new Date(114, 4, 1, 0, 0, 0);
			defaultEndSyncDate = new Date(114, 5, 1, 0, 0, 0);
			// }
			if (transactionSyncStartString.isEmpty()) {
				transactionSyncStartString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultStartSyncDate);
				transactionSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultEndSyncDate);
			} else {
				Log.d("timeStamp", transactionSyncStartString);
				Date transacTionStartDate = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.parse(transactionSyncStartString);
				transacTionStartDate
						.setDate(transacTionStartDate.getDate() + 14);
				transactionSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(transacTionStartDate);
			}
			if (billItemsSyncStartString.isEmpty()) {
				billItemsSyncStartString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultStartSyncDate);
				billItemsSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultEndSyncDate);
			} else {
				Log.d("timeStamp", billItemsSyncStartString);
				Date transacTionStartDate = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.parse(billItemsSyncStartString);
				transacTionStartDate
						.setDate(transacTionStartDate.getDate() + 14);
				billItemsSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(transacTionStartDate);
			}
			if (batchSyncStartString.isEmpty()) {
				batchSyncStartString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultStartSyncDate);
				batchSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultEndSyncDate);
			} else {
				Log.d("timeStamp", batchSyncStartString);
				Date batcDate = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.parse(batchSyncStartString);
				batcDate.setDate(batcDate.getDate() + 14);
				batchSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(batcDate);
			}
			if (prodSyncStartString.isEmpty()) {
				prodSyncStartString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultStartSyncDate);
				prodSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultEndSyncDate);
			} else {
				Log.d("timeStamp", prodSyncStartString);
				Date batcDate = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.parse(prodSyncStartString);
				batcDate.setDate(batcDate.getDate() + 14);
				prodSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(batcDate);
			}
			if (invSyncStartString.isEmpty()) {
				invSyncStartString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultStartSyncDate);
				invSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(defaultEndSyncDate);
			} else {
				Log.d("timeStamp", invSyncStartString);
				Date batcDate = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.parse(invSyncStartString);
				batcDate.setDate(batcDate.getDate() + 14);
				invSyncEndString = new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						.format(batcDate);
			}

			Log.d("date b4", transactionSyncEndString);
			isRunning = true;
			SQLiteDatabase.loadLibs(this);
			commonContext = SnapCommonUtils.getSnapContext(this);
			accessToken = SnapSharedUtils.getStoreAuthKey(commonContext);
			storeId = SnapSharedUtils.getStoreId(commonContext);
			deviceId = SnapSharedUtils.getDeviceId(commonContext);
			String companyTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_COMPANY_TIMESTAMP_KEY,
					commonContext);
			if (companyTime.isEmpty()) {
				new GetSyncCompanyTask(this, this, GETSYNC_COMPANY_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncCompanyTask(this, this, GETSYNC_COMPANY_TASKCODE)
						.execute(companyTime);
			}
			String brandTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_BRANDS_TIMESTAMP_KEY,
					commonContext);
			if (brandTime.isEmpty()) {
				new GetSyncBrandTask(this, this, GETSYNC_BRAND_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncBrandTask(this, this, GETSYNC_BRAND_TASKCODE)
						.execute(brandTime);
			}
			new GetForceSyncProductSkuTask(this, this, this,
					GETSYNC_PRODUCTS_TASKCODE, prodSyncStartString)
					.execute(prodSyncEndString);
			new GetForceSyncInventoryTask(this, this, this,
					GETSYNC_INVENTORY_TASKCODE, invSyncStartString)
					.execute(invSyncEndString);
			new GetForceSyncInventoryBatchTask(this, this, this,
					GETSYNC_INVENTORYBATCH_TASKCODE, batchSyncStartString)
					.execute(batchSyncEndString);
			String customerTime = SnapSharedUtils
					.retrieveForceSyncKey(
							SnapToolkitConstants.FORCE_SYNC_CUSTOMER_KEY,
							commonContext);
			if (customerTime.isEmpty()) {
				new GetSyncCustomerTask(this, this, GETSYNC_CUSTOMERS_TASKCODE)
						.execute("0000-00-00 00:00:00");
			} else {
				new GetSyncCustomerTask(this, this, GETSYNC_CUSTOMERS_TASKCODE)
						.execute(customerTime);
			}
			String distributorTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_DISTRIBUTOR_KEY,
					commonContext);
			if (distributorTime.isEmpty()) {
				new GetSyncDistributorTask(this, this,
						GETSYNC_DISTRIBUTOR_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncDistributorTask(this, this,
						GETSYNC_DISTRIBUTOR_TASKCODE).execute(distributorTime);
			}
			String distributorBrandMapTime = SnapSharedUtils
					.retrieveForceSyncKey(
							SnapToolkitConstants.FORCE_SYNC_DISTRIBUTOR_BRAND_MAP_KEY,
							commonContext);
			if (distributorBrandMapTime.isEmpty()) {
				new GetSyncDistriButorBrandMapTask(this, this,
						GETSYNC_DISTRIBUTOR_BRAND_MAP_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncDistriButorBrandMapTask(this, this,
						GETSYNC_DISTRIBUTOR_BRAND_MAP_TASKCODE)
						.execute(distributorBrandMapTime);
			}
			String orderTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PO_KEY, commonContext);
			if (orderTime.isEmpty()) {
				new GetSyncOrderTask(this, this, GETSYNC_ORDER_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncOrderTask(this, this, GETSYNC_ORDER_TASKCODE)
						.execute(orderTime);
			}
			String orderDetailsTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PO_DETAILS_KEY,
					commonContext);
			if (orderDetailsTime.isEmpty()) {
				new GetSyncOrderDetailsTask(this, this,
						GETSYNC_ORDERDETAILS_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncOrderDetailsTask(this, this,
						GETSYNC_ORDERDETAILS_TASKCODE)
						.execute(orderDetailsTime);
			}
			new GetForceSyncTransactionsTask(this, this, this,
					GETSYNC_TRANSACTIONS_TASKCODE, transactionSyncStartString)
					.execute(transactionSyncEndString);
			// new
			// GetSyncTransactionsTask(this,this,GETSYNC_TRANSACTIONS_TASKCODE).execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			String paymentTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PAYMENT_KEY, commonContext);
			if (paymentTime.isEmpty()) {
				new GetSyncPaymentsTask(this, this, GETSYNC_PAYMENTS_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncPaymentsTask(this, this, GETSYNC_PAYMENTS_TASKCODE)
						.execute(paymentTime);
			}
			new GetForceSyncBillItemsTask(this, this, this,
					GETSYNC_BILLITEMS_TASKCODE, billItemsSyncStartString)
					.execute(billItemsSyncEndString);
			String invoiceTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_INVOICE_KEY, commonContext);
			if (invoiceTime.isEmpty()) {
				new GetSyncInvoiceTask(this, this, GET_SYNC_INVOICE_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncInvoiceTask(this, this, GET_SYNC_INVOICE_TASKCODE)
						.execute(invoiceTime);
			}
			String customerPaymentTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_CUSTOMER_PAYMENT_KEY,
					commonContext);
			if (customerPaymentTime.isEmpty()) {
				new GetSyncCustomerPaymentTask(this, this,
						GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncCustomerPaymentTask(this, this,
						GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE)
						.execute(customerPaymentTime);
			}
			new GetSyncDeleteTask(this, this, GETSYNC_DELETE_TASKCODE)
					.execute();
			String receiveTime = SnapSharedUtils.retrieveForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_RECEIVE_KEY, commonContext);
			if (receiveTime.isEmpty()) {
				new GetSyncReceiveItemTask(this, this,
						GETSYNC_RECEIVE_ITEMS_TASKCODE)
						.execute(SnapToolkitConstants.OLDEST_SYNC_DATE);
			} else {
				new GetSyncReceiveItemTask(this, this,
						GETSYNC_RECEIVE_ITEMS_TASKCODE).execute(receiveTime);
			}
			new DashboardTask(this, this, DASHBOARD_TASKCODE).execute();
            new GetSyncCategoryTask(this, this, GET_SYNC_CATEGORY_TASKCODE).execute(SnapSharedUtils.getLastCategoryTimestamp(commonContext));            
			new GetSettingsTask(this, GET_SYNC_SETTINGS_TASKCODE, this).execute();
			new GetAppVersionTask(this,APPVERSION_TASKCODE , this).execute();
		} catch (Exception e) {
			stopSelf();
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccess(ResponseContainer response) {
		// TODO Auto-generated method stub
		if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ONE
				.ordinal()) {
			new GetForceSyncBillItemsTask(this, this, this,
					GETSYNC_BILLITEMS_TASKCODE, billItemsSyncStartString)
					.execute(billItemsSyncEndString);
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_BILLITEM_TIMESTAMP_KEY,
					billItemsSyncStartString, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWO
				.ordinal()) {
			new GetForceSyncProductSkuTask(this, this, this,
					GETSYNC_PRODUCTS_TASKCODE, prodSyncStartString)
					.execute(prodSyncEndString);
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PRODSKU_TIMESTAMP_KEY,
					prodSyncStartString, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THREE
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_TRANSACTION_TIMESTAMP_KEY,
					transactionSyncStartString, commonContext);
			new GetForceSyncTransactionsTask(this, this, this,
					GETSYNC_TRANSACTIONS_TASKCODE, transactionSyncStartString)
					.execute(transactionSyncEndString);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FOUR
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_CUSTOMER_KEY,
					customerSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FIVE
				.ordinal()) {
			new GetForceSyncInventoryTask(this, this, this,
					GETSYNC_INVENTORY_TASKCODE, invSyncStartString)
					.execute(invSyncEndString);
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_INVSKU_TIMESTAMP_KEY,
					invSyncStartString, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_SIX
				.ordinal()) {
			new GetForceSyncInventoryBatchTask(this, this, this,
					GETSYNC_INVENTORYBATCH_TASKCODE, batchSyncStartString)
					.execute(batchSyncEndString);
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_INV_BATCH_TIMESTAMP_KEY,
					batchSyncStartString, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_SEVEN
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_COMPANY_TIMESTAMP_KEY,
					companySyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_EIGHT
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_BRANDS_TIMESTAMP_KEY,
					brandSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TEN
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_DISTRIBUTOR_KEY,
					distributorSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ELEVEN
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_DISTRIBUTOR_BRAND_MAP_KEY,
					distributorBrandMapSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWELVE
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PAYMENT_KEY,
					paymentsSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTEEN
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PO_KEY, orderSyncTimestamp,
					commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FOURTEEN
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_PO_DETAILS_KEY,
					orderDetailsSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYTHREE
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_CUSTOMER_PAYMENT_KEY,
					customerPaymentSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_NINETEEN
				.ordinal()) {
			new ClearDeleteRecordsTask(this, this,
					CLEAR_DELETED_RECORDS_TASKCODE).execute();
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYFIVE
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_RECEIVE_KEY,
					receivedItemsSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYSIX
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_INVOICE_KEY,
					invoiceSyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		}
		else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTYTWO
				.ordinal()) {
			SnapSharedUtils.storeForceSyncKey(
					SnapToolkitConstants.FORCE_SYNC_CATEGORY_KEY,
					categorySyncTimestamp, commonContext);
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance()
					.getTime(), commonContext);
		}
		runningTasks--;
	}

	@Override
	public void onError(ResponseContainer response, RequestCodes requestCode) {
		// TODO Auto-generated method stub
		if (response.getResponseCode() == ResponseCodes.BLOCKED
				.getResponseValue()) {
			SnapSharedUtils.storeDeviceState(DeviceState.DISABLED,
					commonContext);
		}

		runningTasks--;
		if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ONE
				.ordinal()) {
			if (runningTasks == 0) {
				Date currentDate = Calendar.getInstance().getTime();
				String date = SnapSharedUtils.retrieveForceSyncKey(
						SnapToolkitConstants.FORCE_SYNC_BILLITEM_TIMESTAMP_KEY,
						commonContext);
				try {
					Date endDate = new SimpleDateFormat(
							SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
							.parse(date);
					if (endDate.before(currentDate)) {
						System.out.println("Sync not complete");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWO
				.ordinal()) {
			if (runningTasks == 0) {
				Date currentDate = Calendar.getInstance().getTime();
				String date = SnapSharedUtils.retrieveForceSyncKey(
						SnapToolkitConstants.FORCE_SYNC_PRODSKU_TIMESTAMP_KEY,
						commonContext);
				try {
					Date endDate = new SimpleDateFormat(
							SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
							.parse(date);
					if (endDate.before(currentDate)) {
						System.out.println("Sync not complete");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THREE
				.ordinal()) {
			if (runningTasks == 0) {
				Date currentDate = Calendar.getInstance().getTime();
				String date = SnapSharedUtils
						.retrieveForceSyncKey(
								SnapToolkitConstants.FORCE_SYNC_TRANSACTION_TIMESTAMP_KEY,
								commonContext);
				try {
					Date endDate = new SimpleDateFormat(
							SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
							.parse(date);
					if (endDate.before(currentDate)) {
						System.out.println("Sync not complete");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FOUR
				.ordinal()) {

		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FIVE
				.ordinal()) {
			if (runningTasks == 0) {
				Date currentDate = Calendar.getInstance().getTime();
				String date = SnapSharedUtils
						.retrieveForceSyncKey(
								SnapToolkitConstants.FORCE_SYNC_INV_BATCH_TIMESTAMP_KEY,
								commonContext);
				try {
					Date endDate = new SimpleDateFormat(
							SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
							.parse(date);
					if (endDate.before(currentDate)) {
						System.out.println("Sync not complete");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_SIX
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_SEVEN
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_EIGHT
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TEN
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ELEVEN
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWELVE
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTEEN
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FOURTEEN
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYTHREE
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_NINETEEN
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYFIVE
				.ordinal()) {
		} else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYSIX
				.ordinal()) {
		}else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTYONE
				.ordinal()) {
		}else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTYTWO
				.ordinal()) {
		}

	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if (taskCode == GETSYNC_BILLITEMS_TASKCODE) {
			List<BillItem> billItemList = (List<BillItem>) responseList;
			BillItemSyncRequest billitemSyncRequest = new BillItemSyncRequest();
			billitemSyncRequest.setRequestFormat(RequestFormat.JSON);
			billitemSyncRequest.setRequestMethod(RequestMethod.POST);
			billitemSyncRequest.setAccessToken(accessToken);
			billitemSyncRequest.setStoreId(storeId);
			billitemSyncRequest.setDeviceId(deviceId);
			billitemSyncRequest.setBillItemList(billItemList);
			ServiceRequest serviceRequest = new ServiceRequest(
					billitemSyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.BILLITEM_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_CUSTOMERS_TASKCODE) {
			List<Customer> customerList = (List<Customer>) responseList;
			CustomerSyncRequest customerSyncRequest = new CustomerSyncRequest();
			customerSyncRequest.setRequestFormat(RequestFormat.JSON);
			customerSyncRequest.setRequestMethod(RequestMethod.POST);
			customerSyncRequest.setAccessToken(accessToken);
			customerSyncRequest.setStoreId(storeId);
			customerSyncRequest.setDeviceId(deviceId);
			customerSyncRequest.setCustomerList(customerList);
			ServiceRequest serviceRequest = new ServiceRequest(
					customerSyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.CUSTOMER_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FOUR);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			customerSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_INVENTORY_TASKCODE) {
			List<InventorySku> inventorySkuList = (List<InventorySku>) responseList;
			for (InventorySku inventorySku : inventorySkuList) {
				if (inventorySku.getProductSku() != null)
					inventorySku.setProductSkuCode(inventorySku.getProductSku()
							.getProductSkuCode());
			}
			InventorySyncRequest inventorySyncRequest = new InventorySyncRequest();
			inventorySyncRequest.setRequestFormat(RequestFormat.JSON);
			inventorySyncRequest.setRequestMethod(RequestMethod.POST);
			inventorySyncRequest.setAccessToken(accessToken);
			inventorySyncRequest.setStoreId(storeId);
			inventorySyncRequest.setDeviceId(deviceId);
			inventorySyncRequest.setInventoryList(inventorySkuList);
			ServiceRequest serviceRequest = new ServiceRequest(
					inventorySyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.INVENTORY_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FIVE);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_PRODUCTS_TASKCODE) {
			Log.d("date after ", transactionSyncEndString);

			List<ProductSku> productSkuList = (List<ProductSku>) responseList;
			for (ProductSku localProductSku : productSkuList) {
				if (localProductSku.getProductBrand() != null)
					localProductSku.setBrandId(localProductSku
							.getProductBrand().getBrandId());
				if (localProductSku.getProductCategory() != null)
					localProductSku.setSubcategoryId(localProductSku
							.getProductCategory().getCategoryId());
			}
			ProductSkuSyncRequest productSkuSyncRequest = new ProductSkuSyncRequest();
			productSkuSyncRequest.setProductSkuList(productSkuList);
			productSkuSyncRequest.setRequestFormat(RequestFormat.JSON);
			productSkuSyncRequest.setRequestMethod(RequestMethod.POST);
			productSkuSyncRequest.setAccessToken(accessToken);
			productSkuSyncRequest.setStoreId(storeId);
			productSkuSyncRequest.setDeviceId(deviceId);
			ServiceRequest serviceRequest = new ServiceRequest(
					productSkuSyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.PRODUCT_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWO);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			serviceThread.execute(serviceRequest);
			Request request = new Request();
			request.setRequestFormat(RequestFormat.MULTI_PART);
			request.setRequestMethod(RequestMethod.POST);
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
			requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY,
					accessToken);
			requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
			for (ProductSku localProductSku : productSkuList) {
				Bitmap bitmap = SnapCommonUtils.getImageBitmap(localProductSku);
				if (bitmap != null) {
					requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID,
							localProductSku.getProductSkuCode());
					request.setBitmap(bitmap);
					request.setRequestParams(requestParamMap);
					ServiceRequest imageServiceRequest = new ServiceRequest(
							request, this);
					imageServiceRequest
							.setMethod(SnapToolkitConstants.UPLOAD_METHOD);
					imageServiceRequest
							.setPath(SnapToolkitConstants.PRODUCTS_PATH);
					imageServiceRequest
							.setResponsibleClass(ResponseContainer.class);
					imageServiceRequest
							.setRequestCode(RequestCodes.REQUEST_CODE_SEVENTEEN);
					serviceThread = new ServiceThread(this, this, false);
					serviceThread.execute(imageServiceRequest);
				}
			}

		} else if (taskCode == GETSYNC_TRANSACTIONS_TASKCODE) {
			Log.d("date after ", transactionSyncEndString);

			List<Transaction> transactionList = (List<Transaction>) responseList;
			for (Transaction transaction : transactionList) {
				if (transaction.getCustomer() != null)
					transaction.setCustomerId(transaction.getCustomer()
							.getCustomerId());
			}
			TransactionSyncRequest transactionSyncRequest = new TransactionSyncRequest();
			transactionSyncRequest.setRequestFormat(RequestFormat.JSON);
			transactionSyncRequest.setRequestMethod(RequestMethod.POST);
			// HashMap<String, String> requestParams = new HashMap<String,
			// String>();
			// requestParams.put("latest_timestamp", new
			// SimpleDateFormat().format(transactionList.get(0).getLastModifiedTimestamp()));
			// transactionSyncRequest.setRequestParams(requestParams);
			Log.d("request ", accessToken + " : :  " + storeId + " : : "
					+ deviceId);
			transactionSyncRequest.setAccessToken(accessToken);
			transactionSyncRequest.setStoreId(storeId);
			transactionSyncRequest.setDeviceId(deviceId);
			transactionSyncRequest.setTransactionList(transactionList);
			ServiceRequest serviceRequest = new ServiceRequest(
					transactionSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.TRANSACTION_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THREE);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_INVENTORYBATCH_TASKCODE) {
			List<InventoryBatch> inventoryBatchList = (List<InventoryBatch>) responseList;
			for (InventoryBatch inventoryBatch : inventoryBatchList) {
				if (inventoryBatch.getProductSku() != null)
					inventoryBatch.setProductSkuCode(inventoryBatch
							.getProductSku().getProductSkuCode());
				if (inventoryBatch.getBatchOrder() != null)
					inventoryBatch.setOrderId(inventoryBatch.getBatchOrder()
							.getOrderNumber());
			}
			InventoryBatchSyncRequest inventoryBatchSyncRequest = new InventoryBatchSyncRequest();
			inventoryBatchSyncRequest.setRequestFormat(RequestFormat.JSON);
			inventoryBatchSyncRequest.setRequestMethod(RequestMethod.POST);
			inventoryBatchSyncRequest.setAccessToken(accessToken);
			inventoryBatchSyncRequest.setStoreId(storeId);
			inventoryBatchSyncRequest.setDeviceId(deviceId);
			inventoryBatchSyncRequest.setInventoryBatchList(inventoryBatchList);
			ServiceRequest serviceRequest = new ServiceRequest(
					inventoryBatchSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.INVENTORY_BATCH_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_SIX);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_COMPANY_TASKCODE) {
			List<Company> companyList = (List<Company>) responseList;
			CompanySyncRequest companySyncRequest = new CompanySyncRequest();
			companySyncRequest.setRequestFormat(RequestFormat.JSON);
			companySyncRequest.setRequestMethod(RequestMethod.POST);
			companySyncRequest.setAccessToken(accessToken);
			companySyncRequest.setStoreId(storeId);
			companySyncRequest.setDeviceId(deviceId);
			companySyncRequest.setCompanyList(companyList);
			ServiceRequest serviceRequest = new ServiceRequest(
					companySyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.COMPANY_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_SEVEN);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			companySyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_BRAND_TASKCODE) {
			List<Brand> brandList = (List<Brand>) responseList;
			BrandSyncRequest brandSyncRequest = new BrandSyncRequest();
			brandSyncRequest.setRequestFormat(RequestFormat.JSON);
			brandSyncRequest.setRequestMethod(RequestMethod.POST);
			brandSyncRequest.setAccessToken(accessToken);
			brandSyncRequest.setStoreId(storeId);
			brandSyncRequest.setDeviceId(deviceId);
			brandSyncRequest.setBrandList(brandList);
			ServiceRequest serviceRequest = new ServiceRequest(
					brandSyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.BRAND_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_EIGHT);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			brandSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
			Request request = new Request();
			request.setRequestFormat(RequestFormat.MULTI_PART);
			request.setRequestMethod(RequestMethod.POST);
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
			requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY,
					accessToken);
			requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
			for (Brand brand : brandList) {
				Bitmap bitmap = SnapCommonUtils.getImageBitmap(brand);
				if (bitmap != null) {
					requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID,
							brand.getBrandId() + "");
					request.setBitmap(bitmap);
					request.setRequestParams(requestParamMap);
					ServiceRequest imageServiceRequest = new ServiceRequest(
							request, this);
					imageServiceRequest
							.setMethod(SnapToolkitConstants.UPLOAD_METHOD);
					imageServiceRequest
							.setPath(SnapToolkitConstants.BRANDS_PATH);
					imageServiceRequest
							.setResponsibleClass(ResponseContainer.class);
					imageServiceRequest
							.setRequestCode(RequestCodes.REQUEST_CODE_FIFTEEN);
					serviceThread = new ServiceThread(this, this, false);
					serviceThread.execute(imageServiceRequest);
				}
			}
		} else if (taskCode == GETSYNC_DISTRIBUTOR_TASKCODE) {
			List<Distributor> distributorList = (List<Distributor>) responseList;
			DistributorSyncRequest distributorSyncRequest = new DistributorSyncRequest();
			distributorSyncRequest.setRequestFormat(RequestFormat.JSON);
			distributorSyncRequest.setRequestMethod(RequestMethod.POST);
			distributorSyncRequest.setAccessToken(accessToken);
			distributorSyncRequest.setStoreId(storeId);
			distributorSyncRequest.setDeviceId(deviceId);
			distributorSyncRequest.setDistributorList(distributorList);
			ServiceRequest serviceRequest = new ServiceRequest(
					distributorSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.DISTRIBUTOR_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TEN);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			distributorSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_DISTRIBUTOR_BRAND_MAP_TASKCODE) {
			List<DistributorBrandMap> distributorBrandMapList = (List<DistributorBrandMap>) responseList;
			for (DistributorBrandMap distributorBrandMap : distributorBrandMapList) {
				if (distributorBrandMap.getDistributorBrand() != null)
					distributorBrandMap.setBrandId(distributorBrandMap
							.getDistributorBrand().getBrandId());
				if (distributorBrandMap.getDistributor() != null)
					distributorBrandMap.setDistributorId(distributorBrandMap
							.getDistributor().getDistributorId());
			}
			DistributorBrandMapSyncRequest distributorBrandMapSyncRequest = new DistributorBrandMapSyncRequest();
			distributorBrandMapSyncRequest.setRequestFormat(RequestFormat.JSON);
			distributorBrandMapSyncRequest.setRequestMethod(RequestMethod.POST);
			distributorBrandMapSyncRequest.setAccessToken(accessToken);
			distributorBrandMapSyncRequest.setStoreId(storeId);
			distributorBrandMapSyncRequest.setDeviceId(deviceId);
			distributorBrandMapSyncRequest
					.setDistributorBrandMapList(distributorBrandMapList);
			ServiceRequest serviceRequest = new ServiceRequest(
					distributorBrandMapSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.DISTRIBUTOR_BRANDMAP_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ELEVEN);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			distributorBrandMapSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_PAYMENTS_TASKCODE) {
			List<Payments> paymentsList = (List<Payments>) responseList;
			for (Payments payments : paymentsList) {
				if (payments.getDistributor() != null)
					payments.setDistributorId(payments.getDistributor()
							.getDistributorId());
			}
			PaymentSyncRequest paymentsSyncRequest = new PaymentSyncRequest();
			paymentsSyncRequest.setRequestFormat(RequestFormat.JSON);
			paymentsSyncRequest.setRequestMethod(RequestMethod.POST);
			paymentsSyncRequest.setAccessToken(accessToken);
			paymentsSyncRequest.setStoreId(storeId);
			paymentsSyncRequest.setDeviceId(deviceId);
			paymentsSyncRequest.setPaymentsList(paymentsList);
			ServiceRequest serviceRequest = new ServiceRequest(
					paymentsSyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.PAYMENTS_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWELVE);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			paymentsSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} 
		else if(taskCode == GET_SYNC_SETTINGS_TASKCODE) {
        List<Settings> settingsList = (List<Settings>) responseList;
        SettingsSyncRequest settingsSyncRequest = new SettingsSyncRequest();
        settingsSyncRequest.setRequestFormat(RequestFormat.JSON);
        settingsSyncRequest.setRequestMethod(RequestMethod.POST);
        settingsSyncRequest.setAccessToken(accessToken);
        settingsSyncRequest.setStoreId(storeId);
        settingsSyncRequest.setDeviceId(deviceId);
        settingsSyncRequest.setSettingsList(settingsList);
        ServiceRequest serviceRequest = new ServiceRequest(settingsSyncRequest, this);
        serviceRequest.setMethod(SnapToolkitConstants.SETTINGS_SYNC_METHOD);
        serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
        serviceRequest.setResponsibleClass(ResponseContainer.class);
        serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THIRTYONE);
        ServiceThread serviceThread = new ServiceThread(this, this, false);
        serviceThread.execute(serviceRequest);
    } 
		else if(taskCode == GET_SYNC_CATEGORY_TASKCODE) {
            List<ProductCategory> productCategoryList = (List<ProductCategory>) responseList;
            CategorySyncRequest productCategoryListSyncRequest = new CategorySyncRequest();
            productCategoryListSyncRequest.setRequestFormat(RequestFormat.JSON);
            productCategoryListSyncRequest.setRequestMethod(RequestMethod.POST);
            productCategoryListSyncRequest.setAccessToken(accessToken);
            productCategoryListSyncRequest.setStoreId(storeId);
            productCategoryListSyncRequest.setDeviceId(deviceId);
            productCategoryListSyncRequest.setProductCategoryList(productCategoryList);
            ServiceRequest serviceRequest = new ServiceRequest(productCategoryListSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.CATEGORY_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THIRTYTWO);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            categorySyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
            serviceThread.execute(serviceRequest);
        }
		else if(taskCode == APPVERSION_TASKCODE) {
	        List<AppVersion> appVersionList = (List<AppVersion>) responseList;       
	        AppVersionSyncRequest versionSyncRequest = new AppVersionSyncRequest();
	        versionSyncRequest.setRequestFormat(RequestFormat.JSON);
	        versionSyncRequest.setRequestMethod(RequestMethod.POST);
	        versionSyncRequest.setAccessToken(accessToken);
	        versionSyncRequest.setStoreId(storeId);
	        versionSyncRequest.setDeviceId(deviceId);
	        versionSyncRequest.setAppVersionList(appVersionList);
	        ServiceRequest serviceRequest = new ServiceRequest(versionSyncRequest, this);
	        serviceRequest.setMethod(SnapToolkitConstants.APPVERSION_SYNC_METHOD);
	        serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
	        serviceRequest.setResponsibleClass(ResponseContainer.class);
	        serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYNINE);
	        ServiceThread serviceThread = new ServiceThread(this, this, false);
	        serviceThread.execute(serviceRequest);
	    } 
		 else if (taskCode == GETSYNC_ORDER_TASKCODE) {
			List<Order> orderList = (List<Order>) responseList;
			for (Order order : orderList) {
				if (order.getDistributorID() != null)
					order.setDistributorsId(order.getDistributorID()
							.getDistributorId());
			}
			OrderSyncRequest ordersSyncRequest = new OrderSyncRequest();
			ordersSyncRequest.setRequestFormat(RequestFormat.JSON);
			ordersSyncRequest.setRequestMethod(RequestMethod.POST);
			ordersSyncRequest.setAccessToken(accessToken);
			ordersSyncRequest.setStoreId(storeId);
			ordersSyncRequest.setDeviceId(deviceId);
			ordersSyncRequest.setOrderList(orderList);
			ServiceRequest serviceRequest = new ServiceRequest(
					ordersSyncRequest, this);
			serviceRequest.setMethod(SnapToolkitConstants.PO_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THIRTEEN);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			orderSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
			Request request = new Request();
			request.setRequestFormat(RequestFormat.MULTI_PART);
			request.setRequestMethod(RequestMethod.POST);
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
			requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY,
					accessToken);
			requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
			for (Order order : orderList) {
				Bitmap bitmap = SnapCommonUtils.getImageBitmap(order);
				if (bitmap != null) {
					requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID,
							order.getOrderNumber() + "");
					request.setBitmap(bitmap);
					request.setRequestParams(requestParamMap);
					ServiceRequest imageServiceRequest = new ServiceRequest(
							request, this);
					imageServiceRequest
							.setMethod(SnapToolkitConstants.UPLOAD_METHOD);
					imageServiceRequest.setPath(SnapToolkitConstants.PO_PATH);
					imageServiceRequest
							.setResponsibleClass(ResponseContainer.class);
					imageServiceRequest
							.setRequestCode(RequestCodes.REQUEST_CODE_EIGHTEEN);
					serviceThread = new ServiceThread(this, this, false);
					serviceThread.execute(imageServiceRequest);
				}
			}
		} else if (taskCode == GETSYNC_ORDERDETAILS_TASKCODE) {
			List<OrderDetails> orderDetailsList = (List<OrderDetails>) responseList;
			for (OrderDetails orderDetails : orderDetailsList) {
				if (orderDetails.getOrder() != null)
					orderDetails.setOrderId(orderDetails.getOrder()
							.getOrderNumber());
				if (orderDetails.getProductSkuID() != null)
					orderDetails.setProductSkuCode(orderDetails
							.getProductSkuID().getProductSkuCode());
			}
			OrderDetailsSyncRequest ordersDetailsSyncRequest = new OrderDetailsSyncRequest();
			ordersDetailsSyncRequest.setRequestFormat(RequestFormat.JSON);
			ordersDetailsSyncRequest.setRequestMethod(RequestMethod.POST);
			ordersDetailsSyncRequest.setAccessToken(accessToken);
			ordersDetailsSyncRequest.setStoreId(storeId);
			ordersDetailsSyncRequest.setDeviceId(deviceId);
			ordersDetailsSyncRequest.setOrderDetailsList(orderDetailsList);
			ServiceRequest serviceRequest = new ServiceRequest(
					ordersDetailsSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.PODETAILS_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FOURTEEN);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			orderDetailsSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GETSYNC_DELETE_TASKCODE) {
			List<DeletedRecords> deleteRecordsList = (List<DeletedRecords>) responseList;
			List<String> syncDeletedRecordsList = new ArrayList<String>();
			TableType tableType = deleteRecordsList.get(0).getTableType();
			DeleteRecordsRequest deleteRecordsRequest = new DeleteRecordsRequest();
			deleteRecordsRequest.setRequestFormat(RequestFormat.JSON);
			deleteRecordsRequest.setRequestMethod(RequestMethod.DELETE);
			deleteRecordsRequest.setAccessToken(accessToken);
			deleteRecordsRequest.setStoreId(storeId);
			deleteRecordsRequest.setDeviceId(deviceId);
			for (DeletedRecords deletedRecord : deleteRecordsList) {
				if (deletedRecord.getTableType().ordinal() != tableType
						.ordinal()) {
					deleteRecordsRequest
							.setDeleteRecordsList(syncDeletedRecordsList);
					ServiceRequest serviceRequest = new ServiceRequest(
							deleteRecordsRequest, this);
					serviceRequest.setMethod(tableType.getTableType());
					serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
					serviceRequest.setResponsibleClass(ResponseContainer.class);
					serviceRequest
							.setRequestCode(RequestCodes.REQUEST_CODE_NINETEEN);
					ServiceThread serviceThread = new ServiceThread(this, this,
							false);
					serviceThread.execute(serviceRequest);
					tableType = deletedRecord.getTableType();
					syncDeletedRecordsList = new ArrayList<String>();
					syncDeletedRecordsList.add(deletedRecord.getRecordId());
				} else {
					Log.d("",
							"added deleted record "
									+ deletedRecord.getRecordId());
					syncDeletedRecordsList.add(deletedRecord.getRecordId());
				}
			}
			deleteRecordsRequest.setDeleteRecordsList(syncDeletedRecordsList);
			ServiceRequest serviceRequest = new ServiceRequest(
					deleteRecordsRequest, this);
			serviceRequest.setMethod(tableType.getTableType());
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_NINETEEN);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			serviceThread.execute(serviceRequest);
		} else if (taskCode == GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE) {
			List<CustomerPayment> customerPaymentList = (List<CustomerPayment>) responseList;
			CustomerPaymentSyncRequest customerPaymentSyncRequest = new CustomerPaymentSyncRequest();
			customerPaymentSyncRequest
					.setCustomerPaymentList(customerPaymentList);
			customerPaymentSyncRequest.setRequestFormat(RequestFormat.JSON);
			customerPaymentSyncRequest.setRequestMethod(RequestMethod.POST);
			customerPaymentSyncRequest.setAccessToken(accessToken);
			customerPaymentSyncRequest.setStoreId(storeId);
			customerPaymentSyncRequest.setDeviceId(deviceId);
			ServiceRequest serviceRequest = new ServiceRequest(
					customerPaymentSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.CUSTOMERPAYMENTS_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest
					.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYTHREE);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			customerPaymentSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).format(Calendar.getInstance().getTime());
			serviceThread.execute(serviceRequest);
		} else if (GETSYNC_RECEIVE_ITEMS_TASKCODE == taskCode) {
			List<ReceiveItems> receiveItems = (List<ReceiveItems>) responseList;
			ReceiveItemsRequest receiveItemsRequest = new ReceiveItemsRequest();
			receiveItemsRequest.setReceiveItems(receiveItems);
			receiveItemsRequest.setRequestFormat(RequestFormat.JSON);
			receiveItemsRequest.setRequestMethod(RequestMethod.POST);
			receiveItemsRequest.setAccessToken(accessToken);
			receiveItemsRequest.setStoreId(storeId);
			receiveItemsRequest.setDeviceId(deviceId);
			ServiceRequest serviceRequest = new ServiceRequest(
					receiveItemsRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.RECEIVE_ITEMS_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYFIVE);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			receivedItemsSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		} else if (GET_SYNC_INVOICE_TASKCODE == taskCode) {
			List<Invoice> invoiceList = (List<Invoice>) responseList;
			InvoiceSyncRequest invoiceSyncRequest = new InvoiceSyncRequest();
			invoiceSyncRequest.setInvoiceList(invoiceList);
			invoiceSyncRequest.setRequestFormat(RequestFormat.JSON);
			invoiceSyncRequest.setRequestMethod(RequestMethod.POST);
			invoiceSyncRequest.setAccessToken(accessToken);
			invoiceSyncRequest.setStoreId(storeId);
			invoiceSyncRequest.setDeviceId(deviceId);
			ServiceRequest serviceRequest = new ServiceRequest(
					invoiceSyncRequest, this);
			serviceRequest
					.setMethod(SnapToolkitConstants.INVOICE_LIST_SYNC_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
			serviceRequest.setResponsibleClass(ResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYSIX);
			ServiceThread serviceThread = new ServiceThread(this, this, false);
			invoiceSyncTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(Calendar.getInstance()
					.getTime());
			serviceThread.execute(serviceRequest);
		}
		runningTasks++;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRunning = false;
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		Log.d(SnapSyncService.class.getName(), errorMessage);
	}

	@Override
	public void updateDateValues(String startDate, String EndDate, int taskCode) {
		// TODO Auto-generated method stub
		if (taskCode == GETSYNC_TRANSACTIONS_TASKCODE) {
			transactionSyncEndString = EndDate;
			transactionSyncStartString = startDate;
		} else if (taskCode == GETSYNC_BILLITEMS_TASKCODE) {
			billItemsSyncEndString = EndDate;
			billItemsSyncStartString = startDate;
		} else if (taskCode == GETSYNC_INVENTORY_TASKCODE) {
			invSyncEndString = EndDate;
			invSyncStartString = startDate;
		} else if (taskCode == GETSYNC_INVENTORYBATCH_TASKCODE) {
			batchSyncEndString = EndDate;
			batchSyncStartString = startDate;
		} else if (taskCode == GETSYNC_PRODUCTS_TASKCODE) {
			prodSyncEndString = EndDate;
			prodSyncStartString = startDate;
		}

	}

}

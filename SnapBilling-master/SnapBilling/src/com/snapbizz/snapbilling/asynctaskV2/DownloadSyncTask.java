package com.snapbizz.snapbilling.asynctaskV2;


import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.CustomerDetails;
import com.snapbizz.snaptoolkit.db.dao.CustomerMonthlySummary;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.db.dao.Inventory;
import com.snapbizz.snaptoolkit.db.dao.Invoices;
import com.snapbizz.snaptoolkit.db.dao.Items;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.db.dao.Products;
import com.snapbizz.snaptoolkit.db.dao.Transactions;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.v2.sync.LocalSyncAPI;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomer;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomerDetails;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomerMonthlySummary;
import com.snapbizz.v2.sync.LocalSyncData.ApiInventory;
import com.snapbizz.v2.sync.LocalSyncData.ApiInvoice;
import com.snapbizz.v2.sync.LocalSyncData.ApiProduct;
import com.snapbizz.v2.sync.LocalSyncData.ApiProductPacks;
import com.snapbizz.v2.sync.LocalSyncData.ApiTransaction;
import com.snapbizz.v2.sync.LocalSyncData.GetCustomerAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetCustomerDetailsAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetCustomerMonthlySummaryResonse;
import com.snapbizz.v2.sync.LocalSyncData.GetInventoryAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetInviceListAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetProductListAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetProductPackListAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetTransactionsListAPIResponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Toast;

public class DownloadSyncTask extends AsyncTask<Void, String, Boolean> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private ProgressDialog progressDialog;
	public enum SyncMethods {
		syncProductPacks, syncProducts, syncCustomers, syncCustomerMonthlySummary,
		syncInventory, syncCustomerDetails, syncInvoice, syncTransaction, 
	};
	
	public DownloadSyncTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener) {
		this.context = context;
		progressDialog = new ProgressDialog(context);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getResources().getString(R.string.download_sync_started_msg));
		progressDialog.show();
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (SnapCommonUtils.isNetworkAvailable(context)) {
			return downloadSyncAPICallingStatus();
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.no_network),
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		progressDialog.setMessage(values[0]);
	}
	
    private Date getDateFormat(String date) {
    	DateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.GDB_SNAPSHOT_TIMEFORMAT); 
    	Date startDate = null;
    	try {
    		startDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return startDate;
    }
    
    public Boolean downloadSyncAPICallingStatus() {
		try {
			SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(context, false);
			SyncMethods[] syncMehtodValues = SyncMethods.values();
			LocalSyncAPI localSyncAPI = new LocalSyncAPI(context);
			for (SyncMethods syncMethod : syncMehtodValues) {
				updateProgressDialogMessage(syncMethod.name());
				String start = null;
				int count = 0;
				do {
					switch (syncMethod) {
						case syncProductPacks:
							do {
								start = syncProductPacks(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncProducts:
							do {
								start = syncProducts(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncCustomers:
							do {
								start = syncCustomers(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncCustomerMonthlySummary:
							do {
								start = syncCustomersMonthlySummary(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncInventory:
							do {
								start = syncInventory(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncCustomerDetails:
							do {
								start = syncCustomersDetails(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncInvoice:
							do {
								start = syncInvoice(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						case syncTransaction:
							do {
								start = syncTransactions(localSyncAPI, start, snapbizzDB);
								start = updateProgressDialogErrorMessage(count, start, syncMethod.name());
								count ++;
							} while (start != null && start.isEmpty());
							break;
						default:
							break;
					}
				} while (start != null);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
	}
    
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
		if (result)
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		else
			onQueryCompleteListener.onTaskError(context.getResources().getString(R.string.download_sync_error_msg), taskCode);
	}
    
    private String syncProductPacks(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
    		GetProductPackListAPIResponse getProductPackListAPIResponse = localSyncAPI.getProductPackListAPI(Long.parseLong(SnapSharedUtils.getStoreId(context)),
    				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
    		if (getProductPackListAPIResponse == null)
    			return "";
    		List<ProductPacks> productPackList = new ArrayList<ProductPacks>();
    		if (getProductPackListAPIResponse.product_packsList != null && getProductPackListAPIResponse.product_packsList.length > 0) {
    			for (ApiProductPacks apiProductPacks : getProductPackListAPIResponse.product_packsList) {
    				ProductPacks productPacks = new ProductPacks(apiProductPacks.product_code, apiProductPacks.pack_size, 
    						apiProductPacks.sale_price1, apiProductPacks.sale_price2, apiProductPacks.sale_price3, 
    						apiProductPacks.is_default, getDateFormat(apiProductPacks.created_at), getDateFormat(apiProductPacks.updated_at));
    				productPackList.add(productPacks);
				}
    			snapbizzDB.packsHelper.insertProductPacks(productPackList);
    			if (getProductPackListAPIResponse.offset != null)
    				return getProductPackListAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
    	return null;
	}
    
    private String syncProducts(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
			GetProductListAPIResponse getProductListAPIResponse = localSyncAPI.getProductListAPI(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
					SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
			if (getProductListAPIResponse == null)
    			return "";
			List<Products> productList = new ArrayList<Products>();
			if (getProductListAPIResponse.productsList != null && getProductListAPIResponse.productsList.length > 0) {
				for (ApiProduct apiProduct : getProductListAPIResponse.productsList) {
					Products products = new Products(apiProduct.product_code, apiProduct.name, apiProduct.mrp, apiProduct.uom, 
							apiProduct.measure, apiProduct.vat_rate, apiProduct.image, apiProduct.is_gdb,
							getDateFormat(apiProduct.created_at), getDateFormat(apiProduct.updated_at));
					productList.add(products);
				}
				snapbizzDB.insertProduct(productList);
				if (getProductListAPIResponse.offset != null)
					return getProductListAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
    
    private String syncCustomers(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
			GetCustomerAPIResponse getCustomerAPIResponse = localSyncAPI.getCustomerList(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
					SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
			if (getCustomerAPIResponse == null)
    			return "";
			List<Customers> customersList = new ArrayList<Customers>();
			if (getCustomerAPIResponse.customersList != null && getCustomerAPIResponse.customersList.length > 0) {
				for (ApiCustomer apiCustomer : getCustomerAPIResponse.customersList) {
					Customers customers = new Customers(apiCustomer.phone, apiCustomer.name, apiCustomer.address, 
							apiCustomer.email, apiCustomer.credit_limit, apiCustomer.is_disabled, 
							getDateFormat(apiCustomer.created_at), getDateFormat(apiCustomer.updated_at));
					customersList.add(customers);
				}
				snapbizzDB.insertCustomers(customersList);
				if (getCustomerAPIResponse.offset != null)
					return getCustomerAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
    
    private String syncCustomersMonthlySummary(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
    		GetCustomerMonthlySummaryResonse getCustomerMonthlySummaryResonse = localSyncAPI.getCustomerMonthlySummaryList(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
    				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
    		if (getCustomerMonthlySummaryResonse == null)
    			return "";
    		List<CustomerMonthlySummary> customerMonthlySummaryList = new ArrayList<CustomerMonthlySummary>();
    		if (getCustomerMonthlySummaryResonse.customer_monthly_summaryList != null && getCustomerMonthlySummaryResonse.customer_monthly_summaryList.length > 0) {
    			for (ApiCustomerMonthlySummary apiCustomerMonthlySummary : getCustomerMonthlySummaryResonse.customer_monthly_summaryList) {
    				CustomerMonthlySummary customersMonthlySummary = new CustomerMonthlySummary(apiCustomerMonthlySummary.phone, apiCustomerMonthlySummary.month, 
    						apiCustomerMonthlySummary.amount_due, apiCustomerMonthlySummary.purchase_value, apiCustomerMonthlySummary.amount_paid,
    						getDateFormat(apiCustomerMonthlySummary.created_at), getDateFormat(apiCustomerMonthlySummary.updated_at));
    				customerMonthlySummaryList.add(customersMonthlySummary);
				}
    			snapbizzDB.insertCustomerMonthlySummaries(customerMonthlySummaryList);
    			if (getCustomerMonthlySummaryResonse.offset != null)
    				return getCustomerMonthlySummaryResonse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
	}
    
    private String syncCustomersDetails(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
    		GetCustomerDetailsAPIResponse getCustomerDetailsAPIResponse = localSyncAPI.getCustomerDetailsListAPI(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
    				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
    		if (getCustomerDetailsAPIResponse == null)
    			return "";
    		List<CustomerDetails> customerDetailsList = new ArrayList<CustomerDetails>();
    		if (getCustomerDetailsAPIResponse.customer_detailsList != null && getCustomerDetailsAPIResponse.customer_detailsList.length > 0) {
    			for (ApiCustomerDetails apiCustomerDetails : getCustomerDetailsAPIResponse.customer_detailsList) {
    				CustomerDetails customerDetails = new CustomerDetails(apiCustomerDetails.phone, apiCustomerDetails.amount_due, apiCustomerDetails.purchase_value, apiCustomerDetails.amount_saved, apiCustomerDetails.last_purchase_amount, 
    						apiCustomerDetails.last_payment_amount, apiCustomerDetails.avg_purchase, apiCustomerDetails.avg_visits, getDateFormat(apiCustomerDetails.last_purchase_date), getDateFormat(apiCustomerDetails.last_payment_date), getDateFormat(apiCustomerDetails.created_at), getDateFormat(apiCustomerDetails.updated_at));
    				customerDetailsList.add(customerDetails);
				}
    			snapbizzDB.insertCustomerDetailses(customerDetailsList);
				if (getCustomerDetailsAPIResponse.offset != null)
					return getCustomerDetailsAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
	}
    
    private String syncInventory(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
    		GetInventoryAPIResponse getInventoryAPIResponse = localSyncAPI.getInventoryList(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
    				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
    		if (getInventoryAPIResponse == null)
    			return "";
    		List<Inventory> inventoryList = new ArrayList<Inventory>();
    		if (getInventoryAPIResponse.inventoriesList != null && getInventoryAPIResponse.inventoriesList.length > 0) {
    			for(ApiInventory apiInventory : getInventoryAPIResponse.inventoriesList) {
    				Inventory inventory = new Inventory(apiInventory.product_code, apiInventory.quantity, apiInventory.minimum_base_quantity, apiInventory.reorder_point, apiInventory.is_deleted, getDateFormat(apiInventory.created_at), getDateFormat(apiInventory.updated_at));
    				inventoryList.add(inventory);
				}
    			snapbizzDB.inventoryHelper.insertInventory(inventoryList);
				if (getInventoryAPIResponse.offset != null)
					return getInventoryAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
    
    private String syncInvoice(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
    		GetInviceListAPIResponse getInviceListAPIResponse = localSyncAPI.getInvoiceListAPI(Long.parseLong(SnapSharedUtils.getStoreId(context)),
    				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
    		if (getInviceListAPIResponse == null)
    			return "";
    		List<Invoices> invoicesList = new ArrayList<Invoices>();
    		List<Items> invoicesItemList = new ArrayList<Items>();
    		if (getInviceListAPIResponse.invoicesList != null && getInviceListAPIResponse.invoicesList.length > 0) {
    			for (ApiInvoice apiInvoice : getInviceListAPIResponse.invoicesList) {
    				Invoices invoices = new Invoices(apiInvoice.invoice_id, apiInvoice.customer_phone, apiInvoice.is_memo, apiInvoice.total_amount,
    						apiInvoice.pending_amount, apiInvoice.total_discount, apiInvoice.total_savings, apiInvoice.parent_memo_id, apiInvoice.is_credit,
    						apiInvoice.total_quantity, apiInvoice.total_items, apiInvoice.total_vat_amount, apiInvoice.is_deleted, apiInvoice.is_delivery,
    						apiInvoice.biller_name, apiInvoice.pos_name, true, getDateFormat(apiInvoice.bill_started_at),
							getDateFormat(apiInvoice.created_at), getDateFormat(apiInvoice.updated_at));
    				invoicesList.add(invoices);
    				for (int i = 0; i < apiInvoice.items.size(); i++) {
    					Items item = new Items(null, apiInvoice.invoice_id, apiInvoice.items.get(i).name, apiInvoice.items.get(i).product_code, apiInvoice.items.get(i).uom, apiInvoice.items.get(i).measure,
    							apiInvoice.items.get(i).quantity, apiInvoice.items.get(i).mrp, apiInvoice.items.get(i).sale_price, apiInvoice.items.get(i).vat_rate, apiInvoice.items.get(i).vat_amount,
    							apiInvoice.items.get(i).savings, apiInvoice.items.get(i).total_amount, apiInvoice.items.get(i).pack_size, getDateFormat(apiInvoice.created_at), getDateFormat(apiInvoice.updated_at));
    					invoicesItemList.add(item);
					}
				}
    			snapbizzDB.invoiceHelper.insertOrReplaceInvoice(invoicesList);
    			snapbizzDB.invoiceHelper.insertOrReplaceItems(invoicesItemList);
				if (getInviceListAPIResponse.offset != null)
					return getInviceListAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
    
    private String syncTransactions(LocalSyncAPI localSyncAPI, String start, SnapbizzDB snapbizzDB) {
    	try {
    		GetTransactionsListAPIResponse getTransactionsListAPIResponse = localSyncAPI.getTransactionsListAPI(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
    				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
    		if (getTransactionsListAPIResponse == null)
    			return "";
    		List<Transactions> transactionsList = new ArrayList<Transactions>();
    		if (getTransactionsListAPIResponse.invoice_transactionsList != null && getTransactionsListAPIResponse.invoice_transactionsList.length > 0) {
    			for (ApiTransaction apiTransaction : getTransactionsListAPIResponse.invoice_transactionsList) {
    				Transactions transactions = new Transactions(apiTransaction.id, apiTransaction.invoice_id, apiTransaction.payment_type, apiTransaction.payment_mode, apiTransaction.payment_reference, apiTransaction.paid_amount, apiTransaction.remaining_amount, apiTransaction.customer_phone, apiTransaction.parent_transaction_id, true, getDateFormat(apiTransaction.created_at), getDateFormat(apiTransaction.updated_at));
    				transactionsList.add(transactions);
				}
//    			snapbizzDB.insertOrReplaceTransaction(transactionsList);
    			if (getTransactionsListAPIResponse.offset != null)
    				return getTransactionsListAPIResponse.offset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
    
    public void updateProgressDialogMessage(final String msg) {
    	Handler handler = new Handler(Looper.getMainLooper());
    	handler.post(new Runnable() {
    	    public void run()
    	    {
    	    	onProgressUpdate(msg);
    	    }
    	});
    }
    
    public String updateProgressDialogErrorMessage(int count, String start, String syncMethodName) {
    	if (count > 11 && start.isEmpty()) {
			updateProgressDialogMessage("Error in " + syncMethodName);
			return null;
		}
		return start; 
    }
}

package com.snapbizz.v2.sync;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.snapbizz.snaptoolkit.db.GlobalDB;
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
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomer;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomerDetails;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomerMonthlySummary;
import com.snapbizz.v2.sync.LocalSyncData.ApiInventory;
import com.snapbizz.v2.sync.LocalSyncData.ApiInvoice;
import com.snapbizz.v2.sync.LocalSyncData.ApiInvoice.ApiInvoiceItem;
import com.snapbizz.v2.sync.LocalSyncData.ApiOtpGeneration;
import com.snapbizz.v2.sync.LocalSyncData.ApiProduct;
import com.snapbizz.v2.sync.LocalSyncData.ApiProductPacks;
import com.snapbizz.v2.sync.LocalSyncData.ApiTransaction;
import com.snapbizz.v2.sync.LocalSyncData.DefaultAPIResponse;

import android.content.Context;
import android.util.Log;

public class UploadSyncService implements GlobalSync.GlobalSyncService {
	private static final int APK_SYNC_INTERVAL = 5 * 60 * 1000;
	private Context context = null;
	
	public UploadSyncService(Context context) {
		this.context = context;
	}

	@Override
	public Long startSync(long currentTime, long lastSyncTime, GlobalDB db, SnapbizzDB sdb) {
		Log.e("LocalSync", "started");
		try {
			LocalSyncAPI localSyncAPI = new LocalSyncAPI(context);
			ApiOtpGeneration apiOtpGeneration = new ApiOtpGeneration();
			apiOtpGeneration.device_id = SnapSharedUtils.getDeviceId(context);
			apiOtpGeneration.store_id = Long.parseLong(SnapSharedUtils.getStoreId(context));
			getProductPackUploadSyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getProductsSyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getCustomerSyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getCustomerMonthlySummarySyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getCustomerDetailsSyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getInventorySyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getInvoiceSyncAPICalling(localSyncAPI, sdb, apiOtpGeneration);
			getTransactionAPICalling(localSyncAPI, sdb, apiOtpGeneration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getSyncInterval() {
		return APK_SYNC_INTERVAL;
	}
	
	private void getProductPackUploadSyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			// TODO: Check this -> fetch from db in a max_limit way
			List<ProductPacks> productPackList = sdb.packsHelper.getPendingProductPacksList(new Date());
			if (productPackList != null && !productPackList.isEmpty()) {
				ArrayList<ApiProductPacks> apiProductPackList = new ArrayList<ApiProductPacks>();
				for (ProductPacks productPacks : productPackList) {
					ApiProductPacks apiProductPacks = new ApiProductPacks(productPacks.getProductCode(),
							productPacks.getPackSize(), productPacks.getSalePrice1(), productPacks.getSalePrice2(),
							productPacks.getSalePrice3(), productPacks.getIsDefault(), getDateFormatString(productPacks.getCreatedAt()), 
							getDateFormatString(productPacks.getUpdatedAt()));
					apiProductPackList.add(apiProductPacks);
				}
				DefaultAPIResponse apiProductPackRespons;
				apiProductPackRespons = localSyncAPI.callProductPacksAPI(apiOtpGeneration, SnapSharedUtils.getStoreAuthKey(context), apiProductPackList);
				Log.e("apiProductPackRespons", apiProductPackRespons.status);
				if (apiProductPackRespons.status != null && apiProductPackRespons.status.equalsIgnoreCase("Success"))
					sdb.packsHelper.updateProductPackSyncTime(productPackList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getProductsSyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<Products> productsList = sdb.getPendingProductList(new Date());
			if (productsList != null && !productsList.isEmpty()) {
				ArrayList<ApiProduct> apiProductsList = new ArrayList<ApiProduct>();
				for (Products product : productsList) {
					ApiProduct apiProduct = new ApiProduct(product.getProductCode(), product.getName(), 
							product.getMrp(), product.getUom(), product.getMeasure(),product.getImage(), 
							product.getVatRate(), product.getIsGdb(), getDateFormatString(product.getCreatedAt()), 
							getDateFormatString(product.getUpdatedAt()));
					apiProductsList.add(apiProduct);
				}
				DefaultAPIResponse ApiProductRespons = localSyncAPI.callProductAPI(apiOtpGeneration, SnapSharedUtils.getStoreAuthKey(context), apiProductsList);
				Log.e("ApiProductRespons", ApiProductRespons.status);
				if (ApiProductRespons.status != null && ApiProductRespons.status.equalsIgnoreCase("Success"))
					sdb.updateProductSyncTime(productsList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void getCustomerSyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<Customers> customersList = sdb.getPendingSyncCustomerList(new Date());
			if (customersList != null && !customersList.isEmpty()) {
				ArrayList<ApiCustomer> apiCustomersList = new ArrayList<ApiCustomer>();
				for (Customers customers : customersList) {
					ApiCustomer apiCustomer = new ApiCustomer(customers.getAddress(), customers.getName(), 
							customers.getPhone(), customers.getEmail(),customers.getCreditLimit(), 
							customers.getIsDisabled(), getDateFormatString(customers.getCreatedAt()), 
							getDateFormatString(customers.getUpdatedAt()));
					apiCustomersList.add(apiCustomer);
				}
				DefaultAPIResponse apiCustomerResponse = localSyncAPI.uploadCustomerData(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
						SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), apiCustomersList);
				Log.e("ApiCustomer", apiCustomerResponse.status);
	
				if (apiCustomerResponse.status != null && apiCustomerResponse.status.equalsIgnoreCase("Success"))
					sdb.updateCustomerSyncTime(customersList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void getCustomerMonthlySummarySyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<CustomerMonthlySummary> customerMonthlySummaryList = sdb.getPendingCustomerMonthlySummaryList(new Date());
			if (customerMonthlySummaryList != null && !customerMonthlySummaryList.isEmpty()) {
				ArrayList<ApiCustomerMonthlySummary> apiCustomerMonthlySummaryList = new ArrayList<ApiCustomerMonthlySummary>();
				for (CustomerMonthlySummary customerMonthlySummary : customerMonthlySummaryList) {
					ApiCustomerMonthlySummary apiCustomerMonthlySummary = new ApiCustomerMonthlySummary(customerMonthlySummary.getPhone(), 
							customerMonthlySummary.getAmountDue(), customerMonthlySummary.getPurchaseValue(), 
							customerMonthlySummary.getAmountPaid(), customerMonthlySummary.getMonth(), 
							getDateFormatString(customerMonthlySummary.getCreatedAt()), getDateFormatString(customerMonthlySummary.getUpdatedAt()));
					apiCustomerMonthlySummaryList.add(apiCustomerMonthlySummary);
				}
				
				DefaultAPIResponse customerMonthlySummaryAPIRespons = localSyncAPI.uploadCustomerMonthlySummaryData(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
						SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), apiCustomerMonthlySummaryList);
				Log.e("CustomerMonthlySummary", customerMonthlySummaryAPIRespons.status);
				if (customerMonthlySummaryAPIRespons.status != null && customerMonthlySummaryAPIRespons.status.equalsIgnoreCase("Success"))
					sdb.updateCustomerMonthlySummarySyncTime(customerMonthlySummaryList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void getCustomerDetailsSyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<CustomerDetails> customerDetailsList = sdb.getPendingCustomerDetailsList(new Date());
			if (customerDetailsList != null && !customerDetailsList.isEmpty()) {
				ArrayList<ApiCustomerDetails> apiCustomerDetList = new ArrayList<ApiCustomerDetails>();
				for (CustomerDetails customerDetails : customerDetailsList) {
					ApiCustomerDetails apiCustomerDetails = new ApiCustomerDetails(customerDetails.getPhone(), customerDetails.getAmountDue(),
							customerDetails.getPurchaseValue(), customerDetails.getAmountSaved(), getDateFormatString(customerDetails.getLastPurchaseDate()), 
							getDateFormatString(customerDetails.getLastPaymentDate()), customerDetails.getLastPurchaseAmount(),
							customerDetails.getLastPaymentAmount(), customerDetails.getAvgVisitsPerMonth(), 
							customerDetails.getAvgPurchasePerVisit(), getDateFormatString(customerDetails.getCreatedAt()),
							getDateFormatString(customerDetails.getUpdatedAt()));
					apiCustomerDetList.add(apiCustomerDetails);
				}
				
				DefaultAPIResponse ApiCustomerDetailsRespons = localSyncAPI.uploadCustomerDetailsData(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
						SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), apiCustomerDetList);
				Log.e("ApiCustomerDetailsRespons", ApiCustomerDetailsRespons.status);
				if (ApiCustomerDetailsRespons.status != null && ApiCustomerDetailsRespons.status.equalsIgnoreCase("Success"))
					sdb.updateCustomerDetailsSyncTime(customerDetailsList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void getInventorySyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB snapbizzDB, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<Inventory> inventoryList = snapbizzDB.inventoryHelper.getPendingInventoryList(new Date());
			if (inventoryList != null && !inventoryList.isEmpty()) {
				ArrayList<ApiInventory> apiInventoryList = new ArrayList<ApiInventory>();
				for (Inventory inventory : inventoryList) {
					ApiInventory apiInventory = new ApiInventory(inventory.getMinimumBaseQuantity(), inventory.getReOrderPoint(), 
							inventory.getQuantity(), inventory.getProductCode(), inventory.getIsDeleted(), 
							getDateFormatString(inventory.getCreatedAt()), getDateFormatString(inventory.getUpdatedAt()));
					apiInventoryList.add(apiInventory);
				}
				
				DefaultAPIResponse inventoryAPIResponse = localSyncAPI.uploadInventoryData(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
						SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), apiInventoryList);
				Log.e("inventoryAPIResponse", inventoryAPIResponse.status);
				if (inventoryAPIResponse.status != null && inventoryAPIResponse.status.equalsIgnoreCase("Success"))
					snapbizzDB.updateInventorySyncTime(inventoryList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getInvoiceSyncAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<Invoices> invoices = sdb.invoiceHelper.getReadyToSyncInvoiceList();
			if (invoices != null && !invoices.isEmpty()) {
				ArrayList<ApiInvoice> apiInvoiceList = new ArrayList<ApiInvoice>();
				if (invoices != null && !invoices.isEmpty()) {
					ArrayList<ApiInvoiceItem> invoiceItemList = new ArrayList<ApiInvoiceItem>();
					for (Invoices invoice : invoices) {
						List<Items> itemList = sdb.invoiceHelper.getItemsByInvoiceID(invoice.getId());
						for (Items item : itemList) {
							ApiInvoiceItem invoiceItem = new ApiInvoiceItem(item.getProductCode(), 
									item.getName(), item.getQuantity(), item.getUom(), item.getMeasure(), item.getPackSize(), 
									item.getMrp(), item.getSalePrice(), item.getTotalAmount(), item.getSavings(), item.getVatRate(), item.getVatAmount());
							invoiceItemList.add(invoiceItem);
						}
						ApiInvoice apiInvoice = new ApiInvoice(invoice.getId(), invoice.getCustomerPhone(),
								invoice.getMemoId(), invoice.getPosName(), invoice.getIsMemo(), invoice.getIsDeleted(),
								invoice.getIsDelivery(), invoice.getIsCredit(), invoice.getBillerName(),
								invoice.getTotalItems(), invoice.getTotalQuantity(), invoice.getTotalSavings(),
								invoice.getTotalDiscount(), invoice.getTotalVatAmount(), invoice.getTotalAmount(),
								invoice.getPendingAmount(), String.valueOf(invoice.getBillStartedAt()),
								getDateFormatString(invoice.getCreatedAt()), getDateFormatString(invoice.getUpdatedAt()), invoiceItemList);
						apiInvoiceList.add(apiInvoice);
					}
				}
				DefaultAPIResponse invoiceAPIRespons = localSyncAPI.callInvoiceAPI(apiOtpGeneration, SnapSharedUtils.getStoreAuthKey(context), apiInvoiceList);
				Log.e("invoiceAPIRespons", invoiceAPIRespons.status);
				if (invoiceAPIRespons.status != null && invoiceAPIRespons.status.equalsIgnoreCase("Success"))
					sdb.invoiceHelper.markSyncCompleteForInvoices(invoices);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void getTransactionAPICalling(LocalSyncAPI localSyncAPI, SnapbizzDB sdb, ApiOtpGeneration apiOtpGeneration) {
		try {
			List<Transactions> transactionList = sdb.invoiceHelper.getReadyToSyncTransactionList();
			if (transactionList != null && !transactionList.isEmpty()) {
				ArrayList<ApiTransaction> apiTransactionList = new ArrayList<ApiTransaction>();
				for (Transactions transaction : transactionList) {
					ApiTransaction apiTransaction = new ApiTransaction(transaction.getId(), transaction.getCustomerPhone(),
							transaction.getInvoiceId(), transaction.getPaymentType(), transaction.getPaymentMode(),
							transaction.getAmount(), transaction.getRemainingAmount(), transaction.getParentTransactionId(),
							transaction.getPaymentReference(), getDateFormatString(transaction.getCreatedAt()),
							getDateFormatString(transaction.getUpdatedAt()));
					apiTransactionList.add(apiTransaction);
				}
				DefaultAPIResponse apiTransactionResponse = localSyncAPI.callTransactionAPI(apiOtpGeneration, SnapSharedUtils.getStoreAuthKey(context), 
						apiTransactionList);
				Log.e("LocalSyncUploadTransactions", apiTransactionResponse.status);
				if (apiTransactionResponse.status != null && apiTransactionResponse.status.equalsIgnoreCase("Success"))
					sdb.invoiceHelper.markSyncCompleteForTransactions(transactionList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getDateFormatString(Date date) {
    	String dateStr = null;
        DateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.GDB_SNAPSHOT_TIMEFORMAT);
        dateStr = dateFormat.format(date);
		return dateStr;
    }
}

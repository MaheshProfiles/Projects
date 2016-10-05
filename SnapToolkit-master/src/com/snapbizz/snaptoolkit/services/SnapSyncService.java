package com.snapbizz.snaptoolkit.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.sqlcipher.database.SQLiteDatabase;
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
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMapSyncRequest;
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

public class SnapSyncService extends IntentService implements OnServiceCompleteListener, OnQueryCompleteListener, OnSyncDateUpdatedListener {

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
    private final int GETSYNC_SETTINGS_TASKCODE = 29;
    private final int GETSYNC_CATEGORY_TASKCODE = 32;
    private final int GETSYNC_ORDER_TASKCODE = 12;
    private final int GETSYNC_ORDERDETAILS_TASKCODE = 13;
    private final int GETSYNC_DELETE_TASKCODE = 14;
    private final int GETSYNC_RECEIVE_ITEMS_TASKCODE = 18;
    private final int GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE = 20;
    private final int GET_SYNC_INVOICE_TASKCODE = 21;
    private final int GETSYNC_DISTRIBUTOR_PRODUCT_MAP_TASKCODE = 22;
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
    private String distributorProductMapTimestamp;
    private String categorySyncTimestamp;
    
    private String productStartTimestamp;
    private String productEndTimestamp;
    private String billItemStartTimestamp;
    private String billItemEndTimestamp;
    private String transactionStartTimestamp;
    private String transactionEndTimestamp;
    private String inventoryStartTimestamp;
    private String inventoryEndTimestamp;
    private String inventoryBatchStartTimestamp;
    private String inventoryBatchEndTimestamp;
    
    private Calendar calendar;
    private Date date;

    private String accessToken;
    private String storeId;
    private String deviceId;

    private static boolean isRunning;

    private Context commonContext;

    public SnapSyncService() {
        super("");
    }

    /** 
	 * retrieve the settings from the Server
	 */
    @Override
    protected void onHandleIntent(Intent arg0) {
        if(isRunning) {
            //stopSelf();
            return;
        }
        try {
            isRunning = true;
            
            Log.d("TAG", "SnapSyncService appVersionList--------before->"+APPVERSION_TASKCODE);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Log.d("TAG", "SnapSyncService Started  time------->"+sdf.format(cal.getTime()));
            
            SQLiteDatabase.loadLibs(this);
            commonContext = SnapCommonUtils.getSnapContext(this);
            accessToken = SnapSharedUtils.getStoreAuthKey(commonContext);
            storeId = SnapSharedUtils.getStoreId(commonContext);
            deviceId = SnapSharedUtils.getDeviceId(commonContext);                   
            
            //new GetSyncCompanyTask(this,this,GETSYNC_COMPANY_TASKCODE).execute(SnapSharedUtils.getLastCompanySyncTimestamp(commonContext));            
            //new GetSyncBrandTask(this,this,GETSYNC_BRAND_TASKCODE).execute(SnapSharedUtils.getLastBrandSyncTimestamp(commonContext));  
            productStartTimestamp = SnapSharedUtils.getLastProductSkuSyncTimestamp(commonContext);
            productEndTimestamp = incByFiveDays(productStartTimestamp);
            new GetForceSyncProductSkuTask(this, this, this, GETSYNC_PRODUCTS_TASKCODE, productStartTimestamp).execute(productEndTimestamp);
            //new GetSyncTransactionsTask(this, this, GETSYNC_TRANSACTIONS_TASKCODE).execute(SnapSharedUtils.getLastTransactionSyncTimestamp(commonContext));
            inventoryStartTimestamp = SnapSharedUtils.getLastInventorySkuSyncTimestamp(commonContext);
            inventoryEndTimestamp = incByFiveDays(inventoryStartTimestamp);
            new GetForceSyncInventoryTask(this, this, this, GETSYNC_INVENTORY_TASKCODE, inventoryStartTimestamp).execute(inventoryEndTimestamp);
            //new GetSyncProductSkuTask(this, this, GETSYNC_PRODUCTS_TASKCODE).execute(SnapSharedUtils.getLastProductSkuSyncTimestamp(commonContext));
            new GetSyncCustomerTask(this, this, GETSYNC_CUSTOMERS_TASKCODE).execute(SnapSharedUtils.getLastCustomerSyncTimestamp(commonContext));  
            
            transactionStartTimestamp = SnapSharedUtils.getLastTransactionSyncTimestamp(commonContext);
            transactionEndTimestamp = incByFiveDays(transactionStartTimestamp);
            new GetForceSyncTransactionsTask(this, this, this, GETSYNC_TRANSACTIONS_TASKCODE, transactionStartTimestamp).execute(transactionEndTimestamp);            
            
            //new GetSyncBillItemsTask(this, this, GETSYNC_BILLITEMS_TASKCODE).execute(SnapSharedUtils.getLastBillItemSyncTimestamp(commonContext));
            new GetSyncCustomerPaymentTask(this, this, GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE).execute(SnapSharedUtils.getLastCustomerPaymentSyncTimestamp(commonContext));            

            //new GetSyncInventoryTask(this, this, GETSYNC_INVENTORY_TASKCODE).execute(SnapSharedUtils.getLastInventorySkuSyncTimestamp(commonContext));
            new GetSyncDistributorTask(this,this,GETSYNC_DISTRIBUTOR_TASKCODE).execute(SnapSharedUtils.getLastDistributorSyncTimestamp(commonContext));            
            new GetSyncDistriButorBrandMapTask(this,this,GETSYNC_DISTRIBUTOR_BRAND_MAP_TASKCODE).execute(SnapSharedUtils.getLastDistributorBrandMapSyncTimestamp(commonContext));                                                
            new GetSyncOrderTask(this,this,GETSYNC_ORDER_TASKCODE).execute(SnapSharedUtils.getLastOrderSyncTimestamp(commonContext));            
            new GetSyncOrderDetailsTask(this,this,GETSYNC_ORDERDETAILS_TASKCODE).execute(SnapSharedUtils.getLastOrderDetailsSyncTimestamp(commonContext));                      
            inventoryBatchStartTimestamp = SnapSharedUtils.getLastBatchSyncTimestamp(commonContext);
            inventoryBatchEndTimestamp = incByFiveDays(inventoryBatchStartTimestamp);
            new GetForceSyncInventoryBatchTask(this, this, this, GETSYNC_INVENTORYBATCH_TASKCODE, inventoryBatchStartTimestamp).execute(inventoryBatchEndTimestamp);                        
            billItemStartTimestamp = SnapSharedUtils.getLastBillItemSyncTimestamp(commonContext);
            billItemEndTimestamp = incByFiveDays(billItemStartTimestamp);
            new GetForceSyncBillItemsTask(this, this,this, GETSYNC_BILLITEMS_TASKCODE,billItemStartTimestamp).execute(billItemEndTimestamp);  
            
            //new GetSyncInventoryBatchTask(this,this,GETSYNC_INVENTORYBATCH_TASKCODE).execute(SnapSharedUtils.getLastBatchSyncTimestamp(commonContext));
            new GetSyncPaymentsTask(this,this,GETSYNC_PAYMENTS_TASKCODE).execute(SnapSharedUtils.getLastPaymentsSyncTimestamp(commonContext));                                             
            new GetSyncReceiveItemTask(this, this, GETSYNC_RECEIVE_ITEMS_TASKCODE).execute(SnapSharedUtils.getLastReceiveItemRetrievalSyncTimestamp(commonContext));
            new GetSyncInvoiceTask(this, this, GET_SYNC_INVOICE_TASKCODE).execute(SnapSharedUtils.getLastInvoiceRetrievalSyncTimestamp(commonContext));
            new GetSyncDeleteTask(this, this, GETSYNC_DELETE_TASKCODE).execute();
            new GetSettingsTask(this,GETSYNC_SETTINGS_TASKCODE , this).execute();
            Log.d("TAG", "appVersionList--------before->"+APPVERSION_TASKCODE);
            new GetAppVersionTask(this,APPVERSION_TASKCODE , this).execute();
            Log.d("TAG", "appVersionList--------after->"+APPVERSION_TASKCODE);
            
            
            new GetSyncCategoryTask(this, this, GETSYNC_CATEGORY_TASKCODE).execute(SnapSharedUtils.getLastCategoryTimestamp(commonContext));
            
            new DashboardTask(this, this, DASHBOARD_TASKCODE).execute();
            
        } catch(Exception e) {
            stopSelf();
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(ResponseContainer response) {
    	Log.d("prod sku","onSuccess ---response.getReturnObj()->"+response.getRequestCode().ordinal()+"--->"+response.getReturnObj());
        if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ONE.ordinal()) {
        	Log.d("prod sku","billItem size LastModifiedTimestamp ---billItemSyncTimestamp->"+((String)response.getReturnObj()));
        	if(response.getReturnObj() != null){
        		SnapSharedUtils.storeLastBillItemSyncTimestamp((String)response.getReturnObj(), commonContext);
        		SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        		new GetForceSyncBillItemsTask(this, this, this, GETSYNC_BILLITEMS_TASKCODE, (String)response.getReturnObj()).execute(billItemEndTimestamp);
        	}
            
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWO.ordinal()) {
        	Log.d("prod sku","productsku size LastModifiedTimestamp ---productSyncTimestamp->"+(String)response.getReturnObj());
        	if(response.getReturnObj() != null){
        		SnapSharedUtils.storeLastProductSkuSyncTimestamp((String)response.getReturnObj(), commonContext);
        		SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        		new GetForceSyncProductSkuTask(this, this, this, GETSYNC_PRODUCTS_TASKCODE, (String)response.getReturnObj()).execute(productEndTimestamp);
        	}
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THREE.ordinal()) {
        	if(response.getReturnObj() != null){
        		SnapSharedUtils.storeLastTransactionSyncTimestamp((String)response.getReturnObj(), commonContext);
        		SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        		new GetForceSyncTransactionsTask(this, this, this, GETSYNC_TRANSACTIONS_TASKCODE, (String)response.getReturnObj()).execute(transactionEndTimestamp);
        	}
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FOUR.ordinal()) {
            SnapSharedUtils.storeLastCustomerSyncTimestamp(customerSyncTimestamp, commonContext);
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FIVE.ordinal()) {
        	if(response.getReturnObj() != null){
        		SnapSharedUtils.storeLastInventorySkuSyncTimestamp((String)response.getReturnObj(), commonContext);
        		SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        		new GetForceSyncInventoryTask(this, this, this, GETSYNC_INVENTORY_TASKCODE, (String)response.getReturnObj()).execute(inventoryEndTimestamp);
        	}
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_SIX.ordinal()) {
        	if(response.getReturnObj() != null){
        		SnapSharedUtils.storeLastInventoryBatchSyncTimestamp((String)response.getReturnObj(),commonContext);
        		SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        		new GetForceSyncInventoryBatchTask(this, this, this, GETSYNC_INVENTORYBATCH_TASKCODE, (String)response.getReturnObj()).execute(inventoryBatchEndTimestamp);
        	}
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_SEVEN.ordinal()) {
            SnapSharedUtils.storeLastCompanySyncTimestamp(companySyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_EIGHT.ordinal()) {
        	Log.e("brand snc timestamp", Calendar.getInstance().getTime()+"");
            SnapSharedUtils.storeLastBrandSyncTimestamp(brandSyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TEN.ordinal()) {
            SnapSharedUtils.storeLastDistributorSyncTimestamp(distributorSyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ELEVEN.ordinal()) {
            SnapSharedUtils.storeLastDistributorBranMapSyncTimestamp(distributorBrandMapSyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWELVE.ordinal()) {
            SnapSharedUtils.storeLastPaymentSyncTimestamp(paymentsSyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTEEN.ordinal()) {
            SnapSharedUtils.storeLastOrderSyncTimestamp(orderSyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_FOURTEEN.ordinal()){
            SnapSharedUtils.storeLastOrderDetailsSyncTimestamp(orderDetailsSyncTimestamp,commonContext);	
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYTHREE.ordinal()) {
            SnapSharedUtils.storeLastCustomerPaymentSyncTimestamp(customerPaymentSyncTimestamp,commonContext);    
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYEIGHT.ordinal()) {
            SnapSharedUtils.storeLastDistributorProductMapTimestamp(distributorProductMapTimestamp, commonContext);
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_NINETEEN.ordinal()) {
            new ClearDeleteRecordsTask(this, this, CLEAR_DELETED_RECORDS_TASKCODE).execute();
        } else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYFIVE.ordinal()) {
            SnapSharedUtils.storeLastReceiveItemRetrievalTimestamp(receivedItemsSyncTimestamp, commonContext);
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_TWENTYSIX.ordinal()) {
            SnapSharedUtils.storeLastInvoiceRetrievalTimestamp(invoiceSyncTimestamp, commonContext);
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        } else if (response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_THIRTYTWO.ordinal()) {
            SnapSharedUtils.storeLastCategoryTimestamp(categorySyncTimestamp, commonContext);
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
        }
    }

    @Override
    public void onError(ResponseContainer response, RequestCodes requestCode) {
        if(response.getResponseCode() == ResponseCodes.BLOCKED.getResponseValue()) {
            SnapSharedUtils.storeDeviceState(DeviceState.DISABLED, commonContext);
        }
    }

    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        if(taskCode == GETSYNC_BILLITEMS_TASKCODE) {
            List<BillItem> billItemList = (List<BillItem>) responseList;
            BillItemSyncRequest billitemSyncRequest = new BillItemSyncRequest();
            billitemSyncRequest.setRequestFormat(RequestFormat.JSON);
            billitemSyncRequest.setRequestMethod(RequestMethod.POST);
            billitemSyncRequest.setAccessToken(accessToken);
            billitemSyncRequest.setStoreId(storeId);
            billitemSyncRequest.setDeviceId(deviceId);
            billitemSyncRequest.setBillItemList(billItemList);
            ServiceRequest serviceRequest = new ServiceRequest(billitemSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.BILLITEM_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
            Object obj = billItemStartTimestamp;
            ServiceThread serviceThread = new ServiceThread(this, this,obj);
            
            /*Date billDate = billItemList.get(0).getLastModifiedTimestamp();
            Log.d("tag", String.valueOf(billDate.getTime()));
            billDate.setTime(billDate.getTime() + 1000);
            Log.d("tag", String.valueOf(billDate.getTime()));
            billItemSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(billDate.getTime());
            */
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_CUSTOMERS_TASKCODE) {
            List<Customer> customerList = (List<Customer>) responseList;
            CustomerSyncRequest customerSyncRequest = new CustomerSyncRequest();
            customerSyncRequest.setRequestFormat(RequestFormat.JSON);
            customerSyncRequest.setRequestMethod(RequestMethod.POST);
            customerSyncRequest.setAccessToken(accessToken);
            customerSyncRequest.setStoreId(storeId);
            customerSyncRequest.setDeviceId(deviceId);
            customerSyncRequest.setCustomerList(customerList);
            ServiceRequest serviceRequest = new ServiceRequest(customerSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.CUSTOMER_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FOUR);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            customerSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_INVENTORY_TASKCODE) {
            List<InventorySku> inventorySkuList = (List<InventorySku>) responseList;
            for(InventorySku inventorySku : inventorySkuList) {
                if(inventorySku.getProductSku() != null)
                    inventorySku.setProductSkuCode(inventorySku.getProductSku().getProductSkuCode());
            }
            InventorySyncRequest inventorySyncRequest = new InventorySyncRequest();
            inventorySyncRequest.setRequestFormat(RequestFormat.JSON);
            inventorySyncRequest.setRequestMethod(RequestMethod.POST);
            inventorySyncRequest.setAccessToken(accessToken);
            inventorySyncRequest.setStoreId(storeId);
            inventorySyncRequest.setDeviceId(deviceId);
            inventorySyncRequest.setInventoryList(inventorySkuList);
            ServiceRequest serviceRequest = new ServiceRequest(inventorySyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.INVENTORY_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FIVE);
            Object obj = inventoryStartTimestamp;
            ServiceThread serviceThread = new ServiceThread(this, this, obj);
            
            /*Date inventoryDate = inventorySkuList.get(0).getLastModifiedTimestamp();
            inventoryDate.setTime(inventoryDate.getTime() + 1000);
            inventorySyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(inventoryDate.getTime());
            */
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_PRODUCTS_TASKCODE) {
            List<ProductSku> productSkuList = (List<ProductSku>) responseList;
            for(ProductSku localProductSku : productSkuList) {
                if(localProductSku.getProductBrand() != null)
                    localProductSku.setBrandId(localProductSku.getProductBrand().getBrandId());
                if(localProductSku.getProductCategory() != null)
                    localProductSku.setSubcategoryId(localProductSku.getProductCategory().getCategoryId());
            }
            ProductSkuSyncRequest productSkuSyncRequest = new ProductSkuSyncRequest();
            productSkuSyncRequest.setProductSkuList(productSkuList);
            productSkuSyncRequest.setRequestFormat(RequestFormat.JSON);
            productSkuSyncRequest.setRequestMethod(RequestMethod.POST);
            productSkuSyncRequest.setAccessToken(accessToken);
            productSkuSyncRequest.setStoreId(storeId);
            productSkuSyncRequest.setDeviceId(deviceId);
            ServiceRequest serviceRequest = new ServiceRequest(productSkuSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.PRODUCT_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWO);
            
            /*Date productDate = productSkuList.get(0).getLastModifiedTimestamp();
            productDate.setTime(productDate.getTime() + 1000);
            productSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(productDate);
            */
            Object obj = productStartTimestamp;
            ServiceThread serviceThread = new ServiceThread(this, this,obj);
            serviceThread.execute(serviceRequest);
            Request request = new Request();
            request.setRequestFormat(RequestFormat.MULTI_PART);
            request.setRequestMethod(RequestMethod.POST);
            HashMap<String, String> requestParamMap = new HashMap<String, String>();
            requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
            requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
            for(ProductSku localProductSku : productSkuList) {
                Bitmap bitmap = SnapCommonUtils.getImageBitmap(localProductSku);
                if(bitmap != null) {
                    requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID, localProductSku.getProductSkuCode());
                    request.setBitmap(bitmap);
                    request.setRequestParams(requestParamMap);
                    ServiceRequest imageServiceRequest = new ServiceRequest(request, this);
                    imageServiceRequest.setMethod(SnapToolkitConstants.UPLOAD_METHOD);
                    imageServiceRequest.setPath(SnapToolkitConstants.PRODUCTS_PATH);
                    imageServiceRequest.setResponsibleClass(ResponseContainer.class);
                    imageServiceRequest.setRequestCode(RequestCodes.REQUEST_CODE_SEVENTEEN);
                    serviceThread = new ServiceThread(this, this, obj);
                    serviceThread.execute(imageServiceRequest);
                }
            }
        } else if(taskCode == GETSYNC_TRANSACTIONS_TASKCODE) {
            List<Transaction> transactionList = (List<Transaction>) responseList;
            for(Transaction transaction : transactionList) {
                if(transaction.getCustomer() != null)
                    transaction.setCustomerId(transaction.getCustomer().getCustomerId());
            }
            TransactionSyncRequest transactionSyncRequest = new TransactionSyncRequest();
            transactionSyncRequest.setRequestFormat(RequestFormat.JSON);
            transactionSyncRequest.setRequestMethod(RequestMethod.POST);
            transactionSyncRequest.setAccessToken(accessToken);
            transactionSyncRequest.setStoreId(storeId);
            transactionSyncRequest.setDeviceId(deviceId);
            transactionSyncRequest.setTransactionList(transactionList);
            ServiceRequest serviceRequest = new ServiceRequest(transactionSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.TRANSACTION_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THREE);
            Object obj = transactionStartTimestamp;
            ServiceThread serviceThread = new ServiceThread(this, this, obj);
            
            /*Date transactionDate = transactionList.get(0).getLastModifiedTimestamp();
            transactionDate.setTime(transactionDate.getTime() + 1000);
            transactionSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(transactionDate.getTime());
            */
           
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_INVENTORYBATCH_TASKCODE) {
            List<InventoryBatch> inventoryBatchList = (List<InventoryBatch>) responseList;
            for(InventoryBatch inventoryBatch : inventoryBatchList) {
                if(inventoryBatch.getProductSku() != null)
                    inventoryBatch.setProductSkuCode(inventoryBatch.getProductSku().getProductSkuCode());
                if(inventoryBatch.getBatchOrder() != null)
                    inventoryBatch.setOrderId(inventoryBatch.getBatchOrder().getOrderNumber());
            }
            InventoryBatchSyncRequest inventoryBatchSyncRequest = new InventoryBatchSyncRequest();
            inventoryBatchSyncRequest.setRequestFormat(RequestFormat.JSON);
            inventoryBatchSyncRequest.setRequestMethod(RequestMethod.POST);
            inventoryBatchSyncRequest.setAccessToken(accessToken);
            inventoryBatchSyncRequest.setStoreId(storeId);
            inventoryBatchSyncRequest.setDeviceId(deviceId);
            inventoryBatchSyncRequest.setInventoryBatchList(inventoryBatchList);
            ServiceRequest serviceRequest = new ServiceRequest(inventoryBatchSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.INVENTORY_BATCH_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_SIX);
            Object obj = inventoryBatchStartTimestamp;
            ServiceThread serviceThread = new ServiceThread(this, this, obj);
            
            /*Date batchDate = inventoryBatchList.get(0).getLastModifiedTimestamp();
            batchDate.setTime(batchDate.getTime() + 1000);
            inventoryBatchSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(batchDate.getTime());
            */
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_COMPANY_TASKCODE) {
            List<Company> companyList = (List<Company>) responseList;
            CompanySyncRequest companySyncRequest = new CompanySyncRequest();
            companySyncRequest.setRequestFormat(RequestFormat.JSON);
            companySyncRequest.setRequestMethod(RequestMethod.POST);
            companySyncRequest.setAccessToken(accessToken);
            companySyncRequest.setStoreId(storeId);
            companySyncRequest.setDeviceId(deviceId);
            companySyncRequest.setCompanyList(companyList);
            ServiceRequest serviceRequest = new ServiceRequest(companySyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.COMPANY_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_SEVEN);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            companySyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_BRAND_TASKCODE) {
            List<Brand> brandList = (List<Brand>) responseList;
            BrandSyncRequest brandSyncRequest = new BrandSyncRequest();
            brandSyncRequest.setRequestFormat(RequestFormat.JSON);
            brandSyncRequest.setRequestMethod(RequestMethod.POST);
            brandSyncRequest.setAccessToken(accessToken);
            brandSyncRequest.setStoreId(storeId);
            brandSyncRequest.setDeviceId(deviceId);
            brandSyncRequest.setBrandList(brandList);
            ServiceRequest serviceRequest = new ServiceRequest(brandSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.BRAND_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_EIGHT);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            brandSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
            Request request = new Request();
            request.setRequestFormat(RequestFormat.MULTI_PART);
            request.setRequestMethod(RequestMethod.POST);
            HashMap<String, String> requestParamMap = new HashMap<String, String>();
            requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
            requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
            for(Brand brand : brandList) {
                Bitmap bitmap = SnapCommonUtils.getImageBitmap(brand);
                if(bitmap != null) {
                    requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID, brand.getBrandId()+"");
                    request.setBitmap(bitmap);
                    request.setRequestParams(requestParamMap);
                    ServiceRequest imageServiceRequest = new ServiceRequest(request, this);
                    imageServiceRequest.setMethod(SnapToolkitConstants.UPLOAD_METHOD);
                    imageServiceRequest.setPath(SnapToolkitConstants.BRANDS_PATH);
                    imageServiceRequest.setResponsibleClass(ResponseContainer.class);
                    imageServiceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FIFTEEN);
                    serviceThread = new ServiceThread(this, this, false);
                    serviceThread.execute(imageServiceRequest);
                }
            }
        } else if(taskCode == GETSYNC_DISTRIBUTOR_TASKCODE) {
            List<Distributor> distributorList = (List<Distributor>) responseList;
            DistributorSyncRequest distributorSyncRequest = new DistributorSyncRequest();
            distributorSyncRequest.setRequestFormat(RequestFormat.JSON);
            distributorSyncRequest.setRequestMethod(RequestMethod.POST);
            distributorSyncRequest.setAccessToken(accessToken);
            distributorSyncRequest.setStoreId(storeId);
            distributorSyncRequest.setDeviceId(deviceId);
            distributorSyncRequest.setDistributorList(distributorList);
            ServiceRequest serviceRequest = new ServiceRequest(distributorSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.DISTRIBUTOR_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TEN);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            distributorSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_DISTRIBUTOR_BRAND_MAP_TASKCODE) {
            List<DistributorBrandMap> distributorBrandMapList = (List<DistributorBrandMap>) responseList;
            for(DistributorBrandMap distributorBrandMap : distributorBrandMapList) {
                if(distributorBrandMap.getDistributorBrand() != null)
                    distributorBrandMap.setBrandId(distributorBrandMap.getDistributorBrand().getBrandId());
                if(distributorBrandMap.getDistributor() != null)
                    distributorBrandMap.setDistributorId(distributorBrandMap.getDistributor().getDistributorId());
            }
            DistributorBrandMapSyncRequest distributorBrandMapSyncRequest = new DistributorBrandMapSyncRequest();
            distributorBrandMapSyncRequest.setRequestFormat(RequestFormat.JSON);
            distributorBrandMapSyncRequest.setRequestMethod(RequestMethod.POST);
            distributorBrandMapSyncRequest.setAccessToken(accessToken);
            distributorBrandMapSyncRequest.setStoreId(storeId);
            distributorBrandMapSyncRequest.setDeviceId(deviceId);
            distributorBrandMapSyncRequest.setDistributorBrandMapList(distributorBrandMapList);
            ServiceRequest serviceRequest = new ServiceRequest(distributorBrandMapSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.DISTRIBUTOR_BRANDMAP_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ELEVEN);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            distributorBrandMapSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_PAYMENTS_TASKCODE) {
            List<Payments> paymentsList = (List<Payments>) responseList;
            for(Payments payments : paymentsList) {
                if(payments.getDistributor() != null)
                    payments.setDistributorId(payments.getDistributor().getDistributorId());
            }
            PaymentSyncRequest paymentsSyncRequest = new PaymentSyncRequest();
            paymentsSyncRequest.setRequestFormat(RequestFormat.JSON);
            paymentsSyncRequest.setRequestMethod(RequestMethod.POST);
            paymentsSyncRequest.setAccessToken(accessToken);
            paymentsSyncRequest.setStoreId(storeId);
            paymentsSyncRequest.setDeviceId(deviceId);
            paymentsSyncRequest.setPaymentsList(paymentsList);
            ServiceRequest serviceRequest = new ServiceRequest(paymentsSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.PAYMENTS_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWELVE);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            paymentsSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        }
        /** 
		 * Sending the settings to the Server
		 */
        else if(taskCode == GETSYNC_SETTINGS_TASKCODE) {
            List<Settings> settingsList = (List<Settings>) responseList;
            Log.d("settingslist", "SnapSyncservice Settilng list");
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
        else if(taskCode == GETSYNC_CATEGORY_TASKCODE) {
            List<ProductCategory> productCategoryList = (List<ProductCategory>) responseList;
            Log.d("TAG", "ProductCategory------productCategoryList-->"+productCategoryList.size());
            for (ProductCategory productCategory : productCategoryList){
            	Log.d("TAG", "ProductCategory------ProductCategorySalePrice-->"+productCategory.getProductCategorySalePrice());
            	Log.d("TAG", "ProductCategory------ProductCategorySalePriceTwo-->"+productCategory.getProductCategorySalePriceTwo());
            	Log.d("TAG", "ProductCategory------ProductCategorySalePriceThree-->"+productCategory.getProductCategorySalePriceThree());
            	
            }
            Log.d("productCategoryList SnapSyncserviceCategorylist", "SnapSyncservice Multiple list");
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
            categorySyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        }
        else if(taskCode == APPVERSION_TASKCODE) {
	        List<AppVersion> appVersionList = (List<AppVersion>) responseList;   
	        Log.d("TAG", "appVersionList--------->"+appVersionList.size());
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
	        Log.d("TAG", "appVersionList--------->Service invoked");
	        
	    }  
        
        else if(taskCode == GETSYNC_ORDER_TASKCODE) {
            List<Order> orderList = (List<Order>) responseList;
            for(Order order : orderList) {
                if(order.getDistributorID() != null)
                    order.setDistributorsId(order.getDistributorID().getDistributorId());
            }
            OrderSyncRequest ordersSyncRequest = new OrderSyncRequest();
            ordersSyncRequest.setRequestFormat(RequestFormat.JSON);
            ordersSyncRequest.setRequestMethod(RequestMethod.POST);
            ordersSyncRequest.setAccessToken(accessToken);
            ordersSyncRequest.setStoreId(storeId);
            ordersSyncRequest.setDeviceId(deviceId);
            ordersSyncRequest.setOrderList(orderList);
            ServiceRequest serviceRequest = new ServiceRequest(ordersSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.PO_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THIRTEEN);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            orderSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
            Request request = new Request();
            request.setRequestFormat(RequestFormat.MULTI_PART);
            request.setRequestMethod(RequestMethod.POST);
            HashMap<String, String> requestParamMap = new HashMap<String, String>();
            requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
            requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
            for(Order order : orderList) {
                Bitmap bitmap = SnapCommonUtils.getImageBitmap(order);
                if(bitmap != null) {
                    requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID, order.getOrderNumber()+"");
                    request.setBitmap(bitmap);
                    request.setRequestParams(requestParamMap);
                    ServiceRequest imageServiceRequest = new ServiceRequest(request, this);
                    imageServiceRequest.setMethod(SnapToolkitConstants.UPLOAD_METHOD);
                    imageServiceRequest.setPath(SnapToolkitConstants.PO_PATH);
                    imageServiceRequest.setResponsibleClass(ResponseContainer.class);
                    imageServiceRequest.setRequestCode(RequestCodes.REQUEST_CODE_EIGHTEEN);
                    serviceThread = new ServiceThread(this, this, false);
                    serviceThread.execute(imageServiceRequest);
                }
            }
        }else if(taskCode == GETSYNC_ORDERDETAILS_TASKCODE) {
            List<OrderDetails> orderDetailsList = (List<OrderDetails>) responseList;
            for(OrderDetails orderDetails : orderDetailsList) {
                if(orderDetails.getOrder() != null)
                    orderDetails.setOrderId(orderDetails.getOrder().getOrderNumber());
                if(orderDetails.getProductSkuID() != null)
                    orderDetails.setProductSkuCode(orderDetails.getProductSkuID().getProductSkuCode());
            }
            OrderDetailsSyncRequest ordersDetailsSyncRequest = new OrderDetailsSyncRequest();
            ordersDetailsSyncRequest.setRequestFormat(RequestFormat.JSON);
            ordersDetailsSyncRequest.setRequestMethod(RequestMethod.POST);
            ordersDetailsSyncRequest.setAccessToken(accessToken);
            ordersDetailsSyncRequest.setStoreId(storeId);
            ordersDetailsSyncRequest.setDeviceId(deviceId);
            ordersDetailsSyncRequest.setOrderDetailsList(orderDetailsList);
            ServiceRequest serviceRequest = new ServiceRequest(ordersDetailsSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.PODETAILS_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_FOURTEEN);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            orderDetailsSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GETSYNC_DELETE_TASKCODE) {
            List<DeletedRecords> deleteRecordsList = (List<DeletedRecords>) responseList;
            List<String> syncDeletedRecordsList = new ArrayList<String>();
            TableType tableType = deleteRecordsList.get(0).getTableType();
            DeleteRecordsRequest deleteRecordsRequest = new DeleteRecordsRequest();
            deleteRecordsRequest.setRequestFormat(RequestFormat.JSON);
            deleteRecordsRequest.setRequestMethod(RequestMethod.DELETE);
            deleteRecordsRequest.setAccessToken(accessToken);
            deleteRecordsRequest.setStoreId(storeId);
            deleteRecordsRequest.setDeviceId(deviceId);
            for(DeletedRecords deletedRecord : deleteRecordsList) {
                if(deletedRecord.getTableType().ordinal() != tableType.ordinal()) {
                    deleteRecordsRequest.setDeleteRecordsList(syncDeletedRecordsList);
                    ServiceRequest serviceRequest = new ServiceRequest(deleteRecordsRequest, this);
                    serviceRequest.setMethod(tableType.getTableType());
                    serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
                    serviceRequest.setResponsibleClass(ResponseContainer.class);
                    serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_NINETEEN);
                    ServiceThread serviceThread = new ServiceThread(this, this, false);
                    serviceThread.execute(serviceRequest);
                    tableType = deletedRecord.getTableType();
                    syncDeletedRecordsList = new ArrayList<String>();
                    syncDeletedRecordsList.add(deletedRecord.getRecordId());
                } else {
                    Log.d("","added deleted record "+deletedRecord.getRecordId());
                    syncDeletedRecordsList.add(deletedRecord.getRecordId());
                }
            }
            deleteRecordsRequest.setDeleteRecordsList(syncDeletedRecordsList);
            ServiceRequest serviceRequest = new ServiceRequest(deleteRecordsRequest, this);
            serviceRequest.setMethod(tableType.getTableType());
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_NINETEEN);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            serviceThread.execute(serviceRequest);
        } else if(taskCode == GET_SYNC_CUSTOMER_PAYMENTS_TASKCODE) {
            List<CustomerPayment> customerPaymentList = (List<CustomerPayment>) responseList;
            CustomerPaymentSyncRequest customerPaymentSyncRequest = new CustomerPaymentSyncRequest();
            customerPaymentSyncRequest.setCustomerPaymentList(customerPaymentList);
            customerPaymentSyncRequest.setRequestFormat(RequestFormat.JSON);
            customerPaymentSyncRequest.setRequestMethod(RequestMethod.POST);
            customerPaymentSyncRequest.setAccessToken(accessToken);
            customerPaymentSyncRequest.setStoreId(storeId);
            customerPaymentSyncRequest.setDeviceId(deviceId);
            ServiceRequest serviceRequest = new ServiceRequest(customerPaymentSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.CUSTOMERPAYMENTS_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYTHREE);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            customerPaymentSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
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
            ServiceRequest serviceRequest = new ServiceRequest(receiveItemsRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.RECEIVE_ITEMS_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYFIVE);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            receivedItemsSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
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
            ServiceRequest serviceRequest = new ServiceRequest(invoiceSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.INVOICE_LIST_SYNC_METHOD);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYSIX);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            invoiceSyncTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            serviceThread.execute(serviceRequest);
        } else if (GETSYNC_DISTRIBUTOR_PRODUCT_MAP_TASKCODE == taskCode) {
            DistributorProductMapSyncRequest distributorProductMapSyncRequest = new DistributorProductMapSyncRequest();
            distributorProductMapSyncRequest.setDistributorProductMapList((List<DistributorProductMap>) responseList);
            distributorProductMapSyncRequest.setRequestFormat(RequestFormat.JSON);
            distributorProductMapSyncRequest.setRequestMethod(RequestMethod.POST);
            distributorProductMapSyncRequest.setAccessToken(accessToken);
            distributorProductMapSyncRequest.setStoreId(storeId);
            distributorProductMapSyncRequest.setDeviceId(deviceId);
            ServiceRequest serviceRequest = new ServiceRequest(distributorProductMapSyncRequest, this);
            serviceRequest.setMethod(SnapToolkitConstants.RETRIEVE_DISTRIBUTOR_PRODUCT_MAP);
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYEIGHT);
            ServiceThread serviceThread = new ServiceThread(this, this, false);
            distributorProductMapTimestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime()); 
            serviceThread.execute(serviceRequest);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        Log.e(SnapSyncService.class.getName(), errorMessage);
    }

    @Override
    public void updateDateValues(String startDate, String EndDate, int taskCode) {
        if (GETSYNC_BILLITEMS_TASKCODE == taskCode) {
            billItemEndTimestamp = EndDate;
            billItemStartTimestamp = startDate;
        } else if (GETSYNC_PRODUCTS_TASKCODE == taskCode) {
            productEndTimestamp = EndDate;
            productStartTimestamp = startDate;
        } else if (GETSYNC_TRANSACTIONS_TASKCODE == taskCode) {
            transactionEndTimestamp = EndDate;
            transactionStartTimestamp = startDate;
        } else if (GETSYNC_INVENTORY_TASKCODE == taskCode) {
            inventoryEndTimestamp = EndDate;
            inventoryStartTimestamp = startDate;
        } else if (GETSYNC_INVENTORYBATCH_TASKCODE == taskCode) {
            inventoryBatchStartTimestamp = startDate;
            inventoryBatchEndTimestamp = EndDate;
        }
    }

    private String incByFiveDays(String currentDateString) {
        try {
            date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(currentDateString);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + SnapToolkitConstants.CHUNK_SIZE);
            return new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(calendar.getTime());
        } catch (Exception e) {
            Log.d(SnapSyncService.class.getName(), "inside inc month by one");
            e.printStackTrace();
            return currentDateString;
        }
    }

}

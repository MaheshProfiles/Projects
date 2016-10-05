package com.snapbizz.snaptoolkit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.DeleteRecordsRequest;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductCategoryContainer;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.domains.VatResponse;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.services.ServiceRequest;
import com.snapbizz.snaptoolkit.services.ServiceThread;

public class SnapDBUtils extends net.sqlcipher.database.SQLiteOpenHelper implements OnServiceCompleteListener {
    private static String DB_PATH = "";
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private String databaseName;
    private boolean isClosed;
    private static final String TAG = SnapDBUtils.class.getSimpleName();
    private static String XTRA_PRODUCTS_CAT_NAME = "Xtra Products";
    private static long IS_QUICK_ADD_CAT = 1;
    private static String DEFAULT_TIMESTAMP = "2015-07-31 00:00:00";
    
    public SnapDBUtils(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        this.databaseName = databaseName;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;
        SQLiteDatabase.loadLibs(context);
    }

    public void createDataBaseFromAssets() throws IOException {
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            mDataBase = this.getReadableDatabase(mContext.getString(R.string.passkey));
            this.close();
            try {
                copyDataBaseFromAssets();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        } else {
            try {
                mDataBase = this.getReadableDatabase(mContext.getString(R.string.passkey));
                this.close();
            } catch(Exception e) {
                this.close();
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + databaseName);
        return dbFile.exists();
    }

    private void copyDataBaseFromAssets() throws IOException {
        InputStream mInput = mContext.getAssets().open(databaseName);
        String outFileName = DB_PATH + databaseName;
        OutputStream mOutput = new FileOutputStream(outFileName);
        OutputStream mSyncOutput = new FileOutputStream(DB_PATH+SnapToolkitConstants.SYNC_DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
            mSyncOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mSyncOutput.flush();
        mSyncOutput.close();
        mInput.close();
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(!isClosed) {
            db.close();
            isClosed = true;
        }
        Log.d("","upgrading old " + oldVersion + " new version " + newVersion);
        switch (oldVersion) {
	        case 1:
	            upgradeVersion1(mDataBase, oldVersion, newVersion);
	            break;
	        case 2:
	            upgradeVersion2(mDataBase, oldVersion, newVersion);
	            break;
	        case 3:
	            upgradeVersion3(mDataBase, oldVersion, newVersion);
	            break;
	        case 4:
	            upgradeVersion4(mDataBase, oldVersion, newVersion);
	            break;
	        case 5:
	            upgradeVersion5(mDataBase, oldVersion, newVersion);
	            break;
	        case 6:
	            upgradeVersion6(mDataBase, oldVersion, newVersion);
	            break;
	        case 7:
	            upgradeVersion7(mDataBase, oldVersion, newVersion);
	            break;
	        case 8:
	            upgradeVersion8(mDataBase, oldVersion, newVersion);
	            break;
	        case 9:
	        	upgradeVersion9(mDataBase, oldVersion, newVersion);
	        	break;
	        case 10:
	        	upgradeVersion10(mDataBase, oldVersion, newVersion);
	        	break;
	        case 11:
	        	upgradeVersion11(mDataBase, oldVersion, newVersion);
	        	break;
	        case 12:
	        	upgradeVersion12(mDataBase, oldVersion, newVersion);
	        	break;
	        case 13:
	        	upgradeVersion13(mDataBase, oldVersion, newVersion);
	        	break;
        }
    }

    public void upgradeVersion13(SQLiteDatabase db, int oldVersion, int newVersion) {
     	try {
     		if (!SnapCommonUtils.getDatabaseHelper(mContext).getDownloadSyncTimestampDao().isTableExists()){
     			SnapCommonUtils.getDatabaseHelper(mContext).createTableSyncTimestamp();
     			SnapSharedUtils.storeProductSkuListUpdate(false, mContext);
     		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
     	
    	onUpgrade(db, ++oldVersion, newVersion);
    }

    public void upgradeVersion12(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try {
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE product_sku ADD 'sku_saleprice_two' FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE product_sku ADD 'sku_saleprice_three' FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductCategoryDao().executeRaw("ALTER TABLE product_category ADD `sku_saleprice` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductCategoryDao().executeRaw("ALTER TABLE product_category ADD `sku_saleprice_two` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductCategoryDao().executeRaw("ALTER TABLE product_category ADD `sku_saleprice_three` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductCategoryDao().executeRaw("ALTER TABLE product_category ADD `profit_margin` INTEGER");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductCategoryDao().executeRaw("ALTER TABLE product_category ADD `lastmodified_timestamp` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("ALTER TABLE campaigns ADD `localImageURL` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("ALTER TABLE campaigns ADD `serverImageURL` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("ALTER TABLE campaigns ADD `download_id` INTEGER");
            SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("ALTER TABLE campaigns ADD `image_retry` SMALLINT default 0");
            //SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("UPDATE campaigns SET image_uid = campaign_code|| '.jpg' ");
            renameOldImages();
            
            /*Reseting the sync timestap to july 30 due to 116 sync issue (upload of table when there is more than
             * 100 rows had issues)*/
            
             SnapSharedUtils.storeLastTransactionSyncTimestamp(SnapToolkitConstants.OLDEST_SYNC_DATE, mContext);
			 SnapSharedUtils.storeLastBillItemSyncTimestamp(SnapToolkitConstants.OLDEST_SYNC_DATE, mContext);
			 SnapSharedUtils.storeLastProductSkuSyncTimestamp(SnapToolkitConstants.OLDEST_SYNC_DATE, mContext);
			 SnapSharedUtils.storeLastInventorySkuSyncTimestamp(SnapToolkitConstants.OLDEST_SYNC_DATE, mContext);
			 SnapSharedUtils.storeLastInventoryBatchSyncTimestamp(SnapToolkitConstants.OLDEST_SYNC_DATE, mContext);
			 
    	} catch (SQLException e) {
    		Log.d(TAG, "exception caught inside upgradeVersion10");
    		e.printStackTrace();
    	}
    	onUpgrade(db, ++oldVersion, newVersion);
    }
    public void upgradeVersion11(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try {
    		SnapCommonUtils.getDatabaseHelper(mContext).getSettingsDao().executeRaw("CREATE TABLE `settings` (`settings_id` INTEGER PRIMARY KEY AUTOINCREMENT, `store_name` VARCHAR, `store_address` VARCHAR , `contact_number` VARCHAR , `store_tin_number` VARCHAR , `store_city` VARCHAR ,  `footer_1` VARCHAR ,`footer_2` VARCHAR ,`footer_3` VARCHAR ,`show_savings` SMALLINT, `sort_order` SMALLINT,`roundoff_status` SMALLINT,`pin_number` VARCHAR)");
    		SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().executeRaw("ALTER TABLE `transactions` ADD `transaction_start_timestamp` VARCHAR");
    		SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().executeRaw("ALTER TABLE `transaction_details` ADD `current_stock` FLOAT");
    		SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("ALTER TABLE campaigns ADD `has_image` SMALLINT default 0");
    		SnapSharedUtils.storeProductSkuListUpdate(false, mContext);
    	} catch (SQLException e) {
    		Log.d(TAG, "exception caught inside upgradeVersion10");
    		e.printStackTrace();
    	}
    	onUpgrade(db, ++oldVersion, newVersion);
    }
    
    public void upgradeVersion10(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try {
    		SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("CREATE TABLE `campaigns` (`campaign_id` INTEGER PRIMARY KEY AUTOINCREMENT,`id` INTEGER ,`campaign_name` VARCHAR , `campaign_type` VARCHAR , `image_uid` VARCHAR , `campaign_code` VARCHAR , `company` VARCHAR , `start_date` VARCHAR , `end_date` VARCHAR )");
    		SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE distributor_product_map ADD `lastmodified_timestamp` VARCHAR");
    	} catch (SQLException e) {
    		Log.d(TAG, "exception caught inside upgradeVersion10");
    		e.printStackTrace();
    	}
    	onUpgrade(db, ++oldVersion, newVersion);
    }
    
    public void upgradeVersion9(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try {
    		SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("CREATE TABLE `campaigns` (`campaign_id` INTEGER PRIMARY KEY AUTOINCREMENT,`id` INTEGER ,`campaign_name` VARCHAR , `campaign_type` VARCHAR , `image_uid` VARCHAR , `campaign_code` VARCHAR , `company` VARCHAR , `start_date` VARCHAR , `end_date` VARCHAR )");
    		SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE distributor_product_map ADD `lastmodified_timestamp` VARCHAR");
    	} catch (SQLException e) {
    		Log.d(TAG, "exception caught inside upgradeVersion10");
    		e.printStackTrace();
    	}
    	SnapSharedUtils.storeProductSkuListUpdate(false,mContext);
    	onUpgrade(db, ++oldVersion, newVersion);
    }
    
    public void upgradeVersion8(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
        	SnapCommonUtils.getDatabaseHelper(mContext).getCampaignsDao().executeRaw("CREATE TABLE `campaigns` (`campaign_id` INTEGER PRIMARY KEY AUTOINCREMENT,`id` INTEGER ,`campaign_name` VARCHAR , `campaign_type` VARCHAR , `image_uid` VARCHAR , `campaign_code` VARCHAR , `company` VARCHAR , `start_date` VARCHAR , `end_date` VARCHAR )");
    		SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE distributor_product_map ADD `lastmodified_timestamp` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("UPDATE inventory_sku SET inventory_sku_quantity = 0 where inventory_sku_quantity<0");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TRIGGER inventory_qty_trigger AFTER UPDATE OF inventory_sku_quantity ON inventory_sku BEGIN UPDATE inventory_sku SET inventory_sku_quantity = 0 WHERE inventory_sku_id = NEW.inventory_sku_id AND NEW.inventory_sku_quantity < 0; END;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }
    
    private void upgradeVersion7(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String[] stateName = mContext.getResources().getStringArray(R.array.state_names);
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TABLE `invoice` (`invoice_id` INTEGER PRIMARY KEY AUTOINCREMENT,`customer_id` INTEGER , `transaction_type` VARCHAR , `transaction_timestamp` VARCHAR , `lastmodified_timestamp` VARCHAR , `total_amount` DOUBLE PRECISION , `is_visible` SMALLINT , `total_discount` FLOAT , `total_qty` INTEGER , `total_savings` FLOAT,`vat` FLOAT, `transaction_id` INTEGER,  `pending_payment` FLOAT, `is_paid` SMALLINT, FOREIGN KEY (`customer_id`) REFERENCES `customer`(`customer_id`))");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TABLE 'received_items' ('received_items_id' INTEGER PRIMARY KEY AUTOINCREMENT, received_qty INTEGER, purchase_price FLOAT, vat_rate FLOAT, vat_amount FLOAT, receive_date VARCHAR, `lastmodified_timestamp` VARCHAR, invoice_number VARCHAR, sku_id VARCHAR, FOREIGN KEY ('sku_id') REFERENCES 'product_sku'('sku_id'))");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TABLE 'state' ('state_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' VARCHAR)");
            for (String string : stateName) {
                SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw(" INTO state (name) VALUES ('" + string + "')");
            }
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TABLE 'vat' ('vat_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'state_id' INTEGER, 'vat_value' FLOAT, 'subcategory_id' INTEGER, FOREIGN KEY ('subcategory_id') REFERENCES 'product_category'('product_category_id'), FOREIGN KEY ('state_id') REFERENCES 'state'('state_id'))");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE product_sku ADD 'vat' FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getBillItemDao().executeRaw("ALTER TABLE transaction_details ADD 'vat_rate' FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getBillItemDao().executeRaw("ALTER TABLE transaction_details ADD 'vat_amount' FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().executeRaw("ALTER TABLE transactions ADD 'vat' FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().executeRaw("ALTER TABLE transactions ADD 'is_invoice' SMALLINT");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("UPDATE product_sku SET vat = (SELECT inventory_sku.tax_rate FROM inventory_sku WHERE inventory_sku.inventory_sku_id = product_sku.sku_id)");
            AssetManager am = mContext.getAssets();
            InputStream is = am.open("vatjson.txt");
            VatResponse vatDetails = ( VatResponse) JsonParser.parseJson(is, VatResponse.class);
            vatDetails.getVatResponseList();
            
            for (int i = 0; i < vatDetails.getVatResponseList().size(); i++) {
            	Iterator<Entry<String, String>> iterator = vatDetails.getVatResponseList().get(i).entrySet().iterator();
            	String subcategoryId=vatDetails.getVatResponseList().get(i).get("Sub-Category");

            	while (iterator.hasNext()) {
            		Map.Entry pairs = (Map.Entry)iterator.next();
            		if (!((String)pairs.getKey()).equals("Sub-Category")) {
            			SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao()
            						   .executeRaw("INSERT INTO vat (vat_value,state_id,subcategory_id) VALUES ('" + pairs.getValue() + "','" + pairs.getKey() + "','"+subcategoryId + "')");
            		}
            	}
            }

            UpdateBuilder<Transaction, Integer> transactionUpdateBuilder = SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().updateBuilder();
            transactionUpdateBuilder.updateColumnValue("transaction_type", TransactionType.MEMO);
            transactionUpdateBuilder.where().eq("transaction_type", TransactionType.BILL);
            transactionUpdateBuilder.update();
            UpdateBuilder<Transaction, Integer> transactionUpdateBuilder2 = SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().updateBuilder();
            transactionUpdateBuilder2.updateColumnValue("transaction_type", TransactionType.DELIVERY_MEMO);
            transactionUpdateBuilder2.where().eq("transaction_type", TransactionType.DELIVERY_NOTE);
            transactionUpdateBuilder2.update();
          
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, 2);
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.YEAR, 2014);
            SnapSharedUtils.storeLastTransactionSyncTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(calendar.getTime()), SnapCommonUtils.getSnapContext(mContext));
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }

    private void upgradeVersion6(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE product_sku ADD `category_name` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE product_sku ADD `subcategory_name` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE product_sku ADD `_id` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE inventory_sku ADD `_id` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE brand ADD `is_mystore` SMALLINT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE transaction_details ADD `visibility_tag` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE product_sku set subcategory_name = (select product_category_name from product_category where product_category.product_category_id = product_sku.sku_subcategory_id limit 1), category_name = (select product_category_name from product_category where product_category.product_category_id = (select product_parentcategory_id from product_category where product_category.product_category_id = product_sku.sku_subcategory_id limit 1) limit 1), lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"'");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE brand set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime())+"', is_mystore = '1' where brand_id in (select brand.brand_id from brand inner join product_sku on product_sku.brand_id = brand.brand_id inner join inventory_sku on inventory_sku.inventory_sku_id = product_sku.sku_id)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE TABLE `distributor_product_map` (`distributor_product_slno` INTEGER PRIMARY KEY AUTOINCREMENT, `distributor_id` INTEGER, `sku_id` VARCHAR, FOREIGN KEY (`distributor_id`) REFERENCES `distributor`(`distributor_id`), FOREIGN KEY (`sku_id`) REFERENCES `product_sku`(`sku_id`))");
            SnapCommonUtils.getDatabaseHelper(mContext).getOrderDetailsDao().executeRaw("DROP TRIGGER sku_codeupdate_trigger");
            SnapCommonUtils.getDatabaseHelper(mContext).getOrderDetailsDao().executeRaw("CREATE TRIGGER sku_codeupdate_trigger AFTER UPDATE OF sku_id ON product_sku BEGIN update inventory_sku set inventory_sku_id = new.sku_id, lastmodified_timestamp = new.lastmodified_timestamp where inventory_sku_id = old.sku_id; update inventory_batch set sku_id = new.sku_id, lastmodified_timestamp = new.lastmodified_timestamp where sku_id = old.sku_id; UPDATE order_details SET product_sku_id = new.sku_id, lastmodified_timestamp = new.lastmodified_timestamp WHERE product_sku_id = old.sku_id; END;");
            List<ProductSku> productSkuList = SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().queryBuilder().where().like("sku_id", "snaplocal%").query();
            List<DeletedRecords> deleteRecordsList = new ArrayList<DeletedRecords>();
            for (ProductSku productSku : productSkuList) {
                DeletedRecords deletedRecords = new DeletedRecords();
                deletedRecords.setRecordId(productSku.getProductSkuCode());
                deletedRecords.setTableType(TableType.PRODUCT_SKU);
                deleteRecordsList.add(deletedRecords);
            }
            List<String> syncDeletedRecordsList = new ArrayList<String>();
            TableType tableType = deleteRecordsList.get(0).getTableType();
            DeleteRecordsRequest deleteRecordsRequest = new DeleteRecordsRequest();
            deleteRecordsRequest.setRequestFormat(RequestFormat.JSON);
            deleteRecordsRequest.setRequestMethod(RequestMethod.DELETE);
            deleteRecordsRequest.setAccessToken(SnapSharedUtils.getStoreAuthKey(mContext));
            deleteRecordsRequest.setStoreId(SnapSharedUtils.getStoreId(mContext));
            deleteRecordsRequest.setDeviceId(SnapSharedUtils.getDeviceId(mContext));
            for (DeletedRecords deletedRecord : deleteRecordsList) {
                if (deletedRecord.getTableType().ordinal() != tableType.ordinal()) {
                    deleteRecordsRequest.setDeleteRecordsList(syncDeletedRecordsList);
                    ServiceRequest serviceRequest = new ServiceRequest(deleteRecordsRequest, mContext);
                    serviceRequest.setMethod(tableType.getTableType());
                    serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
                    serviceRequest.setResponsibleClass(ResponseContainer.class);
                    serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_NINETEEN);
                    ServiceThread serviceThread = new ServiceThread(mContext, this, false);
                    serviceThread.execute(serviceRequest);
                    tableType = deletedRecord.getTableType();
                    syncDeletedRecordsList = new ArrayList<String>();
                    syncDeletedRecordsList.add(deletedRecord.getDeletedRecordId()+"");
                } else {
                    syncDeletedRecordsList.add(deletedRecord.getDeletedRecordId()+"");
                }
            }
            deleteRecordsRequest.setDeleteRecordsList(syncDeletedRecordsList);
            ServiceRequest serviceRequest = new ServiceRequest(deleteRecordsRequest, mContext);
            serviceRequest.setMethod(tableType.getTableType());
            serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
            serviceRequest.setResponsibleClass(ResponseContainer.class);
            serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_NINETEEN);
            ServiceThread serviceThread = new ServiceThread(mContext, this, false);
            serviceThread.execute(serviceRequest);
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE product_sku set sku_id = 'snaplocal' || rowid where sku_id like ('snaplocal%')");            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }
    
    private void upgradeVersion5(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), mContext);
            SnapSharedUtils.storeBackupDbName(mContext);
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE TABLE `customer_payment` (`customer_payment_id` INTEGER PRIMARY KEY AUTOINCREMENT, `customer_id` INTEGER , `payment_amount` FLOAT, `lastmodified_timestamp` VARCHAR, payment_date VARCHAR, payment_type VARCHAR, FOREIGN KEY (`customer_id`) REFERENCES `customer`(`customer_id`))");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE transactions ADD `pending_payment` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `last_payment_date` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `last_purchase_date` VARCHAR");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `last_payment_amount` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `last_purchase_amount` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `amount_paid` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `avg_visits` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE customer ADD `avg_purchase` FLOAT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE customer set last_purchase_date = (select transaction_timestamp from transactions where transactions.customer_id=customer.customer_id order by transaction_timestamp DESC LIMIT 1)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE customer set last_purchase_amount = (select total_amount - total_discount - total_savings from transactions where transactions.customer_id=customer.customer_id order by transaction_timestamp DESC LIMIT 1)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE customer set avg_visits = (select count(*) / ( select count(DISTINCT SUBSTR(transaction_timestamp, 0, 8)) from transactions where transactions.customer_id = customer.customer_id) from transactions where transactions.customer_id = customer.customer_id)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE customer set avg_purchase = (select Sum(total_amount - total_discount - total_savings) / ( select count(DISTINCT SUBSTR(transaction_timestamp, 0, 8)) from transactions where transactions.customer_id = customer.customer_id) from transactions where transactions.customer_id = customer.customer_id)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE product_sku set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"' where is_quickadd_product = '1'");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("Delete from inventory_batch where sku_id = 'snaplocal'");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("Delete from inventory_sku where inventory_sku_id = 'snaplocal'");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("Delete from product_sku where sku_id = 'snaplocal'");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("DROP TRIGGER inventory_batch_update_trigger");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE TRIGGER inventory_batch_update_trigger AFTER UPDATE OF sku_available_qty ON inventory_batch BEGIN update inventory_sku set inventory_sku_quantity = inventory_sku_quantity + new.sku_available_qty - old.sku_available_qty, lastmodified_timestamp = new.lastmodified_timestamp where inventory_sku_id = new.sku_id; END;");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE product_sku set is_quickadd_product = 1, lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"' where sku_id like ('snaploose%')");
            List<Customer> customerList = SnapCommonUtils.getDatabaseHelper(mContext).getCustomerDao().queryForAll();
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("DROP TABLE `customer`");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE TABLE `customer` (`state` VARCHAR , `city` VARCHAR , `pincode` VARCHAR , `customer_address` VARCHAR , `lastmodified_timestamp` VARCHAR , `membership_date` VARCHAR , `customer_name` VARCHAR , `customer_phone` VARCHAR , `customer_email` VARCHAR , `customer_id` INTEGER PRIMARY KEY AUTOINCREMENT , `credit_limit` FLOAT , `amount_due` FLOAT, `amount_paid` FLOAT, `last_purchase_date` VARCHAR, `last_payment_date` VARCHAR, `last_purchase_amount` FLOAT, `last_payment_amount` FLOAT, `avg_visits` FLOAT, `avg_purchase` FLOAT, UNIQUE (`customer_phone`))");    
            for (int i = 0; i < customerList.size(); i++) {
                Customer customer = customerList.get(i);
                try {
                    SnapCommonUtils.getDatabaseHelper(mContext).getCustomerDao().create(customer);
                } catch(Exception e) {
                    Customer createdCustomer;
                    for (int j = 0; j < i; j++) {
                        createdCustomer = customerList.get(j);
                        if(createdCustomer.getCustomerPhoneNumber().equals(customer.getCustomerPhoneNumber())) {
                            List<Transaction> transactionList = SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().queryForEq("customer_id", customer.getCustomerId());
                            for (Transaction transaction : transactionList) {
                                transaction.setCustomer(createdCustomer);
                                SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().update(transaction);
                            }
                        }
                    }
                }
            }
            QueryBuilder<InventorySku, Integer> inventorySkuQueryBuilder = SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().queryBuilder();
            List<ProductSku> productSkuList = SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().queryBuilder().join(inventorySkuQueryBuilder).query();
            for (ProductSku productSku : productSkuList) {
                SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().update(productSku);
            }
        } catch (SQLException e) {
             e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }

    private void upgradeVersion4(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE product_sku ADD `is_quickadd_product` SMALLINT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("ALTER TABLE product_category ADD `is_quickadd_category` SMALLINT");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE INDEX quickadd_index ON product_sku (`is_quickadd_product`)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE INDEX sku_name_index ON product_sku (`sku_name`)");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("UPDATE product_sku set is_quickadd_product = 1 where sku_id like ('"+SnapToolkitConstants.SNAP_LOOSE_PREFIX_KEY+"%')");
        } catch(Exception e) {
            e.printStackTrace();
        }
        updateCategory();
        try {
            List<Brand> brandList = SnapCommonUtils.getDatabaseHelper(mContext).getBrandDao().queryForAll();
            for(Brand brand : brandList) {
                if(brand.isHasImage() && brand.isGDB())
                    brand.setBrandImageUrl("brands/" + brand.getBrandName().toLowerCase() + ".jpg");
                SnapCommonUtils.getDatabaseHelper(mContext).getBrandDao().update(brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }

    private void updateCategory() {
        Log.d("","update category");
        AssetManager am = mContext.getAssets();
        try {
            InputStream is = am.open("category.txt");
            ProductCategoryContainer productCategoryContainer = (ProductCategoryContainer) JsonParser.parseJson(is, ProductCategoryContainer.class);
            String categoryId = "";
            for(ProductCategory productCategory : productCategoryContainer.getProductCategoryList()) {
                categoryId += productCategory.getCategoryId()+",";
            }
            categoryId = categoryId.substring(0, categoryId.length() - 1);
            try {
                SnapCommonUtils.getDatabaseHelper(mContext).getProductCategoryDao().executeRaw("update product_category set is_quickadd_category = 1 where product_category_id IN ("+categoryId+")");
                Log.d("","update category success");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    
    
    

    private void upgradeVersion3(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("update product_sku set sku_subcategory_id = 95 where sku_subcategory_id = 0 or sku_subcategory_id = 102 or sku_subcategory_id IS NULL");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("update product_sku set brand_id = 1302 where brand_id = 1179 or brand_id IS NULL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            GenericRawResults<InventorySku> inventorySkuList = SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().queryRaw("select inventory_sku.* from inventory_sku left outer join inventory_batch on inventory_sku.inventory_sku_id = inventory_batch.sku_id where inventory_batch.sku_id IS NULL", SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().getRawRowMapper());
            for (InventorySku inventorySku : inventorySkuList) {
                InventoryBatch inventoryBatch;
                try {
                    inventoryBatch = new InventoryBatch(inventorySku.getProductSku().getProductSkuCode(), inventorySku.getProductSku().getProductSkuMrp(), inventorySku.getProductSku().getProductSkuSalePrice(), inventorySku.getPurchasePrice(), new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).parse(inventorySku.getTimestamp()), null, inventorySku.getQuantity(), 0, inventorySku.getTaxRate());
                    SnapCommonUtils.getDatabaseHelper(mContext).getInventoryBatchDao().create(inventoryBatch);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        try {
            List<Brand> brandList = SnapCommonUtils.getDatabaseHelper(mContext).getBrandDao().queryForAll();
            for(Brand brand : brandList) {
                if (brand.isHasImage() && brand.isGDB())
                    brand.setBrandImageUrl("brands/snapbizz_brand_" + brand.getBrandName().toLowerCase().replaceAll(" ", "_")
                    												+ "_160x150.jpg");
                SnapCommonUtils.getDatabaseHelper(mContext).getBrandDao().update(brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }

    private void upgradeVersion2(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("update product_sku set sku_subcategory_id = 95 where sku_subcategory_id = 0 or sku_subcategory_id = 102");
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("update product_sku set brand_id = 1302 where brand_id = 1179");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Brand> brandList = SnapCommonUtils.getDatabaseHelper(mContext).getBrandDao().queryForAll();
            for(Brand brand : brandList) {
                if(brand.isHasImage() && brand.isGDB())
                    brand.setBrandImageUrl("brands/snapbizz_brand_" + brand.getBrandName().toLowerCase().replaceAll(" ", "_") + "_160x150.jpg");
                SnapCommonUtils.getDatabaseHelper(mContext).getBrandDao().update(brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }

    private void upgradeVersion1(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TABLE `customer_suggestions` (`suggestion_id` INTEGER PRIMARY KEY AUTOINCREMENT, `customer_id` INTEGER, `sku_id` VARCHAR, FOREIGN KEY (`customer_id`) REFERENCES `customer`(`customer_id`), FOREIGN KEY (`sku_id`) REFERENCES `product_sku`(`sku_id`))");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE TABLE `deleted_records` (`deleted_record_id` INTEGER PRIMARY KEY AUTOINCREMENT, `record_id` VARCHAR, `table_name` VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("ALTER TABLE `transaction_details` add `sku_name` VARCHAR");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().executeRaw("CREATE INDEX sku_index ON product_sku (`sku_id`)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SnapCommonUtils.getDatabaseHelper(mContext).getInventorySkuDao().executeRaw("CREATE INDEX inventory_index ON inventory_sku (`inventory_slno`)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<ProductSku> productSkuList = SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().queryForAll();
            ProductCategory category = new ProductCategory();
            category.setCategoryId(95);
            for(ProductSku productSku : productSkuList) {
                if(productSku.isHasImage() && productSku.isGDB())
                    productSku.setImageUrl("products/"+SnapCommonUtils.getHash("snapbizz_product_" + productSku.getProductSkuCode().toLowerCase().replaceAll(" ", "_") + "_160x150").toLowerCase() + ".jpg");
                SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().update(productSku);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<BillItem> billItemList = SnapCommonUtils.getDatabaseHelper(mContext).getBillItemDao().queryForAll();
            for(BillItem bilLitem : billItemList) {
                SnapCommonUtils.getDatabaseHelper(mContext).getProductSkuDao().refresh(bilLitem.getProductSku());
                if(bilLitem.getProductSku() != null)
                    bilLitem.setProductSkuName(bilLitem.getProductSku().getProductSkuName());
                SnapCommonUtils.getDatabaseHelper(mContext).getBillItemDao().update(bilLitem);
            }
        } catch (Exception e) {
        }
        onUpgrade(db, ++oldVersion, newVersion);
    }

    @Override
    public void onSuccess(ResponseContainer response) {
        
    }

    @Override
    public void onError(ResponseContainer response, RequestCodes requestCode) {
        
    }
    
    private void renameOldImages() {
    	 try {
    		 SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(SnapCommonUtils.getSnapContext(mContext));
    		 List<Campaigns> campaignsList = databaseHelper.getCampaignsDao().queryForAll();
    		 String oldImageName;
    		 String newImageName;
    		 boolean hasImage = false;
    		 int downloadId = 0;
    		 boolean imageRetry = false;
    		 Log.d("TAG", "Campaign Log-----  inside renameOldImages campaignsList="+campaignsList.size());
    		 for (Campaigns campaigns : campaignsList) {
    			 hasImage = false;
				 downloadId = 0;
				 imageRetry = false;
    			 oldImageName = SnapToolkitConstants.VISIBILITY_IMAGE_PATH + "/campaign/" + campaigns.getCode() + ".jpg";
    			 newImageName = SnapToolkitConstants.VISIBILITY_IMAGE_PATH + "/campaign/" + campaigns.getImage_uid();
    			 if (SnapCommonUtils.checkProductDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/" + campaigns.getCode() +".jpg",
    					 								 SnapCommonUtils.getSnapContext(mContext)) != null) {
    				 File file = new File(oldImageName);
    				 if (file.exists()) {
    					 File file2 = new File(newImageName);
    					 boolean success = file.renameTo(file2);
    					 if (success) {
    						 Log.d("TAG", "Campaign Log-----  inside renameOldImages success");
    						 hasImage = true;
    						 downloadId = 8;
    						 imageRetry = false;
    					
    					 } else {
    						 Log.d("TAG", "Campaign Log-----  inside renameOldImages fail");
    						 hasImage = false;
    						 downloadId = 0;
    						 imageRetry = true;
    					 }
    				 }
    			 
    			 }
    			 Log.d("TAG", "Campaign Log-----  inside renameOldImages DB Updateding="+hasImage);
    			 UpdateBuilder<Campaigns, Integer> updateBuilder = databaseHelper.getCampaignsDao().updateBuilder();
    			 updateBuilder.where().eq("id", campaigns.getId());
				 updateBuilder.updateColumnValue("has_image", hasImage);
				 updateBuilder.updateColumnValue("download_id", downloadId);
				 updateBuilder.updateColumnValue("image_retry", imageRetry);
				 updateBuilder.update(); 
				 Log.d("TAG", "Campaign Log-----  inside renameOldImages DB Updated");
    			 
    		 }
    		 
    	 } catch (Exception e) {
    		 Log.e("TAG","Error in changing image name",e);
    	 }
    	
    }
    
    public static void checkAndUpdateVat(Context context, SnapBizzDatabaseHelper dbHelper, boolean bForce) {
    	String stateId = SnapSharedUtils.getStoreStateId(context);
    	Log.d(TAG, "checkAndUpdateVat Called: "+(stateId != null ? stateId : "null"));
    	if (stateId != null && stateId.trim().equalsIgnoreCase("15")) {
        	if (!bForce && SnapSharedUtils.isMhVatUpdated(context))
        		return;
    		try {
    			// Effective Apr 2016, maharashtra vat is updated from 5% to 5.5%
    			dbHelper.getVatDao().queryRaw("update vat set vat_value=5.5 where vat_value=5.0 and state_id=15;");
    			dbHelper.getProductSkuDao().queryRaw("update product_sku set vat=5.5 where vat=5.0;");
    			SnapSharedUtils.storeMhVatUpdated(context, true);
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static boolean copyOrRestoreDB(String dbSourcePath, String dbDestinationPath, boolean bDeleteSource) {
    	File dbFile = new File(dbDestinationPath);
        File sourceFile = new File(dbSourcePath);
        try {
            File file = new File(SnapToolkitConstants.DB_EXTPATH);
            boolean success = true;
            if (!file.exists())
                success = file.mkdirs();
            if (success) {
            	FileChannel source = null;
				FileChannel destination = null;
				source = new FileInputStream(sourceFile).getChannel();
				destination = new FileOutputStream(dbFile).getChannel();
                if (source != null && destination != null) {
                	destination.transferFrom(source, 0, source.size());
	                source.close();
	                destination.close();
	                if (bDeleteSource)
	                	sourceFile.delete();
	                return true;
                } 
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addXtraProductQuickAddCategory(Context context, SnapBizzDatabaseHelper dbHelper, boolean bForce) {
    	if (!bForce && SnapSharedUtils.isXtraQuickProductAdded(context))
    		return;
		try {
			dbHelper.getProductCategoryDao().updateRaw("insert or ignore into product_category values(?,?,?,?,?,?,?,?,?)",
					String.valueOf(SnapToolkitConstants.OTHERS_CAT_ID), XTRA_PRODUCTS_CAT_NAME,
					String.valueOf(SnapToolkitConstants.XTRA_PRODUCTS_CAT_ID), String.valueOf(1),
					"0", "0", "0", "0", SnapToolkitConstants.OLDEST_CATAGORY_SYNC_DATE);
			SnapSharedUtils.storeXtraQuickProductAdded(context, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    public class LooseItemUpdate {
		@SerializedName("barCode")
    	public String barCode;
		@SerializedName("unitType")
    	public String unitType;
    }

    public static void checkAndUpdateLooseItemsUom(Context context, SnapBizzDatabaseHelper dbHelper, boolean bForce) {
    	if (!bForce && SnapSharedUtils.isLooseItemsUomUpdated(context))
    		return;

		try {
			InputStreamReader reader = new InputStreamReader(context.getAssets().open("updatebarcodesheet.json"));
			LooseItemUpdate[] items = new Gson().fromJson(reader, LooseItemUpdate[].class);
			for(LooseItemUpdate item : items)
				dbHelper.getProductSkuDao().updateRaw("update product_sku set sku_units = ? where sku_id = ?", item.unitType, item.barCode);
			SnapSharedUtils.storeLooseItemsUomUpdated(context, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
}
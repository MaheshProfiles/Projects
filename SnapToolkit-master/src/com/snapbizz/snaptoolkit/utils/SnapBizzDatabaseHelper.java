package com.snapbizz.snaptoolkit.utils;

import java.sql.SQLException;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.domains.CustomerSuggestions;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.domains.Product;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ReceiveItems;
import com.snapbizz.snaptoolkit.domains.Settings;
import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.domains.SyncTimestamp;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.domains.VAT;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class SnapBizzDatabaseHelper extends OrmLiteSqliteOpenHelper {

	protected Dao<DeletedRecords, Integer> deletedRecordsDao = null;
	protected Dao<CustomerSuggestions, Integer> customerSuggestionsDao = null;
	protected Dao<BillItem, Integer> billItemDao = null;
	protected Dao<Brand, Integer> brandDao = null;
	protected Dao<Customer, Integer> customerDao = null;
	protected Dao<Product, Integer> productDao = null;
	protected Dao<ProductSku, Integer> productSkuDao = null;
	protected Dao<Transaction, Integer> transactionDao = null;
	protected Dao<ProductCategory, Integer> productCategoryDao = null;
	protected Dao<InventorySku, Integer> inventorySkuDao = null;
	protected Dao<Distributor, Integer> distributorDao = null;
	protected Dao<Company, Integer> companyDao = null;
	protected Dao<DistributorBrandMap, Integer> distributorBrandMapDao = null;
	protected Dao<Order, Integer> orderDao = null;
	protected Dao<OrderDetails, Integer> orderDetailsDao = null;
	protected Dao<Payments, Integer> PaymentDao = null;
	protected Dao<CompanyDistributorMap, Integer> companyDistributorDao = null;
	protected Dao<CustomerPayment, Integer> customerPaymentDao = null;
	protected Dao<InventoryBatch, Integer> inventoryBatchDao = null;
	protected Dao<DistributorProductMap, Integer> distributorProductMapDao = null;
	protected Dao<State, Integer> stateDao = null;
	protected Dao<VAT, Integer> vatDao = null;
	protected Dao<Invoice, Integer> invoiceDao = null;
	protected Dao<SyncTimestamp, Integer> syncTimestampDao = null;
	protected RuntimeExceptionDao<VAT, Integer> vatRuntimeDao = null;
	protected RuntimeExceptionDao<State, Integer> stateRuntimeDao = null;
	protected Dao<ReceiveItems, Integer> receiveItemsDao = null;
	protected RuntimeExceptionDao<BillItem, Integer> billItemRuntimeDao = null;
	protected RuntimeExceptionDao<Brand, Integer> brandRuntimeDao = null;
	protected RuntimeExceptionDao<Customer, Integer> customerRuntimeDao = null;
	protected RuntimeExceptionDao<Product, Integer> productRuntimeDao = null;
	protected RuntimeExceptionDao<ProductSku, Integer> productSkuRuntimeDao = null;
	protected RuntimeExceptionDao<Transaction, Integer> transactionRuntimeDao = null;
	protected RuntimeExceptionDao<ProductCategory, Integer> productCategoryRuntimeDao = null;
	protected RuntimeExceptionDao<InventorySku, Integer> inventorySkuRuntimeDao = null;
	protected RuntimeExceptionDao<Distributor, Integer> distributorRuntimeDao = null;
	protected RuntimeExceptionDao<DistributorBrandMap, Integer> distributorBrandMapRuntimeDao = null;
	protected RuntimeExceptionDao<Order, Integer> orderRuntimeDao = null;
	protected RuntimeExceptionDao<Company, Integer> companyRuntimeDao = null;
	protected RuntimeExceptionDao<OrderDetails, Integer> orderDetailsRuntimeDao = null;
	protected RuntimeExceptionDao<Payments, Integer> PaymentRuntimeDao = null;
	protected RuntimeExceptionDao<CompanyDistributorMap, Integer> companyDistributorRuntimeDao = null;
	protected RuntimeExceptionDao<InventoryBatch, Integer> inventoryBatchRuntimeDao = null;
	protected RuntimeExceptionDao<DeletedRecords, Integer> deletedRecordsRuntimeDao = null;
	protected RuntimeExceptionDao<CustomerSuggestions, Integer> customerSuggestionsRuntimeDao = null;
	protected RuntimeExceptionDao<CustomerPayment, Integer> customerPaymentRuntimeDao = null;
	protected RuntimeExceptionDao<DistributorProductMap, Integer> distributorProductMapRuntimeDao = null;
	protected RuntimeExceptionDao<ReceiveItems, Integer> receiveItemsRuntimeExceptionDao = null;
	protected RuntimeExceptionDao<Invoice, Integer> invoiceRuntimeExceptionDao = null;
	protected Dao<Campaigns, Integer> campaignsDao = null;
	protected Dao<Settings, Integer> settingsDao = null;

	protected RuntimeExceptionDao<Campaigns, Integer> campaignRuntimeDao = null;
	protected RuntimeExceptionDao<Settings, Integer> settingsRuntimeDao = null;
	protected RuntimeExceptionDao<SyncTimestamp, Integer> syncTimestampRuntimeDao = null;
	protected Context context;

	public SnapBizzDatabaseHelper(Context context) {
		super(context, SnapToolkitConstants.DB_NAME, null,
				SnapToolkitConstants.DB_VERSION, R.raw.ormlite_config, context
						.getResources().getString(R.string.passkey));
		SQLiteDatabase.loadLibs(context);
		this.context = context;
	}

	public SnapBizzDatabaseHelper(Context context, String dbName) {
		super(context, dbName, null, SnapToolkitConstants.DB_VERSION,
				R.raw.ormlite_config, context.getResources().getString(
						R.string.passkey));
		SQLiteDatabase.loadLibs(context);
		this.context = context;
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {		
		// try {
		// // Log.i(ORMDatabaseHelper.class.getName(), "onCreate");
		// TableUtils.createTable(connectionSource, ProductSku.class);
		// TableUtils.createTable(connectionSource, Product.class);
		// TableUtils.createTable(connectionSource, ProductCategory.class);
		// TableUtils.createTable(connectionSource, InventorySku.class);
		// } catch (SQLException e) {
		// // Log.e(ORMDatabaseHelper.class.getName(), "Can't create database",
		// // e);
		// throw new RuntimeException(e);
		// }
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {

	}

	public Dao<DistributorProductMap, Integer> getDistributorProductMapDao()
			throws SQLException {
		if (distributorProductMapDao == null) {
			distributorProductMapDao = getDao(DistributorProductMap.class);
		}
		return distributorProductMapDao;
	}

	public Dao<ReceiveItems, Integer> getReceiveItemsDao() throws SQLException {
		if (null == receiveItemsDao) {
			receiveItemsDao = getDao(ReceiveItems.class);
		}
		return receiveItemsDao;
	}

	public Dao<Invoice, Integer> getInvoiceDao() throws SQLException {
		if (null == invoiceDao) {
			invoiceDao = getDao(Invoice.class);
		}
		return invoiceDao;
	}

	public Dao<DeletedRecords, Integer> getDeletedRecordsDao()
			throws SQLException {
		if (deletedRecordsDao == null) {
			deletedRecordsDao = getDao(DeletedRecords.class);
		}
		return deletedRecordsDao;
	}

	public Dao<CustomerSuggestions, Integer> getCustomerSuggestionsDao()
			throws SQLException {
		if (customerSuggestionsDao == null) {
			customerSuggestionsDao = getDao(CustomerSuggestions.class);
		}
		return customerSuggestionsDao;
	}

	public Dao<DistributorBrandMap, Integer> getDistributorBrandMapDao()
			throws SQLException {
		if (distributorBrandMapDao == null) {
			distributorBrandMapDao = getDao(DistributorBrandMap.class);
		}
		return distributorBrandMapDao;
	}

	public Dao<Distributor, Integer> getDistributorDao() throws SQLException {
		if (distributorDao == null) {
			distributorDao = getDao(Distributor.class);
		}
		return distributorDao;
	}

	public Dao<CompanyDistributorMap, Integer> getCompanyDistributorDao()
			throws SQLException {
		if (companyDistributorDao == null) {
			companyDistributorDao = getDao(CompanyDistributorMap.class);
		}
		return companyDistributorDao;
	}

	public Dao<BillItem, Integer> getBillItemDao() throws SQLException {
		if (billItemDao == null) {
			billItemDao = getDao(BillItem.class);
		}
		return billItemDao;
	}

	public Dao<Brand, Integer> getBrandDao() throws SQLException {
		if (brandDao == null) {
			brandDao = getDao(Brand.class);
		}
		return brandDao;
	}

	public Dao<Product, Integer> getProductDao() throws SQLException {
		if (productDao == null) {
			productDao = getDao(Product.class);
		}
		return productDao;
	}

	public Dao<ProductSku, Integer> getProductSkuDao() throws SQLException {
		if (productSkuDao == null) {
			productSkuDao = getDao(ProductSku.class);
		}
		return productSkuDao;
	}

	public Dao<Transaction, Integer> getTransactionDao() throws SQLException {
		if (transactionDao == null) {
			transactionDao = getDao(Transaction.class);
		}
		return transactionDao;
	}

	public Dao<Customer, Integer> getCustomerDao() throws SQLException {
		if (customerDao == null) {
			customerDao = getDao(Customer.class);
		}
		return customerDao;
	}

	public Dao<ProductCategory, Integer> getProductCategoryDao()
			throws SQLException {
		if (productCategoryDao == null) {
			productCategoryDao = getDao(ProductCategory.class);
		}
		return productCategoryDao;
	}

	public Dao<InventorySku, Integer> getInventorySkuDao() throws SQLException {
		if (inventorySkuDao == null) {
			inventorySkuDao = getDao(InventorySku.class);
		}
		return inventorySkuDao;
	}

	public Dao<Order, Integer> getOrderDao() throws SQLException {
		if (null == orderDao) {
			orderDao = getDao(Order.class);
		}
		return orderDao;
	}

	public Dao<OrderDetails, Integer> getOrderDetailsDao() throws SQLException {
		if (null == orderDetailsDao) {
			orderDetailsDao = getDao(OrderDetails.class);
		}
		return orderDetailsDao;
	}

	public Dao<Payments, Integer> getPaymentsDao() throws SQLException {
		if (null == PaymentDao) {
			PaymentDao = getDao(Payments.class);
		}
		return PaymentDao;
	}

	public Dao<Company, Integer> getCompanyDao() throws SQLException {
		if (null == companyDao) {
			companyDao = getDao(Company.class);
		}
		return companyDao;
	}

	public Dao<InventoryBatch, Integer> getInventoryBatchDao()
			throws SQLException {
		if (null == inventoryBatchDao) {
			inventoryBatchDao = getDao(InventoryBatch.class);
		}
		return inventoryBatchDao;
	}

	public Dao<CustomerPayment, Integer> getCustomerPaymentDao()
			throws SQLException {
		if (null == customerPaymentDao) {
			customerPaymentDao = getDao(CustomerPayment.class);
		}
		return customerPaymentDao;
	}

	public Dao<VAT, Integer> getVatDao() throws SQLException {
		if (null == vatDao) {
			vatDao = getDao(VAT.class);
		}
		return vatDao;
	}
	
	public Dao<SyncTimestamp, Integer> getDownloadSyncTimestampDao() throws SQLException {
		if (null == syncTimestampDao) {
			syncTimestampDao = getDao(SyncTimestamp.class);
		}
		return syncTimestampDao;
	}
	
	public RuntimeExceptionDao<SyncTimestamp, Integer> getDownloadSyncTimestampRuntimeDao()
			throws SQLException {
		if (syncTimestampRuntimeDao == null) {
			syncTimestampRuntimeDao = getRuntimeExceptionDao(SyncTimestamp.class);
		}
		return syncTimestampRuntimeDao;
	}
	
	public RuntimeExceptionDao<VAT, Integer> getVatRuntimeDao()
			throws SQLException {
		if (vatRuntimeDao == null) {
			vatRuntimeDao = getRuntimeExceptionDao(VAT.class);
		}
		return vatRuntimeDao;
	}

	public RuntimeExceptionDao<DistributorProductMap, Integer> getDistributorProductMapRuntimeDao()
			throws SQLException {
		if (distributorProductMapRuntimeDao == null) {
			distributorProductMapRuntimeDao = getRuntimeExceptionDao(DistributorProductMap.class);
		}
		return distributorProductMapRuntimeDao;
	}

	public RuntimeExceptionDao<ReceiveItems, Integer> getReceiveItemsRuntimeDao()
			throws SQLException {
		if (null == receiveItemsRuntimeExceptionDao) {
			receiveItemsRuntimeExceptionDao = getRuntimeExceptionDao(ReceiveItems.class);
		}
		return receiveItemsRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<Invoice, Integer> getInvoiceRuntimeExceptionDao()
			throws SQLException {
		if (null == invoiceRuntimeExceptionDao) {
			invoiceRuntimeExceptionDao = getRuntimeExceptionDao(Invoice.class);
		}
		return invoiceRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<Company, Integer> getCompanyRuntimeDao()
			throws SQLException {
		if (companyRuntimeDao == null) {
			companyRuntimeDao = getRuntimeExceptionDao(Company.class);
		}
		return companyRuntimeDao;
	}

	public RuntimeExceptionDao<Distributor, Integer> getDistributorRuntimeDao()
			throws SQLException {
		if (distributorRuntimeDao == null) {
			distributorRuntimeDao = getRuntimeExceptionDao(Distributor.class);
		}
		return distributorRuntimeDao;
	}

	public RuntimeExceptionDao<BillItem, Integer> getBillItemRuntimeDao()
			throws SQLException {
		if (billItemRuntimeDao == null) {
			billItemRuntimeDao = getRuntimeExceptionDao(BillItem.class);
		}
		return billItemRuntimeDao;
	}

	public RuntimeExceptionDao<Brand, Integer> getBrandRuntimeDao()
			throws SQLException {
		if (brandRuntimeDao == null) {
			brandRuntimeDao = getRuntimeExceptionDao(Brand.class);
		}
		return brandRuntimeDao;
	}

	public RuntimeExceptionDao<Product, Integer> getProductRuntimeDao()
			throws SQLException {
		if (productRuntimeDao == null) {
			productRuntimeDao = getRuntimeExceptionDao(Product.class);
		}
		return productRuntimeDao;
	}

	public RuntimeExceptionDao<CompanyDistributorMap, Integer> getCompanyDistributorRuntimeDao()
			throws SQLException {
		if (companyDistributorRuntimeDao == null) {
			companyDistributorRuntimeDao = getRuntimeExceptionDao(CompanyDistributorMap.class);
		}
		return companyDistributorRuntimeDao;
	}

	public RuntimeExceptionDao<ProductSku, Integer> getProductSkuRuntimeDao()
			throws SQLException {
		if (productSkuRuntimeDao == null) {
			productSkuRuntimeDao = getRuntimeExceptionDao(ProductSku.class);
		}
		return productSkuRuntimeDao;
	}

	public RuntimeExceptionDao<Transaction, Integer> getTransactionRuntimeDao()
			throws SQLException {
		if (transactionRuntimeDao == null) {
			transactionRuntimeDao = getRuntimeExceptionDao(Transaction.class);
		}
		return transactionRuntimeDao;
	}

	public RuntimeExceptionDao<Customer, Integer> getCustomerRuntimeDao()
			throws SQLException {
		if (customerRuntimeDao == null) {
			customerRuntimeDao = getRuntimeExceptionDao(Customer.class);
		}
		return customerRuntimeDao;
	}

	public RuntimeExceptionDao<ProductCategory, Integer> getProductCategoryRuntimeDao()
			throws SQLException {
		if (productCategoryRuntimeDao == null) {
			productCategoryRuntimeDao = getRuntimeExceptionDao(ProductCategory.class);
		}
		return productCategoryRuntimeDao;
	}

	public RuntimeExceptionDao<InventorySku, Integer> getInventorySkuRuntimeDao()
			throws SQLException {
		if (inventorySkuRuntimeDao == null) {
			inventorySkuRuntimeDao = getRuntimeExceptionDao(InventorySku.class);
		}
		return inventorySkuRuntimeDao;
	}

	public RuntimeExceptionDao<DistributorBrandMap, Integer> getDistributorBrandMapRuntimeDao()
			throws SQLException {
		if (distributorBrandMapRuntimeDao == null) {
			distributorBrandMapRuntimeDao = getRuntimeExceptionDao(DistributorBrandMap.class);
		}
		return distributorBrandMapRuntimeDao;
	}

	public RuntimeExceptionDao<Order, Integer> getOrderRuntimeDao()
			throws SQLException {
		if (null == orderRuntimeDao) {
			orderRuntimeDao = getRuntimeExceptionDao(Order.class);
		}
		return orderRuntimeDao;
	}

	public RuntimeExceptionDao<OrderDetails, Integer> getOrderDetailsRuntimeDao()
			throws SQLException {
		if (null == orderDetailsRuntimeDao) {
			orderDetailsRuntimeDao = getRuntimeExceptionDao(OrderDetails.class);
		}
		return orderDetailsRuntimeDao;
	}

	public RuntimeExceptionDao<InventoryBatch, Integer> getInventoryBatchRuntimeDao()
			throws SQLException {
		if (null == inventoryBatchRuntimeDao) {
			inventoryBatchRuntimeDao = getRuntimeExceptionDao(InventoryBatch.class);
		}
		return inventoryBatchRuntimeDao;
	}

	public RuntimeExceptionDao<Payments, Integer> getPaymentRuntimeDao()
			throws SQLException {
		if (null == PaymentRuntimeDao) {
			PaymentRuntimeDao = getRuntimeExceptionDao(Payments.class);
		}
		return PaymentRuntimeDao;
	}

	public RuntimeExceptionDao<DeletedRecords, Integer> getDeletedRecordsRuntimeDao()
			throws SQLException {
		if (deletedRecordsRuntimeDao == null) {
			deletedRecordsRuntimeDao = getRuntimeExceptionDao(DeletedRecords.class);
		}
		return deletedRecordsRuntimeDao;
	}

	public RuntimeExceptionDao<CustomerSuggestions, Integer> getCustomerSuggestionsRuntimeDao()
			throws SQLException {
		if (customerSuggestionsRuntimeDao == null) {
			customerSuggestionsRuntimeDao = getRuntimeExceptionDao(CustomerSuggestions.class);
		}
		return customerSuggestionsRuntimeDao;
	}

	public RuntimeExceptionDao<CustomerPayment, Integer> getCustomerPaymentRuntimeDao()
			throws SQLException {
		if (customerPaymentRuntimeDao == null) {
			customerPaymentRuntimeDao = getRuntimeExceptionDao(CustomerPayment.class);
		}
		return customerPaymentRuntimeDao;
	}

	public Dao<State, Integer> getStateDao() throws SQLException {
		if (null == stateDao) {
			stateDao = getDao(State.class);
		}
		return stateDao;
	}

	public RuntimeExceptionDao<State, Integer> getStateRuntimeDao()
			throws SQLException {
		if (stateRuntimeDao == null) {
			stateRuntimeDao = getRuntimeExceptionDao(State.class);
		}
		return stateRuntimeDao;
	}

	public RuntimeExceptionDao<Campaigns, Integer> getCampaignRuntimeDao()
			throws SQLException {
		if (campaignRuntimeDao == null) {
			campaignRuntimeDao = getRuntimeExceptionDao(Campaigns.class);
		}
		return campaignRuntimeDao;
	}

	public RuntimeExceptionDao<Settings, Integer> getSettingsRuntimeDao()
			throws SQLException {
		if (settingsRuntimeDao == null) {
			settingsRuntimeDao = getRuntimeExceptionDao(Settings.class);
		}
		return settingsRuntimeDao;
	}

	public Dao<Campaigns, Integer> getCampaignsDao() throws SQLException {
		if (null == campaignsDao) {
			campaignsDao = getDao(Campaigns.class);
		}
		return campaignsDao;
	}

	public Dao<Settings, Integer> getSettingsDao() throws SQLException {
		if (null == settingsDao) {
			settingsDao = getDao(Settings.class);
		}
		return settingsDao;
	}
	public void createTableSyncTimestamp() throws SQLException {
		try {
			   this.getDownloadSyncTimestampDao().executeRaw("CREATE TABLE `sync_timestamp`(`name` VARCHAR PRIMARY KEY, `lastmodified_timestamp` VARCHAR)");
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		productSkuDao = null;
		deletedRecordsDao = null;
		customerSuggestionsDao = null;
		productDao = null;
		customerDao = null;
		distributorDao = null;
		distributorBrandMapDao = null;
		companyDistributorDao = null;
		brandDao = null;
		transactionDao = null;
		billItemDao = null;
		companyDao = null;
		productCategoryDao = null;
		inventorySkuDao = null;
		orderDao = null;
		orderDetailsDao = null;
		inventoryBatchDao = null;
		PaymentDao = null;
		customerPaymentDao = null;
		syncTimestampDao = null;
		productSkuRuntimeDao = null;
		companyRuntimeDao = null;
		productRuntimeDao = null;
		customerRuntimeDao = null;
		brandRuntimeDao = null;
		transactionRuntimeDao = null;
		billItemRuntimeDao = null;
		productSkuRuntimeDao = null;
		productCategoryRuntimeDao = null;
		inventorySkuRuntimeDao = null;
		orderRuntimeDao = null;
		orderDetailsRuntimeDao = null;
		inventoryBatchRuntimeDao = null;
		PaymentRuntimeDao = null;
		deletedRecordsRuntimeDao = null;
		customerSuggestionsRuntimeDao = null;
		customerPaymentRuntimeDao = null;
		stateDao = null;
		stateRuntimeDao = null;
		vatDao = null;
		vatRuntimeDao = null;
		settingsDao = null;
		campaignsDao = null;
		campaignRuntimeDao = null;
		settingsRuntimeDao = null;
		syncTimestampRuntimeDao = null;
	}
}
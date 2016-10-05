package com.snapbizz.snaptoolkit.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.snapbizz.snaptoolkit.db.dao.CustomerDetails;
import com.snapbizz.snaptoolkit.db.dao.CustomerDetailsDao;
import com.snapbizz.snaptoolkit.db.dao.CustomerMonthlySummary;
import com.snapbizz.snaptoolkit.db.dao.CustomerMonthlySummaryDao;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.db.dao.CustomersDao;
import com.snapbizz.snaptoolkit.db.dao.DaoMaster;
import com.snapbizz.snaptoolkit.db.dao.DaoMaster.DevOpenHelper;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.db.dao.DaoSession;
import com.snapbizz.snaptoolkit.db.dao.Inventory;
import com.snapbizz.snaptoolkit.db.dao.InventoryDao;
import com.snapbizz.snaptoolkit.db.dao.Products;
import com.snapbizz.snaptoolkit.db.dao.ProductsDao;



import de.greenrobot.dao.query.QueryBuilder;


public class SnapbizzDB {
	private static final int MAX_RESULTS = 100;
	public static final long PRODUCT_GID_OFFSET = 1000000000000000000L;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db = null;
	
	private CustomerDetailsDao customerDetailsDao = null;
	private CustomerMonthlySummaryDao customerMonthlySummaryDao = null;
	private ProductsDao productsDao;
	private CustomersDao customersDao = null;
	private static SnapbizzDB[] instance = new SnapbizzDB[2];
	private static final int LIMIT = 10;

	// Helper DAOs
	public ProductPacksHelper packsHelper = null;
	public InvoiceHelper invoiceHelper = null;
	public InventoryHelper inventoryHelper = null;

	private SnapbizzDB() { }

	private void initDB(Context context, boolean bReadOnly) {
		db = getDatabaseInstance(context, bReadOnly);
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		productsDao = daoSession.getProductsDao();
		customerDetailsDao = daoSession.getCustomerDetailsDao();
		customerMonthlySummaryDao = daoSession.getCustomerMonthlySummaryDao();
		customersDao = daoSession.getCustomersDao();

		// Initialize helper DAOs
		packsHelper = new ProductPacksHelper(daoSession);
		invoiceHelper = new InvoiceHelper(daoSession);
		inventoryHelper = new InventoryHelper(daoSession);
	}

	private SQLiteDatabase getDatabaseInstance(Context context, boolean isReadOnly) {
        // TODO: Add encryption password
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, SnapToolkitConstants.DB_NAME_V2, null);
		if (db == null || !db.isOpen())
			db = isReadOnly ? helper.getReadableDatabase() : helper.getWritableDatabase();
		return db;
	}
	
	/**
	 * Public helper to get db instance. Use this to get a readonly or read-write database.
	 * 
	 * @param context Context
	 * @param isReadOnly gives the readableDatabase instance when true
	 * @return the DB instance
	 */
	public static SnapbizzDB getInstance(Context context, boolean isReadOnly) {
		context = context != null ? context.getApplicationContext() : null;
		int i = isReadOnly ? 0 : 1;
		if (instance[i] == null || instance[i].db == null || !instance[i].db.isOpen()) {
			if(context == null)
				return null;
			instance[i] = new SnapbizzDB();
			instance[i].initDB(context, isReadOnly);
		}
		return instance[i];
	}

	public List<Long> getInventory() {
		ArrayList<Long> result = new ArrayList<Long>();
		final String rawQuery = "SELECT " + InventoryDao.Properties.ProductCode.columnName + " FROM " + InventoryDao.TABLENAME + " WHERE "
				+ InventoryDao.Properties.IsDeleted.columnName + " = 0 ";
		Cursor cursor = daoSession.getDatabase().rawQuery(rawQuery, null);
		try{
			if (cursor.moveToFirst()) {
				do {
					result.add(cursor.getLong(0));
				} while (cursor.moveToNext());
		    }
		} finally {
			cursor.close();
		}
		if (result != null && result.size() > 0)
			return result;
		else
			return null;
	}

	/**
	 * 
	 * @param productName
	 * @param isBarCode
	 * @param isDescription
	 * @return
	 */
	// TODO: Fix this (use GDB.searchProducts as example)
	public List<Products> searchProducts(String productName, boolean isBarCode, boolean isDescription) {
		QueryBuilder<Products> productsQuery = productsDao.queryBuilder().where(ProductsDao.Properties.Name.like(productName + "%"));
		if (isBarCode && isDescription) 
			productsQuery = productsDao.queryBuilder().where(ProductsDao.Properties.ProductCode.like(productName), ProductsDao.Properties.Name.like(productName));
		else if (isBarCode)
			productsQuery = productsDao.queryBuilder().where(ProductsDao.Properties.ProductCode.eq(productName));
		productsQuery.limit(MAX_RESULTS);
		List<Products> productsList = productsQuery.list();
		if (productsList != null && productsList.size() > 0)
			return productsList;
        return null;
    }
	
	public void updateInventorySyncTime(List<Inventory> inventoryList) {
 		if (inventoryList != null && !inventoryList.isEmpty()) {
 			for (Inventory inventory : inventoryList) {
 				inventory.setUpdatedAt(new Date());
 				daoSession.getInventoryDao().insertOrReplace(inventory);
 			}
 		}
	}
	 
    public void insertOrReplaceCustomer(Customers customer) {
        customersDao.insertOrReplace(customer);
    }

	public List<Customers> getCustomerByPhone(Long phone) {
		return customersDao.queryBuilder().where(CustomersDao.Properties.Phone.eq(phone)).list();
	}
	
	/** Customer **/

	public List<Customers> getAllCustomers() {
		return customersDao.loadAll();
	}

	public List<Customers> getCustomerByPhoneNo(Long phoneNo) {
		return customersDao.queryBuilder().where(CustomersDao.Properties.Phone.eq(phoneNo)).list();
	}

	public List<Customers> getCustomerByNameOrAddressOrPhone(String customerNameOrAddressOrPhone) {
		return customersDao.queryBuilder()
						   .whereOr((CustomersDao.Properties.Name.like("%" + customerNameOrAddressOrPhone + "%")),
								   (CustomersDao.Properties.Address.like("%" + customerNameOrAddressOrPhone + "%")),
								   (CustomersDao.Properties.Phone.like("%" + customerNameOrAddressOrPhone + "%"))).list();
	}

	public void insertCustomer(Customers customer) {
		customersDao.insertInTx(customer);
	}

	public void insertCustomers(List<Customers> customers) {
		customersDao.insertInTx(customers);
	}
	
	public void updateCustomerByPhoneNo(Customers customer) {
		if (getCustomerByPhoneNo(customer.getPhone()).size() > 0)
			daoSession.getCustomersDao().update(customer);
    }

	public void updateCustomerByPhoneNo(long phone, String name, String address, String email, Integer creditLimit) {
		List<Customers> customerList = getCustomerByPhoneNo(phone);
		if (!customerList.isEmpty()) {
			Customers customer = customerList.get(0);
			if (name != null)
				customer.setName(name);
			if (address != null)
				customer.setAddress(address);
			if (email != null)
				customer.setName(email);
			if (creditLimit != 0)
				customer.setCreditLimit(creditLimit);
			customer.setUpdatedAt(new Date());
			daoSession.getCustomersDao().update(customer);
		}
	}
	
	public List<Customers> sortCustomer(boolean isDueSort) {
		if (!isDueSort) {
			return customersDao.queryBuilder().orderAsc(CustomersDao.Properties.Name).where(CustomersDao.Properties.IsDisabled.eq(false)).list();
		} else {
			List<CustomerDetails> customerDueList = customerDetailsDao.queryBuilder().orderDesc(CustomerDetailsDao.Properties.AmountDue).list();
			List<Customers> customer = new ArrayList<Customers>();
			Customers duesortCustomer;
			for (CustomerDetails customerDetails : customerDueList) {
				duesortCustomer = new Customers();
				List<Customers> customerList = getCustomerByPhoneNo(customerDetails.getPhone());
				if (!customerList.get(0).getIsDisabled()) {
					duesortCustomer.setAddress(customerList.get(0).getAddress());
					duesortCustomer.setName(customerList.get(0).getName());
					duesortCustomer.setPhone(customerDetails.getPhone());
					customer.add(duesortCustomer);
				}
			}
			return customer;
		}
	}
	
	public List<Customers> getPendingSyncCustomerList(Date date) {
		return customersDao.queryBuilder().where(CustomersDao.Properties.UpdatedAt.lt(date)).list();
	}

	public void updateCustomerSyncTime(List<Customers> customersList) {
			if (customersList != null && !customersList.isEmpty()) {
				for (Customers customer : customersList) {
					customer.setUpdatedAt(new Date());
					daoSession.getCustomersDao().insertOrReplace(customer);
				}
			}
	}
	
	public List<CustomerDetails> getCustomerDetailsByPhoneNo(Long phoneNO) {
		return customerDetailsDao.queryBuilder()
							    .where(CustomerDetailsDao.Properties.Phone.eq(phoneNO)).list();
	}
	
	public int getCustomerDuePhoneNo(Long phoneNo) {
		List<CustomerDetails> customerDetails  = getCustomerDetailsByPhoneNo(phoneNo);
		int customerDueValue = 0;
		for (CustomerDetails customerDue : customerDetails)
			customerDueValue = customerDue.getAmountDue();
		return customerDueValue;
	}

	public List<CustomerDetails> getPendingCustomerDetailsList(Date date) {
			return customerDetailsDao.queryBuilder().where(CustomerDetailsDao.Properties.UpdatedAt.lt(date)).list();
	}
	
	public void updateCustomerDetailsSyncTime(List<CustomerDetails> customerDetailsList) {
			if (customerDetailsList != null && !customerDetailsList.isEmpty()) {
				for (CustomerDetails customerDetails : customerDetailsList) {
					customerDetails.setUpdatedAt(new Date());
					daoSession.getCustomerDetailsDao().insertOrReplace(customerDetails);
				}
			}
	}
	
	public void insertCustomerDetail(CustomerDetails customerDetails) {
		customerDetailsDao.insert(customerDetails);
	}
	
	public void insertCustomerDetailses(List<CustomerDetails> customerDetailsList) {
		customerDetailsDao.insertInTx(customerDetailsList);
	}
	
	public void updateCustomerDetails(CustomerDetails customerDetails) {
		if (getCustomerByPhoneNo(customerDetails.getPhone()).size() > 0)
			customerDetailsDao.update(customerDetails);
    }

	public void insertCustomerDetails(CustomerDetails customerDetails) {
		customerDetailsDao.insertInTx(customerDetails);
	}
	
	public void updateCustomerDetails(long phone, int amountDue, int purchaseValue, int amountSaved, Integer lastPurchaseAmount, Integer lastPaymentAmount, Integer avgPurchasePerVisit, Float avgVisitsPerMonth, java.util.Date lastPurchaseDate, java.util.Date lastPaymentDate) {
		List<CustomerDetails> customerDetailsList = getCustomerDetailsByPhoneNo(phone);
		if (!customerDetailsList.isEmpty()) {
			CustomerDetails customerDetails = customerDetailsList.get(0);
			if (amountDue != 0)
				customerDetails.setAmountDue(amountDue);
			if (purchaseValue != 0)
				customerDetails.setPurchaseValue(purchaseValue);
			if (amountSaved != 0)
				customerDetails.setAmountSaved(amountSaved);
			if (lastPurchaseAmount != 0)
				customerDetails.setLastPurchaseAmount(lastPurchaseAmount);
			if (avgPurchasePerVisit != 0)
				customerDetails.setAvgPurchasePerVisit(avgPurchasePerVisit);
			if (lastPaymentAmount != 0)
				customerDetails.setLastPaymentAmount(lastPaymentAmount);
			if (avgVisitsPerMonth != 0)
				customerDetails.setAvgVisitsPerMonth(avgVisitsPerMonth);
			if (lastPurchaseDate != null)
				customerDetails.setLastPurchaseDate(lastPurchaseDate);
			if (lastPaymentDate != null)
				customerDetails.setLastPaymentDate(lastPaymentDate);
			customerDetails.setUpdatedAt(new Date());
			daoSession.getCustomerDetailsDao().update(customerDetails);
		}
	}

	public List<CustomerMonthlySummary> getMonthlySummaryByPhoneNo(Long phoneNO) {
		return customerMonthlySummaryDao.queryBuilder()
									    .where(CustomerMonthlySummaryDao.Properties.Phone.eq(phoneNO)).list();
	}

	public void insertCustomerMonthlySummary(CustomerMonthlySummary customerMonthlySummary) {
		customerMonthlySummaryDao.insertInTx(customerMonthlySummary);
	}
	
	public void insertCustomerMonthlySummaries(List<CustomerMonthlySummary> customerMonthlySummaryList) {
		customerMonthlySummaryDao.insertInTx(customerMonthlySummaryList);
	}
	
	public void updateCustomerMonthlySummary(CustomerMonthlySummary customerMonthlySummary) {
		if (getCustomerByPhoneNo(customerMonthlySummary.getPhone()).size() > 0)
			customerMonthlySummaryDao.update(customerMonthlySummary);
    }
	
	public List<CustomerMonthlySummary> getPendingCustomerMonthlySummaryList(Date date) {
		return customerMonthlySummaryDao.queryBuilder().where(CustomerMonthlySummaryDao.Properties.UpdatedAt.lt(date)).list();
	}

	public void updateCustomerMonthlySummarySyncTime(List<CustomerMonthlySummary> customerMonthlySummaryList) {
			if (customerMonthlySummaryList != null && !customerMonthlySummaryList.isEmpty()) {
				for (CustomerMonthlySummary customerMonthlySummary : customerMonthlySummaryList) {
					customerMonthlySummary.setUpdatedAt(new Date());
					daoSession.getCustomerMonthlySummaryDao().insertOrReplace(customerMonthlySummary);
				}
			}
	}
	
	public void updateCustomerByPhone(Long phone, Customers customer) {
		if (getCustomerByPhone(phone).size() > 0) 
			daoSession.getCustomersDao().insertOrReplace(customer);
    }
	 
 ////////////////////////////////////////Product////////////////////////////////////////////////////
	 
	public void syncAddProduct(List<Products> productList) {
		productsDao.insertOrReplaceInTx(productList);
	}

	public Products getProductByPId(long productCode) {
		List<Products> productsList = productsDao.queryBuilder()
												.where(ProductsDao.Properties.ProductCode.eq(productCode)).limit(1).list();
		if (productsList != null && productsList.size() > 0)
			return productsList.get(0);
		return null;
	}

	public void insertProduct(List<Products> productList) {
		productsDao.insertInTx(productList);
	}
	 
	public List<Products> getPendingProductList(Date date) {
		return productsDao.queryBuilder().where(ProductsDao.Properties.UpdatedAt.lt(date)).list();
	}
	 
	public List<Products> getProductByProductCode(long productCode) {
		return productsDao.queryBuilder().where(ProductsDao.Properties.ProductCode.eq(productCode)).list();
	}
	
	public void insertOrReplaceProducts(Products products) {
		 productsDao.insertOrReplaceInTx(products);
	}
	
	public void updateProductSyncTime(List<Products> productsList) {
		if (productsList != null && !productsList.isEmpty()) {
			for (Products products : productsList) {
				products.setUpdatedAt(new Date());
	 			daoSession.getProductsDao().insertOrReplace(products);
	 		}
	 	}
	}
}

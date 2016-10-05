package com.snapbizz.snaptoolkit.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.query.QueryBuilder;
import com.snapbizz.snaptoolkit.gdb.dao.LastSyncTime;
import com.snapbizz.snaptoolkit.gdb.dao.LastSyncTimeDao;
import com.snapbizz.snaptoolkit.gdb.dao.Brands;
import com.snapbizz.snaptoolkit.gdb.dao.BrandsDao;
import com.snapbizz.snaptoolkit.gdb.dao.Companies;
import com.snapbizz.snaptoolkit.gdb.dao.CompaniesDao;
import com.snapbizz.snaptoolkit.gdb.dao.DaoMaster;
import com.snapbizz.snaptoolkit.gdb.dao.DaoMaster.DevOpenHelper;
import com.snapbizz.snaptoolkit.gdb.dao.DaoSession;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategoriesDao;
import com.snapbizz.snaptoolkit.gdb.dao.Products;
import com.snapbizz.snaptoolkit.gdb.dao.ProductsDao;
import com.snapbizz.snaptoolkit.gdb.dao.VatCategories;
import com.snapbizz.snaptoolkit.gdb.dao.VatCategoriesDao;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;


public class GlobalDB {
	private static final int MAX_RESULTS = 100;
	
	
	private ProductsDao gdbProductDao;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db = null;
	private LastSyncTimeDao lastSyncTimeDao;
	private BrandsDao brandDao;
	private CompaniesDao companyDao;
	private ProductCategoriesDao categoryDao;
	private VatCategoriesDao vatCategoryDao;
	private static GlobalDB[] instance = new GlobalDB[2];

	private void initDB(Context context, boolean isReadOnly) {		
		db = getDatabaseInstance(context, isReadOnly);
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		gdbProductDao = daoSession.getProductsDao();
		lastSyncTimeDao = daoSession.getLastSyncTimeDao();
		brandDao = daoSession.getBrandsDao();
		companyDao = daoSession.getCompaniesDao();
		categoryDao = daoSession.getProductCategoriesDao();
		vatCategoryDao = daoSession.getVatCategoriesDao();
	}

	private GlobalDB() { }
	
	public static GlobalDB getInstance(Context context, boolean isReadOnly) {
		context = context != null ? context.getApplicationContext() : null;
		int i = isReadOnly ? 0 : 1;
		if (instance[i] == null || instance[i].db == null || !instance[i].db.isOpen()) {
			if(context == null)
				return null;
			instance[i] = new GlobalDB();
			instance[i].initDB(context, isReadOnly);
		}
		return instance[i];
	}
	
	private SQLiteDatabase getDatabaseInstance(Context context, boolean isReadOnly) {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, SnapToolkitConstants.GLOBAL_DB_NAME, null);
		if (db == null || !db.isOpen()) 
			db = isReadOnly ? helper.getReadableDatabase() : helper.getWritableDatabase();
		return db;
	}

	public Long getLastSyncTime(String syncClass) {
		QueryBuilder<LastSyncTime> lastSyncQuery = lastSyncTimeDao.queryBuilder().where(
				LastSyncTimeDao.Properties.SyncClass.eq(syncClass));
		List<LastSyncTime> lastSyncTimes = lastSyncQuery.list();
		if (lastSyncTimes != null && lastSyncTimes.size() > 0)
			return lastSyncTimes.get(0).getTimestamp().getTime() / 1000;
		return 0L;
	}

	public void setLastSyncTime(String syncClass, Long timeInSec) {
		Date date = new Date();
		date.setTime(timeInSec * 1000);
		LastSyncTime lastSyncTime = new LastSyncTime(syncClass, date);
		lastSyncTimeDao.insertOrReplace(lastSyncTime);
	}

	public Products getProductByBarcode(long barcode) {
		List<Products> productsList = gdbProductDao.queryBuilder()
												   .where(ProductsDao.Properties.Barcode.eq(barcode))
												   .limit(1).list();
		if (productsList != null && productsList.size() > 0)
			return productsList.get(0);
		return null;
	}
	
	public List<Products> getProductsByBarcodes(List<Long> barcodes) {
		List<Products> productsList = gdbProductDao.queryBuilder().where(ProductsDao.Properties.Barcode.in(barcodes)).limit(100).list();
		if (productsList != null && productsList.size() > 0)
			return productsList;
		else
			return new ArrayList<Products>();
	}
	
	public List<Products> getProductByGroupId(long groupId) {
		List<Products> productsList = gdbProductDao.queryBuilder().where(ProductsDao.Properties.ProductGid.eq(groupId)).list();
		if (productsList != null && productsList.size() > 0)
			return productsList;
		return null;
	}
	
	public List<Products> getProductBySubCategory(long id) {
		List<Products> productsList = gdbProductDao.queryBuilder().where(ProductsDao.Properties.CategoryId.eq(id)).list();
		if (productsList != null && productsList.size() > 0)
			return productsList;
		return null;
	}
	
	public List<Products> getAllProducts() {
		List<Products> productsList = gdbProductDao.queryBuilder().limit(MAX_RESULTS).list();
		if (productsList != null && productsList.size() > 0)
			return productsList;
		return null;
	}
	
	public List<Products> searchProducts(String productName, boolean isBarCode, boolean isDescription) {
		QueryBuilder<Products> productsQuery = gdbProductDao.queryBuilder();

		if (isBarCode && isDescription) {
			productsQuery.whereOr(ProductsDao.Properties.Barcode.like("%" + productName + "%"),
												  ProductsDao.Properties.Name.like("%" + productName + "%"));
		} else if (isBarCode) {
			productsQuery.where(ProductsDao.Properties.ProductGid.like("%" + productName + "%"));
		} else {
			productsQuery.where(ProductsDao.Properties.Name.like("%" + productName + "%"));
		}
		productsQuery.limit(MAX_RESULTS);
		List<Products> productsList = productsQuery.list();
		if (productsList != null && productsList.size() > 0)
			return productsList;
		return null;
	}
	
	public Long getProductsLastUpdatedTime() {
		QueryBuilder<Products> getProductQuery = gdbProductDao.queryBuilder()
															  .orderDesc(ProductsDao.Properties.UpdatedAt)
															  .limit(1);
		List<Products> lastProduct = getProductQuery.list();
		if (lastProduct != null && lastProduct.size() > 0)
			return lastProduct.get(0).getUpdatedAt().getTime();
		return null;
	}
	
	public Brands getBrandById(long brandId) {
		QueryBuilder<Brands> getBrandQuery = brandDao.queryBuilder().where(
				BrandsDao.Properties.Id.eq(brandId));
		List<Brands> brands = getBrandQuery.list();
		if (brands != null && brands.size() > 0)
			return brands.get(0);
		return null;
	}

	public float getVatRate(long vatId) {
		QueryBuilder<VatCategories> getVatQuery = vatCategoryDao.queryBuilder().where(
				VatCategoriesDao.Properties.Id.eq(vatId)).limit(1);
		List<VatCategories> vatCategories = getVatQuery.list();
		if (vatCategories != null && vatCategories.size() > 0)
			return vatCategories.get(0).getVatRate();
		return 0;
	}
	
	public List<VatCategories>  getVatRateByState(int stateId) {
		QueryBuilder<VatCategories> getVatQuery = vatCategoryDao.queryBuilder().where(
				VatCategoriesDao.Properties.State.eq(stateId));
		List<VatCategories> vatCategories = getVatQuery.list();
		if (vatCategories != null && vatCategories.size() > 0)
			return vatCategories;
		return null;
	}
	
	public ProductCategories getCategory(long categoryId) {
		QueryBuilder<ProductCategories> productCategoryQuery = categoryDao.queryBuilder().where(
				ProductCategoriesDao.Properties.Id.eq(categoryId)).limit(1);
		List<ProductCategories> categories = productCategoryQuery.list();
		if (categories != null && categories.size() > 0)
			return categories.get(0);
		return null;
	}
	
	public List<ProductCategories> getAllCategory() {
		QueryBuilder<ProductCategories> productCategoryQuery = categoryDao.queryBuilder().where(ProductCategoriesDao.Properties.ParentId.le(0));
		List<ProductCategories> categoriesList = productCategoryQuery.list();
		if (categoriesList != null && categoriesList.size() > 0)
			return categoriesList;
		return null;
	}
	
	public List<ProductCategories> getAllSubCategory() {
		QueryBuilder<ProductCategories> productCategoryQuery = categoryDao.queryBuilder().where(ProductCategoriesDao.Properties.ParentId.gt(0));
		List<ProductCategories> categoriesList = productCategoryQuery.list();
		if (categoriesList != null && categoriesList.size() > 0)
			return categoriesList;
		return null;
	}
	
	public List<ProductCategories> getSubCategory(long parentId) {
		QueryBuilder<ProductCategories> productCategoryQuery = categoryDao.queryBuilder().where(ProductCategoriesDao.Properties.ParentId.eq(parentId));
		List<ProductCategories> categoriesList = productCategoryQuery.list();
		if (categoriesList != null && categoriesList.size() > 0)
			return categoriesList;
		return null;
	}
	
	public String getProductCategoryName(long id) {
		QueryBuilder<ProductCategories> productCategoryQuery = categoryDao.queryBuilder().where(
				ProductCategoriesDao.Properties.Id.eq(id));
		List<ProductCategories> categories = productCategoryQuery.list();
		if (categories != null && categories.size() > 0)
			return categories.get(0).getName();
		return null;
	}

	public void syncAddProduct(List<Products> productList) {
		gdbProductDao.insertOrReplaceInTx(productList);
	}

	public void syncAddBrands(List<Brands> brandList) {
		brandDao.insertOrReplaceInTx(brandList);
	}

	public void syncAddComapanies(List<Companies> companyList) {
		companyDao.insertOrReplaceInTx(companyList);
	}

	public void syncProductCategories(List<ProductCategories> productCategoryList) {
		categoryDao.insertOrReplaceInTx(productCategoryList);
	}
	
	public void syncVatCategories(List<VatCategories> vatCategoryList) {
		vatCategoryDao.insertOrReplaceInTx(vatCategoryList);
	}
}

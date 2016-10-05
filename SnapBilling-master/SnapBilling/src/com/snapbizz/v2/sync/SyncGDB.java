package com.snapbizz.v2.sync;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.snapbizz.snapbilling.domainsV2.SyncGDBData;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.gdb.dao.Brands;
import com.snapbizz.snaptoolkit.gdb.dao.Companies;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import com.snapbizz.snaptoolkit.gdb.dao.Products;
import com.snapbizz.snaptoolkit.gdb.dao.VatCategories;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.Call;


public class SyncGDB implements GlobalSync.GlobalSyncService {
	private static final String TAG = "[SyncGDB]";
	private static final int SYNC_INTERVAL = 60 * 60 * 24;			// One Day
	private static final String SYNC_PRODUCT_API = "products";
	private static final String SYNC_BRANDS_API = "brands";
	private static final String SYNC_CATEGORIES_API = "product_categories";
	private static final String SYNC_COMPANY_API = "companies";
	private static final String VAT_CATEGORIES_API = "vat_categories/";
	private Context context;
	

	public enum SyncMethods {
		syncProducts, syncCompanies, syncBrands, syncProductCategories, syncVatCategories
	};
	
	public SyncGDB(Context context) {
		this.context = context;
	}
	
	/* ------------------------------------- Rest Interfaces 	*/

	public interface ProductsAPI {
		@GET(SYNC_PRODUCT_API + "?snapshot")
		Call<SyncGDBData.SnapShot> getSnapShot(@Query("lang") String language, 
											   @Query("store_id") String storeId, 
											   @Query("device_id") String deviceId, 
											   @Query("access_token") String accessToken);
		@GET(SYNC_PRODUCT_API)
		Call<List<SyncGDBData.GDBProduct>> getNewProducts(@Query("timestamp") String timestamp, 
														@Query("lang") String langugage,
														@Query("store_id") String storeId, 
														@Query("device_id") String deviceId, 
														@Query("access_token") String accessToken, 
														@Query("start") int start);
	}
	
	public interface BrandsAPI {
		@GET(SYNC_BRANDS_API)
		Call<List<SyncGDBData.Brand>> getNewBrands(@Query("store_id") String storeId, 
												 @Query("device_id") String deviceId, 
												 @Query("access_token") String accessToken, 
												 @Query("start") int start);
	}
	
	public interface CompaniesAPI {
		@GET(SYNC_COMPANY_API)
		Call<List<SyncGDBData.GDBCompanies>> getNewCompanies(@Query("store_id") String storeId, 
														   @Query("device_id") String deviceId, 
														   @Query("access_token") String accessToken,
														   @Query("start") int start);
	}
	
	public interface CategoriesAPI {
		@GET(SYNC_CATEGORIES_API)
		Call<List<SyncGDBData.GDBProductCategories>> getNewCategories(@Query("store_id") String storeId, 
																	@Query("device_id") String deviceId, 
																	@Query("access_token") String accessToken, 
																	@Query("start") int start);
	}
	
	public interface VatCategoriesAPI {
		@GET(VAT_CATEGORIES_API + "{stateId}")
		Call<List<SyncGDBData.GDBVatCategories>> getVatCategories(@Path("stateId") String stateId,
																@Query("store_id") String storeId, 
																@Query("device_id") String deviceId, 
																@Query("access_token") String accessToken, 
																@Query("start") int start);
	}
	
	/* ------------------------------------- Sync Calls --------------------------------*/
	private SyncGDBData.SnapShot getSnapShot(Retrofit retrofit) throws IOException {
		ProductsAPI productsAPI = retrofit.create(ProductsAPI.class);
		Call<SyncGDBData.SnapShot> call = productsAPI.getSnapShot(GlobalSync.sync_language,
				SnapSharedUtils.getStoreId(context), SnapSharedUtils.getDeviceId(context),
				SnapSharedUtils.getStoreAuthKey(context));
		return call.execute().body();
	}
	
	private Date snapShotSync(Retrofit retrofit, GlobalDB db) throws IOException {
		Gson gson = new GsonBuilder().setDateFormat(SnapToolkitConstants.GDB_SNAPSHOT_TIMEFORMAT).create();
		SyncGDBData.SnapShot snapshot = getSnapShot(retrofit);
		if (snapshot == null)
			return null;
		
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(snapshot.file).build();
		Response response = client.newCall(request).execute();
		GZIPInputStream stream = new GZIPInputStream(new BufferedInputStream(response.body().byteStream()));
		JsonReader reader = new JsonReader(new InputStreamReader(stream));
		reader.setLenient(true);
		reader.beginArray();
		int n = 0;
		List<Products> productList = new ArrayList<Products>();
		while (reader.hasNext()) {
			SyncGDBData.GDBProduct product = gson.fromJson(reader, SyncGDBData.GDBProduct.class);
			Products storeProduct = new Products(product.barcode, product.product_gid, product.name, 
					product.mrp, product.alt_mrp, product.uom, product.measure, product.brand_id, 
					product.category_id, product.vat_id, product.is_disabled, product.image_url, 
					product.local_name, product.is_pc, product.created_at, product.updated_at);			
			productList.add(storeProduct);
			if ((n++) % 100 == 0) {
				Log.i(TAG, "Completed:"+n);
			}
		}
		db.syncAddProduct(productList);
		reader.endArray();
		return snapshot.timestamp;
	}

	@Override
	public Long startSync(long currentTime, long lastSyncTime, GlobalDB db, SnapbizzDB sdb) {
		try {
			int productRecords = 0;
			if (SnapSharedUtils.getStoreStateId(context) == null)
				return null;
			Date date = new Date(lastSyncTime * 1000);
			SyncMethods[] syncMehtodValues = SyncMethods.values();
			for (SyncMethods syncMethod : syncMehtodValues) {
				int start = 0;
                int recordSize = 0;
				do {
					switch (syncMethod) {
					    case syncCompanies:
						    recordSize = syncCompanies(date, db, start);
    						break;
						
	    				case syncBrands:
		    				recordSize = syncBrands(date, db, start);
			    			break;

					    case syncProductCategories:
				    		recordSize = syncProductCategories(date, db, start);
						    break;
						
    					case syncVatCategories:
	    					recordSize = syncVatCategories(date, db, start);
		    				break;
						
			    		case syncProducts:
				    		// Do this when synching for the first time
					    	if (lastSyncTime == 0) {
						    	date = snapShotSync(ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2), db);
						    	productRecords = 1;
						    	break;
					    	}
    						recordSize = syncProducts(new Date(db.getProductsLastUpdatedTime()), db, 0);
    						productRecords += recordSize;
	    					break;
						
				    	default:
                            recordSize = 0;
			    			break;
					}
                    start = start + recordSize;
				} while (recordSize >= SnapBillingConstants.API_OFFSET_LIMIT);
				
				if (productRecords == 0)
					break;
			}
			return date.getTime() / 1000;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private int syncCompanies(Date date, GlobalDB db, Integer start) {		
		CompaniesAPI companiesAPI = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2).create(CompaniesAPI.class);
		Call<List<SyncGDBData.GDBCompanies>> companyCall = companiesAPI.getNewCompanies(
				SnapSharedUtils.getStoreId(context), SnapSharedUtils.getDeviceId(context),
				SnapSharedUtils.getStoreAuthKey(context), start);		
		try {
			List<SyncGDBData.GDBCompanies> companies = companyCall.execute().body();
			if (companies != null && !companies.isEmpty()) {	
				List<Companies> companyList = new ArrayList<Companies>();
				for (SyncGDBData.GDBCompanies company : companies) {
					if (date == null || date.before(company.updated_at))
						date = company.updated_at;
					
					Companies companyObj = new Companies(company.id, company.name,
							company.image_url, company.created_at,company.updated_at);
					companyList.add(companyObj);
				}
				db.syncAddComapanies(companyList);	
				return companies.size();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return 0;
	}
	
	private int syncBrands(Date date, GlobalDB db, int start) {
		BrandsAPI brnadsAPI = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2).create(BrandsAPI.class);
		Call<List<SyncGDBData.Brand>> brandsCall = brnadsAPI.getNewBrands(
				SnapSharedUtils.getStoreId(context), SnapSharedUtils.getDeviceId(context),
				SnapSharedUtils.getStoreAuthKey(context), start);
		try {
			List<SyncGDBData.Brand> brands = brandsCall.execute().body();
			if (brands != null && !brands.isEmpty()) {
				List<Brands> brandList = new ArrayList<Brands>();
				for (SyncGDBData.Brand brand : brands) {
					if (date == null || date.before(brand.updated_at))
						date = brand.updated_at;
					
					Brands brandObj = new Brands(brand.id, brand.parent_id, brand.name, 
						brand.company_id, brand.image_url, brand.created_at,brand.updated_at);
					brandList.add(brandObj);					
				}
				db.syncAddBrands(brandList);	
				return brands.size();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int syncProductCategories(Date date, GlobalDB db, int start) {
		CategoriesAPI categoriesAPI = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2)
				.create(CategoriesAPI.class);
		Call<List<SyncGDBData.GDBProductCategories>> categoryCall = categoriesAPI.getNewCategories(
						SnapSharedUtils.getStoreId(context), SnapSharedUtils.getDeviceId(context),
						SnapSharedUtils.getStoreAuthKey(context), start);
		try {
			List<SyncGDBData.GDBProductCategories> categories = categoryCall.execute().body();
			if (categories != null && !categories.isEmpty()) {
				List<ProductCategories> productCategoriesList = new ArrayList<ProductCategories>();
				for (SyncGDBData.GDBProductCategories category : categories) {					
					if (date == null || date.before(category.updated_at))
						date = category.updated_at;
					
					ProductCategories prodCategoryObj = new ProductCategories(category.id, category.name,
								category.parent_id, category.vat_id, category.is_quick_add,
								category.created_at, category.updated_at);
					
					productCategoriesList.add(prodCategoryObj);
				}
				db.syncProductCategories(productCategoriesList);
				return categories.size();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int syncVatCategories(Date date, GlobalDB db, int start) {
		VatCategoriesAPI vatCategoriesAPI = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2)
				.create(VatCategoriesAPI.class);
		Call<List<SyncGDBData.GDBVatCategories>> vatCall = vatCategoriesAPI.getVatCategories(
				SnapSharedUtils.getStoreStateId(context), SnapSharedUtils.getStoreId(context),
				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
		try {
			List<SyncGDBData.GDBVatCategories> vatCategories = vatCall.execute().body();
			if (vatCategories != null && !vatCategories.isEmpty()) {
				List<VatCategories> vatCategoryList = new ArrayList<VatCategories>();
				for (SyncGDBData.GDBVatCategories category : vatCategories) {					
					if (date == null || date.before(category.updated_at))
						date = category.updated_at;
					
					VatCategories vatCategoryObj = new VatCategories((int)category.id, category.state,
								category.vat_rate, category.created_at, category.updated_at);
					vatCategoryList.add(vatCategoryObj);
				}
				db.syncVatCategories(vatCategoryList);	
				return vatCategories.size();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int syncProducts(Date date, GlobalDB db, int start) {
		ProductsAPI productsAPI = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2).create(ProductsAPI.class);
		Call<List<SyncGDBData.GDBProduct>> productCall = productsAPI.getNewProducts(
				new SimpleDateFormat(SnapToolkitConstants.GDB_SNAPSHOT_TIMEFORMAT, Locale.US).format(date), 
				GlobalSync.sync_language, SnapSharedUtils.getStoreId(context),
				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context), start);
		try {
			List<SyncGDBData.GDBProduct> products = productCall.execute().body();
			List<Products> productList = new ArrayList<Products>();
			if (products != null && !products.isEmpty()) {
				for (SyncGDBData.GDBProduct product : products) {
					if (date == null || date.before(product.updated_at))
						date = product.updated_at;

					Products storeProduct = new Products(product.barcode, product.product_gid, product.name, 
							product.mrp, product.alt_mrp, product.uom, product.measure, product.brand_id, 
							product.category_id, product.vat_id, product.is_disabled, product.image_url, 
							product.local_name, product.is_pc, product.created_at, product.updated_at);
					productList.add(storeProduct);
				}
				db.syncAddProduct(productList);
				return products.size();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return 0;
	}
	
	@Override
	public final long getSyncInterval() {
		return SYNC_INTERVAL;
	}

}

package com.snapbizz.v2.sync;

import java.io.IOException;
import java.util.List;

import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomer;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomerDetails;
import com.snapbizz.v2.sync.LocalSyncData.ApiCustomerMonthlySummary;
import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationResponse.ApiEditStore;
import com.snapbizz.v2.sync.LocalSyncData.ApiDistributor;
import com.snapbizz.v2.sync.LocalSyncData.ApiDistributorsPayment;
import com.snapbizz.v2.sync.LocalSyncData.ApiInventory;
import com.snapbizz.v2.sync.LocalSyncData.ApiInvoice;
import com.snapbizz.v2.sync.LocalSyncData.ApiOtpGeneration;
import com.snapbizz.v2.sync.LocalSyncData.ApiProduct;
import com.snapbizz.v2.sync.LocalSyncData.ApiProductPacks;
import com.snapbizz.v2.sync.LocalSyncData.ApiTabletSettings;
import com.snapbizz.v2.sync.LocalSyncData.ApiTransaction;
import com.snapbizz.v2.sync.LocalSyncData.DefaultAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetCustomerAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetCustomerDetailsAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetCustomerMonthlySummaryResonse;
import com.snapbizz.v2.sync.LocalSyncData.GetInventoryAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetInviceListAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetProductListAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetProductPackListAPIResponse;
import com.snapbizz.v2.sync.LocalSyncData.GetTransactionsListAPIResponse;

import android.content.Context;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class LocalSyncAPI {
	private Retrofit retrofit = null;
	private static final String SYNC_INVENTORY_API = "inventory";
	private static final String SYNC_CUSTOMER_API = "customers";
	private static final String SYNC_CUSTOMER_MONTHLY_SUMMARY = "customer_monthly_summary";
	private static final String SYNC_CUSTOMER_DETAILS_API = "customer_details";
	private static final String SYNC_INVOICE_API = "invoices";
	private static final String SYNC_PRODUCT_API = "products";
	private static final String SYNC_PRODUCT_PACK_API = "product_packs";
	private static final String SYNC_TRANSACTION_API = "transactions";
	
	
	public LocalSyncAPI(Context context) {
		retrofit = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2 + Long.parseLong(SnapSharedUtils.getStoreId(context)) + "/");
	}
	
	private interface LocalSyncAPIHelper {
		
		@POST(SYNC_INVENTORY_API)
		Call<DefaultAPIResponse> uploadInventory(@Query ("store_id") long storeId,
											     @Query ("device_id") String deviceId, 
												 @Query ("access_token") String accessToken, 
				                                 @Body List<ApiInventory> inventoryList);
		
		@POST(SYNC_CUSTOMER_API)
		Call<DefaultAPIResponse> uploadCustomer(@Query ("store_id") long storeId,
												@Query ("device_id") String deviceId, 
												@Query ("access_token") String accessToken, 
				                                @Body List<ApiCustomer> customerList);
		
		@POST(SYNC_CUSTOMER_MONTHLY_SUMMARY)
		Call<DefaultAPIResponse> uploadCustomerMonthlySummary(@Query ("store_id") long storeId,
															  @Query ("device_id") String deviceId, 
															  @Query ("access_token") String accessToken, 
				                                 			  @Body List<ApiCustomerMonthlySummary> customerMonthlySummaryList);
		
		@POST(SYNC_CUSTOMER_DETAILS_API)
		Call<DefaultAPIResponse> uploadCustomerDetails(@Query ("store_id") long storeId, 
													   @Query ("device_id") String deviceId, 
													   @Query ("access_token") String accessToken, 
				                                       @Body List<ApiCustomerDetails> customerDetailsList);
		
		@POST("distributors")
		Call<DefaultAPIResponse> uploadsDistributors(@Query ("device_id") String deviceId, 
													 @Query ("access_token") String accessToken, 
				                                     @Body List<ApiDistributor> distributorsList);
		
		@POST("distributors_payments")
		Call<DefaultAPIResponse> uploadsDistributorsPayment(@Query ("store_id") long storeId, 
															@Query ("device_id") String deviceId, 
															@Query ("access_token") String accessToken, 
															@Body List<ApiDistributorsPayment> distributorsPaymentList);
		
		@POST("otp_generation")
		Call<DefaultAPIResponse> generateOTP(@Query ("device_id") String deviceId, 
											 @Query ("store_id") long storeId);

//		@POST("device_registration")
//		Call<ApiDeviceRegistrationResponse> deviceRegistration(@Query ("store_phone_number") long storePhoneNumber, 
//															   @Body ApiDeviceRegistrationInput deviceDetails);

		@POST("edit_store")
		Call<DefaultAPIResponse> editStore(@Query ("device_id") String deviceId, 
										   @Query ("access_token") String accessToken, 
										   @Query ("store_id") long storeId, 
										   @Body ApiEditStore storeDetails);

		@POST(SYNC_INVOICE_API)
		Call<DefaultAPIResponse> uploadInvoice(@Query ("store_id") long storeId, 
											   @Query ("device_id") String deviceId, 
											   @Query ("access_token") String accessToken, 
											   @Body List<ApiInvoice> invoiceDetails);

		@POST(SYNC_TRANSACTION_API)
		Call<DefaultAPIResponse> uploadTransaction(@Query ("store_id") long storeId, 
												   @Query ("device_id") String deviceId, 
												   @Query ("access_token") String accessToken, 
												   @Body List<ApiTransaction> transactionDetails);
		
		@POST(SYNC_PRODUCT_API)
		Call<DefaultAPIResponse> uploadProduct(@Query ("store_id") long storeId, 
											   @Query ("device_id") String deviceId, 
											   @Query ("access_token") String accessToken, 
											   @Body List<ApiProduct> productDetails);
		
		@POST("tablet_settings")
		Call<DefaultAPIResponse> uploadTabletSettings(@Query ("store_id") long storeId, 
													  @Query ("device_id") String deviceId, 
													  @Query ("access_token") String accessToken, 
													  @Body ApiTabletSettings tabletData);
		
		@POST(SYNC_PRODUCT_PACK_API)
		Call<DefaultAPIResponse> uploadProductPacksData(@Query ("store_id") long storeId, 
														@Query ("device_id") String deviceId, 
														@Query ("access_token") String accessToken, 
														@Body List<ApiProductPacks> productData);
		
		@GET(SYNC_INVENTORY_API)
		Call<GetInventoryAPIResponse> getInventories(@Query("store_id") long storeId,
													 @Query("device_id") String deviceId,
													 @Query("access_token") String accessToken,
													 @Query("start") String start);
		
		@GET(SYNC_INVENTORY_API + "/{productCode}")
		Call<GetInventoryAPIResponse> getInventoriesByProductCode(@Path("parentId") long productCode,
																  @Query("store_id") long storeId,
																  @Query("device_id") String deviceId,
																  @Query("access_token") String accessToken);

		@GET(SYNC_CUSTOMER_API)
		Call<GetCustomerAPIResponse> getCustomers(@Query("store_id") long storeId,
												  @Query("device_id") String deviceId,
												  @Query("access_token") String accessToken,
												  @Query("start") String start);

		@GET(SYNC_CUSTOMER_API + "/{phone}")
		Call<ApiCustomer> getCustomerByPhone(@Path("phone") long phone,
											 @Query("store_id") long storeId,
											 @Query("device_id") String deviceId,
											 @Query("access_token") String accessToken);
		
		@GET(SYNC_CUSTOMER_MONTHLY_SUMMARY)
		Call<GetCustomerMonthlySummaryResonse> getCustomerMonthlySummary(@Query("store_id") long storeId,
																		 @Query("device_id") String deviceId,
																		 @Query("access_token") String accessToken,
																		 @Query("start") String start);
		
		@GET(SYNC_CUSTOMER_MONTHLY_SUMMARY + "/{phone}/{month}")
		Call<ApiCustomerMonthlySummary> getCustomerMonthlySummaryByPhoneAndMonth(@Path("phone") long phone,
																				 @Path("month") int month, 
																				 @Query("store_id") long storeId,
																				 @Query("device_id") String deviceId, 
																				 @Query("access_token") String accessToken, 
																				 @Query("start") String start);
	
		@GET(SYNC_CUSTOMER_DETAILS_API)
		Call<GetCustomerDetailsAPIResponse> getCustomerDetailsList(@Query("store_id") long storeId,
				  												  @Query("device_id") String deviceId, 
				  												  @Query("access_token") String accessToken, 
				  												  @Query("start") String start);

		@GET(SYNC_CUSTOMER_DETAILS_API + "/{phone}")
		Call<ApiCustomerDetails> getCustomerDetailsByPhone(@Path("phone") long phone,
														   @Query("store_id") long storeId,
														   @Query("device_id") String deviceId, 
														   @Query("access_token") String accessToken);

		@GET(SYNC_INVOICE_API)
		Call<GetInviceListAPIResponse> getInvoiceList(@Query("store_id") long storeId,
				  									  @Query("device_id") String deviceId, 
				  									  @Query("access_token") String accessToken, 
				  									  @Query("start") String start);
		
		@GET(SYNC_PRODUCT_API)
		Call<GetProductListAPIResponse> getProductList(@Query("store_id") long storeId,
				  									   @Query("device_id") String deviceId, 
				  									   @Query("access_token") String accessToken, 
				  									   @Query("start") String start);
		
		@GET(SYNC_PRODUCT_PACK_API)
		Call<GetProductPackListAPIResponse> getProductPackList(@Query("store_id") long storeId,
				  											   @Query("device_id") String deviceId, 
				  											   @Query("access_token") String accessToken, 
				  											   @Query("start") String start);
		
		@GET(SYNC_TRANSACTION_API)
		Call<GetTransactionsListAPIResponse> getTransactionsList(@Query("store_id") long storeId,
				  												 @Query("device_id") String deviceId, 
				  												 @Query("access_token") String accessToken, 
				  												 @Query("start") String start);
		
	}
	
	private LocalSyncAPIHelper getUploadSyncAPI() {
		return retrofit.create(LocalSyncAPIHelper.class);
	}
	
	public DefaultAPIResponse uploadInventoryData(long storeId, String deviceId, String accessToken, 
			List<ApiInventory> inventoryList) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadInventory(storeId, deviceId, 
				accessToken, inventoryList);
		return response.execute().body();
	}
	
	public DefaultAPIResponse uploadCustomerData(long storeId, String deviceId, String accessToken, 
			List<ApiCustomer> customerList) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadCustomer(storeId, deviceId, 
				accessToken, customerList);
		return response.execute().body();
	}
	
	public DefaultAPIResponse uploadCustomerDetailsData(long storeId, String deviceId, String accessToken, 
			List<ApiCustomerDetails> customerDetailsList) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadCustomerDetails(storeId, 
				deviceId, accessToken, customerDetailsList);
		return response.execute().body();
	}
	
	public DefaultAPIResponse uploadCustomerMonthlySummaryData(long storeId, String deviceId, String accessToken, 
			List<ApiCustomerMonthlySummary> customerMonthlySummaryList) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadCustomerMonthlySummary(storeId, 
				deviceId, accessToken, customerMonthlySummaryList);
		return response.execute().body();
	}
	
	public DefaultAPIResponse uploadsDistributorsData(String deviceId, String accessToken, 
			List<ApiDistributor> distributorsList) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadsDistributors(deviceId, 
				accessToken, distributorsList);
		return response.execute().body();
	}
	
	public DefaultAPIResponse uploadsDistributorsPaymentsData(long storeId, String deviceId, 
			String accessToken, List<ApiDistributorsPayment> distributorsPaymentList) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadsDistributorsPayment(storeId, 
				deviceId, accessToken, distributorsPaymentList);
		return response.execute().body();
	}
	
	public DefaultAPIResponse generateOTPForStore(ApiOtpGeneration apiOtpGeneration) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().generateOTP(apiOtpGeneration.device_id, 
				apiOtpGeneration.store_id);
		return response.execute().body();
	}
	
//	public ApiDeviceRegistrationResponse callDeviceRegistration(long storePhoneNo, 
//			ApiDeviceRegistrationInput apiDeviceRegistration) throws IOException {
//		Call<ApiDeviceRegistrationResponse> response = getUploadSyncAPI().deviceRegistration(storePhoneNo,
//				apiDeviceRegistration);
//		return response.execute().body();
//	}
//	
	public DefaultAPIResponse callEditStoreAPI(ApiOtpGeneration apiOTPGeneration, 
			String accessToken, ApiEditStore storeDetails) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().editStore(apiOTPGeneration.device_id, 
				accessToken, apiOTPGeneration.store_id, storeDetails);
		return response.execute().body();
	}
	
	public DefaultAPIResponse callInvoiceAPI(ApiOtpGeneration apiOTPGeneration, 
			String accessToken, List<ApiInvoice> invoiceDetails) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadInvoice(apiOTPGeneration.store_id, 
				apiOTPGeneration.device_id, accessToken, invoiceDetails);
		return response.execute().body();
	}
	
	public DefaultAPIResponse callTransactionAPI(ApiOtpGeneration apiOTPGeneration, 
			String accessToken, List<ApiTransaction> transactionDetails) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadTransaction(apiOTPGeneration.store_id,
				apiOTPGeneration.device_id, accessToken, transactionDetails);
		return response.execute().body();
	}
	
	public DefaultAPIResponse callProductAPI(ApiOtpGeneration apiOTPGeneration, String accessToken, 
			List<ApiProduct> productDetails) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadProduct(apiOTPGeneration.store_id, 
				apiOTPGeneration.device_id, accessToken, productDetails);
		return response.execute().body();
	}
	
	public DefaultAPIResponse callTabletSettingsAPI(ApiOtpGeneration apiOTPGeneration, 
			String accessToken, ApiTabletSettings tabletData) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadTabletSettings(apiOTPGeneration.store_id,
				apiOTPGeneration.device_id, accessToken, tabletData);
		return response.execute().body();
	}
	
	public DefaultAPIResponse callProductPacksAPI(ApiOtpGeneration apiOTPGeneration, 
			String accessToken, List<ApiProductPacks> productPacksData) throws IOException {
		Call<DefaultAPIResponse> response = getUploadSyncAPI().uploadProductPacksData(apiOTPGeneration.store_id, 
				apiOTPGeneration.device_id, accessToken, productPacksData);
		return response.execute().body();
	}
	
	
/////////////////////////////////////////DownloadSyncAPICall//////////////////////////////////////////////////////////
	
	public GetInventoryAPIResponse getInventoryList(long storeId, String deviceId, 
			String accessToken, String start) throws IOException {
		Call<GetInventoryAPIResponse> response = getUploadSyncAPI().getInventories(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public GetInventoryAPIResponse getInventoryDataByProductCode(long storeId, String deviceId, 
			String accessToken, long productCode) throws IOException {
		Call<GetInventoryAPIResponse> response = getUploadSyncAPI().getInventoriesByProductCode(productCode, storeId, 
				deviceId, accessToken);
		return response.execute().body();
	}
	
	public GetCustomerAPIResponse getCustomerList(long storeId, String deviceId, 
			String accessToken, String start) throws IOException {
		Call<GetCustomerAPIResponse> response = getUploadSyncAPI().getCustomers(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public ApiCustomer getCustomerByPhone(long storeId, String deviceId, 
			String accessToken, long phoneNo) throws IOException {
		Call<ApiCustomer> response = getUploadSyncAPI().getCustomerByPhone(phoneNo, storeId, deviceId, 
				accessToken);
		return response.execute().body();
	}
	
	public GetCustomerMonthlySummaryResonse getCustomerMonthlySummaryList(long storeId, String deviceId, 
			String accessToken, String start) throws IOException {
		Call<GetCustomerMonthlySummaryResonse> response = getUploadSyncAPI().getCustomerMonthlySummary(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public ApiCustomerMonthlySummary getCustomerMonthlySummaryByPhoneAndMonth(long storeId, String deviceId, 
			String accessToken, String start, long phone, int month) throws IOException {
		Call<ApiCustomerMonthlySummary> response = getUploadSyncAPI().getCustomerMonthlySummaryByPhoneAndMonth(phone, month, storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public GetCustomerDetailsAPIResponse getCustomerDetailsListAPI(long storeId, String deviceId, 
			String accessToken, String start) throws IOException {
		Call<GetCustomerDetailsAPIResponse> response = getUploadSyncAPI().getCustomerDetailsList(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public ApiCustomerDetails getCustomerDetailsByPhoneAPI(long storeId, String deviceId, 
			String accessToken, long phoneNo) throws IOException {
		Call<ApiCustomerDetails> response = getUploadSyncAPI().getCustomerDetailsByPhone(phoneNo, storeId, deviceId, 
				accessToken);
		return response.execute().body();
	}
	
	public GetProductListAPIResponse getProductListAPI(long storeId, String deviceId, String accessToken, String start) throws IOException {
		Call<GetProductListAPIResponse> response = getUploadSyncAPI().getProductList(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public GetProductPackListAPIResponse getProductPackListAPI(long storeId, String deviceId, 
			String accessToken, String start) throws IOException {
		Call<GetProductPackListAPIResponse> response = getUploadSyncAPI().getProductPackList(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public GetInviceListAPIResponse getInvoiceListAPI(long storeId, String deviceId, String accessToken, String start) throws IOException {
		Call<GetInviceListAPIResponse> response = getUploadSyncAPI().getInvoiceList(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
	
	public GetTransactionsListAPIResponse getTransactionsListAPI(long storeId, String deviceId, 
			String accessToken, String start) throws IOException {
		Call<GetTransactionsListAPIResponse> response = getUploadSyncAPI().getTransactionsList(storeId, deviceId, 
				accessToken, start);
		return response.execute().body();
	}
}
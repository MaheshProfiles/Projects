package com.snapbizz.v2.sync;

import java.io.IOException;
import android.content.Context;

import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;
import com.snapbizz.v2.sync.LocalSyncData.ApiOtpGeneration;
import com.snapbizz.v2.sync.LocalSyncData.DefaultAPIResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ToolsAPI {
	private Retrofit retrofit = null;
	private Context context;
	
	public ToolsAPI(Context context) {
		this.context = context;
		retrofit = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_V2 + Long.parseLong(SnapSharedUtils.getStoreId(context)) + "/");
	}
	
	private interface ToolsAPIHelper {
		@GET("visibility_status")
		Call<DefaultAPIResponse> sendVisibilityStatus(@Query ("store_id") long storeId, 
													  @Query ("device_id") String deviceId, 
													  @Query ("access_token") String accessToken);
	}
	
	private ToolsAPIHelper getToolsAPI() {
		return retrofit.create(ToolsAPIHelper.class);
	}
	
	public DefaultAPIResponse callVisibilityStatus() throws IOException {
		Call<DefaultAPIResponse> response = getToolsAPI().sendVisibilityStatus(Long.parseLong(SnapSharedUtils.getStoreId(context)), 
				SnapCommonUtils.getDeviceID(SnapCommonUtils.getSnapContext(context)), SnapSharedUtils.getStoreAuthKey(context));
		return response.execute().body();
	}
}

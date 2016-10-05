package com.snapbizz.v2.sync;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snapbizz.snapbilling.domainsV2.ApkData;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

public class UpgradeVersion implements GlobalSync.GlobalSyncService {
	private final static String TAG = "[UpgradeVersion]";
	private List<ApkData.AppVersionCode> response = null;
	private ApkData.ApkURL apkResponse = null;
	private static final String APK_FILE_DOWNLOAD = "/Upgrade";
	private static final int APK_SYNC_INTERVAL = 60 * 60 * 3;
	private static final int APK_FILE_BUFFER = 8192;
	private static final String REQUEST_HEADER_FIELD = "Range";
	private static final int SLEEP_TIME = 20000;		// 20 sec
	private Thread downloadThread = null;
	private Context context;
	private String baseUrl = SnapBillingConstants.BASE_URL_V2;
	
	public UpgradeVersion(Context context) {
		this.context = context;
	}
	
	@Override
	public Long startSync(long currentTime, long lastSyncTime, GlobalDB gdb, SnapbizzDB sdb) {
		Retrofit syncRetrofit = ToolkitV2.buildRetrofit(baseUrl + SnapSharedUtils.getStoreId(context) + "/");
		try {
			try {
				List<ApkData.CurrentAppVersions> currentAppVersionArray = new ArrayList<ApkData.CurrentAppVersions>();
				for (String packageName : SnapBillingConstants.APK_PACKAGES) {
					if (isPackageInstalled(packageName, context)) {
						ApkData.CurrentAppVersions currentAppVersions = new ApkData.CurrentAppVersions(packageName,
								SnapCommonUtils.getCurrentVersionCode(context, packageName));
						currentAppVersionArray.add(currentAppVersions);
					}
				}
				response = getNewVersionInfo(syncRetrofit, currentAppVersionArray);
				if (response != null) {
					Gson gson = new GsonBuilder().excludeFieldsWithModifiers(
							Modifier.TRANSIENT).create();
					SnapSharedUtils.setVersionCodeDetails(context,
							gson.toJson(response));
					apkUpgradeDetails(response);
					return currentTime;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	

	@Override
	public long getSyncInterval() {
		return APK_SYNC_INTERVAL;
	}
	
	private void deleteAllFiles(File folder) {
		if (!folder.exists() || !folder.isDirectory())
			return;
		String[] files = folder.list();
		if (files != null) {
			for (String file : files)
				new File(folder, file).delete();
		}
	}

	private void apkUpgradeDetails(List<ApkData.AppVersionCode> versionCodes) {
		String appName = null;
		String upgradeBefore = null;
		int currentVersion = 0, proposedVersion = 0;
		Map<String, Integer> dataToDownloadApks = new HashMap<String, Integer>();
		for (int i = 0; i < versionCodes.size(); i++) {
			currentVersion = versionCodes.get(i).current_version_code;
			proposedVersion = versionCodes.get(i).proposed_version_code;
			appName = versionCodes.get(i).package_name;
			upgradeBefore = versionCodes.get(i).upgrade_before;
			
			if (upgradeBefore != null && currentVersion < proposedVersion) {
				dataToDownloadApks.put(appName, proposedVersion);
			}
		}
		if (dataToDownloadApks.size() <= 0) {
			// Clear the folder
			deleteAllFiles(new File(SnapToolkitConstants.DB_EXTPATH, APK_FILE_DOWNLOAD));
		} else if (downloadThread == null) {
			downloadThread = new Thread(new GetApkThread(dataToDownloadApks));
			downloadThread.start();
		}
	}

	private class GetApkThread implements Runnable {
		Map<String, Integer> dataToDownloadApks;
		
		public GetApkThread(Map<String, Integer> dataToDownloadApks) {	
			this.dataToDownloadApks = dataToDownloadApks;
		}

		@Override
		public void run() {
			Retrofit apkRetrofit = ToolkitV2.buildRetrofit(baseUrl + SnapSharedUtils.getStoreId(context) + "/");
			while(true) {
				try {
					Thread.sleep(SLEEP_TIME);
					if (!SnapCommonUtils.isNetworkAvailable(context)) {
						Thread.sleep(SLEEP_TIME);
						continue;
					}

					for (Map.Entry<String, Integer> entry : dataToDownloadApks.entrySet()) {
						apkResponse = getApkURL(apkRetrofit, entry.getKey());
						String[] packageNameSplit = entry.getKey().split("\\.");
						if (apkResponse != null && apkResponse.status == null) {
							downloadApkFile(apkResponse.apk_url,
											packageNameSplit[packageNameSplit.length - 1],
											entry.getValue(), apkResponse.apk_size);
						} else {
							throw new Exception();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					// Stop thread if interrupted
				} catch (Exception e) {
					continue;
				}
				break;
			}
			downloadThread = null;
		}
	}

	private void downloadApkFile(String urlString, String appName, int version, Long size) {
		while (true) {
			try {
				Thread.sleep(SLEEP_TIME);
				if (!SnapCommonUtils.isNetworkAvailable(context)) {
					Thread.sleep(SLEEP_TIME);
					continue;
				}
				int count;
				File upgradeFolder = new File(SnapToolkitConstants.DB_EXTPATH, APK_FILE_DOWNLOAD);
				if (!upgradeFolder.exists())
					upgradeFolder.mkdir();
				File apkFile = new File(SnapToolkitConstants.UPGRADE_EXTPATH, appName + "_" + version + ".apk");
				URL downloadUrl = new URL(urlString);
				URLConnection connection = downloadUrl.openConnection();
				if (!apkFile.exists()) {
					apkFile.createNewFile();
				} else {
					// Already downloaded
					if(size != null && size <= apkFile.length()) {
						Log.d(TAG, "File already downloaded: "+appName+":"+apkFile.length());
						return;
					}
					Log.d(TAG, "Resuming download: "+appName+":"+apkFile.length());
					connection.setRequestProperty(REQUEST_HEADER_FIELD, "bytes=" + apkFile.length() + "-");
				}
				connection.setConnectTimeout(SnapBillingConstants.DEFAULT_CONNECTION_TIMEOUT*1000);
				connection.setReadTimeout(SnapBillingConstants.DEFAULT_READ_TIMEOUT*1000);
				connection.connect();
				InputStream input = new BufferedInputStream(connection.getInputStream(), APK_FILE_BUFFER);
				OutputStream output = new FileOutputStream(apkFile, true);
				byte data[] = new byte[APK_FILE_BUFFER];
				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				continue;
			}
			break;
		}
	}

	private static interface NewVersionAPI {
		@POST("app_version")
		Call<List<ApkData.AppVersionCode>> generate(
				@Body List<ApkData.CurrentAppVersions> currentAppVersionArray,
				@Query("store_id") String storeId, @Query("device_id") String deviceId,
				@Query("access_token") String accessToken);
	}

	private List<ApkData.AppVersionCode> getNewVersionInfo(Retrofit retrofit, List<ApkData.CurrentAppVersions> currentAppVersionJson)
			throws IOException {
		NewVersionAPI newVersionAPI = retrofit.create(NewVersionAPI.class);
		Call<List<ApkData.AppVersionCode>> call = newVersionAPI.generate(currentAppVersionJson, 
				SnapSharedUtils.getStoreId(context),
				SnapSharedUtils.getDeviceId(context),
				SnapSharedUtils.getStoreAuthKey(context));
		return call.execute().body();
	}

	private static interface ApkUrlAPI {
		@GET("apk_download")
		Call<ApkData.ApkURL> generate(@Query("package_name") String packageName,
				@Query("store_id") String storeId, @Query("device_id") String deviceId,
				@Query("access_token") String accessToken);
	}

	private ApkData.ApkURL getApkURL(Retrofit retrofit, String packageName) throws IOException {
		ApkUrlAPI apkUrlAPI = retrofit.create(ApkUrlAPI.class);
		Call<ApkData.ApkURL> call = apkUrlAPI.generate(packageName, SnapSharedUtils.getStoreId(context),
				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context));
		return call.execute().body();
	}
	
	private boolean isPackageInstalled(String packagename, Context context) {
	    try {
	    	PackageManager pm = context.getPackageManager();
	        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        return true;
	    } catch (NameNotFoundException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}

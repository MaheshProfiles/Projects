package com.snapbizz.snaptoolkit.asynctasks;

import java.util.ArrayList;
import java.util.List;


import com.snapbizz.snaptoolkit.domains.AppVersion;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class GetAppVersionTask  extends AsyncTask<Void, Void, List<AppVersion>> {
	
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Unable to get State details.";
	private Context context;
	private int taskCode;
	
	public GetAppVersionTask(Context context,
			int taskCode, OnQueryCompleteListener onQueryCompleteListener) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}

	@Override
	protected List<AppVersion> doInBackground(Void... arg0) {
		Log.d("TAG", "appVersionList--------->doInBackground");
		 List<AppVersion> myList = new ArrayList<AppVersion>();
		try {
			 AppVersion appVersion=new AppVersion();
			 String billingVersion="";
			 String inventoryVersion ="";
			 String dashboardVersion="";
			 String visibilityVersion="";
			 String pushOffersVersion="";
			 PackageInfo billingPackageInfo =null;
			 PackageInfo inventoryPackageInfo=null;
			 PackageInfo visibilityPackageInfo=null;
			 PackageInfo pushOffersPackageInfo=null;
			 PackageInfo dashboardPackageInfo=null;
			 
			 String tabletDbId=SnapToolkitConstants.DB_VERSION+"";
			 Log.d("TAG", "appVersionList--------->doInBackground--Step1");
			 try {
			  billingPackageInfo = context.getPackageManager().getPackageInfo(SnapToolkitConstants.SNAP_PACKAGENAME, 0);
			 } catch (Exception e) {}
			 
			 Log.d("TAG", "appVersionList--------->doInBackground--Step1-billingPackageInfo="+billingPackageInfo);
			 try {
			  inventoryPackageInfo = context.getPackageManager().getPackageInfo(SnapToolkitConstants.SNAP_INVENTORY_PACKAGENAME, 0);
			 } catch (Exception e) {}
			 Log.d("TAG", "appVersionList--------->doInBackground--Step1-inventoryPackageInfo="+inventoryPackageInfo);
			 try {
			  visibilityPackageInfo = context.getPackageManager().getPackageInfo(SnapToolkitConstants.SNAP_VISIBILITY_PACKAGENAME, 0);
			 } catch (Exception e) {}
			 try {
			  pushOffersPackageInfo = context.getPackageManager().getPackageInfo(SnapToolkitConstants.SNAP_PUSHOFFERS_PACKAGENAME, 0);
			 } catch (Exception e) {}
			 try {
			  dashboardPackageInfo = context.getPackageManager().getPackageInfo(SnapToolkitConstants.SNAP_DASHBOARD_PACKAGENAME, 0);
			 } catch (Exception e) {}

			 if(billingPackageInfo!=null){
				 billingVersion=billingPackageInfo.versionName;
			 }
			 if(inventoryPackageInfo!=null){
				 inventoryVersion = inventoryPackageInfo.versionName;
			 }
			 if(dashboardPackageInfo!=null){
				 dashboardVersion=dashboardPackageInfo.versionName;
			 }
			 if(visibilityPackageInfo!=null){
				 visibilityVersion=visibilityPackageInfo.versionName;
			 }
			 if(pushOffersPackageInfo!=null){
				 pushOffersVersion=pushOffersPackageInfo.versionName;
			 }
			 
			 tabletDbId=SnapToolkitConstants.DB_VERSION+"";
			 
			 Log.d("TAG", "appVersionList--------->doInBackground---1");
			 
			 if(checkVersionChange(billingVersion,inventoryVersion,dashboardVersion,visibilityVersion,pushOffersVersion,tabletDbId)){
				 Log.d("TAG", "appVersionList--------->doInBackground---2");
				 appVersion.setBillingVersion(billingVersion);
				 appVersion.setDashboardVersion(dashboardVersion);
				 appVersion.setInventoryVersion(inventoryVersion);
				 appVersion.setPushOffersVersion(pushOffersVersion);
				 appVersion.setTabletDbId(tabletDbId);
				 appVersion.setVisibilityVersion(visibilityVersion);
				 myList.add(appVersion);
				 SnapSharedUtils.storeLastInvoiceVersionName(context,inventoryVersion);
				 SnapSharedUtils.storeLastInvoiceVersionName(context,billingVersion);
				 SnapSharedUtils.storeLastVisibilityVersionName(context,visibilityVersion);
				 SnapSharedUtils.storeLastPushoffersVersionName(context,pushOffersVersion);
				 SnapSharedUtils.storeLastDashboardVersionName(context,dashboardVersion);
				 SnapSharedUtils.storeLastTabletDbId(context,tabletDbId);
			 }
			
		} catch (Exception e) {
			Log.e("TAG", "appVersionList--------->Exception--",e);
		}
		
		return myList;
	}
	
	@Override
	protected void onPostExecute( List<AppVersion> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
	
	private boolean checkVersionChange(String billingVersion,String inventoryVersion,String dashboardVersion,String visibilityVersion,String pushOffersVersion,String tabletDbId){
		String lastInvoiceVersionName=SnapSharedUtils.getLastInvoiceVersionName(context);
		String lastBillingVersionName=SnapSharedUtils.getLastBillingVersionName(context);
		String lastVisibilityVersionName=SnapSharedUtils.getLastVisibilityVersionName(context);
		String lastPushoffersVersionName=SnapSharedUtils.getLastPushoffersVersionName(context);
		String lastDashboardVersionName=SnapSharedUtils.getLastDashboardVersionName(context);
		String lastTabletDbId=SnapSharedUtils.getLastTabletDbId(context);
		
		if((lastInvoiceVersionName.equals("")||!inventoryVersion.equals(lastInvoiceVersionName))||
				(lastBillingVersionName.equals("")||!billingVersion.equals(lastBillingVersionName))||
				(lastVisibilityVersionName.equals("")||!visibilityVersion.equals(lastVisibilityVersionName))||
				(lastPushoffersVersionName.equals("")||!pushOffersVersion.equals(lastPushoffersVersionName))||
				(lastDashboardVersionName.equals("")||!dashboardVersion.equals(lastDashboardVersionName))||
				(lastTabletDbId.equals("")||!lastTabletDbId.equals(tabletDbId))){
			return true;
			
		}
		
		return false;
	}

}

package com.snapbizz.snapstock.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.snapbizz.snapstock.utils.SnapInventoryConstants;

public class SummaryWidgetIntentReceiver extends BroadcastReceiver{
	private static final String TAG = SummaryWidgetIntentReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,  TAG + ":onReceive()");
		
		Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
		PackageManager manager = context.getPackageManager();
		launcherIntent = manager.getLaunchIntentForPackage(context.getPackageName());
		launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		launcherIntent.putExtra(SnapInventoryConstants.INTENT_EXTRA_KEY_SHOW_DASHBOARD, SnapInventoryConstants.INTENT_EXTRA_SHOW_DASHBOARD);
		context.startActivity(launcherIntent);
	}
}

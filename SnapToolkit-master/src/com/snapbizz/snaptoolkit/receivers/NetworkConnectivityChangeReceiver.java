package com.snapbizz.snaptoolkit.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.snapbizz.snaptoolkit.services.SnapSyncService;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class NetworkConnectivityChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
		if(isConnected) {
			Context commonContext = SnapCommonUtils.getSnapContext(context);
			Intent syncIntent = new Intent(commonContext, SnapSyncService.class);
	        commonContext.startService(syncIntent);
	        
		}

	}

}

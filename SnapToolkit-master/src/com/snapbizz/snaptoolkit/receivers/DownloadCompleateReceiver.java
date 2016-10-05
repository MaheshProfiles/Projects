package com.snapbizz.snaptoolkit.receivers;

import com.snapbizz.snaptoolkit.services.SaveCampaignImageService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadCompleateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("TAG", "Campaign Log-----  inside DownloadCompleateReceiver");
		Intent saveCampaignImageService = new Intent(context,SaveCampaignImageService.class);
		context.startService(saveCampaignImageService);
	}
}

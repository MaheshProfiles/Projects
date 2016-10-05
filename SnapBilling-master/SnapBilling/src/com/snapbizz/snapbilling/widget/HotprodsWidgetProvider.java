package com.snapbizz.snapbilling.widget;

import java.io.File;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;

public class HotprodsWidgetProvider extends AppWidgetProvider {
	private static final String TAG = HotprodsWidgetProvider.class
			.getSimpleName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, TAG + "::onUpdate");

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.hotprods_widget_layout);
		
		String path = SnapBillingUtils.getHotProdWidgetImagepath(context);
		File summaryImageFile = new File(path);
		if(summaryImageFile.exists())
			remoteViews.setImageViewUri(R.id.image_widget, Uri.fromFile(new File(path)));
		remoteViews.setOnClickPendingIntent(R.id.image_widget, buildButtonPendingIntent(context));

		pushWidgetUpdate(context, remoteViews);
	}
	
	public static PendingIntent buildButtonPendingIntent(Context context) {
		// initiate widget update request
		Intent intent = new Intent();
		intent.setAction(SnapBillingConstants.WIDGET_UPDATE_ACTION_HOTPRODS);
		return PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
		ComponentName myWidget = new ComponentName(context,
				HotprodsWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(myWidget, remoteViews);
	}
}

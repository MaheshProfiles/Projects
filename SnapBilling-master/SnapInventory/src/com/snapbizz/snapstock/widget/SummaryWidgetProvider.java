package com.snapbizz.snapstock.widget;

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

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;

public class SummaryWidgetProvider extends AppWidgetProvider {
	private static final String TAG = SummaryWidgetProvider.class
			.getSimpleName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, TAG + "::onUpdate");

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.summary_widget_layout);
		
		String path = SnapInventoryUtils.getSummaryWidgetImagePath(context);
		File summaryImageFile = new File(path);
		if(summaryImageFile.exists())
			remoteViews.setImageViewUri(R.id.image_widget, Uri.fromFile(new File(path)));
		remoteViews.setOnClickPendingIntent(R.id.image_widget, buildButtonPendingIntent(context));

		pushWidgetUpdate(context, remoteViews);
	}
	
	public static PendingIntent buildButtonPendingIntent(Context context) {
		// initiate widget update request
		Intent intent = new Intent();
		intent.setAction(SnapInventoryConstants.WIDGET_UPDATE_ACTION_SUMMARY);
		return PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
		ComponentName myWidget = new ComponentName(context,
				SummaryWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(myWidget, remoteViews);
	}
}

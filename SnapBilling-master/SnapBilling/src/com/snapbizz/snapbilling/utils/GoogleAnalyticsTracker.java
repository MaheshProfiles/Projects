package com.snapbizz.snapbilling.utils;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GoogleAnalyticsTracker {
	private EasyTracker easyTracker = null;
	public static GoogleAnalyticsTracker googleAnalyticsTracker = null;
	private String storeId;
	
	GoogleAnalyticsTracker(Context context) { 
		easyTracker = EasyTracker.getInstance(context);
		storeId = SnapSharedUtils.getStoreId(context);
	}
	
	public static GoogleAnalyticsTracker getInstance(Context context) {
	      if (googleAnalyticsTracker == null) {
	    	  if(!SnapToolkitConstants.PRODUCTION_BUILD)
	    		  GoogleAnalytics.getInstance(context).setDryRun(true);
	    	  googleAnalyticsTracker = new GoogleAnalyticsTracker(context);
	      }
	      return googleAnalyticsTracker;
	}

	public void startActivity(Activity activity) {
		this.easyTracker.activityStart(activity);
	}
	
	public void stopActivity(Activity activity) {
		this.easyTracker.activityStop(activity);
	}
	
	public void fragmentLoaded(String fragmentName, Context context) {
		this.easyTracker.set(Fields.SCREEN_NAME, fragmentName + " - " + this.storeId);
		this.easyTracker.send(MapBuilder.createAppView().build());	
	}
	
	public void onButtonClick(String eventType, String msg, Context context) {
		this.easyTracker.send((Map<String, String>) MapBuilder.createEvent(eventType, msg + " in store - " + this.storeId, 
				null, null).build());	
	}
	
	public static void sendGoogleAnalyticsOnView(Context context, View v , String className) {
		if (v instanceof Button && !((Button)v).getText().toString().trim().isEmpty())
			getInstance(context).onButtonClick(context.getString(R.string.button_clicked),
					((Button)v).getText() + " " + className, context);
		else if (v instanceof TextView && !((TextView)v).getText().toString().trim().isEmpty())
			getInstance(context).onButtonClick(context.getString(R.string.button_clicked),
					((TextView)v).getText() + " " + className, context);
		else
			getInstance(context).onButtonClick(context.getString(R.string.button_clicked),
					context.getResources().getResourceEntryName(v.getId()) + " " + className, context);
	}
	
	public static void sendGoogleAnalyticsOnView(Context context, MenuItem item , String className) {
		getInstance(context).onButtonClick(context.getString(R.string.menuitem_clicked), item.getTitle() + " " + className, context);
	}
	
	public static void sendGoogleAnalyticsOnView(Context context, int menuItemId , String className) {
		getInstance(context).onButtonClick(context.getString(R.string.menuitem_clicked),
				context.getResources().getResourceEntryName(menuItemId) + " " + className, context);
	}
	
	public static void sendGoogleAnalyticsOnString(Context context, String s , String className) {
		getInstance(context).onButtonClick(context.getString(R.string.button_clicked), s + " " + className, context);
    }	
}
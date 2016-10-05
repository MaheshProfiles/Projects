package com.snapbizz.snapbilling.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.Settings;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.DeviceState;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public abstract class SnapActivity extends Activity implements OnQueryCompleteListener {

	private boolean hasBeenChecked = false;
	private static AlertDialog alertDialog;
	private Context activityContext;
	List<Settings> reteriveSettingsdata = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
	SQLiteDatabase.loadLibs(this);
	Log.d("TAG","App Strated---getStoreApiKey----->"+SnapSharedUtils.getStoreApiKey(SnapCommonUtils.getSnapContext(this)));
	if (SnapSharedUtils
			.getStoreAuthKey(SnapCommonUtils.getSnapContext(this)).isEmpty()) {
		Intent registerationIntent = new Intent(this,
				RetailerRegistrationActivity.class);
		registerationIntent.putExtra(SnapToolkitConstants.ACTIVITY_KEY,
				this.getClass().getName());
		startActivity(registerationIntent);
		finish();
	} else {
		activityContext = SnapCommonUtils.getSnapContext(this);
		if (SnapSharedUtils.getDeviceState(activityContext).ordinal() == DeviceState.DISABLED
				.ordinal()) {
			SnapCommonUtils.showAlert(this,
					SnapToolkitConstants.DEVICE_DISABLED_TITLE,
					SnapToolkitConstants.DEVICE_DISABLED_MESSAGE,
					onDeviceClickListener, onDeviceClickListener, true);
		} else {
			Date lastSyncDate = SnapSharedUtils
					.getDeviceLastSyncTime(activityContext);
			if (lastSyncDate != null) {
				int days = (int) ((Calendar.getInstance().getTimeInMillis() - lastSyncDate
						.getTime()) / 86400000);
				if (days >= SnapToolkitConstants.MAX_UNSYNCED_DAYS) {
					SnapCommonUtils.showAlert(this,
							SnapToolkitConstants.DEVICE_DISABLED_TITLE,
							SnapToolkitConstants.DEVICE_UNSYNCED_MESSAGE,
							onDeviceSyncClickListener,
							onDeviceSyncClickListener, true);
				}
			}
		}
	}
		
	}

	View.OnClickListener onDeviceClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			SnapCommonUtils.dismissAlert();
			finish();
		}
	};

	View.OnClickListener onDeviceSyncClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			SnapCommonUtils.dismissAlert();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (hasBeenChecked) {
			if (alertDialog != null)
				alertDialog.dismiss();
			checkUpdateStatus();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void checkUpdateStatus() {
		String currentBillingversionCode = null;
		String billingVersion = null;
		String currentStockversionCode = null;
		String stockVersion = null;
		billingVersion = SnapSharedUtils.getBillingAppVersion(SnapCommonUtils
				.getSnapContext(this));
		stockVersion = SnapSharedUtils.getStockAppVersion(SnapCommonUtils
				.getSnapContext(this));
		try {
			currentBillingversionCode = getPackageManager().getPackageInfo(
					"com.snapbizz.snapbilling", 0).versionCode
					+ ".0";
			currentStockversionCode = getPackageManager().getPackageInfo(
					"com.snapbizz.snapstock", 0).versionCode
					+ ".0";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (billingVersion != null && currentBillingversionCode != null)
			if (Float.parseFloat(currentBillingversionCode) < Float
					.parseFloat(billingVersion)) 
				checkAndShowUpdateVersionAlert(SnapSharedUtils.getLastAvailableBillingUpdateTime(SnapCommonUtils
															  .getSnapContext(this)));
			if (stockVersion != null && currentStockversionCode != null)
				checkAndShowUpdateVersionAlert(SnapSharedUtils.getLastAvailableStockUpdateTime(SnapCommonUtils
															  .getSnapContext(this)));
	}

	OnClickListener onUpdateAppClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String authKey = SnapSharedUtils.getStoreAuthKey(SnapActivity.this);
			if (authKey.isEmpty())
				return;
		}
	};

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {

	}

	public static void showAlert(Context context, String title, String message,
			OnClickListener positiveClickListener,
			OnClickListener negativeClickListener, boolean isNotCancelable) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(!isNotCancelable);
		if (positiveClickListener == null) {
			positiveClickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
		}
		builder.setPositiveButton(context.getString(android.R.string.ok),
				positiveClickListener);
		if (negativeClickListener != null) {
			builder.setNegativeButton(
					context.getString(android.R.string.cancel),
					negativeClickListener);
		}
		if (!((Activity) context).isFinishing()) {
			alertDialog = builder.create();
			alertDialog.show();
		}
	}
	
	public void checkAndShowUpdateVersionAlert(String lastUpdateTime) {
		Date lastDate = null;
		Date currentDate = Calendar.getInstance().getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT);
		if (lastUpdateTime != null) {
			try {
				lastDate = dateFormat.parse(lastUpdateTime);
				if (daysBetween(lastDate, currentDate) > 0) {
					// Display Alert Dialog here
					showAlert(this, "Update App",
							getResources().getString(R.string.update_app_version_msg),
							onUpdateAppClickListener, null, true);
					return;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
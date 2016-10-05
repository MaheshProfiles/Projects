package com.snapbizz.snapstock;

import java.io.File;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.snapbizz.snapstock.asynctasks.UpgradeAppTask;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.activity.SplashscreenActivity;
import com.snapbizz.snaptoolkit.asynctasks.SaveVideoTask;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class InventorySplashscreenActivity extends SplashscreenActivity {
	private View dialogueView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		crittercismId = SnapToolkitConstants.INVENTORY_CRITTERCISM_ID;
	    startingActivity = InventoryActivity.class;
		super.onCreate(savedInstanceState);
	}

    @Override
    public void upgradeVersion(int oldVersion, int newVersion) {
        new UpgradeAppTask(this, this, oldVersion, newVersion, UPGRADE_APP_TASKCODE).execute();
    }
    
    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        super.onTaskSuccess(responseList, taskCode);
        if(taskCode == UPGRADE_APP_TASKCODE) {
        	if (!SnapInventoryUtils.isInventoryHelpVideosStored(this)) {
        		try {
        			File inventoryDirectory = new File(SnapToolkitConstants.HELP_VIDEO_INVENTORY_PATH);
        			if(!inventoryDirectory.exists()){
        				inventoryDirectory.mkdirs();
        			}
                	String inventoryList[];
                    inventoryList = this.getAssets().list("inventory");
            		if(inventoryList!=null){
            			for( int i=0; i< inventoryList.length; i++)
            			{
            				InputStream mInput = this.getAssets().open("inventory/"+inventoryList[i]);
            				new SaveVideoTask(inventoryList[i], "inventory").execute(mInput);
            			}
            		}
            		SnapInventoryUtils.storeIsInventoryHelpVideosStored(this, true);
                } catch (Exception e){
                	e.printStackTrace();
                }
        	}
        	
            if(!checkBillingVersion())
                return;

            if (SnapSharedUtils.getLastStoredPin(SnapCommonUtils.getSnapContext(this)) != null && !SnapSharedUtils.getLastStoredPin
                    (SnapCommonUtils.getSnapContext(this)).trim().equals("")) {
                dialogueView =SnapCommonUtils.showPinAlert(InventorySplashscreenActivity.this,positiveDeleteClickListener,
                        negativeDeleteClickListener, true);
            } else {
                startActivity();
            }
        }
    }
    OnClickListener positiveDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			TextView alertErrorTextview = (TextView)dialogueView.findViewById(R.id.alert_error_textview);
			EditText pinEdittext = (EditText) dialogueView.findViewById(R.id.pin_edittext);
			String pin=pinEdittext.getText().toString().trim();
			if (pinEdittext.getText()
					.toString()
					.equals(SnapSharedUtils.getLastStoredPin(SnapCommonUtils
							.getSnapContext(InventorySplashscreenActivity.this)))) {
				
				SnapCommonUtils.dismissAlert();
				dialogueView=null;
				startActivity();
			}else{
				alertErrorTextview.setVisibility(View.VISIBLE);
			}
		}
	};
	
	OnClickListener negativeDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
				SnapCommonUtils.dismissAlert();
				dialogueView=null;
				finish();
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SnapCommonUtils.dismissAlert();
	}

	@Override
	protected void onStop() {
		super.onStop();
		SnapCommonUtils.dismissAlert();
	}
	
	private boolean checkBillingVersion(){
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info;
			info = manager.getPackageInfo(SnapToolkitConstants.SNAP_PACKAGENAME, 0);
			int billingVersion = info.versionCode;
			if(SnapToolkitConstants.BILLING_VERSION < billingVersion) {
				showVersionWarning(R.string.upgrade_inventory);
				return false;	
			} else if(SnapToolkitConstants.BILLING_VERSION > billingVersion) {
				showVersionWarning(R.string.upgrade_billing);
				return false;	
			}
			int billingDBVersion=0;
	         try {
	        	 Context con = createPackageContext(SnapToolkitConstants.SNAP_PACKAGENAME, 0);
	             SharedPreferences pref = con.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE, Context.MODE_PRIVATE);
	             billingDBVersion = Integer.parseInt(pref.getString(SnapToolkitConstants.TABLET_DB_VERSION, "0"));
	         }
	         catch (NameNotFoundException e) {
	             Log.e("No data ", e.toString());
	         }
	         if(SnapToolkitConstants.DB_VERSION < billingDBVersion) {
	        	 showVersionWarning(R.string.upgrade_inventory);
				return false;	
			 } /*else if(SnapToolkitConstants.DB_VERSION > billingDBVersion) {
				showVersionWarning(R.string.restart_billing);
				return false;	
			 }*/
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	private void showVersionWarning(int msg) {
			AlertDialog dialog = new AlertDialog.Builder(InventorySplashscreenActivity.this).create();
	        dialog.setMessage(getResources().getString(msg));
	        dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
	}
}

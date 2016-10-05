package com.snapbizz.snaptoolkit.activity;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.crittercism.app.CrittercismConfig;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.asynctasks.CopyDbTask;
import com.snapbizz.snaptoolkit.asynctasks.CreateDbTask;
import com.snapbizz.snaptoolkit.asynctasks.OtpGenerateTask;
import com.snapbizz.snaptoolkit.asynctasks.PopulateProdSkuVatTask;
import com.snapbizz.snaptoolkit.asynctasks.PopulateSellingPriceTask;
import com.snapbizz.snaptoolkit.asynctasks.RestoreDbTask;
import com.snapbizz.snaptoolkit.asynctasks.UpdateBrandListTask;
import com.snapbizz.snaptoolkit.asynctasks.UpdateProductSkuListTask;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapDBUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public abstract class SplashscreenActivity extends Activity implements
		OnQueryCompleteListener, OnServiceCompleteListener {
	protected final int CREATE_DB_TASKCODE = 0;
	protected final int CREATE_DBNEW_TASKCODE = 2;
	protected final int COPY_DB_TASKCODE = 1;
	protected final int RESTORE_DB_TASKCODE = 16;
	protected final int UPGRADE_APP_TASKCODE = 5;
	protected final int GET_STORE_STATE_TASKCODE = 7;
	private final int UPDATE_PRODUCT_SKU_LIST_TASKCODE = 11;
	private final int POPULATE_VAT_TASKCODE = 12;
	private final int POPULATE_MRP_TASKCODE = 13;
	private final int POPULATE_BRAND_TASKCODE = 15;
	private final int OTP_GENERATE_TASKCODE = 17;
	private final int OTP_VERIFICATION_TASKCODE = 18;
	private static final int OTP_GENERATE_LIMIT = 3;
	protected String accessToken;
	protected String storeId;
	protected String deviceId;
	protected Context commonContext;
	protected Class<?> startingActivity;
	protected final int SET_BRAND_FLAG = 4;
	private UpdateProductSkuListTask mUpdateProductSkuListTask;
	private PopulateProdSkuVatTask mPopulateProdSkuVatTask;
	private PopulateSellingPriceTask mPopulateSellingPriceTask;
	private Integer otpVerifyCount = 0;
	private Dialog dialog = null;
	private EditText etOtpInput = null;
	protected CopyDbTask copyDbTask;
	protected RestoreDbTask restoreDbTask;
	protected CreateDbTask createDbTask;
	protected boolean isBillingApp = false;
	protected String crittercismId = SnapToolkitConstants.TOOLKIT_CRITTERCISM_ID;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		SQLiteDatabase.loadLibs(this);
		this.commonContext = SnapCommonUtils.getSnapContext(this);
		accessToken = SnapSharedUtils.getStoreAuthKey(commonContext);
		storeId = SnapSharedUtils.getStoreId(commonContext);
		deviceId = SnapSharedUtils.getDeviceId(commonContext);
		CrittercismConfig config = new CrittercismConfig();
		config.setLogcatReportingEnabled(true);
		Crittercism.setLoggingLevel(Crittercism.LoggingLevel.Error);
		Crittercism.initialize(getApplicationContext(), crittercismId, config);
        if(storeId != null && !storeId.isEmpty())
			Crittercism.setUsername(storeId);
		else
			Crittercism.setUsername("0");
		if (SnapSharedUtils.getStoreApiKey(commonContext) != null) {
			Log.d("time", "Copy DB task is going to work");
			new CreateDbTask(this, this, CREATE_DB_TASKCODE).execute();
			Log.d("time",
					new SimpleDateFormat(
							SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
							Locale.getDefault()).format(Calendar.getInstance()
							.getTime()));
			SnapCommonUtils.getDrawableCache();
			File restoreFile = new File(SnapToolkitConstants.DB_RESTORE_EXTPATH);
			if (!restoreFile.exists()) {
				restoreFile.mkdirs();
			}
			File restoreDBFile = new File(
					SnapToolkitConstants.DB_RESTORE_EXTPATH
							+ SnapToolkitConstants.DB_NAME);
			File v2DBRestoreFile = new File(
					SnapToolkitConstants.DB_RESTORE_EXTPATH
							+ SnapToolkitConstants.DB_NAME_V2);
			if(isBillingApp) {
				try {
					if (restoreDBFile.exists() || v2DBRestoreFile.exists()) {
						if(SnapCommonUtils.isNetworkAvailable(SplashscreenActivity.this)){
							new OtpGenerateTask(SplashscreenActivity.this , SplashscreenActivity.this,
									 OTP_GENERATE_TASKCODE).execute("");
						}
						else{
							SnapCommonUtils.showErrorDialogAndFinish(SplashscreenActivity.this, 
									this.getResources().getString(R.string.no_network));
	
						}
	
					} else {
						copyDbTask = new CopyDbTask(this, this, COPY_DB_TASKCODE);
						copyDbTask.execute();
					}
				} catch (Exception e) {
					Log.e("SplashscreenActivity", "restoreDBFile", e);
				}
			} else {
				checkUpdateBrandList();
			}
		} else {
			new CreateDbTask(this, this, CREATE_DBNEW_TASKCODE).execute();
			onAppUpgrade();
		}

	}

	public void onAppUpgrade() {
		try {
			int currentVersion = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode;
			upgradeVersion(SnapSharedUtils.getLastVersionCode(this), currentVersion);
			SnapSharedUtils.storeVersionCode(this);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// new
		// DeleteUnsoldProductTask(this,DELETE_UNSOLD_PRODUCT_TASKCODE).execute();
	}

	@Override
	public void onBackPressed() {

	};
	
	private void checkUpdateBrandList() {
		Log.d("time",
				new SimpleDateFormat(
						SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
						Locale.getDefault()).format(Calendar.getInstance().getTime()));
		if (!SnapSharedUtils.isProdSkuListUpdated(commonContext)) {
			UpdateBrandListTask updateBrandListTask = new UpdateBrandListTask(this, this, POPULATE_BRAND_TASKCODE);
			updateBrandListTask.execute();
		} else {
			SnapDBUtils.checkAndUpdateVat(commonContext, SnapCommonUtils.getDatabaseHelper(commonContext), false);
			if(isBillingApp) {
				SnapDBUtils.addXtraProductQuickAddCategory(commonContext, SnapCommonUtils.getDatabaseHelper(commonContext), false);
				SnapDBUtils.checkAndUpdateLooseItemsUom(commonContext, SnapCommonUtils.getDatabaseHelper(commonContext), false);
			}
			onAppUpgrade();
			SnapCommonUtils.getDrawableCache();
		}
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		
		if (COPY_DB_TASKCODE == taskCode || RESTORE_DB_TASKCODE == taskCode) {
			checkUpdateBrandList();
		} else if (POPULATE_BRAND_TASKCODE == taskCode) {
			try {
				if (!SnapSharedUtils.isProdSkuListUpdated(commonContext)) {
					mUpdateProductSkuListTask = new UpdateProductSkuListTask(
							commonContext, this, this,
							UPDATE_PRODUCT_SKU_LIST_TASKCODE);
					mUpdateProductSkuListTask.execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (UPDATE_PRODUCT_SKU_LIST_TASKCODE == taskCode) {
			CustomToast.showCustomToast(this, getString(R.string.msg_no_products_added), Toast.LENGTH_SHORT, CustomToast.INFORMATION);
			try {
				QueryBuilder<State, Integer> stateQB = SnapCommonUtils
						.getDatabaseHelper(this).getStateDao().queryBuilder();
				State state = stateQB.where().eq("name",
								SnapSharedUtils.getStoreDetails(commonContext)
										.getState()).queryForFirst();
				
				mPopulateProdSkuVatTask = new PopulateProdSkuVatTask(this,
						this, POPULATE_VAT_TASKCODE, String.valueOf(state
								.getStateID()),null);
				mPopulateProdSkuVatTask.execute();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (POPULATE_VAT_TASKCODE == taskCode) {
			try {
				mPopulateSellingPriceTask = new PopulateSellingPriceTask(this,
						this, POPULATE_MRP_TASKCODE);
				mPopulateSellingPriceTask.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (POPULATE_MRP_TASKCODE == taskCode) {
			try {
				SnapCommonUtils
						.getDatabaseHelper(this)
						.getProductSkuDao()
						.executeRaw(
								"UPDATE transaction_details SET vat_rate = (SELECT product_sku.vat FROM product_sku WHERE transaction_details.sku_id = product_sku.sku_id)");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			onAppUpgrade();
		}
		else if(OTP_GENERATE_TASKCODE == taskCode) {
			showOTPDialog();
		}
		else if(OTP_VERIFICATION_TASKCODE == taskCode) {
			if(dialog != null) {
            	SnapCommonUtils.hideSoftKeyboard(SplashscreenActivity.this,
            			etOtpInput.getWindowToken());
				dialog.dismiss();
			 }
			 restoreDbTask = new RestoreDbTask(SplashscreenActivity.this,
					 SplashscreenActivity.this, RESTORE_DB_TASKCODE);
			            restoreDbTask.execute();
		}
		
	}

	public void startActivity() {
		Intent intent = new Intent(SplashscreenActivity.this, startingActivity);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if(POPULATE_BRAND_TASKCODE == taskCode) {
			SnapCommonUtils.forceCloseApp(this);
			return;
		}
		if(OTP_VERIFICATION_TASKCODE == taskCode) {
			if(etOtpInput != null)
				etOtpInput.setText("");

			CustomToast.showCustomToast(SplashscreenActivity.this,
					                    getString(R.string.wrong_password), Toast.LENGTH_SHORT,
					                    CustomToast.INFORMATION);
			
			if(otpVerifyCount < OTP_GENERATE_LIMIT) {
				otpVerifyCount++;
			} else {
				if(SnapCommonUtils.isNetworkAvailable(SplashscreenActivity.this)) {
					new OtpGenerateTask(SplashscreenActivity.this, SplashscreenActivity.this,
							 OTP_GENERATE_TASKCODE).execute("");
				} else{
					SnapCommonUtils.showErrorDialogAndFinish(SplashscreenActivity.this, 
							this.getResources().getString(R.string.no_network));
				}
				otpVerifyCount = 0;
			}
		} else if(OTP_GENERATE_TASKCODE == taskCode) {
			SnapCommonUtils.showErrorDialogAndFinish(SplashscreenActivity.this, errorMessage);
		} else if (POPULATE_BRAND_TASKCODE == taskCode) {
			SnapCommonUtils.forceCloseApp(this);
        } else {
            CustomToast.showCustomToast(getApplicationContext(), errorMessage,
					Toast.LENGTH_SHORT, CustomToast.INFORMATION);
        }
		
	}

	@Override
	public void onSuccess(ResponseContainer response) {

	}

	@Override
	public void onError(ResponseContainer response, RequestCodes requestCode) {

	}

	public abstract void upgradeVersion(int oldVersion, int newVersion);
	
	private void showOTPDialog() {
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();

        dialog = new Dialog(SplashscreenActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_db_pwd);
        etOtpInput = (EditText) dialog.findViewById(R.id.otp_edittext);
        etOtpInput.setFocusable(true);
        Button btCancelDialog, btConfirmDialog;
        btCancelDialog = (Button) dialog.findViewById(R.id.cancel_button);
        btConfirmDialog = (Button) dialog.findViewById(R.id.ok_button);

        btCancelDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btConfirmDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etOtpInput.getText().toString().equalsIgnoreCase("")) {
                    if(SnapCommonUtils.isNetworkAvailable(SplashscreenActivity.this)) {
                    	new OtpGenerateTask(SplashscreenActivity.this, SplashscreenActivity.this,
                    			            OTP_VERIFICATION_TASKCODE).execute(etOtpInput.getText().toString());
                    }
                    else {
                        SnapCommonUtils.showErrorDialogAndFinish(SplashscreenActivity.this,
                                                        getApplicationContext().getResources().getString(R.string.no_network));
                    }
                }
                else {
                    CustomToast.showCustomToast(SplashscreenActivity.this,
                                                getString(R.string.db_password), Toast.LENGTH_SHORT,
                                                CustomToast.INFORMATION);
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}

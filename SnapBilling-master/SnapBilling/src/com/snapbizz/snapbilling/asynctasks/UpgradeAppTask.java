package com.snapbizz.snapbilling.asynctasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.snapbizz.snaptoolkit.asynctasks.SaveVideoTask;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class UpgradeAppTask extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "unable to upgrade app";
	private int oldVersion;
	private int newVersion;

	public UpgradeAppTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int oldVersion, int newVersion, int taskCode) {
		this.context = SnapCommonUtils.getSnapContext(context);
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		upgradeVersion(oldVersion, newVersion);
		copyDb();
		return true;
	}

	public void copyDb() {
	    String DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        File dbFile = new File(DB_PATH+SnapToolkitConstants.DB_NAME);
        try {
            File file = new File(SnapToolkitConstants.DB_EXTPATH);
            boolean success = false;
            if (!file.exists()) {
                success = file.mkdirs();
            } else {
                success = true;
            }
            if(success) {
                FileInputStream fileInputStream = new FileInputStream(dbFile);
                OutputStream mOutput = new FileOutputStream(SnapToolkitConstants.DB_EXTPATH+SnapSharedUtils.getDbBackUpName(SnapCommonUtils.getSnapContext(context)));
                OutputStream mSyncOutput = new FileOutputStream(DB_PATH+SnapToolkitConstants.SYNC_DB_NAME);
                byte[] mBuffer = new byte[1024];
                int mLength;
                while ((mLength = fileInputStream.read(mBuffer)) > 0) {
                    mOutput.write(mBuffer, 0, mLength);
                    mSyncOutput.write(mBuffer, 0, mLength);
                }
                mOutput.flush();
                mOutput.close();
                mSyncOutput.flush();
                mSyncOutput.close();
                fileInputStream.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public void upgradeVersion(int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 8:
			upgradeVersion8(oldVersion, newVersion);
			break;
		default:
			if(++oldVersion <= newVersion){
				upgradeVersion(oldVersion, newVersion);

			}
			Log.e("oldVersion",oldVersion+"");
			break;
		}
	}
	
	public void upgradeVersion8(int oldVersion, int newVersion) {
		
	}

	public void upgradeVersion7(int oldVersion, int newVersion) {
		Context snapDataContext = SnapCommonUtils.getSnapDataContext(context);
		File file = new File(SnapToolkitConstants.PRODUCTS_FOLDER);
		if(!file.exists())
			file.mkdirs();
		file = new File(SnapToolkitConstants.BRANDS_FOLDER);
		if(!file.exists())
			file.mkdirs();
		file = new File(SnapToolkitConstants.COMPANY_FOLDER);
		if(!file.exists())
			file.mkdirs();
		file = new File(SnapToolkitConstants.PO_FOLDER);
		if(!file.exists())
			file.mkdirs();
		try {
			List<ProductSku> productSkuList = SnapCommonUtils.getDatabaseHelper(context).getProductSkuDao().queryForEq("is_gdb", false);
			for(ProductSku productSku : productSkuList) {
				try {
					if(productSku.getImageUrl() != null) {
						String imageUrl = SnapCommonUtils.storeImageBitmap(MediaStore.Images.Media.getBitmap(snapDataContext.getContentResolver(), Uri.parse(productSku.getImageUrl())), productSku);
						snapDataContext.getContentResolver().delete(Uri.parse(productSku.getImageUrl()), null, null);
						productSku.setImageUrl(imageUrl);
						SnapCommonUtils.getDatabaseHelper(context).getProductSkuDao().update(productSku);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		File directory = new File(SnapToolkitConstants.HELP_VIDEO_PATH);
		if(!directory.exists()){
			directory.mkdirs();
		}
		File billingDirectory = new File(SnapToolkitConstants.HELP_VIDEO_BILLING_PATH);
		if(!billingDirectory.exists()){
			billingDirectory.mkdirs();
		}
		File inventoryDirectory = new File(SnapToolkitConstants.HELP_VIDEO_INVENTORY_PATH);
		if(!inventoryDirectory.exists()){
			inventoryDirectory.mkdirs();
		}
		File pushofferDirectory = new File(SnapToolkitConstants.HELP_VIDEO_PUSHOFFERS_PATH);
		if(!pushofferDirectory.exists()){
			pushofferDirectory.mkdirs();
		}
		String billingList[];
		String inventoryList[];
		String pushOfferList[];
		try {
			billingList = context.getAssets().list("billing");
			if(billingList!=null){
				for( int i=0; i< billingList.length; i++)
				{
					InputStream mInput = context.getAssets().open("billing/"+billingList[i]);
					new SaveVideoTask(billingList[i], "billing").execute(mInput);
				}
			}
			inventoryList = context.getAssets().list("inventory");
			if(inventoryList!=null){
				for( int i=0; i< inventoryList.length; i++)
				{
					InputStream mInput = context.getAssets().open("inventory/"+inventoryList[i]);
					new SaveVideoTask(inventoryList[i], "inventory").execute(mInput);
				}
			}
			pushOfferList = context.getAssets().list("push offers");
			if(pushOfferList!=null){
				for( int i=0; i< pushOfferList.length; i++)
				{
					InputStream mInput = context.getAssets().open("push offers/"+pushOfferList[i]);
					new SaveVideoTask(pushOfferList[i], "push_offer").execute(mInput);
				}    

			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		upgradeVersion(++oldVersion, newVersion);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
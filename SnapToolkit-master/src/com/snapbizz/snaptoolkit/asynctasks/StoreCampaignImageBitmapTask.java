package com.snapbizz.snaptoolkit.asynctasks;

import java.io.InputStream;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class StoreCampaignImageBitmapTask extends AsyncTask<Void, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private final String errorMessage = "Unable to Store Image";
	private InputStream is;
	private String imageUrl;

	public StoreCampaignImageBitmapTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, InputStream is,int taskCode,String imageUrl) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.is = is;
		this.imageUrl=imageUrl;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean result;
		try{
			result= SnapCommonUtils.storeCampaignBitmap(BitmapFactory.decodeStream(is),imageUrl);
		}catch(Exception e){
			return false;
		}
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("TAG", "Campaign Log-----  inside StoreCampaignImageBitmapTask-status="+result+"");
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}


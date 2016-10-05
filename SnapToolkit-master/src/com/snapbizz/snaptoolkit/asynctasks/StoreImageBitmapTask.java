package com.snapbizz.snaptoolkit.asynctasks;

import java.io.InputStream;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class StoreImageBitmapTask  extends AsyncTask<Void, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to Store Image";
	private InputStream is;
	private String imageUrl;
	
	public StoreImageBitmapTask(Context context,
	OnQueryCompleteListener onQueryCompleteListener, InputStream is, String imageUrl, int taskCode) {
		this.context = SnapCommonUtils.getSnapContext(context);
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.is = is;
		this.imageUrl = imageUrl;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
	    SnapCommonUtils.storeImageBitmap(BitmapFactory.decodeStream(is), imageUrl);
	    return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}

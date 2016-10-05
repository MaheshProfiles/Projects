package com.snapbizz.snaptoolkit.asynctasks;

import java.io.File;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.ImageUtil;

public class SaveViewToFileTask extends AsyncTask<Void, Void, Boolean>{
	private static final String TAG = SaveViewToFileTask.class.getSimpleName();

	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private String filePath;
	//private String errorMessage = "failed to save image";
	private String errorMessage = "";
	private Bitmap bitmap;

	public SaveViewToFileTask(OnQueryCompleteListener onQueryCompleteListener,
			int taskCode, String filePath, Bitmap bitmap) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.filePath = filePath;
		if(bitmap != null && !bitmap.isRecycled())
			this.bitmap = Bitmap.createBitmap(bitmap);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if(bitmap == null)
			return false;
		File file = new File(filePath);
		file.mkdirs();
		if(file.exists())
			file.delete();
		if(bitmap.getHeight() == 0 || bitmap.getWidth() == 0)
			return false;
		ImageUtil.saveBitmapToPath(file, bitmap, 90);
		//bitmap.recycle();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result == false || result == null) {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		} else {
			onQueryCompleteListener.onTaskSuccess(null, taskCode);
		}
	}
}

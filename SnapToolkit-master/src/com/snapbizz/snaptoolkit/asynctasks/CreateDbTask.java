package com.snapbizz.snaptoolkit.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapDBUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class CreateDbTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "Db could not be created";
    private ProgressDialog progressDialog;

    public CreateDbTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            SnapDBUtils db = new SnapDBUtils(SnapCommonUtils.getSnapContext(context), SnapToolkitConstants.DB_NAME, SnapToolkitConstants.DB_VERSION);
            db.createDataBaseFromAssets();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        //progressDialog.cancel();
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}
